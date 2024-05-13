package com.graduate.work.sporterapp.data.api.pojo

import com.google.gson.annotations.SerializedName


data class ElevationResponsePojo(
    @SerializedName("elevation") val elevation: List<Double>,
)