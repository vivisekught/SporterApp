package com.graduate.work.sporterapp.domain.maps.gpx

import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route

interface GpxFileRepository {

    suspend fun importGpx(gpxPath: String): String

    suspend fun saveGpxFileInInternalStorage(route: Route): Boolean
}