package net.inqer.touringapp.data.remote

import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.TourRouteBrief
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.math.BigInteger

interface RoutesApi {
    @GET("routes/{route_id}/")
    suspend fun fetchRoute(@Path("route_id") routeId: Long): Response<TourRoute>

    @GET("routes/")
    suspend fun fetchRoutesBrief(): Response<List<TourRoute>>
}