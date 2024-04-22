package com.graduate.work.sporterapp.utils.core.auth

sealed class Response<out T> {
    data object Loading: Response<Nothing>()
    data class Success<out T>(val data: T?): Response<T>()
    data class Failure(val message: String) : Response<Nothing>()
}