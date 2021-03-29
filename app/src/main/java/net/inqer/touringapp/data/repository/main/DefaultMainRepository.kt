package net.inqer.touringapp.data.repository.main

import android.util.Log
import kotlinx.coroutines.flow.*
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
        val api: RoutesApi,
        val database: AppDatabase,
        val dispatchers: DispatcherProvider
) : Repository(), MainRepository {

    override suspend fun getRoutesBrief(): Resource<List<TourRouteBrief>> {
        return try {
            processResponse(api.fetchRoutesBrief())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "")
        }
    }

    override fun getRoutesBriefFlow(): Flow<List<TourRouteBrief>> =
            database.tourRouteBriefDao().getRoutesFlow()
                    .flowOn(dispatchers.io)
                    .onStart {
                        refreshTourRoutes()
                    }
                    .onCompletion {
                        Log.d(TAG, "getRoutesBriefFlow: onCompletion")
                    }
                    .onEach {
                        Log.d(TAG, "getRoutesBriefFlow: onEach $it")
                    }

    private suspend fun refreshTourRoutes() {
        withContext(dispatchers.io) {
            val response = api.fetchRoutesBrief()
            val result = response.body()

            if (response.isSuccessful && result != null) {
//                Resource.Success(result)
                database.tourRouteBriefDao().insertAll(result)
            } else {
                Log.e(TAG, "refreshTourRoutes: failed to fetch ${response.message()}")
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