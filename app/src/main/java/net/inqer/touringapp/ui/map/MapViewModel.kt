package net.inqer.touringapp.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteFlow
import net.inqer.touringapp.util.DispatcherProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
        dispatchers: DispatcherProvider,
        val gpsLocationProvider: GpsMyLocationProvider,
        val fusedLocationProviderClient: FusedLocationProviderClient,
        @ActiveTourRouteFlow private val activeTourRouteFlow: Flow<TourRoute>
) : ViewModel() {
    var lastLocation: Location? = null

    val activeTourRoute: LiveData<TourRoute?> = activeTourRouteFlow.asLiveData(dispatchers.io)
}