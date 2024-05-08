package com.graduate.work.sporterapp.features.home.screens.map.utils

import android.Manifest
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions

object MapUtils {

    const val LAYER_ID = "line_layer"
    const val SOURCE_ID = "line_source"
    const val PITCH_OUTLINE = "top"


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