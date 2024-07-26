package com.graduate.work.sporterapp.data.maps.files

import com.graduate.work.sporterapp.core.ext.convertTimestampToTime
import com.graduate.work.sporterapp.core.ext.createFile
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.graduate.work.sporterapp.domain.maps.files.ExportFileService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class GpxFileService @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
) : ExportFileService() {

    override suspend fun importRoutes(routesPath: String): Route {
        TODO("Not yet implemented")
    }

    override suspend fun saveRouteInInternalStorage(route: Route): Boolean {
        val gpxFile = File(context.filesDir, "${route.name}.gpx")
        val header =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>" +
                    "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" " +
                    "creator=\"MapSource 6.15.5\" version=\"1.1\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  " +
                    "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 " +
                    "http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
        val name = "<name>${route.name}</name><trkseg>\n"
        val segments = StringBuilder()
        route.points.forEach { point ->
            segments.append("<trkpt lat=\"" + point.latitude() + "\" lon=\"" + point.longitude() + "\">" + "\n<ele>" + point.altitude() + "</ele>\n" + "</trkpt>\n")
        }
        val footer = "</trkseg></trk></gpx>"
        return gpxFile.createFile(header, name, segments.toString(), footer)
    }

    override suspend fun saveWorkoutInInternalStorage(workout: Workout): Boolean {
        // create gpx file in external storage
        val gpxFile = File(context.filesDir, "${workout.name}.gpx")
        // create header
        val header =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>" +
                    "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" " +
                    "creator=\"MapSource 6.15.5\" version=\"1.1\" " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  " +
                    "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 " +
                    "http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n"
        // create name
        val name = "<name>${workout.name}</name><trkseg>\n"
        // create segments
        val segments = StringBuilder()
        // add all points
        workout.points?.forEach { point ->
            segments.append(
                // add point latitude and longitude
                "<trkpt lat=\"" + point.point.latitude() +
                        "\" lon=\"" + point.point.longitude() + "\">" +
                        // add altitude
                        "\n<ele>" + point.point.altitude() + "</ele>\n" +
                        // add time in yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format
                        "<time>" + point.timeStamp.convertTimestampToTime() + "</time>" +
                        "</trkpt>\n"
            )
        }
        // create footer
        val footer = "</trkseg></trk></gpx>"
        // return gpx file
        return gpxFile.createFile(header, name, segments.toString(), footer)
    }
}