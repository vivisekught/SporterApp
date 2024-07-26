package com.graduate.work.sporterapp.data.api.strava.service

import com.graduate.work.sporterapp.core.annotation.ApiUrl
import com.graduate.work.sporterapp.data.api.strava.responce.StravaTokenResponse
import com.graduate.work.sporterapp.data.api.strava.responce.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

@ApiUrl(url = "https://www.strava.com/")
interface StravaService {
    @FormUrlEncoded
    @POST("oauth/token")
    fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): Call<StravaTokenResponse>

    @Multipart
    @POST("api/v3/uploads")
    fun uploadActivity(
        @Part file: MultipartBody.Part,
        @Part("name") name: String,
        @Part("description") description: String,
        @Query("data_type") data_type: String,
        @Header("Authorization") authorization: String
    ): Call<UploadResponse>
}