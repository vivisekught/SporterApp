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

fun <T> MutableList<T>.move(from: Int, to: Int): MutableList<T> {
    if (from == to)
        return this

    val element = this.removeAt(from) ?: return this
    this.add(to, element)
    return this
}

fun Int.getAlphabetLetterByIndex(): String {
    return if (this in 0..25) {
        ('A' + this).toString()
    } else {
        val letterIndex = this % 26
        val numberIndex = this / 26
        ('A' + letterIndex).toString() + (numberIndex + 1)
    }
}

fun Double.roundTo6(): Double = Math.round(this * 1000000.0) / 1000000.0