package com.graduate.work.sporterapp.domain.maps.mapbox.domain

import com.mapbox.geojson.Point

data class MapPoint(
    val name: String,
    val point: Point,
)
