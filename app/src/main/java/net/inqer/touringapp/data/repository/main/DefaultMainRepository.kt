package net.inqer.touringapp.data.repository.main

import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.data.remote.RoutesApi
import net.inqer.touringapp.data.repository.Repository
import net.inqer.touringapp.util.Resource
import javax.inject.Inject

class DefaultMainRepository constructor(
        private val api: RoutesApi
) : Repository(), MainRepository {

    override suspend fun getRoutesBrief(): Resource<List<TourRouteBrief>> {
        return try {
            processResponse(api.getRoutesBrief())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "")
        }
    }

    override suspend fun getRoute(id: Int): Resource<TourRoute> {
        return try {
            processResponse(api.getRoute(id))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "")
        }
    }
}