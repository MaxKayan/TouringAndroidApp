package net.inqer.touringapp.data.repository

import net.inqer.touringapp.util.Resource
import retrofit2.Response

abstract class Repository {
    protected fun <T> processResponse(response: Response<T>): Resource<T> {
        val result = response.body()

        return if (response.isSuccessful && result != null) {
            Resource.Success(result)
        } else {
            Resource.Error(response.message())
        }
    }
}
