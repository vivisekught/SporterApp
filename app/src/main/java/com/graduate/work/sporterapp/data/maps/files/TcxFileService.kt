package com.graduate.work.sporterapp.data.maps.files

import android.content.Context
import com.graduate.work.sporterapp.core.ext.convertTimestampToTime
import com.graduate.work.sporterapp.core.ext.createFile
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.graduate.work.sporterapp.domain.maps.files.ExportFileService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class TcxFileService @Inject constructor(
    @ApplicationContext private val context: Context,
) : ExportFileService() {
    val header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<TrainingCenterDatabase xsi:schemaLocation=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2 http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd\" xmlns:ns5=\"http://www.garmin.com/xmlschemas/ActivityGoals/v1\" xmlns:ns4=\"http://www.garmin.com/xmlschemas/ProfileExtension/v1\" xmlns:ns3=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\" xmlns:ns2=\"http://www.garmin.com/xmlschemas/UserProfile/v2\" xmlns=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"

    override suspend fun importRoutes(routesPath: String): Route {
        TODO("Not yet implemented")
    }

    override suspend fun saveRouteInInternalStorage(route: Route): Boolean {
        val tcxFile = File(context.filesDir, "${route.name}.tcx")
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
    override suspend fun saveWorkoutInInternalStorage(workout: Workout): Boolean {
        // create tcx file in external storage
        val tcxFile = File(context.filesDir, "${workout.name}.tcx")
        // create header
        val name = "<Activities>\n" +
                "<Activity Sport=\"Biking\">\n" +
                "<Id>${workout.timeStamp.convertTimestampToTime()}</Id>\n" +
                "<Lap StartTime=\"${workout.timeStamp.convertTimestampToTime()}\">\n" +
                "<TotalTimeSeconds>${workout.durationInSeconds}</TotalTimeSeconds>\n" +
                "<DistanceMeters>${workout.distance * 1000}</DistanceMeters>\n" +
                "<MaximumSpeed><Speed>${workout.maxSpeed}</Speed></MaximumSpeed>\n" +
                "<Calories>${workout.calories}</Calories>" +
                "<Track>\n"
        val segments = StringBuilder()
        // create segments
        workout.points?.forEach { point ->
            segments.append(
                "<Trackpoint>\n",
                "<Time>${point.timeStamp.convertTimestampToTime()}</Time>\n",
                "<Position>\n",
                "<LatitudeDegrees>${point.point.latitude()}</LatitudeDegrees>\n",
                "<LongitudeDegrees>${point.point.longitude()}</LongitudeDegrees>\n",
                "</Position>\n",
                "<AltitudeMeters>${point.point.altitude()}</AltitudeMeters>\n",
                "<DistanceMeters>${point.distanceFromStart}</DistanceMeters>\n",
                "<Extensions>\n",
                "<TPX xmlns=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\">\n",
                "<Speed>${point.speed}</Speed></TPX>\n",
                "</Extensions>\n",
                "</Trackpoint>"
            )
        }
        // create footer
        val footer = "</Track>\n" +
                "</Lap>\n" +
                "</Activity>\n" +
                "</Activities>\n" +
                "</TrainingCenterDatabase>"
        // write to file
        return tcxFile.createFile(header, name, segments.toString(), footer)
    }
}