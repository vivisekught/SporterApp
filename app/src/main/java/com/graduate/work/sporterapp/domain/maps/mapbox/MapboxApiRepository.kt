package com.graduate.work.sporterapp.domain.maps.mapbox

import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.mapbox.geojson.Point
import java.net.URL

interface MapboxApiRepository {

    suspend fun getRoute(coordinates: List<Point>): Response<Route?>

    fun getStaticMapUrl(
        startPoint: Point?,
        endPoint: Point?,
        geometry: String?,
        style: MapBoxStyle,
    ): URL

}