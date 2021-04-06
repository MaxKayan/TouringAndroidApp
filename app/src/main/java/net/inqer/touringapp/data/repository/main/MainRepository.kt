package net.inqer.touringapp.data.repository.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.util.Resource

interface MainRepository {
    fun getRoutesFlow(): Flow<List<TourRoute>>
    fun getRoutesEvents(): StateFlow<Resource<List<TourRoute>>>

    suspend fun getRoute(id: Long): Resource<TourRoute>
    suspend fun refreshTourRoutes()
    suspend fun refreshFullRouteData(id: Long)

    suspend fun setActiveRoute(id: Long)
}
