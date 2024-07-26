package com.graduate.work.sporterapp.data.api.strava.responce

import com.google.gson.annotations.SerializedName

data class StravaTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("athlete") val athlete: Athlete
)

data class Athlete(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String
)