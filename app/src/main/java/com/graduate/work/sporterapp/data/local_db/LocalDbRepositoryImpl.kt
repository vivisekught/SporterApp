package com.graduate.work.sporterapp.data.local_db

import android.content.SharedPreferences
import com.graduate.work.sporterapp.domain.local_db.LocalDbRepository
import javax.inject.Inject

class LocalDbRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): LocalDbRepository {
    override fun getUserToken(): String? {
        return sharedPreferences.getString(USER_TOKEN, null)
    }

    override fun saveUserToken(token: String) {
        sharedPreferences.edit().putString(USER_TOKEN, token).apply()
    }

    companion object {
        private const val USER_TOKEN = "user_token"
    }
}