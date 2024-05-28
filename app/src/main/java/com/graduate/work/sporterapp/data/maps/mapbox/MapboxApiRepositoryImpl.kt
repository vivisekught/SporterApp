package com.graduate.work.sporterapp.data.maps.mapbox

import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.Response.Failure
import com.graduate.work.sporterapp.core.Response.Success
import com.graduate.work.sporterapp.core.ext.getCoordinates
import com.graduate.work.sporterapp.core.ext.toDirectionsString
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.di.maps.annotations.MapboxPublicToken
import com.graduate.work.sporterapp.domain.maps.mapbox.MapboxApiRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.StaticMapCriteria
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.api.staticmap.v1.models.StaticPolylineAnnotation
import com.mapbox.geojson.Point
import retrofit2.Call
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapboxApiRepositoryImpl @Inject constructor(
    @MapboxPublicToken private val publicToken: String,
) : MapboxApiRepository {

    override suspend fun getRoute(coordinates: List<Point>): Response<Route?> {
        return suspendCoroutine { continuation ->
            val client = MapboxDirections.builder()
                .accessToken(publicToken)
                .routeOptions(
                    RouteOptions.builder()
                        .geometries(DirectionsCriteria.GEOMETRY_POLYLINE)
                        .coordinates(coordinates.toDirectionsString())
                        .profile(DirectionsCriteria.PROFILE_CYCLING)
                        .overview(DirectionsCriteria.OVERVIEW_FULL)
                        .build()
                ).build()
            client?.enqueueCall(object : retrofit2.Callback<DirectionsResponse> {
                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: retrofit2.Response<DirectionsResponse>,
                ) {
                    if (response.body() == null || ((response.body()?.routes()?.size ?: 0) < 1)) {
                        continuation.resume(Failure("Response is empty"))
                        return
                    }
                    if (response.isSuccessful) {
                        val route = response.body()?.routes()?.get(0)
                        val distance = route?.distance() ?: 0.0
                        val duration = route?.duration() ?: 0.0
                        val polylineWithoutElevation =
                            route?.geometry()?.getCoordinates() ?: emptyList()
                        val geometry = route?.geometry()
                        val mapRoute = Route(
                            distance = distance,
                            duration = duration,
                            points = polylineWithoutElevation,
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

    override fun getStaticMapUrl(
        startPoint: Point?,
        endPoint: Point?,
        geometry: String?,
        style: MapBoxStyle,
    ): URL {
        val styleId = when (style) {
            MapBoxStyle.STREET -> StaticMapCriteria.STREET_STYLE
            MapBoxStyle.SATELLITE -> StaticMapCriteria.SATELLITE_STYLE
            MapBoxStyle.LIGHT -> StaticMapCriteria.LIGHT_STYLE
            MapBoxStyle.DARK -> StaticMapCriteria.DARK_STYLE
        }
        val staticImage = MapboxStaticMap.builder()
            .accessToken(publicToken)
            .styleId(styleId)
            .staticMarkerAnnotations(
                listOf(
                    StaticMarkerAnnotation
                        .builder()
                        .label(FIRST_ROUTE_POINT_MARKER_LABEL)
                        .color(ROUTE_LINE_COLOR)
                        .lnglat(startPoint)
                        .build(),
                    StaticMarkerAnnotation
                        .builder()
                        .label(LAST_ROUTE_POINT_MARKER_LABEL)
                        .color(ROUTE_LINE_COLOR)
                        .lnglat(endPoint)
                        .build(),
                )
            )
            .staticPolylineAnnotations(
                listOf(
                    geometry?.let {
                        StaticPolylineAnnotation.builder().fillColor("00246b").strokeWidth(5.0)
                            .polyline(geometry)
                            .build()
                    }
                )
            )
            .cameraAuto(true)
            .width(STATIC_MAP_SIZE)
            .height(STATIC_MAP_SIZE)
            .retina(true)
            .build()
        return staticImage.url().toUrl()
    }

    companion object {
        const val FIRST_ROUTE_POINT_MARKER_LABEL = "a"
        const val LAST_ROUTE_POINT_MARKER_LABEL = "b"
        const val ROUTE_LINE_COLOR = "cadcfc"
        const val STATIC_MAP_SIZE = 420
    }
}