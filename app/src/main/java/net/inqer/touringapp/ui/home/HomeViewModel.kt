package net.inqer.touringapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.repository.main.MainRepository
import net.inqer.touringapp.util.DispatcherProvider
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val repository: MainRepository,
        private val dispatchers: DispatcherProvider
) : ViewModel() {

    init {
        refreshRoutes()
    }

    val routes = repository.getRoutesFlow().asLiveData()
    val routesEvents = repository.getRoutesEvents()

    fun refreshRoutes() {
        viewModelScope.launch {
            repository.refreshTourRoutes()
        }
    }

    fun activateRoute(id: Long) {
        viewModelScope.launch {
            repository.setActiveRoute(id)
        }
    }

    fun deactivateRoutes() {
        viewModelScope.launch {
            repository.deactivateRoutes()
        }
    }

    fun refreshFullRouteData(id: Long) {
        viewModelScope.launch {
            repository.refreshFullRouteData(id)
        }
    }

    fun observeActiveRoute(): Flow<TourRoute?> = repository.observeActiveRoute()
}