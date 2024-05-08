package com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.graduate.work.sporterapp.R
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation

@OptIn(MapboxExperimental::class)
@Composable
fun AddPointAnnotation(context: Context, text: String, point: Point, onClick: () -> Unit = { }) {
    val drawable = ResourcesCompat.getDrawable(
        context.resources,
        R.drawable.ic_checkpoint,
        null
    )
    val bitmap = drawable?.toBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    PointAnnotation(
        iconImageBitmap = bitmap,
        iconSize = 0.75,
        point = point,
        textField = text,
        textColorInt = Color.WHITE,
        textSize = 20.0
    ) {
        onClick()
        true
    }
}