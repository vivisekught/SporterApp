package com.graduate.work.sporterapp.domain.maps.gpx.usecases

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route
import com.graduate.work.sporterapp.domain.maps.gpx.GpxFileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class GetGpxFileIntentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gpxFileRepository: GpxFileRepository,
) {

    suspend operator fun invoke(route: Route): Intent? {
        val isSaved = gpxFileRepository.saveGpxFileInInternalStorage(route)
        if (!isSaved) return null
        return try {
            val file = File(context.filesDir, "${route.name}.gpx")
            if (file.exists()) {
                val uri =
                    FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                Intent(Intent.ACTION_SEND).run {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "application/gpx+xml"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            null
        }
    }
}