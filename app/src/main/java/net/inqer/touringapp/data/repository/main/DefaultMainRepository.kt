package net.inqer.touringapp.data.repository.main

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import net.inqer.touringapp.data.local.AppDatabase
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.TourRouteBrief
import net.inqer.touringapp.data.remote.RoutesApi
import net.inqer.touringapp.data.repository.Repository
import net.inqer.touringapp.util.DispatcherProvider
import net.inqer.touringapp.util.Resource
import javax.inject.Inject


class DefaultMainRepository @Inject constructor(
        private val api: RoutesApi,
        private val database: AppDatabase,
        private val dispatchers: DispatcherProvider
) : Repository(), MainRepository {

    override fun getRoutesBrief(): Flow<List<TourRouteBrief>> =
            database.tourRouteBriefDao().getRoutesFlow()

    override fun getRoutesBriefEvents() = routesBriefEvents.asStateFlow()

    private val routesBriefEvents = MutableStateFlow<Resource<List<TourRouteBrief>>>(Resource.Empty())

    override suspend fun refreshTourRoutes() {
        routesBriefEvents.value = Resource.Loading()
        withContext(dispatchers.io) {
            try {

                val response = api.fetchRoutesBrief()
                val result = response.body()

                if (response.isSuccessful && result != null) {
                    routesBriefEvents.value = Resource.Updated()
                    database.tourRouteBriefDao().insertAll(result)
                } else {
                    Log.e(TAG, "refreshTourRoutes: the response was not successful" +
                            " ${response.message()}")
                    routesBriefEvents.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Log.e(TAG, "refreshTourRoutes: failed to fetch", e)
                routesBriefEvents.value = Resource.Error(e.message ?: "Ошибка загрузки данных!")
            }
        }
    }

    override suspend fun getRoute(id: Int): Resource<TourRoute> {
        return try {
            processResponse(api.getRoute(id))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "")
        }
    }

    companion object {
        private const val TAG = "DefaultMainRepository"
    }
}