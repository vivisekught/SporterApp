package com.graduate.work.sporterapp.features.track.helper

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.graduate.work.sporterapp.main.MainActivity
import com.graduate.work.sporterapp.navigation.AppNavigation

object TrackHelper {

    val pendingUri =
        "sporterapp://${AppNavigation.Workout.TRACK_FEATURE_SCREEN_ROUTE}/${AppNavigation.Workout.TrackScreen.route}"

    const val NOTIFICATION_CLICK_REQUEST_CODE = 100
    fun createPendingIntent(context: Context): PendingIntent {
        val intent =
            Intent(Intent.ACTION_VIEW, pendingUri.toUri(), context, MainActivity::class.java)
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(NOTIFICATION_CLICK_REQUEST_CODE, PendingIntent.FLAG_IMMUTABLE)
        }
    }
}

