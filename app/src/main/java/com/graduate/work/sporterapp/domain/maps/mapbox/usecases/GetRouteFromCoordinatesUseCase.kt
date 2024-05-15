package com.graduate.work.sporterapp.domain.maps.mapbox.usecases

import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.domain.api.ElevationApiRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.MapboxApiRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.mapbox.geojson.Point
import javax.inject.Inject

class GetRouteFromCoordinatesUseCase @Inject constructor(
    private val mapboxApiRepository: MapboxApiRepository,
    private val elevationApiRepository: ElevationApiRepository,
) {

    suspend operator fun invoke(coordinates: List<Point>): Response<Route?> {
        val response = mapboxApiRepository.getRoute(coordinates)
        return if (response is Response.Success) {
            val routeWithoutElevation = response.data
            val pointsWithElevation =
                elevationApiRepository.getPointsWithElevationFromCoordinates(routeWithoutElevation?.points)
            val (climb, descent) = getClimbAndDescent(pointsWithElevation ?: emptyList())
            val routeWithElevation = routeWithoutElevation?.copy(
                points = pointsWithElevation,
                climb = climb,
                descent = descent
            )
            Response.Success(routeWithElevation)
        } else {
            response
        }
    }

    private fun getClimbAndDescent(points: List<Point>): Pair<Double, Double> {
        var climb = 0.0
        var descent = 0.0
        var diff: Double
        points.forEachIndexed { index, point ->
            if (index > 0) {
                diff = point.altitude() - points[index - 1].altitude()
                if (diff < 0) {
                    descent += -diff
                } else {
                    climb += diff
                }
            }
        }
        return Pair(climb, descent)
    }
}