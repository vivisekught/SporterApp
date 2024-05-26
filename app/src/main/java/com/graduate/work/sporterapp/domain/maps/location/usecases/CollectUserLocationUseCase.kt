package com.graduate.work.sporterapp.domain.maps.location.usecases

import com.graduate.work.sporterapp.domain.maps.location.UserLocationRepository
import javax.inject.Inject

class CollectUserLocationUseCase @Inject constructor(
    private val userLocationRepository: UserLocationRepository,
) {
    operator fun invoke(interval: Long? = null) =
        userLocationRepository.collectUserLocation(interval)
}