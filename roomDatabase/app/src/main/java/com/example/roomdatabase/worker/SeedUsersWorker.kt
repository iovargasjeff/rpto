package com.example.roomdatabase.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.roomdatabase.application.RoomApplication
import com.example.roomdatabase.data.entity.UserEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SeedUsersWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        withContext(Dispatchers.IO) {
            val dao = RoomApplication.db.userDao()
            val examples = listOf(
                UserEntity(nombres = "Juan", apellidos = "Perez"),
                UserEntity(nombres = "Maria", apellidos = "Lopez"),
                UserEntity(nombres = "Pedro", apellidos = "Gonzalez"),
                UserEntity(nombres = "Ana", apellidos = "Martinez"),
                UserEntity(nombres = "Luis", apellidos = "Garcia")
            )
            val currentUsers = dao.getAll()
            val missingUsers = examples.filter { example ->
                currentUsers.none { current ->
                    current.nombres.equals(example.nombres, ignoreCase = true) &&
                        current.apellidos.equals(example.apellidos, ignoreCase = true)
                }
            }

            if (missingUsers.isNotEmpty()) {
                dao.insertUserList(missingUsers)
            }
            Result.success(workDataOf("inserted_count" to missingUsers.size))
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
    }

    companion object {
        const val UNIQUE_WORK_NAME = "seed-sample-users"
        private const val MAX_RETRIES = 3
    }
}
