package com.graduate.work.sporterapp.data.maps.files

import android.content.Context
import com.graduate.work.sporterapp.core.ext.createFile
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.graduate.work.sporterapp.domain.maps.routes.ExportFileService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class TcxFileService @Inject constructor(
    @ApplicationContext private val context: Context,
) : ExportFileService() {

    override suspend fun importRoutes(routesPath: String): Route {
        TODO("Not yet implemented")
    }

    override suspend fun saveRouteInInternalStorage(route: Route): Boolean {
        val tcxFile = File(context.filesDir, "${route.name}.tcx")
        val header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" +
                "xsi:schemaLocation=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2 http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd\" xmlns:ns5=\"http://www.garmin.com/xmlschemas/ActivityGoals/v1\" xmlns:ns4=\"http://www.garmin.com/xmlschemas/ProfileExtension/v1\" xmlns:ns3=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\" xmlns:ns2=\"http://www.garmin.com/xmlschemas/UserProfile/v2\" xmlns=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
        val name = "<Course>\n<Name>${route.name}</Name>\n<Track>\n"
        val segments = StringBuilder()
        route.points.forEach { point ->
            segments.append(
                "<Trackpoint>\n<Position>\n<LatitudeDegrees>${point.latitude()}</LatitudeDegrees>\n" +
                        "<LongitudeDegrees>${point.longitude()}</LongitudeDegrees>\n" +
                        "</Position>\n" +
                        "<AltitudeMeters>${point.altitude()}</AltitudeMeters>\n" +
                        "</Trackpoint>"
            )
        }
        val footer = "</Track>\n</Course></TrainingCenterDatabase>"
        return tcxFile.createFile(header, name, segments.toString(), footer)
    }
}