package com.graduate.work.sporterapp.domain.maps.routes.usecases

import android.content.Context
import android.content.Intent
import com.graduate.work.sporterapp.core.ext.createFileIntent
import com.graduate.work.sporterapp.data.maps.files.TcxFileService
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetTcxFileIntentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tcxFileService: TcxFileService,
) {

    suspend operator fun invoke(route: Route): Intent? {
        val isSaved = tcxFileService.saveRouteInInternalStorage(route)
        if (!isSaved) return null
        val fileName = route.name + ".tcx"
        return createFileIntent(context, fileName, "application/tcx+xml")
    }
}