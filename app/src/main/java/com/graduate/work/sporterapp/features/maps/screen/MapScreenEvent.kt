package com.graduate.work.sporterapp.features.maps.screen

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.maps.mapbox.domain.MapPoint
import com.mapbox.geojson.Point

sealed class MapScreenEvent {
    data object SetLastSelectedPointAsStart : MapScreenEvent()
    data object SetUserLocationAsStart : MapScreenEvent()
    data object SetLastPointAsDestination : MapScreenEvent()
    data class ChangeLastSelectedPoint(val point: Point) : MapScreenEvent()
    data class OnPointClicked(val mapPoint: MapPoint) : MapScreenEvent()
    data object DeleteLastSelectedPoint : MapScreenEvent()
    data class ChangeMapStyle(val style: MapBoxStyle) : MapScreenEvent()
    data object ResetRouteError : MapScreenEvent()
    data class OnPointIndexChanged(val from: Int, val to: Int) : MapScreenEvent()
    data class OnPointDeleteClick(val index: Int) : MapScreenEvent()
}