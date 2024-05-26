package com.graduate.work.sporterapp.features.home.screens.route_builder.vm

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessage
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.MapPoint
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.mapbox.geojson.Point

data class RouteBuilderState(
    val currentMapStyle: MapBoxStyle = MapBoxStyle.STREET,
    val userLocationPoint: Point? = null,
    val lastSelectedPoint: Point? = null,
    val pointAlphabetIndex: Int = 0,
    val userPoints: List<MapPoint> = emptyList(),
    val route: Route? = null,
    val isRouteLoading: Boolean = false,
    val isNewPointDialogOpened: Boolean = false,
    val snackbarMessage: SnackbarMessage? = null,
)