package com.graduate.work.sporterapp.domain.local_db

interface LocalDbRepository {

    fun getUserToken(): String?

    fun saveUserToken(token: String)
}