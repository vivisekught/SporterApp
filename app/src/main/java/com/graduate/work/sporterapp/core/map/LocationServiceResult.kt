package com.graduate.work.sporterapp.core.map

import android.location.Location

sealed class LocationServiceResult {

    data class Success(val location: Location) : LocationServiceResult()
    data class Failure(val reason: FailureReason) : LocationServiceResult()
    enum class FailureReason {
        PERMISSION_IS_DENIED,
        LOCATION_IS_DISABLED,
    }
}