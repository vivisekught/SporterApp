package com.graduate.work.sporterapp.data.maps.location

import android.annotation.SuppressLint
import android.content.Context
import com.graduate.work.sporterapp.core.ext.isLocationPermissionGranted
import com.graduate.work.sporterapp.core.map.LocationServiceResult
import com.graduate.work.sporterapp.domain.maps.location.UserLocationRepository
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.common.location.toAndroidLocation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserLocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : UserLocationRepository {
    @SuppressLint("MissingPermission")
    override fun getUserLocation(): Flow<LocationServiceResult> = callbackFlow {
        if (!context.isLocationPermissionGranted()) {
            trySend(LocationServiceResult.Failure(LocationServiceResult.FailureReason.PERMISSION_IS_DENIED))
            close()
            return@callbackFlow
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        val isGpsEnabled =
            locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        val isNetworkEnabled =
            locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
        if (!isGpsEnabled && !isNetworkEnabled) {
            trySend(LocationServiceResult.Failure(LocationServiceResult.FailureReason.LOCATION_IS_DISABLED))
        }

        val locationService: LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider: DeviceLocationProvider? = null

        val request = LocationProviderRequest.Builder()
            .interval(
                IntervalSettings.Builder()
                    .interval(INTERVAL)
                    .minimumInterval(MINIMUM_INTERVAL)
                    .maximumInterval(MAX_INTERVAL)
                    .build()
            )
            .displacement(DISPLACEMENT)
            .accuracy(AccuracyLevel.HIGH)
            .build()

        val result = locationService.getDeviceLocationProvider(request)
        if (result.isValue) {
            locationProvider = result.value
        } else {
            trySend(LocationServiceResult.Failure(LocationServiceResult.FailureReason.LOCATION_IS_DISABLED))
        }
        val locationObserver =
            LocationObserver { locations ->
                launch { trySend(LocationServiceResult.Success(locations[0].toAndroidLocation())) }
            }
        locationProvider?.addLocationObserver(locationObserver)

        awaitClose {
            locationProvider?.removeLocationObserver(locationObserver)
        }
    }

    companion object {
        private const val INTERVAL: Long = 2000
        private const val MINIMUM_INTERVAL: Long = 1000
        private const val MAX_INTERVAL: Long = 5000
        private const val DISPLACEMENT: Float = 5F
    }
}