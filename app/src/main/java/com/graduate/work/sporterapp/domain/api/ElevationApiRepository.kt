package com.graduate.work.sporterapp.domain.api

import com.mapbox.geojson.Point

interface ElevationApiRepository {
    suspend fun getPointsWithElevationFromCoordinates(
        points: List<Point>?,
    ): List<Point>?
}