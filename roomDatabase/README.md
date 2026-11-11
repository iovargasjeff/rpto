# roomDatabase

Aplicación Android en Kotlin para el Laboratorio 06: **Implementación de Room Database en Kotlin**.

## Qué incluye

- Persistencia local con Room y una entidad `UserEntity` con `userId`, nombres, apellidos y `email` opcional.
- DAO con inserción individual y por lote, actualización, eliminación, consulta observable y búsqueda por apellido con `LIKE`.
- Base de datos versión 2 con `.fallbackToDestructiveMigration()`, tal como solicita la guía.
- Pantalla Jetpack Compose para registrar, editar, eliminar, buscar y ver usuarios.
- Corrutinas en `Dispatchers.IO` para las operaciones del `ViewModel`.
- Un `CoroutineWorker` idempotente que carga el lote de ejemplo de la guía en segundo plano. WorkManager conserva el trabajo en su cola para reanudarlo cuando el sistema vuelva a permitir la ejecución.
- El informe editable con las conclusiones y respuestas del cuestionario: `INFORME_LABORATORIO.md`.

## Abrir y ejecutar

1. Abre esta carpeta (`roomDatabase`) en Android Studio.
2. Espera a que termine la sincronización de Gradle.
3. Ejecuta la configuración `app` en un emulador o dispositivo Android.

También puedes compilar desde PowerShell con `gradlew.bat :app:assembleDebug`.

El proyecto sigue las versiones de Kotlin, KSP, Compose BOM y Room indicadas en la guía. Requiere JDK 17 o superior y Android SDK API 36.
