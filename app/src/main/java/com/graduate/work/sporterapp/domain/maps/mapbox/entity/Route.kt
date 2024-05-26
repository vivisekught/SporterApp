package com.graduate.work.sporterapp.domain.maps.mapbox.entity

import android.os.Parcelable
import com.mapbox.geojson.Point
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(
    val routeId: String = "",
    val name: String = "",
    val description: String = "",
    val userId: String? = null,
    val points: List<Point>? = null,
    val distance: Double? = null,
    val duration: Double? = null,
    val climb: Double? = null,
    val descent: Double? = null,
    val geometry: String? = null,
    val routeImgUrl: String? = null,
    val timeStamp: Long? = null,
) : Parcelable
