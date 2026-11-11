package com.example.retrofitandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.retrofitandroid.ui.theme.RetrofitAndroidTheme
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private val deviceService: DeviceService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeviceService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetrofitAndroidTheme {
                var devices by remember { mutableStateOf(emptyList<Device>()) }
                var isLoading by remember { mutableStateOf(true) }
                var isMutating by remember { mutableStateOf(false) }
                var message by remember { mutableStateOf<String?>(null) }
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    try {
                        devices = deviceService.getAllDevices()
                    } catch (exception: Exception) {
                        message = "No se pudieron cargar los dispositivos: ${exception.message ?: "error desconocido"}"
                    } finally {
                        isLoading = false
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        modifier = Modifier.padding(innerPadding),
                        devices = devices,
                        isLoading = isLoading,
                        isMutating = isMutating,
                        message = message,
                        onAddDevice = { request ->
                            scope.launch {
                                isMutating = true
                                message = null
                                try {
                                    val createdDevice = deviceService.addDevice(request)
                                    devices = devices + createdDevice
                                    message = "Dispositivo agregado con ID ${createdDevice.id}."
                                } catch (exception: Exception) {
                                    message = "No se pudo agregar el dispositivo: ${exception.message ?: "error desconocido"}"
                                } finally {
                                    isMutating = false
                                }
                            }
                        },
                        onDeleteDevice = { id ->
                            scope.launch {
                                isMutating = true
                                message = null
                                try {
                                    deviceService.deleteDevice(id)
                                    devices = devices.filterNot { it.id == id }
                                    message = "Dispositivo $id eliminado."
                                } catch (exception: Exception) {
                                    message = "No se pudo eliminar el dispositivo: ${exception.message ?: "error desconocido"}"
                                } finally {
                                    isMutating = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RetrofitAndroidTheme {
        Greeting("Android")
    }
}
