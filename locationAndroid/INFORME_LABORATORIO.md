# Informe de laboratorio 09: Geolocalización

**Curso:** Soluciones Móviles I  
**Estudiante:** ______________________________________________  
**Docente:** Mag. Alberto Johnatan Flor Rodríguez  
**Fecha:** 11 de noviembre de 2026  
**Lugar:** Tacna, Perú

## 1. Objetivos

- Implementar un mapa interactivo con Google Maps Compose en una aplicación Android con Kotlin.
- Solicitar permisos de ubicación y obtener la posición del dispositivo mediante Google Play Services.
- Separar el estado y la lógica de la pantalla usando un ViewModel.
- Consultar Google Places para mostrar información de establecimientos próximos.

## 2. Desarrollo

Se completó el proyecto locationAndroid indicado en la guía. La pantalla inicia centrada en la Universidad Privada de Tacna y ofrece los modos normal y satelital. Solicita ubicación precisa o aproximada; cuando se concede, obtiene la última posición conocida y solicita una posición actual si no existe una guardada. El usuario puede mantener pulsado otro punto del mapa para cambiar el centro de búsqueda.

La integración de Google Places permite buscar lugares en un radio de 1.5 km. Se puede consultar cualquier tipo de lugar o filtrar restaurantes, cafés, farmacias, hospitales y universidades. Los resultados aparecen en una lista y como marcadores; cada elemento muestra nombre, categoría y dirección cuando el servicio la proporciona.

La clave de Google se lee desde local.properties, archivo excluido del control de versiones. El proyecto no contiene una clave activa. Para habilitar los mapas y las consultas es necesario crear una clave propia en Google Cloud, habilitar Maps SDK for Android y Places API (New), vincular facturación y restringir la clave a la aplicación.

## 3. Procedimiento de ejecución

1. Abrir locationAndroid en Android Studio y sincronizar Gradle.
2. Agregar MAPS_API_KEY=TU_CLAVE_DE_GOOGLE a local.properties, sin borrar la propiedad sdk.dir que agrega Android Studio.
3. Habilitar Maps SDK for Android y Places API (New) en el mismo proyecto de Google Cloud.
4. Ejecutar la aplicación en un emulador con Google Play Services o en un dispositivo Android.
5. Conceder el permiso de ubicación.
6. Elegir una categoría y pulsar Buscar cercanos. Para buscar alrededor de otro lugar, mantener pulsado el mapa y repetir la consulta.

## 4. Cuestionario

### Implemente el API de Google Places para consultar sobre la información de lugares cercanos.

Se implementó Nearby Search de Places API (New) mediante Places SDK for Android 5.2.0. Al iniciar la aplicación, MainActivity inicializa el cliente con la clave configurada y lo entrega al ViewModel por medio de GooglePlacesRepository.

Al pulsar Buscar cercanos, el repositorio construye una restricción circular de 1,500 metros con el centro de búsqueda actual. Si el usuario eligió una categoría, agrega ese tipo a la solicitud; si seleccionó Todos, consulta los tipos disponibles en el área. La solicitud limita la respuesta a 20 lugares y define los campos que se necesitan para la interfaz: identificador, nombre, dirección, ubicación y nombre visible de la categoría.

El ViewModel expone los resultados y los estados de carga y error. MapScreen dibuja un marcador para cada resultado y muestra sus datos en una lista seleccionable. También mantiene un texto de atribución de Google. Si no se configuró una clave, la aplicación explica cómo hacerlo; los errores de conexión o autorización se presentan al usuario.

**Archivos de la implementación:**

- locationAndroid/app/src/main/java/com/smi/locatioandroid/MainActivity.kt
- locationAndroid/app/src/main/java/com/smi/locatioandroid/data/GooglePlacesRepository.kt
- locationAndroid/app/src/main/java/com/smi/locatioandroid/viewmodel/MapViewModel.kt
- locationAndroid/app/src/main/java/com/smi/locatioandroid/screens/MapScreen.kt

## 5. Conclusiones

1. Google Maps Compose permite integrar mapas, marcadores y controles de cámara dentro de una interfaz declarativa de Jetpack Compose.
2. La ubicación debe solicitarse en tiempo de ejecución y puede ser aproximada; la aplicación conserva un centro inicial en la UPT para seguir permitiendo la consulta cuando el usuario no comparte su ubicación.
3. Nearby Search devuelve lugares dentro de una restricción geográfica. Solicitar un conjunto de campos limitado facilita controlar los datos y el nivel de uso facturable.
4. Separar la pantalla, el ViewModel y el repositorio hace que la interfaz se mantenga enfocada en presentar estados, mientras que la lógica de búsqueda queda aislada.

## 6. Verificación

La aplicación compiló correctamente con Gradle y generó el APK de depuración en locationAndroid/app/build/outputs/apk/debug/app-debug.apk. La visualización del mapa y la respuesta real de Places requieren una clave válida, las APIs habilitadas y un dispositivo o emulador con Google Play Services.

## 7. Referencias

- Guía Práctica de Laboratorio SMI09, “Geolocalización”, Escuela Profesional de Ingeniería de Sistemas, 2026.
- Google for Developers, Maps Compose Library: https://developers.google.com/maps/documentation/android-sdk/maps-compose
- Google for Developers, Nearby Search (New) para Android: https://developers.google.com/maps/documentation/places/android-sdk/nearby-search
- Google for Developers, configuración del Places SDK para Android: https://developers.google.com/maps/documentation/places/android-sdk/config

## Observación sobre la guía

Los resultados de aprendizaje y algunas referencias de la guía mencionan Workers y WorkManager, mientras que el título, los objetivos, el procedimiento y el cuestionario describen geolocalización y Google Maps. Esta solución sigue el procedimiento y el cuestionario específico de geolocalización.
