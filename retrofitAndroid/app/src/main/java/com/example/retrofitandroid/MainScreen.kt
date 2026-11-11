package com.example.retrofitandroid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.retrofitandroid.ui.theme.Typography

@Composable
fun MainView(
    modifier: Modifier,
    devices: List<Device>,
    isLoading: Boolean,
    isMutating: Boolean,
    message: String?,
    onAddDevice: (DeviceRequest) -> Unit,
    onDeleteDevice: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Dispositivos",
            modifier = Modifier.fillMaxWidth(),
            style = Typography.displayMedium,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del dispositivo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isMutating
        )
        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isMutating
        )
        OutlinedTextField(
            value = capacity,
            onValueChange = { capacity = it },
            label = { Text("Capacidad (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isMutating
        )
        Button(
            onClick = {
                val spec = if (color.isBlank() && capacity.isBlank()) {
                    null
                } else {
                    Spec(
                        color = color.takeIf { it.isNotBlank() },
                        capacity = capacity.takeIf { it.isNotBlank() }
                    )
                }
                onAddDevice(DeviceRequest(name = name.trim(), data = spec))
                name = ""
                color = ""
                capacity = ""
            },
            enabled = name.isNotBlank() && !isMutating,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isMutating) "Procesando..." else "Agregar dispositivo")
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        message?.let { Text(text = it) }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(items = devices, key = { device -> device.id }) { device ->
                DeviceItemView(
                    device = device,
                    enabled = !isMutating,
                    onDelete = { onDeleteDevice(device.id) }
                )
            }
        }
    }
}
