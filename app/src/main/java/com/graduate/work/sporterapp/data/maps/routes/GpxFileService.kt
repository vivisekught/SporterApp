package com.graduate.work.sporterapp.data.maps.routes

import com.graduate.work.sporterapp.core.ext.createFile
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.graduate.work.sporterapp.domain.maps.routes.RoutesFileService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class GpxFileService @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
) : RoutesFileService() {

    override suspend fun importRoutes(routesPath: String): Route {
        TODO("Not yet implemented")
    }

    override suspend fun saveRouteInInternalStorage(route: Route): Boolean {
        val gpxFile = File(context.filesDir, "${route.name}.gpx")
        val header =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
        val name = "<name>${route.name}</name><trkseg>\n"
        val segments = StringBuilder()
        route.points?.forEach { point ->
            segments.append("<trkpt lat=\"" + point.latitude() + "\" lon=\"" + point.longitude() + "\">" + "\n<ele>" + point.altitude() + "</ele>\n" + "</trkpt>\n")
        }
        val footer = "</trkseg></trk></gpx>"
        return gpxFile.createFile(header, name, segments.toString(), footer)
    }
}