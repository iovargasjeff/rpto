# retrofitAndroid

Aplicación Android de demostración que ilustra el uso de **Retrofit** para consumir APIs REST y **Jetpack Compose** para la interfaz de usuario moderna.

## Descripción
Esta app obtiene una lista de dispositivos desde la API pública `https://api.restful-api.dev/objects` usando Retrofit, y muestra la información en una lista desplazable construida con Jetpack Compose. Es un proyecto de aprendizaje enfocado en la integración de redes y UI declarativa en Android.

## Características
- Consumo de API RESTful con Retrofit y corrutinas de Kotlin
- Creación de dispositivos mediante `POST /objects`
- Eliminación de dispositivos mediante `DELETE /objects/{id}`
- Visualización de datos en `LazyColumn` (Jetpack Compose)
- Muestra nombre, color y capacidad de cada dispositivo
- Diseño con Material 3
- Gestión de estado con `mutableStateOf` de Compose
- Estados de carga y mensajes para errores y operaciones completadas

## Arquitectura
La aplicación usa una arquitectura simple (sin patrones MVVM/MVP completos):
- No se emplea ViewModel; el estado se gestiona directamente con Compose en `MainActivity`
- La consulta inicial se inicia una vez con `LaunchedEffect`; Retrofit se conserva en la Activity
- UI modular basada en composables de Compose

## Estructura del Proyecto
```
retrofitAndroid/
├── build.gradle.kts                 # Configuración de build a nivel de proyecto
├── gradle/
│   └── libs.versions.toml           # Catálogo de versiones de dependencias
└── app/
    ├── build.gradle.kts             # Configuración de build de la aplicación
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/example/retrofitandroid/
        │   ├── MainActivity.kt       # Punto de entrada, inicializa Retrofit y gestiona estado
        │   ├── Constants.kt         # Constantes de la API
        │   ├── Device.kt            # Modelo de datos para dispositivos
        │   ├── DeviceRequest.kt     # Cuerpo de solicitud para crear un dispositivo
        │   ├── Spec.kt              # Modelo de datos para especificaciones
        │   ├── DeviceService.kt     # Interfaz de Retrofit para endpoints
        │   ├── MainScreen.kt        # Composable principal de la UI
        │   ├── DeviceItem.kt        # Composable para elemento individual de dispositivo
        │   └── ui/theme/            # Temas y estilos de Jetpack Compose
        └── res/                     # Recursos de la app (íconos, strings, etc.)
```

## Tecnologías y Dependencias
| Biblioteca | Versión | Propósito |
|------------|---------|-----------|
| Retrofit | 3.0.0 | Cliente HTTP para llamadas a API |
| Gson Converter | 3.0.0 | Serialización/deserialización JSON para Retrofit |
| Kotlin | 2.3.21 | Lenguaje de programación |
| Android Gradle Plugin (AGP) | 9.1.1 | Sistema de build |
| Jetpack Compose BOM | 2026.05.00 | Toolkit de UI (Bill of Materials) |
| Lifecycle Runtime KTX | 2.10.0 | Componentes conscientes del ciclo de vida |
| Activity Compose | 1.13.0 | Integración de Compose con Activity |

## Detalles de la API
- **URL Base**: `https://api.restful-api.dev`
- **Endpoints**:
  - `GET /objects` - Obtiene la lista de dispositivos
  - `POST /objects` - Crea un dispositivo con nombre y datos opcionales de color/capacidad
  - `DELETE /objects/{id}` - Elimina el dispositivo seleccionado
- **Modelos de Datos**:
  ```kotlin
  data class Device(val id: String, val name: String, val data: Spec?)
  data class DeviceRequest(val name: String, val data: Spec?)
  data class Spec(val color: String?, val capacity: String?)
  ```

## Requisitos Previos
- Android Studio con soporte para AGP 9.1.1
- JDK 21 (el Gradle Wrapper del proyecto usa esta versión)
- SDK de Android con plataforma API 36 instalada
- Dispositivo o emulador con Android 11 (API 30) o superior
- Conexión a internet (para descargar dependencias y consumir la API)

## Instrucciones de Build y Ejecución
1. Abre el proyecto en Android Studio: `D:\AndroidStudioProjects\retrofitAndroid`
2. Sincroniza los archivos de Gradle (*File > Sync Project with Gradle Files*)
3. Conecta un dispositivo o inicia un emulador (API 30+)
4. Haz clic en *Run* o usa `Shift + F10`

### Comandos de Gradle (terminal):
```bash
# Limpiar y construir el proyecto
./gradlew clean build

# Instalar versión debug en dispositivo/emulador conectado
./gradlew installDebug
```

## Configuración de SDK
| Parámetro | Valor |
|-----------|-------|
| minSdk | 30 (Android 11) |
| targetSdk | 36 (Android 16) |
| compileSdk | 36 |
| versionCode | 1 |
| versionName | "1.0" |

## Notas
- La app requiere el permiso `android.permission.INTERNET` para realizar llamadas a la API
- La instancia de Retrofit se crea de forma diferida y se reutiliza desde `MainActivity`
- La API utilizada (`restful-api.dev`) es una API de prueba gratuita para fines demostrativos
