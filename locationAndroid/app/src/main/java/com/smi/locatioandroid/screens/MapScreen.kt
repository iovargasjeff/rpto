package com.smi.locatioandroid.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.smi.locatioandroid.data.NearbyPlace
import com.smi.locatioandroid.viewmodel.MapViewModel

private data class PlaceCategory(
    val label: String,
    val placeType: String?
)

private val placeCategories = listOf(
    PlaceCategory("Todos", null),
    PlaceCategory("Restaurantes", "restaurant"),
    PlaceCategory("Cafés", "cafe"),
    PlaceCategory("Farmacias", "pharmacy"),
    PlaceCategory("Hospitales", "hospital"),
    PlaceCategory("Universidades", "university")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(mapViewModel: MapViewModel) {
    val context = LocalContext.current
    val locationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(MapViewModel.UPT_LOCATION, 14f)
    }

    var hasLocationPermission by remember {
        mutableStateOf(hasLocationPermission(context))
    }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var selectedCategory by remember { mutableStateOf(placeCategories.first()) }

    val userLocation by mapViewModel.userLocation
    val searchCenter by mapViewModel.searchCenter
    val nearbyPlaces by mapViewModel.nearbyPlaces
    val isSearching by mapViewModel.isSearching
    val locationMessage by mapViewModel.locationMessage
    val searchMessage by mapViewModel.searchMessage

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            mapViewModel.fetchUserLocation(locationClient)
        } else {
            mapViewModel.selectSearchCenter(MapViewModel.UPT_LOCATION)
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            mapViewModel.fetchUserLocation(locationClient)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(searchCenter) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(searchCenter, 15f),
            durationMs = 650
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Geolocalización")
                        Text(
                            "Universidad Privada de Tacna",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            mapType = if (mapType == MapType.NORMAL) {
                                MapType.SATELLITE
                            } else {
                                MapType.NORMAL
                            }
                        }
                    ) {
                        Text(if (mapType == MapType.NORMAL) "Satélite" else "Mapa")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = mapType,
                        isMyLocationEnabled = hasLocationPermission
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = false,
                        compassEnabled = true
                    ),
                    onMapLongClick = mapViewModel::selectSearchCenter
                ) {
                    Marker(
                        state = rememberUpdatedMarkerState(position = MapViewModel.UPT_LOCATION),
                        title = "Universidad Privada de Tacna",
                        snippet = "Marcador inicial del laboratorio"
                    )

                    userLocation?.let { location ->
                        Marker(
                            state = rememberUpdatedMarkerState(position = location),
                            title = "Tu ubicación actual",
                            snippet = "Ubicación obtenida con Google Play Services"
                        )
                    }

                    if (searchCenter != MapViewModel.UPT_LOCATION &&
                        searchCenter != userLocation
                    ) {
                        Marker(
                            state = rememberUpdatedMarkerState(position = searchCenter),
                            title = "Centro de búsqueda",
                            snippet = "Punto seleccionado con una pulsación larga"
                        )
                    }

                    nearbyPlaces.forEach { place ->
                        key(place.id) {
                            Marker(
                                state = rememberUpdatedMarkerState(position = place.position),
                                title = place.name,
                                snippet = place.address
                                    ?: place.category
                                    ?: "Lugar cercano"
                            )
                        }
                    }
                }

                OutlinedButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    onClick = {
                        if (hasLocationPermission) {
                            mapViewModel.fetchUserLocation(locationClient)
                            mapViewModel.resetSearchCenterToUser()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Mi ubicación")
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Lugares cercanos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(placeCategories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category.label) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = !isSearching,
                            onClick = {
                                mapViewModel.searchNearby(selectedCategory.placeType)
                            }
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(end = 8.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            Text(if (isSearching) "Buscando..." else "Buscar cercanos")
                        }
                        TextButton(
                            onClick = { mapViewModel.resetSearchCenterToUser() }
                        ) {
                            Text("Restablecer")
                        }
                    }

                    Text(
                        locationMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    searchMessage?.let { message ->
                        Text(
                            message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (nearbyPlaces.isEmpty() && !isSearching) {
                            item {
                                Text(
                                    "Elige una categoría y pulsa Buscar cercanos. " +
                                        "Mantén pulsado el mapa para cambiar el punto de búsqueda.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        items(
                            items = nearbyPlaces,
                            key = { place -> place.id }
                        ) { place ->
                            NearbyPlaceCard(
                                place = place,
                                onClick = {
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(place.position, 16f)
                                }
                            )
                        }
                    }

                    Text(
                        "Información de lugares proporcionada por Google.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun NearbyPlaceCard(
    place: NearbyPlace,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                place.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            place.category?.let { category ->
                Text(
                    category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            place.address?.let { address ->
                Text(
                    address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fineGranted || coarseGranted
}
