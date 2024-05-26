package com.graduate.work.sporterapp.domain.maps.location

import com.graduate.work.sporterapp.core.map.LocationServiceResult
import kotlinx.coroutines.flow.Flow

interface UserLocationRepository {

    fun collectUserLocation(interval: Long? = null): Flow<LocationServiceResult>
}