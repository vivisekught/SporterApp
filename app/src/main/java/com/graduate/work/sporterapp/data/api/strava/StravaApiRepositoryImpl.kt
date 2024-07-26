package com.graduate.work.sporterapp.data.api.strava

import com.graduate.work.sporterapp.core.Response.Success
import com.graduate.work.sporterapp.data.api.strava.responce.StravaTokenResponse
import com.graduate.work.sporterapp.data.api.strava.responce.UploadResponse
import com.graduate.work.sporterapp.data.api.strava.service.StravaService
import com.graduate.work.sporterapp.domain.api.StravaApiRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StravaApiRepositoryImpl @Inject constructor(
    private val stravaService: StravaService,
    @ApplicationContext private val context: android.content.Context,
) : StravaApiRepository {
    override suspend fun getToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ): String? {
        return suspendCoroutine { continuation ->
            val call = stravaService.getToken(clientId, clientSecret, code)
            call.enqueue(object : retrofit2.Callback<StravaTokenResponse> {
                override fun onResponse(
                    call: Call<StravaTokenResponse>,
                    response: retrofit2.Response<StravaTokenResponse>,
                ) {
                    if (response.isSuccessful) {
                        val tokenResponse = response.body()
                        val accessToken = tokenResponse?.accessToken
                        continuation.resume(accessToken)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<StravaTokenResponse>, t: Throwable) {
                    continuation.resume(null)
                }
            })
        }
    }

    override suspend fun uploadGpxFile(
        name: String,
        token: String,
    ): com.graduate.work.sporterapp.core.Response<Unit> {
        val filePart = prepareFilePart(filePath = name)
        val call = stravaService.uploadActivity(
            file = filePart,
            data_type = "gpx",
            name = name,
            description = "Uploaded via SporterApp",
            authorization = "Bearer $token"
        )
        return suspendCoroutine { continuation ->
            call.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: retrofit2.Response<UploadResponse>,
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(Success(Unit))
                    } else {
                        continuation.resume(
                            com.graduate.work.sporterapp.core.Response.Failure(
                                response.errorBody()?.string() ?: "Unknown error"
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    continuation.resume(
                        com.graduate.work.sporterapp.core.Response.Failure(
                            t.localizedMessage ?: "Unknown error"
                        )
                    )
                }
            })
        }
    }

    fun prepareFilePart(filePath: String): MultipartBody.Part {
        val file = File(context.filesDir, filePath)
        val requestFile = file.asRequestBody("application/gpx+xml".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }
}