package net.inqer.touringapp.data.repository.main

import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.util.Resource

interface MainRepository {
    suspend fun getRoutesBrief(): Resource<List<TourRouteBrief>>
    suspend fun getRoute(id: Int): Resource<TourRoute>
}
