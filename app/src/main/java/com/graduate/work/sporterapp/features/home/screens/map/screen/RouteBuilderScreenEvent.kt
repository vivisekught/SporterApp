package com.graduate.work.sporterapp.features.home.screens.route_builder.screen

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.mapbox.geojson.Point

sealed class RouteBuilderScreenEvent {
    data object SetLastSelectedPointAsStart : RouteBuilderScreenEvent()
    data object SetUserLocationAsStart : RouteBuilderScreenEvent()
    data object SetLastPointAsDestination : RouteBuilderScreenEvent()
    data object SetFirstPointAsDestination : RouteBuilderScreenEvent()
    data class ChangeLastSelectedPoint(val point: Point) : RouteBuilderScreenEvent()
    data object DeleteLastSelectedPoint : RouteBuilderScreenEvent()
    data class ChangeRouteBuilderStyle(val style: MapBoxStyle) : RouteBuilderScreenEvent()
    data class OnPointIndexChanged(val from: Int, val to: Int) : RouteBuilderScreenEvent()
    data class OnPointDeleteClick(val index: Int) : RouteBuilderScreenEvent()
    data class SaveRoute(val routeName: String, val routeDescription: String) :
        RouteBuilderScreenEvent()

    data object DismissSnackbar : RouteBuilderScreenEvent()
    data class ShowSnackbar(val message: String) : RouteBuilderScreenEvent()
}