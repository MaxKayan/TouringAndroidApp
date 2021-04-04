package net.inqer.touringapp.data.repository.main

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import net.inqer.touringapp.data.local.dao.TourRouteDao
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.remote.RoutesApi
import net.inqer.touringapp.data.repository.Repository
import net.inqer.touringapp.util.DispatcherProvider
import net.inqer.touringapp.util.Resource
import retrofit2.Response
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
            try {
                val response = api.fetchRoutesBrief()
                val result = response.body()

                if (response.isSuccessful && result != null) {
                    routesEvents.value = Resource.Updated()
                    routeDao.insertAll(result)
                } else {
                    Log.e(TAG, "refreshTourRoutes: the response was not successful" +
                            " ${response.message()}")
                    routesEvents.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Log.e(TAG, "refreshTourRoutes: failed to fetch", e)
                routesEvents.value = Resource.Error(e.message ?: "Ошибка загрузки данных!")
            }
        }
    }

    override suspend fun refreshFullRouteData(id: Long) {
        genericApiOperation(routesEvents, { api.fetchRoute(id) }, { result ->
            routeDao.insert(result)
        })
    }

    private suspend fun <T> genericApiOperation(events: MutableStateFlow<Resource<List<T>>>,
                                                apiCall: suspend () -> Response<T>,
                                                onSuccess: suspend (result: T) -> Unit,
                                                onError: ((e: Exception) -> Unit)? = null
    ) {
        events.value = Resource.Loading()
        withContext(dispatchers.io) {
            try {
                val response = apiCall()
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    events.value = Resource.Updated()
                    onSuccess(body)
                } else {
                    Log.e(TAG, "refreshTourRoutes: the response was not successful" +
                            " ${response.message()}")
                    events.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Log.e(TAG, "refreshTourRoutes: failed to fetch", e)
                events.value = Resource.Error(e.message ?: "Ошибка загрузки данных!")
                onError?.let { it(e) }
            }
        }
    }

    interface ApiOperationCallbacks<T> {
        suspend fun apiCall(): Response<T>
        fun onSuccess(result: T)
        fun onError(e: Exception)
    }

    suspend fun <T> genericApiOperation2(events: MutableStateFlow<Resource<T>>,
                                         callbacks: ApiOperationCallbacks<T>
    ) {
        events.value = Resource.Loading()
        withContext(dispatchers.io) {
            try {
                val response = callbacks.apiCall()
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    events.value = Resource.Updated()
                    callbacks.onSuccess(body)
                } else {
                    Log.e(TAG, "refreshTourRoutes: the response was not successful" +
                            " ${response.message()}")
                    events.value = Resource.Error(response.message())
                }
            } catch (e: Exception) {
                Log.e(TAG, "refreshTourRoutes: failed to fetch", e)
                events.value = Resource.Error(e.message ?: "Ошибка загрузки данных!")
                callbacks.onError(e)
            }
        }
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