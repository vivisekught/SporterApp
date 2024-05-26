package com.graduate.work.sporterapp.core.ext

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
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
    val lineString = LineString.fromPolyline(this, Constants.PRECISION_5)
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

@SuppressLint("DefaultLocale")
fun Double?.parseSeconds(): String {
    if (this == null) return "0:00"
    val hours = this / 3600
    val minutes = ceil((this % 3600) / 60).toInt()
    return String.format("%02d:%02d", hours.toInt(), minutes)
}

fun Double?.roundTo2(): Double {
    if (this == null) return 0.0
    return Math.round(this * 100.0) / 100.0
}

fun Double?.roundTo6(): Double {
    if (this == null) return 0.0
    return Math.round(this * 1000000.0) / 1000000.0
}

fun Bitmap?.convertToByteArray(): ByteArray? {
    if (this == null) return null
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    return baos.toByteArray()
}

fun Long?.getDateTime(): String {
    if (this == null) return ""
    return try {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val netDate = Date(this)
        sdf.format(netDate)
    } catch (e: Exception) {
        ""
    }
}

fun List<Double>.closestValue(value: Double) = minBy { abs(value - it) }

suspend fun File.createFile(header: String, name: String, segments: String, footer: String) =
    try {
        withContext(Dispatchers.IO) {
            FileWriter(this@createFile, false).apply {
                append(header)
                append(name)
                append(segments)
                append(footer)
                flush()
                close()
            }
            true
        }
    } catch (e: IOException) {
        false
    }

fun createFileIntent(context: Context, fileName: String, fileExportType: String): Intent? {
    return try {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            val uri =
                FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            Intent(Intent.ACTION_SEND).run {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = fileExportType
                putExtra(Intent.EXTRA_STREAM, uri)
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            null
        }
    } catch (e: java.lang.Exception) {
        null
    }
}

fun Int.padTimerValue() = this.toString().padStart(2, '0')
fun Long.padTimerValue() = this.toString().padStart(2, '0')

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

fun Float.convertMetersPerSecondToKilometersPerHour(): Float = this * 3.6f