package com.smi.locatioandroid.data

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest

data class NearbyPlace(
    val id: String,
    val name: String,
    val address: String?,
    val category: String?,
    val position: LatLng
)

class GooglePlacesRepository(
    private val placesClient: PlacesClient
) {
    fun searchNearby(
        center: LatLng,
        includedType: String?,
        onSuccess: (List<NearbyPlace>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.PRIMARY_TYPE_DISPLAY_NAME
        )
        val requestBuilder = SearchNearbyRequest.builder(
            CircularBounds.newInstance(center, SEARCH_RADIUS_METERS),
            fields
        )
            .setMaxResultCount(MAX_RESULTS)
            .setRegionCode("PE")

        if (includedType != null) {
            requestBuilder.setIncludedTypes(listOf(includedType))
        }

        placesClient.searchNearby(requestBuilder.build())
            .addOnSuccessListener { response ->
                val nearbyPlaces = response.places.mapNotNull { place ->
                    val position = place.location ?: return@mapNotNull null
                    val name = place.displayName
                        ?.takeIf { it.isNotBlank() }
                        ?: "Lugar sin nombre"

                    NearbyPlace(
                        id = place.id ?: name + position.toString(),
                        name = name,
                        address = place.formattedAddress,
                        category = place.primaryTypeDisplayName,
                        position = position
                    )
                }
                onSuccess(nearbyPlaces)
            }
            .addOnFailureListener(onFailure)
    }

    private companion object {
        const val SEARCH_RADIUS_METERS = 1500.0
        const val MAX_RESULTS = 20
    }
}
