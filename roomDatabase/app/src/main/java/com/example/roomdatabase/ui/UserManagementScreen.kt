package com.example.roomdatabase.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.roomdatabase.data.entity.UserEntity
import com.example.roomdatabase.viewmodel.MainViewModel

@Composable
fun UserManagementScreen(viewModel: MainViewModel) {
    val users by viewModel.users.collectAsState()
    val searchQuery by viewModel.lastNameQuery.collectAsState()
    val message by viewModel.uiMessage.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var editingUserId by rememberSaveable { mutableIntStateOf(0) }
    var hasTriedToSave by rememberSaveable { mutableStateOf(false) }
    var userPendingDeletion by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessage()
        }
    }

    if (userPendingDeletion != null) {
        AlertDialog(
            onDismissRequest = { userPendingDeletion = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Quieres eliminar a ${userPendingDeletion?.nombres.orEmpty()} ${userPendingDeletion?.apellidos.orEmpty()}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userPendingDeletion?.let(viewModel::deleteUser)
                        userPendingDeletion = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { userPendingDeletion = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Room Database",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Registro local de usuarios",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre") },
                        singleLine = true,
                        isError = hasTriedToSave && firstName.isBlank()
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Apellido") },
                        singleLine = true,
                        isError = hasTriedToSave && lastName.isBlank()
                    )
                    if (hasTriedToSave && (firstName.isBlank() || lastName.isBlank())) {
                        Text(
                            text = "Completa el nombre y el apellido.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Button(
                        onClick = {
                            hasTriedToSave = true
                            if (firstName.isNotBlank() && lastName.isNotBlank()) {
                                viewModel.saveUser(firstName, lastName, editingUserId.takeIf { it != 0 })
                                firstName = ""
                                lastName = ""
                                editingUserId = 0
                                hasTriedToSave = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isBusy,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (editingUserId == 0) "Guardar Usuario" else "Actualizar Usuario")
                    }
                    if (editingUserId != 0) {
                        TextButton(
                            onClick = {
                                firstName = ""
                                lastName = ""
                                editingUserId = 0
                                hasTriedToSave = false
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Cancelar edición")
                        }
                    }
                    OutlinedButton(
                        onClick = viewModel::enqueueSampleUsersWorker,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isBusy,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Cargar lote de ejemplo con WorkManager")
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::searchUsersByLastName,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar por apellido") },
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Usuarios guardados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = users.size.toString(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            if (users.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) {
                            "Aún no hay usuarios. Registra uno para comenzar."
                        } else {
                            "No se encontraron usuarios con ese apellido."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = users, key = { it.userId }) { user ->
                        UserCard(
                            user = user,
                            onEdit = {
                                editingUserId = user.userId
                                firstName = user.nombres.orEmpty()
                                lastName = user.apellidos.orEmpty()
                                hasTriedToSave = false
                            },
                            onDelete = { userPendingDeletion = user }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(
    user: UserEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.nombres.orEmpty()} ${user.apellidos.orEmpty()}".trim(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "ID ${user.userId}${user.email?.let { " · $it" }.orEmpty()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = onEdit, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text("Editar")
                }
                TextButton(onClick = onDelete, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
