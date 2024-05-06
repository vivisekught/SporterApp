package com.graduate.work.sporterapp.data.maps.mapbox.directions

import com.graduate.work.sporterapp.core.Response.Failure
import com.graduate.work.sporterapp.core.Response.Success
import com.graduate.work.sporterapp.core.ext.getCoordinates
import com.graduate.work.sporterapp.core.ext.toDirectionsString
import com.graduate.work.sporterapp.di.maps.annotations.MapboxPublicToken
import com.graduate.work.sporterapp.domain.maps.mapbox.MapboxDirectionsRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.domain.MapRoute
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapboxDirectionsRepositoryImpl @Inject constructor(
    @MapboxPublicToken private val publicToken: String,
) : MapboxDirectionsRepository {

    override suspend fun getRoute(coordinates: List<Point>): com.graduate.work.sporterapp.core.Response<MapRoute?> =
        suspendCoroutine { continuation ->
            val client = MapboxDirections.builder()
                .accessToken(publicToken)
                .routeOptions(
                    RouteOptions.builder()
                        .coordinates(coordinates.toDirectionsString())
                        .profile(DirectionsCriteria.PROFILE_CYCLING)
                        .build()
                ).build()
            client?.enqueueCall(object : retrofit2.Callback<DirectionsResponse> {
                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>,
                ) {
                    if (response.body() == null || ((response.body()?.routes()?.size ?: 0) < 1)) {
                        continuation.resume(Failure("Response is empty"))
                        return
                    }
                    if (response.isSuccessful) {
                        val route = response.body()?.routes()?.get(0)
                        val distance = route?.distance() ?: 0.0
                        val duration = route?.duration() ?: 0.0
                        val polyline = route?.geometry()?.getCoordinates()
                        val geometry = route?.geometry()
                        val mapRoute = MapRoute(
                            distance = distance,
                            duration = duration,
                            points = polyline,
                            geometry = geometry
                        )
                        route?.let {
                            continuation.resume(Success(mapRoute))
                        } ?: kotlin.run {
                            continuation.resume(Failure("Response is not successful"))
                        }
                    }

                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    continuation.resume(Failure(t.localizedMessage ?: "Unknown error"))
                }
            })
        }
}