package com.graduate.work.sporterapp.domain.api.usecases

import android.util.Log
import com.graduate.work.sporterapp.BuildConfig
import com.graduate.work.sporterapp.domain.api.StravaApiRepository
import com.graduate.work.sporterapp.domain.local_db.LocalDbRepository
import javax.inject.Inject

class GetAndSaveStravaTokenUseCase @Inject constructor(
    private val stravaApiRepository: StravaApiRepository,
    private val localDbRepository: LocalDbRepository
) {

    private val clientId = "126680"
    suspend operator fun invoke(
        code: String
    ): String? {
        val token = stravaApiRepository.getToken(clientId, BuildConfig.STRAVA_SECRET_CLIENT, code)
        if (token != null) {
            localDbRepository.saveUserToken(token)
        }
        return token
    }
}