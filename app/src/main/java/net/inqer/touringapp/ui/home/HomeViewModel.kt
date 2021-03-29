package net.inqer.touringapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.data.repository.main.MainRepository
import net.inqer.touringapp.util.DispatcherProvider
import net.inqer.touringapp.util.Resource
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val repository: MainRepository,
        private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _routes = MutableStateFlow<Resource<List<TourRouteBrief>>>(Resource.Empty())
    val routes: StateFlow<Resource<List<TourRouteBrief>>> = _routes

    fun fetchRoutesBrief() {
        viewModelScope.launch(dispatchers.io) {
            _routes.value = Resource.Loading()
            when (val routesList = repository.getRoutesBrief()) {
                is Resource.Success -> _routes.value = routesList

                else -> _routes.value = routesList
            }
        }
    }

    val routesBriefFlow: Flow<List<TourRouteBrief>>
        get() = repository.getRoutesBriefFlow()
}