package net.inqer.touringapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    //    private val _routes = MutableStateFlow<Resource<List<TourRouteBrief>>>(Resource.Empty())
//    val routes: StateFlow<Resource<List<TourRouteBrief>>> = repository.getRoutesBriefFlow().asLiveData()
    val routes = repository.getRoutesFlow().asLiveData()
    val routesEvents = repository.getRoutesEvents()

//    init {
//        viewModelScope.launch(dispatchers.io) {
//            repository.getRoutesBriefFlow()
//                    .onStart {
//                        _routes.value = Resource.Loading()
//                    }
//                    .onEach { routes ->
//                        _routes.value = Resource.Success(routes)
//                    }
//                    .catch { cause ->
//                        _routes.value = Resource.Error(cause.message ?: "Ошибка")
//                    }
//                    .collect()
//        }
//    }

    fun refreshRoutes() {
        viewModelScope.launch {
            repository.refreshTourRoutes()
        }
    }

    fun refreshFullRouteData(id: Long) {
        viewModelScope.launch {
            repository.refreshFullRouteData(id)
        }
    }

//    val routesBriefFlow: Flow<List<TourRouteBrief>>
//        get() = repository.getRoutesBriefFlow()
}