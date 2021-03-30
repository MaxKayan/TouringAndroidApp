package net.inqer.touringapp.data.repository.main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.util.Resource

interface MainRepository {
    fun getRoutesBrief(): Flow<List<TourRouteBrief>>
    fun getRoutesBriefEvents(): StateFlow<Resource<List<TourRouteBrief>>>

    suspend fun getRoute(id: Int): Resource<TourRoute>
    suspend fun refreshTourRoutes()
}
