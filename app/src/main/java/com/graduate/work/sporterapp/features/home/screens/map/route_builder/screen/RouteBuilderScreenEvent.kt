package com.graduate.work.sporterapp.features.home.screens.map.route_builder.screen

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.maps.mapbox.domain.MapPoint
import com.mapbox.geojson.Point

sealed class RouteBuilderScreenEvent {
    data object SetLastSelectedPointAsStart : RouteBuilderScreenEvent()
    data object SetUserLocationAsStart : RouteBuilderScreenEvent()
    data object SetLastPointAsDestination : RouteBuilderScreenEvent()
    data class ChangeLastSelectedPoint(val point: Point) : RouteBuilderScreenEvent()
    data class OnPointClicked(val mapPoint: MapPoint) : RouteBuilderScreenEvent()
    data object DeleteLastSelectedPoint : RouteBuilderScreenEvent()
    data class ChangeRouteBuilderStyle(val style: MapBoxStyle) : RouteBuilderScreenEvent()
    data object ResetRouteError : RouteBuilderScreenEvent()
    data class OnPointIndexChanged(val from: Int, val to: Int) : RouteBuilderScreenEvent()
    data class OnPointDeleteClick(val index: Int) : RouteBuilderScreenEvent()
}