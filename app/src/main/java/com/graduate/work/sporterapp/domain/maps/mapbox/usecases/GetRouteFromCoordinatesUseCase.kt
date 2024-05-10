package com.graduate.work.sporterapp.domain.maps.mapbox.usecases

import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route
import com.graduate.work.sporterapp.domain.maps.mapbox.MapboxApiRepository
import com.mapbox.geojson.Point
import javax.inject.Inject

class GetRouteFromCoordinatesUseCase @Inject constructor(
    private val mapboxApiRepository: MapboxApiRepository,
) {

    suspend operator fun invoke(coordinates: List<Point>): Response<Route?> =
        mapboxApiRepository.getRoute(coordinates)
}