# Laboratorio 06: Implementación de Room Database en Kotlin

**Curso:** Soluciones Móviles I  
**Estudiante:** ____________________________________  
**Docente:** Mag. Alberto Johnatan Flor Rodríguez  
**Año:** 2026

## Implementación realizada

Se creó una aplicación Android en Kotlin con el paquete `com.example.roomdatabase`. La base de datos local usa Room y contiene la entidad `UserEntity`, con identificador autogenerado, nombres, apellidos y un campo `email` nullable. `AppDatabase` está configurada en versión 2 y usa la migración destructiva básica solicitada por la guía.

`UserDao` permite insertar usuarios individuales o por lote, actualizar, eliminar, obtener usuarios y buscar por apellido mediante `LIKE`. La pantalla Compose permite registrar, editar, eliminar y buscar usuarios. La lista se observa como un `Flow`, por lo que refleja los cambios de Room después de cada operación.

Las operaciones del `ViewModel` se ejecutan en `Dispatchers.IO`. También se añadió una acción demostrativa de WorkManager: el botón **Cargar lote de ejemplo con WorkManager** encola un `CoroutineWorker` que inserta los usuarios de ejemplo de forma idempotente. WorkManager ejecuta trabajo asíncrono programable y mantiene la solicitud en su sistema de trabajo para ejecutarla cuando corresponda, incluso si el proceso de la app deja de estar activo ([guía oficial de WorkManager](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started), [CoroutineWorker](https://developer.android.com/develop/background-work/background-tasks/persistent/threading/coroutineworker)).

El formulario muestra los dos campos solicitados (nombre y apellido); `email` queda nullable en la entidad y no se pide en la interfaz. La migración destructiva está configurada como ejercicio básico, tal como indica la guía.

## Conclusiones

1. Room simplifica la persistencia local al separar la representación de los datos (`Entity`), las operaciones (`DAO`) y la configuración de la base de datos (`Database`). Esta separación facilita mantener el código y permite que Room valide las consultas SQL durante la compilación ([Room y DAO](https://developer.android.com/training/data-storage/room/accessing-data)).

2. El uso de un `Flow` desde el DAO permite que Compose observe la base de datos y actualice la lista al insertar, modificar o eliminar usuarios, sin tener que recargarla manualmente.

3. `viewModelScope` organiza las operaciones vinculadas a la pantalla y `Dispatchers.IO` se usa para el trabajo de entrada/salida. Así se evita trasladar trabajo de base de datos a la lógica de dibujo de la interfaz ([corrutinas en Android](https://developer.android.com/kotlin/coroutines/coroutines-adv)).

4. La migración destructiva resuelve de manera sencilla el cambio de esquema a la versión 2, pero elimina las tablas existentes cuando no hay una migración definida. En una aplicación que deba conservar datos, se debería escribir una migración explícita.

## Cuestionario

### 1. ¿Cuál es la función principal de `@Dao` y qué ventaja ofrece sobre el uso directo de `SQLiteOpenHelper`?

`@Dao` identifica una interfaz o clase que declara cómo leer y modificar los datos de Room. Sus métodos pueden usar anotaciones como `@Insert`, `@Update`, `@Delete` y `@Query`. Room genera la implementación durante la compilación y valida las consultas SQL, lo que reduce código repetitivo y permite detectar errores de consulta antes de ejecutar la app ([documentación de DAO](https://developer.android.com/training/data-storage/room/accessing-data)).

### 2. ¿Qué sucede si se intenta insertar un objeto con `userId` inicializado en 0 y `@PrimaryKey(autoGenerate = true)`?

Para una clave primaria `Int` o `Long` autogenerada, Room trata el valor `0` como “sin asignar”. Al insertar la entidad, SQLite genera un identificador único y la fila recibe ese valor ([documentación de `@PrimaryKey`](https://developer.android.com/reference/androidx/room/PrimaryKey)).

### 3. ¿Por qué ejecutar las funciones del DAO dentro de `viewModelScope.launch(Dispatchers.IO)`? ¿Qué ocurriría en el hilo principal?

`viewModelScope` inicia la operación dentro del ciclo de vida del `ViewModel`; el trabajo se cancela cuando este se elimina. `Dispatchers.IO` está destinado a operaciones de entrada/salida, como lectura y escritura en almacenamiento. Si una operación bloqueante se ejecutara en el hilo principal, la interfaz podría dejar de responder y, si el bloqueo se prolonga, Android podría mostrar un ANR. Las funciones suspend de Room gestionan internamente su ejecución segura, pero la guía pide usar `Dispatchers.IO` explícitamente ([guía de corrutinas de Android](https://developer.android.com/kotlin/coroutines/coroutines-adv)).

### 4. ¿Cuál es el propósito de heredar de `Application` para inicializar la base de datos?

La clase `Application` sirve para mantener estado compartido durante la vida del proceso de la app. Su `onCreate()` se ejecuta antes de que se creen las actividades, por lo que allí se puede inicializar una instancia única de Room y compartirla con los componentes de la aplicación. En este proyecto, `RoomApplication` crea `AppDatabase` y expone su DAO ([referencia de `Application`](https://developer.android.com/reference/android/app/Application)).

### 5. ¿Qué función cumple KSP en relación con Room?

KSP procesa símbolos y anotaciones Kotlin durante la compilación. Room lo usa para leer las anotaciones de `UserEntity`, `UserDao` y `AppDatabase`, generar el código de acceso a datos y comprobar las consultas. Esto evita escribir manualmente la implementación del DAO ([migración de procesadores a KSP](https://developer.android.com/build/migrate-to-ksp)).

## Comprobación de compilación

Se compiló la variante de depuración con:

```powershell
.\gradlew.bat :app:assembleDebug
```

Resultado: `BUILD SUCCESSFUL`. El APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Referencias bibliográficas

- Android Developers. *Save data in a local database using Room*. https://developer.android.com/training/data-storage/room
- Android Developers. *Access data using Room DAOs*. https://developer.android.com/training/data-storage/room/accessing-data
- Android Developers. *Define data using Room entities*. https://developer.android.com/training/data-storage/room/defining-data
- Android Developers. *Improve app performance with Kotlin coroutines*. https://developer.android.com/kotlin/coroutines/coroutines-adv
- Android Developers. *Application*. https://developer.android.com/reference/android/app/Application
- Android Developers. *Getting started with WorkManager*. https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started
- Android Developers. *Threading in CoroutineWorker*. https://developer.android.com/develop/background-work/background-tasks/persistent/threading/coroutineworker
- Android Developers. *Migrate from kapt to KSP*. https://developer.android.com/build/migrate-to-ksp
