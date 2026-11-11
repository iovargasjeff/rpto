package com.example.roomdatabase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.roomdatabase.application.RoomApplication
import com.example.roomdatabase.data.dao.UserDao
import com.example.roomdatabase.data.entity.UserEntity
import com.example.roomdatabase.worker.SeedUsersWorker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val userDao: UserDao = RoomApplication.db.userDao()

    private val _lastNameQuery = MutableStateFlow("")
    val lastNameQuery: StateFlow<String> = _lastNameQuery.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val users: StateFlow<List<UserEntity>> = _lastNameQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                userDao.observeAll()
            } else {
                userDao.searchByLastName(query.trim())
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insertUser(
        nombres: String = "Juan",
        apellidos: String = "Perez",
        email: String? = null
    ) {
        runDatabaseAction("Usuario guardado.") {
            userDao.insertUser(UserEntity(nombres = nombres, apellidos = apellidos, email = email))
        }
    }

    fun insertUserList(people: List<UserEntity> = sampleUsers()) {
        if (people.isEmpty()) return
        runDatabaseAction("Se guardó el lote de usuarios.") {
            userDao.insertUserList(people)
        }
    }

    fun updateUser(user: UserEntity) {
        runDatabaseAction("Usuario actualizado.") {
            check(userDao.updateUser(user) > 0) { "No se encontró el usuario para actualizar." }
        }
    }

    fun deleteUser(user: UserEntity) {
        runDatabaseAction("Usuario eliminado.") {
            check(userDao.deleteUser(user) > 0) { "No se encontró el usuario para eliminar." }
        }
    }

    fun saveUser(nombres: String, apellidos: String, editingUserId: Int? = null) {
        val nombre = nombres.trim()
        val apellido = apellidos.trim()
        if (nombre.isBlank() || apellido.isBlank()) {
            _uiMessage.value = "Escribe el nombre y el apellido."
            return
        }

        if (editingUserId == null) {
            insertUser(nombre, apellido)
        } else {
            updateUser(UserEntity(userId = editingUserId, nombres = nombre, apellidos = apellido))
        }
    }

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.observeAll()

    fun searchUsersByLastName(query: String) {
        _lastNameQuery.value = query
    }

    fun enqueueSampleUsersWorker() {
        val request = OneTimeWorkRequestBuilder<SeedUsersWorker>().build()
        WorkManager.getInstance(RoomApplication.instance).enqueueUniqueWork(
            SeedUsersWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
        _uiMessage.value = "Se programó la carga de ejemplo en segundo plano."
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    private fun runDatabaseAction(successMessage: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            if (_isBusy.value) return@launch
            _isBusy.value = true
            _uiMessage.value = null
            try {
                withContext(Dispatchers.IO) { action() }
                _uiMessage.value = successMessage
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                _uiMessage.value = exception.message ?: "No se pudo completar la operación."
            } finally {
                _isBusy.value = false
            }
        }
    }

    private fun sampleUsers(): List<UserEntity> = listOf(
        UserEntity(nombres = "Juan", apellidos = "Perez"),
        UserEntity(nombres = "Maria", apellidos = "Lopez"),
        UserEntity(nombres = "Pedro", apellidos = "Gonzalez"),
        UserEntity(nombres = "Ana", apellidos = "Martinez"),
        UserEntity(nombres = "Luis", apellidos = "Garcia")
    )
}
