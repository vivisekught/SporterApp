package com.graduate.work.sporterapp.domain.firebase.storage.routes.usecases

import com.graduate.work.sporterapp.domain.firebase.storage.routes.CloudStorageRouteRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import javax.inject.Inject

class GetRouteByIdUseCase @Inject constructor(
    private val cloudStorageRouteRepository: CloudStorageRouteRepository,
) {

    operator fun invoke(routeId: String, onError: (Throwable) -> Unit, onSuccess: (Route) -> Unit) =
        cloudStorageRouteRepository.getRoute(routeId, onError, onSuccess)
}