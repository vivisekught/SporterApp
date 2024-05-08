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
import kotlin.math.ceil

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

fun Double?.metersToKms(): Double = ((this ?: 0.0) / 1000).roundTo2()

fun Double?.parseSeconds(): String {
    if (this == null) return "0:00"
    val hours = this / 3600
    val minutes = ceil((this % 3600) / 60).toInt()
    return String.format("%02d:%02d", hours.toInt(), minutes)
}

fun Double.roundTo2(): Double = Math.round(this * 100.0) / 100.0

fun Double.roundTo6(): Double = Math.round(this * 1000000.0) / 1000000.0