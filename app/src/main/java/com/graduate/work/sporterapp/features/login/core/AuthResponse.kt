package com.graduate.work.sporterapp.features.login.core

sealed class AuthResponse<out T> {
    data class Success<out T>(val data: T?) : AuthResponse<T>()
    data class Failure(val message: String) : AuthResponse<Nothing>()
}