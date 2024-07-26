package com.graduate.work.sporterapp.domain.api

import com.graduate.work.sporterapp.core.Response
import java.io.File

interface StravaApiRepository {

    suspend fun getToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): String?

    suspend fun uploadGpxFile(name: String, token: String): Response<Unit>
}