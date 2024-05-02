package com.graduate.work.sporterapp.core.ext

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point

fun Context.isLocationPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

}

fun Location.toPoint(): Point = Point.fromLngLat(longitude, latitude, altitude)

fun List<Point>.toDirectionsString(): String = joinToString(separator = ";") { pair ->
    "${pair.longitude()},${pair.latitude()}"
}

fun String.getCoordinates(): List<Point> {
    val lineString = LineString.fromPolyline(this, Constants.PRECISION_6)
    return lineString.coordinates()
}
