package com.graduate.work.sporterapp.features.home.screens.route_builder.utils

import android.Manifest
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions

object MapUtils {

    @OptIn(MapboxExperimental::class)
    fun MapViewportState.transitionToGeometry(
        points: List<Point>,
        padding: Double = 200.0,
        duration: Long = 1000,
        onAnimationComplete: () -> Unit = {},
    ) {
        val polygon = Polygon.fromLngLats(listOf(points))
        transitionToOverviewState(
            overviewViewportStateOptions = OverviewViewportStateOptions.Builder()
                .geometry(polygon)
                .animationDurationMs(duration)
                .padding(EdgeInsets(padding, padding, padding, padding))
                .build()
        ) {
            onAnimationComplete()
        }
    }
    @OptIn(MapboxExperimental::class)
    fun MapViewportState.flyToUserPosition() {
        transitionToFollowPuckState(
            followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                .pitch(null)
                .build(),
        )
    }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}