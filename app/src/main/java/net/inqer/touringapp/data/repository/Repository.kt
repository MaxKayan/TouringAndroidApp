package net.inqer.touringapp.data.repository

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import net.inqer.touringapp.util.Resource
import retrofit2.Response

abstract class Repository {
    protected suspend fun <T> processResponse(
            apiCall: suspend () -> Response<T>,
            events: MutableStateFlow<Resource<List<T>>>? = null,
            onSuccess: (suspend (result: T) -> Unit)? = null,
            onError: ((e: Exception) -> Unit)? = null
    ): Resource<T> {
        try {
            val response = apiCall()
            val body = response.body()

            return if (response.isSuccessful && body != null) {
                events?.value = Resource.Updated()
                onSuccess?.invoke(body)
                Resource.Success(body)
            } else {
                Log.e(TAG, "refreshTourRoutes: the response was not successful" +
                        " ${response.message()}")
                events?.value = Resource.Error(response.message())
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "refreshTourRoutes: failed to fetch", e)
            events?.value = Resource.Error(e.message ?: "Ошибка загрузки данных!")
            onError?.invoke(e)
            return Resource.Error(e.message ?: "Ошибка загрузки данных!")
        }
    }

    companion object {
        private const val TAG = "Repository"
    }
}
