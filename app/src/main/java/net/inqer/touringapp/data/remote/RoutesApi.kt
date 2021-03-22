package net.inqer.touringapp.data.remote

import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.TourRouteBrief
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.math.BigInteger

interface RoutesApi {
    @GET("routes/{route_id}/")
    suspend fun getRoute(@Path("route_id") routeId: Int): Response<TourRoute>

    @GET("routes/")
    suspend fun getRoutesBrief(): Response<List<TourRouteBrief>>
}