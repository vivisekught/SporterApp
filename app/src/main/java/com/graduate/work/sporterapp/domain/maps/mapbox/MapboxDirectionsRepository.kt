package com.graduate.work.sporterapp.domain.maps.mapbox

import com.graduate.work.sporterapp.core.Response
import com.mapbox.geojson.Point

interface MapboxDirectionsRepository {

    suspend fun getRoute(coordinates: List<Point>): Response<List<Point>?>
}