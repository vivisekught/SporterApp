package com.graduate.work.sporterapp.domain.maps.mapbox.domain

import com.mapbox.geojson.Point

data class MapRoute(
    val points: List<Point>? = null,
    val distance: Double? = null,
    val duration: Double? = null,
    val geometry: String? = null,
)
