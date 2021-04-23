package net.inqer.touringapp.ui.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import net.inqer.touringapp.data.models.ActiveRouteDataBus
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteFlow
import net.inqer.touringapp.util.DispatcherProvider
import net.inqer.touringapp.util.GpsLocationProvider
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
        dispatchers: DispatcherProvider,
        val gpsLocationProvider: GpsLocationProvider,
        val fusedLocationProviderClient: FusedLocationProviderClient,
        @ActiveTourRouteFlow private val activeTourRouteFlow: Flow<TourRoute?>,
        val routeDataBus: ActiveRouteDataBus
) : ViewModel() {
    private val _mutableCurrentLocation: MutableLiveData<GeoPoint?> = MutableLiveData()

    private var lastLocation: Location? = null

    private var lastLocationPoint: GeoPoint? = null

    fun updateLocation(location: Location?) {
        lastLocation = location
        lastLocationPoint = GeoPoint(location)

        _mutableCurrentLocation.value = lastLocationPoint

        Log.d(TAG, "updateLocation: $lastLocation ; $lastLocationPoint")
    }

    val activeTourRoute: LiveData<TourRoute?> = activeTourRouteFlow.asLiveData(dispatchers.io)

    val currentLocation: LiveData<GeoPoint?> = _mutableCurrentLocation

    companion object {
        private const val TAG = "MapViewModel"
    }
}