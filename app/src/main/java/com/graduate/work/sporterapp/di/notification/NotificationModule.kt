package com.graduate.work.sporterapp.di.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.data.maps.location.TrackingUserWorkoutService.Companion.CHANNEL_ID
import com.graduate.work.sporterapp.features.track.helper.TrackHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationCompatBuilder(
        @ApplicationContext context: Context,
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.tracking_your_workout))
            .setContentText("00:00:00")
            .setSmallIcon(R.drawable.baseline_pedal_bike_24)
            .setOngoing(true)
            .setContentIntent(TrackHelper.createPendingIntent(context))

    @ServiceScoped
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): android.app.NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

}