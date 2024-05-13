package com.graduate.work.sporterapp.data.firebase.storage.pojo

import com.google.firebase.firestore.DocumentId

data class RoutePoint(
    val lng: Double = 0.0,
    val lat: Double = 0.0,
    val altitude: Double = 0.0,
)

data class FirestoreRoutePojo(
    @DocumentId val routeId: String = "",
    val userId: String? = null,
    val name: String = "",
    val description: String = "",
    val points: List<RoutePoint>? = null,
    val distance: Double? = null,
    val duration: Double? = null,
    val climb: Double? = null,
    val descent: Double? = null,
    val geometry: String? = null,
    val routeImgUrl: String? = null,
    val timeStamp: Long? = null,
)
