package com.smi.locatioandroid.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.smi.locatioandroid.data.GooglePlacesRepository
import com.smi.locatioandroid.data.NearbyPlace

class MapViewModel(
    private val placesRepository: GooglePlacesRepository?
) : ViewModel() {
    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation

    private val _searchCenter = mutableStateOf(UPT_LOCATION)
    val searchCenter: State<LatLng> = _searchCenter

    private val _nearbyPlaces = mutableStateOf<List<NearbyPlace>>(emptyList())
    val nearbyPlaces: State<List<NearbyPlace>> = _nearbyPlaces

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    private val _locationMessage = mutableStateOf(
        "Se mostrará el mapa centrado en la Universidad Privada de Tacna."
    )
    val locationMessage: State<String> = _locationMessage

    private val _searchMessage = mutableStateOf<String?>(null)
    val searchMessage: State<String?> = _searchMessage

    fun fetchUserLocation(locationClient: FusedLocationProviderClient) {
        _locationMessage.value = "Obteniendo la ubicación del dispositivo..."
        try {
            locationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        updateUserLocation(
                            LatLng(location.latitude, location.longitude)
                        )
                    } else {
                        requestCurrentLocation(locationClient)
                    }
                }
                .addOnFailureListener {
                    requestCurrentLocation(locationClient)
                }
        } catch (exception: SecurityException) {
            _locationMessage.value =
                "No se pudo acceder a la ubicación. Revisa el permiso del dispositivo."
        }
    }

    private fun requestCurrentLocation(locationClient: FusedLocationProviderClient) {
        try {
            locationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            )
                .addOnSuccessListener { location ->
                    if (location == null) {
                        _locationMessage.value =
                            "No se obtuvo una ubicación. Puedes buscar alrededor del marcador de la UPT."
                    } else {
                        updateUserLocation(
                            LatLng(location.latitude, location.longitude)
                        )
                    }
                }
                .addOnFailureListener {
                    _locationMessage.value =
                        "No se obtuvo la ubicación. Puedes buscar alrededor del marcador de la UPT."
                }
        } catch (exception: SecurityException) {
            _locationMessage.value =
                "No se pudo acceder a la ubicación. Revisa el permiso del dispositivo."
        }
    }

    private fun updateUserLocation(location: LatLng) {
        _userLocation.value = location
        _searchCenter.value = location
        _locationMessage.value = "Ubicación actual obtenida."
    }

    fun selectSearchCenter(location: LatLng) {
        _searchCenter.value = location
        _locationMessage.value =
            "Centro de búsqueda actualizado. Mantén pulsado otro punto para cambiarlo."
    }

    fun resetSearchCenterToUser() {
        val location = _userLocation.value
        if (location == null) {
            _searchCenter.value = UPT_LOCATION
            _locationMessage.value =
                "No hay ubicación disponible; se usará la Universidad Privada de Tacna."
        } else {
            _searchCenter.value = location
            _locationMessage.value = "Centro de búsqueda: tu ubicación actual."
        }
    }

    fun searchNearby(includedType: String?) {
        val repository = placesRepository
        if (repository == null) {
            _searchMessage.value =
                "Configura MAPS_API_KEY en local.properties para habilitar Google Places."
            return
        }
        if (_isSearching.value) return

        _isSearching.value = true
        _searchMessage.value = "Buscando lugares en un radio de 1.5 km..."
        repository.searchNearby(
            center = _searchCenter.value,
            includedType = includedType,
            onSuccess = { places ->
                _nearbyPlaces.value = places
                _isSearching.value = false
                _searchMessage.value = if (places.isEmpty()) {
                    "No se encontraron lugares en esta zona."
                } else {
                    "Se encontraron " + places.size + " lugares cercanos."
                }
            },
            onFailure = { exception ->
                _isSearching.value = false
                _searchMessage.value = when {
                    exception is SecurityException ->
                        "Google Places no autorizó la solicitud. Verifica la clave y las APIs habilitadas."
                    exception.message?.contains("network", ignoreCase = true) == true ->
                        "No hay conexión. Comprueba el acceso a internet e inténtalo de nuevo."
                    else ->
                        "No se pudo consultar Google Places. Verifica la clave, la facturación y la conexión."
                }
            }
        )
    }

    class Factory(
        private val placesRepository: GooglePlacesRepository?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(MapViewModel::class.java)) {
                "ViewModel no compatible: " + modelClass.name
            }
            return MapViewModel(placesRepository) as T
        }
    }

    companion object {
        val UPT_LOCATION = LatLng(-18.004821689652044, -70.22611719999999)
    }
}
