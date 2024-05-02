package com.graduate.work.sporterapp.core.map

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.graduate.work.sporterapp.R
import com.mapbox.maps.Style

enum class MapBoxStyle(val style: String, @DrawableRes val resId: Int, @StringRes val textId: Int) {
    STREET(Style.MAPBOX_STREETS, R.drawable.map_preview_street, R.string.street),
    SATELLITE(Style.SATELLITE_STREETS, R.drawable.map_preview_satellite, R.string.satellite),
    LIGHT(Style.LIGHT, R.drawable.map_preview_light, R.string.light),
    DARK(Style.DARK, R.drawable.map_preview_dark, R.string.dark),
}