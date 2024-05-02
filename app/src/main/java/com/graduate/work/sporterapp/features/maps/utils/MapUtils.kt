package com.graduate.work.sporterapp.features.maps.utils

import android.Manifest

object MapUtils {

    const val LAYER_ID = "line_layer"
    const val SOURCE_ID = "line_source"
    const val PITCH_OUTLINE = "top"

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}