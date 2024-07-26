package com.graduate.work.sporterapp.data.api.strava.responce

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("id_str") val idStr: String,
    @SerializedName("external_id") val externalId: String,
    @SerializedName("error") val error: String?,
    @SerializedName("status") val status: String,
    @SerializedName("activity_id") val activityId: Long?
)