package com.graduate.work.sporterapp.domain.maps.gpx

import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route

interface GpxFileRepository {

    suspend fun importGpx(gpxPath: String): String

    suspend fun saveGpxFileInInternalStorage(route: Route): Boolean
}