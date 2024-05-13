package com.graduate.work.sporterapp.data.api.services

import com.graduate.work.sporterapp.core.annotation.ApiUrl
import com.graduate.work.sporterapp.data.api.pojo.ElevationResponsePojo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@ApiUrl(url = "https://api.open-meteo.com/v1/")
interface ElevationService {
    @GET("elevation")
    fun getElevation(
        @Query("latitude") latitudes: List<Double>,
        @Query("longitude") longitudes: List<Double>,
    ): Call<ElevationResponsePojo>
}