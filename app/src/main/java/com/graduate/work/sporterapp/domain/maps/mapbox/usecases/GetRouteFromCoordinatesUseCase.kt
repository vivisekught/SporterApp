package com.graduate.work.sporterapp.domain.maps.mapbox.usecases

import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.domain.maps.mapbox.MapboxDirectionsRepository
import com.mapbox.geojson.Point
import javax.inject.Inject

class GetRouteFromCoordinatesUseCase @Inject constructor(
    private val mapboxDirectionsRepository: MapboxDirectionsRepository,
) {

    suspend operator fun invoke(coordinates: List<Point>): Response<List<Point>?> =
        mapboxDirectionsRepository.getRoute(coordinates)
}