# Laboratorio 09: Geolocalización

Aplicación Android del curso Soluciones Móviles I. Integra Google Maps Compose, la ubicación del dispositivo y Nearby Search de Google Places (API New).

## Funcionalidades

- Muestra el mapa centrado inicialmente en la Universidad Privada de Tacna.
- Alterna entre el mapa normal y la vista satelital.
- Solicita permisos de ubicación precisa o aproximada en tiempo de ejecución.
- Obtiene la última ubicación conocida y solicita una ubicación actual si no hay una disponible.
- Muestra marcadores para la UPT, el dispositivo y los lugares encontrados.
- Permite seleccionar un centro de búsqueda con una pulsación larga sobre el mapa.
- Busca lugares cercanos en un radio de 1.5 km; ofrece filtros de restaurantes, cafés, farmacias, hospitales y universidades.
- Presenta nombre, dirección y categoría de cada lugar.
- Deja que el usuario inicie cada consulta para evitar llamadas innecesarias al servicio.

## Configurar Google Maps y Places

1. En Google Cloud, habilita Maps SDK for Android y Places API (New) y vincula facturación al proyecto.
2. Crea una clave restringida para la aplicación Android com.smi.locatioandroid, con las APIs anteriores habilitadas. Configura también la huella SHA-1 de la clave de firma que usarás.
3. Abre el archivo local.properties de la raíz de este proyecto y conserva la línea sdk.dir que administra Android Studio. Agrega o reemplaza:

        MAPS_API_KEY=TU_CLAVE_DE_GOOGLE

4. Sincroniza Gradle y ejecuta la aplicación en un dispositivo o emulador con Google Play Services.
5. Acepta el permiso de ubicación. Pulsa Buscar cercanos y elige una categoría para consultar Places.

El mismo valor de MAPS_API_KEY se entrega al SDK de Maps y al cliente de Places. No se incluye ninguna clave en el código versionado. Las claves de aplicaciones móviles pueden inspeccionarse desde el paquete instalado; protégelas con restricciones de aplicación y de API.

## Compilar

Desde esta carpeta, en PowerShell:

    .\gradlew.bat :app:assembleDebug

El APK de depuración se genera en:

    app/build/outputs/apk/debug/app-debug.apk

## Componentes principales

- app/src/main/java/com/smi/locatioandroid/MainActivity.kt: inicializa Places y crea el ViewModel ligado al ciclo de vida.
- app/src/main/java/com/smi/locatioandroid/screens/MapScreen.kt: mapa, permisos, filtros y resultados.
- app/src/main/java/com/smi/locatioandroid/viewmodel/MapViewModel.kt: estado de ubicación y consultas.
- app/src/main/java/com/smi/locatioandroid/data/GooglePlacesRepository.kt: construcción de la solicitud Nearby Search y conversión de resultados.
- app/src/main/AndroidManifest.xml: permisos y clave de Maps mediante placeholder.

La búsqueda solicita solo los campos necesarios para presentar nombre, dirección, ubicación y categoría. La atribución de Google se mantiene visible junto a los resultados.
