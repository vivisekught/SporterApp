package com.graduate.work.sporterapp.data.maps.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.graduate.work.sporterapp.core.ext.convertMetersPerSecondToKilometersPerHour
import com.graduate.work.sporterapp.core.ext.padTimerValue
import com.graduate.work.sporterapp.core.ext.parcelable
import com.graduate.work.sporterapp.core.ext.roundTo2
import com.graduate.work.sporterapp.core.ext.toPoint
import com.graduate.work.sporterapp.core.map.LocationServiceResult
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.Workout
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.WorkoutRoutePoint
import com.graduate.work.sporterapp.domain.firebase.storage.workout.usecases.SaveWorkoutInFirestoreUseCase
import com.graduate.work.sporterapp.domain.maps.location.usecases.CollectUserLocationUseCase
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.mapbox.turf.TurfMeasurement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Date
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class TrackingUserWorkoutService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val binder = LocalBinder()

    @Inject
    lateinit var saveWorkoutInFirestoreUseCase: SaveWorkoutInFirestoreUseCase

    @Inject
    lateinit var collectUserLocationUseCase: CollectUserLocationUseCase

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder


    private lateinit var timer: Timer

    var preparedRoute by mutableStateOf<Route?>(null)
        private set

    var points: List<WorkoutRoutePoint> = mutableStateListOf()
        private set

    private var workoutDuration: Duration = Duration.ZERO

    var hours by mutableStateOf("00")
        private set

    var minutes by mutableStateOf("00")
        private set

    var seconds by mutableStateOf("00")
        private set

    var distance by mutableDoubleStateOf(0.00)
        private set

    var speed by mutableDoubleStateOf(0.00)
        private set

    var avgSpeed by mutableDoubleStateOf(0.00)
        private set

    var climb by mutableDoubleStateOf(0.00)
        private set

    var descent by mutableDoubleStateOf(0.00)
        private set

    var maxSpeed by mutableDoubleStateOf(0.00)
        private set

    var isTracking by mutableStateOf(false)
        private set

    var isWorkoutStarted by mutableStateOf(false)
        private set

    override fun onBind(p0: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            TRACKING_ACTION_START -> startTracking()
            TRACKING_ACTION_STOP -> stopTracking()
            TRACKING_ACTION_PAUSE -> pauseTracking()
            TRACKING_ACTION_RESUME -> resumeTracking()
            TRACKING_ACTION_CREATE_PREPARED_ROUTE -> savedPreparedRoute(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    fun saveWorkout(name: String, onResult: (Throwable?) -> Unit) {
        val calories = calculateBurnedCalories(
            durationInMinutes = workoutDuration.inWholeMinutes.toDouble(),
            age = 20,
            weight = 70,
            isMale = true
        )
        val workout = Workout(
            name = name,
            points = points,
            distance = distance,
            duration = workoutDuration.inWholeSeconds.toDouble(),
            avgSpeed = avgSpeed,
            maxSpeed = speed,
            climb = climb,
            descent = descent,
            calories = calories,
            timeStamp = Date().time
        )
        saveWorkoutInFirestoreUseCase(workout, MapBoxStyle.STREET, onResult)
    }

    private fun calculateBurnedCalories(
        durationInMinutes: Double,
        age: Int,
        weight: Int,
        isMale: Boolean,
    ): Double {
        return if (isMale) {
            ((age * 0.2017) + (weight * 0.09036) - (durationInMinutes * 0.6309) - 55.0969) * ((durationInMinutes + 1) / 4.184)
        } else {
            ((age * 0.074) + (weight * 0.05741) - (durationInMinutes * 0.4472) - 20.4022) * ((durationInMinutes + 1) / 4.184)
        }
    }

    private fun calculateAvgSpeed(points: List<WorkoutRoutePoint>): Double {
        val totalDistance = points.sumOf { it.speed }
        return totalDistance / points.size
    }

    private fun savedPreparedRoute(intent: Intent?) {
        val preparedRoute = intent?.parcelable<Route>(PREPARED_ROUTE_KEY)
        if (preparedRoute != null) {
            this.preparedRoute = preparedRoute
        }
    }

    private fun startTracking() {
        isTracking = true
        isWorkoutStarted = true
        collectUserLocationUseCase(TRACKING_INTERVAL).onEach { locationResult ->
            when (locationResult) {
                is LocationServiceResult.Failure -> {
                    // TODO: handle error
                }

                is LocationServiceResult.Success -> {
                    if (isTracking) {
                        val location = locationResult.location
                        val lastPoint = points.lastOrNull()
                        if (lastPoint != null) {
                            val distanceFromLastPoint =
                                TurfMeasurement.distance(location.toPoint(), lastPoint.point)
                            distance += distanceFromLastPoint.roundTo2()
                            val diff = location.altitude - lastPoint.point.altitude()
                            if (diff < 0) {
                                descent += -diff
                            } else {
                                climb += diff
                            }
                        }
                        val parsedSpeed =
                            location.speed.convertMetersPerSecondToKilometersPerHour()
                                .toDouble().roundTo2()
                        speed = parsedSpeed
                        if (speed > maxSpeed) {
                            maxSpeed = speed
                        }
                        avgSpeed = calculateAvgSpeed(points).roundTo2()
                        points = points + WorkoutRoutePoint(
                            point = location.toPoint(),
                            distanceFromStart = distance,
                            speed = parsedSpeed,
                            timeStamp = locationResult.location.time
                        )
                    }
                }
            }
        }.launchIn(scope)

        startTimer()
        startNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun pauseTracking() {
        isTracking = false
        speed = 0.0
    }

    private fun resumeTracking() {
        isTracking = true
    }

    private fun stopTracking() {
        isWorkoutStarted = false
        isTracking = false
        stopTimer()
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun startTimer() {
        timer = fixedRateTimer(initialDelay = TRACKING_INTERVAL, period = TRACKING_INTERVAL) {
            if (isTracking) {
                workoutDuration = workoutDuration.plus(1.seconds)
                updateWorkoutDuration()
                updateNotification()
            }
        }
    }

    private fun stopTimer() {
        if (this::timer.isInitialized) timer.cancel()
    }

    private fun updateWorkoutDuration() {
        workoutDuration.toComponents { hours, minutes, seconds, _ ->
            this.hours = hours.padTimerValue()
            this.minutes = minutes.padTimerValue()
            this.seconds = seconds.padTimerValue()
        }
    }

    private fun updateNotification() {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText("$hours:$minutes:$seconds").build()
        )
    }

    private fun startNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TRACKING_ACTION_START = "start"
        private const val TRACKING_ACTION_STOP = "stop"
        private const val TRACKING_ACTION_PAUSE = "pause"
        private const val TRACKING_ACTION_RESUME = "resume"
        private const val TRACKING_ACTION_CREATE_PREPARED_ROUTE = "create_prepared_route"
        private const val PREPARED_ROUTE_KEY = "prepared_route"

        const val CHANNEL_ID = "tracking_channel"

        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_NAME = "tracking_channel"
        private const val TRACKING_INTERVAL = 1000L

        fun createStartIntent(context: Context): Intent {
            val intent = Intent(context, TrackingUserWorkoutService::class.java)
            intent.action = TRACKING_ACTION_START
            return intent
        }

        fun createToggleIntent(isTracking: Boolean, context: Context): Intent {
            val intent = Intent(context, TrackingUserWorkoutService::class.java)
            val action = if (isTracking) TRACKING_ACTION_PAUSE else TRACKING_ACTION_RESUME
            intent.action = action
            return intent
        }

        fun createRouteIntent(context: Context, route: Route): Intent {
            val intent = Intent(context, TrackingUserWorkoutService::class.java)
            intent.action = TRACKING_ACTION_CREATE_PREPARED_ROUTE
            intent.putExtra(PREPARED_ROUTE_KEY, route)
            return intent
        }

        fun createStopIntent(context: Context): Intent {
            val intent = Intent(context, TrackingUserWorkoutService::class.java)
            intent.action = TRACKING_ACTION_STOP
            return intent
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): TrackingUserWorkoutService = this@TrackingUserWorkoutService
    }
}