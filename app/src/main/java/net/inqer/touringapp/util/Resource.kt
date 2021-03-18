package net.inqer.touringapp.util

sealed class Resource<T>(val data: T?, val message: String?) {
    class Success<T>(data: T) : Resource<T>(data, null)
    class Error<T>(message: String) : Resource<T>(null, message)
    class Loading<T>(message: String? = null) : Resource<T>(null, message)
    class Empty<T> : Resource<T>(null, null)
}