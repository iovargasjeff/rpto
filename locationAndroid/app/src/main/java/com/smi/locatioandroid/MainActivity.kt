package com.smi.locatioandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.smi.locatioandroid.data.GooglePlacesRepository
import com.smi.locatioandroid.screens.MapScreen
import com.smi.locatioandroid.ui.theme.LocatioAndroidTheme
import com.smi.locatioandroid.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = BuildConfig.PLACES_API_KEY
        val placesRepository = if (apiKey.isNotBlank() && apiKey != "DEFAULT_API_KEY") {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
            GooglePlacesRepository(Places.createClient(applicationContext))
        } else {
            null
        }
        val mapViewModel = ViewModelProvider(
            this,
            MapViewModel.Factory(placesRepository)
        )[MapViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            LocatioAndroidTheme {
                MapScreen(mapViewModel)
            }
        }
    }
}
