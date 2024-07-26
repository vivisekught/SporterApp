package com.graduate.work.sporterapp.domain.api.usecases

import android.content.Intent
import android.util.Log
import com.graduate.work.sporterapp.BuildConfig
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.data.maps.files.GpxFileService
import com.graduate.work.sporterapp.domain.api.StravaApiRepository
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import com.graduate.work.sporterapp.domain.local_db.LocalDbRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import javax.inject.Inject

class UploadGpxWorkoutToStravaUseCase @Inject constructor(
    private val stravaApiRepository: StravaApiRepository,
    private val localDbRepository: LocalDbRepository,
    private val gpxFileService: GpxFileService,
) {

    suspend operator fun invoke(workout: Workout): Response<Unit> {
        val isSaved = gpxFileService.saveWorkoutInInternalStorage(workout)
        if (!isSaved) return Response.Failure("Failed to save workout in internal storage")
        val fileName = workout.name + ".gpx"
        val token = localDbRepository.getUserToken() ?: return Response.Failure("Failed to get user token")
        return stravaApiRepository.uploadGpxFile(fileName, token)
    }
}