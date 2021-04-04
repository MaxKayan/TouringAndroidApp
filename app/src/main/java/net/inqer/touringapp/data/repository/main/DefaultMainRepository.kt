package net.inqer.touringapp.data.repository.main

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import net.inqer.touringapp.data.local.dao.TourRouteDao
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.data.remote.RoutesApi
import net.inqer.touringapp.data.repository.Repository
import net.inqer.touringapp.util.DispatcherProvider
import net.inqer.touringapp.util.Resource
import javax.inject.Inject


class DefaultMainRepository @Inject constructor(
        private val api: RoutesApi,
        private val routeDao: TourRouteDao,
        private val dispatchers: DispatcherProvider
) : Repository(), MainRepository {

    override fun getRoutesFlow(): Flow<List<TourRoute>> =
            routeDao.getRoutesFlow()

    override fun getRoutesEvents() = routesEvents.asStateFlow()

    private val routesEvents = MutableStateFlow<Resource<List<TourRoute>>>(Resource.Empty())

    override suspend fun refreshTourRoutes() {
        routesEvents.value = Resource.Loading()
        withContext(dispatchers.io) {
            val currentList = async { routeDao.getRoutesList() }
            val newList = async { processResponse({ api.fetchRoutesBrief() }) }

            val diff = currentList.await().toMutableList()
            val createdTours = mutableListOf<TourRouteBrief>()
            newList.await().data?.forEach { tourRoute ->
                diff.indexOfFirst { it.id == tourRoute.id }.let { index ->
                    if (index > -1) diff.removeAt(index) else createdTours.add(tourRoute)
                }
            }

            if (diff.isNotEmpty()) {
                routeDao.deleteAll(diff)
            }

            routeDao.createByBriefList(createdTours)
            routeDao.updateByBriefList(newList.await().data)

            routesEvents.value = Resource.Updated()
        }
    }

    override suspend fun refreshFullRouteData(id: Long) {
        processResponse({ api.fetchRoute(id) }, routesEvents, { result ->
            routeDao.insert(result)
        })
    }

    override suspend fun getRoute(id: Long): Resource<TourRoute> {
        return try {
            processResponse({ api.fetchRoute(id) })
        } catch (e: Exception) {
            Resource.Error(e.message ?: "")
        }
    }

    companion object {
        private const val TAG = "DefaultMainRepository"
    }
}