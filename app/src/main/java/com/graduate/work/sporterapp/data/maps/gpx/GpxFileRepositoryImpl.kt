package com.graduate.work.sporterapp.data.maps.gpx

import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route
import com.graduate.work.sporterapp.domain.maps.gpx.GpxFileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject


class GpxFileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
) : GpxFileRepository {
    override suspend fun importGpx(gpxPath: String): String {
        return ""
    }

    override suspend fun saveGpxFileInInternalStorage(route: Route): Boolean {
        val gpxFile = File(context.filesDir, "${route.name}.gpx")
        val header =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
        val name = "<name>${route.name}</name><trkseg>\n"
        val segments = StringBuilder()
        route.points?.forEach { point ->
            segments.append("<trkpt lat=\"" + point.latitude() + "\" lon=\"" + point.longitude() + "\">" + "\n<ele>" + point.altitude() + "</ele>\n" + "</trkpt>\n")
        }
        val footer = "</trkseg></trk></gpx>"
        return try {
            withContext(Dispatchers.IO) {
                val writer = FileWriter(gpxFile, false)
                writer.append(header)
                writer.append(name)
                writer.append(segments)
                writer.append(footer)
                writer.flush()
                writer.close()
                true
            }
        } catch (e: IOException) {
            false
        }
    }
}