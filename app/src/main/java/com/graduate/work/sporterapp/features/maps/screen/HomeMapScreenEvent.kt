package com.graduate.work.sporterapp.features.maps.screen

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.mapbox.geojson.Point

sealed class HomeMapScreenEvent {
    data object SetLastSelectedPointAsStart : HomeMapScreenEvent()
    data object SetUserLocationAsStart : HomeMapScreenEvent()
    data object SetLastPointAsDestination : HomeMapScreenEvent()
    data class ChangeLastSelectedPoint(val point: Point) : HomeMapScreenEvent()
    data class OnPointClicked(val point: Point) : HomeMapScreenEvent()
    data object DeleteLastSelectedPoint : HomeMapScreenEvent()
    data class ChangeMapStyle(val style: MapBoxStyle) : HomeMapScreenEvent()
    data object ResetRouteError : HomeMapScreenEvent()
}