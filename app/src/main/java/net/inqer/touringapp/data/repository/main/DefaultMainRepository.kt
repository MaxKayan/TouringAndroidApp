package net.inqer.touringapp.data.repository.main

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import net.inqer.touringapp.data.local.dao.TourRouteDao
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.response.TourRouteBrief
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
            val currentListJob = async { routeDao.getRoutesList() }
            val newListJob = async { processResponse({ api.fetchRoutesBrief() }, routesEvents) }

            val difference = currentListJob.await().toMutableList()
            val createdTours = mutableListOf<TourRouteBrief>()

            val apiResponse = newListJob.await()
            if (apiResponse.data != null) {
                apiResponse.data.forEach { tourRoute ->
                    difference.indexOfFirst { it.id == tourRoute.id }.let { index ->
                        if (index > -1) difference.removeAt(index) else createdTours.add(tourRoute)
                    }
                }
                if (difference.isNotEmpty()) {
                    routeDao.deleteAll(difference)
                }

                routeDao.createByBriefList(createdTours)
                apiResponse.data.let { routeDao.updateByBriefList(it) }

                routesEvents.value = Resource.Updated()
            } else {
                routesEvents.value = Resource.Error(
                    apiResponse.message
                        ?: "Неизвестная ошибка загрузки данных с сервера!"
                )
            }
        }
    }

    override suspend fun refreshFullRouteData(id: Long) {
        withContext(dispatchers.io) {
            processResponse({ api.fetchRoute(id) }, routesEvents, { result ->
                routeDao.updateFullRoute(result)
            })
        }
    }

    override suspend fun setActiveRoute(id: Long) {
        withContext(dispatchers.io) {
            routeDao.setActiveRoute(id)
        }
    }

    override suspend fun deactivateRoutes() {
        withContext(dispatchers.io) {
            routeDao.deactivateRoutes()
        }
    }

    override fun observeActiveRoute(): Flow<TourRoute> =
        routeDao.observeActiveRoute()
            .distinctUntilChanged()
//                .flowOn(dispatchers.io)


    override suspend fun getRoute(id: Long): TourRoute? =
        routeDao.getRoute(id)

    companion object {
        private const val TAG = "DefaultMainRepository"
    }
}