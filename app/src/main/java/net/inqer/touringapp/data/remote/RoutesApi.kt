package net.inqer.touringapp.data.remote

import net.inqer.touringapp.data.models.TourRoute
import retrofit2.Response
import retrofit2.http.GET

interface RoutesApi {
    @GET("/routes/")
    suspend fun getRoutes(): Response<TourRoute>
}