package net.inqer.touringapp.ui.map

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.inqer.touringapp.data.models.ActiveRouteDataBus
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.repository.main.MainRepository
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteFlow
import net.inqer.touringapp.preferences.AppConfig
import net.inqer.touringapp.service.RouteService
import net.inqer.touringapp.util.DispatcherProvider
import net.inqer.touringapp.util.GpsLocationProvider
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
        val appConfig: AppConfig,

        private val mainRepository: MainRepository,

        dispatchers: DispatcherProvider,
        val gpsLocationProvider: GpsLocationProvider,
        val fusedLocationProviderClient: FusedLocationProviderClient,
        @ActiveTourRouteFlow private val activeTourRouteFlow: Flow<TourRoute?>,
        val routeDataBus: ActiveRouteDataBus
) : ViewModel() {
    private val _mutableCurrentLocation: MutableLiveData<Location?> = MutableLiveData()

    private var lastLocation: Location? = null

    fun updateLocation(location: Location?) {
        lastLocation = location

        _mutableCurrentLocation.value = lastLocation
    }

    val activeTourRoute: LiveData<TourRoute?> = activeTourRouteFlow.asLiveData(dispatchers.io)

    val currentLocation: LiveData<Location?> = _mutableCurrentLocation


    fun nextWaypoint(context: Context) = RouteService.nextWaypoint(context)

    fun prevWaypoint(context: Context) = RouteService.prevWaypoint(context)

    fun cancelTourRoute() {
        viewModelScope.launch {
            mainRepository.deactivateRoutes()
        }
    }

    companion object {
        private const val TAG = "MapViewModel"
    }
}