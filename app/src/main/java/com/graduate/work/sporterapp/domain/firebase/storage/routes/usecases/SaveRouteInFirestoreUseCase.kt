package com.graduate.work.sporterapp.domain.firebase.storage.routes.usecases

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.GetUserIdUseCase
import com.graduate.work.sporterapp.domain.firebase.storage.routes.CloudStorageRouteRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.graduate.work.sporterapp.domain.maps.mapbox.usecases.GetStaticMapUrlUseCase
import javax.inject.Inject

class SaveRouteInFirestoreUseCase @Inject constructor(
    private val cloudStorageRouteRepository: CloudStorageRouteRepository,
    private val getStaticMapUrlUseCase: GetStaticMapUrlUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
) {

    operator fun invoke(route: Route, style: MapBoxStyle?, onResult: (Throwable?) -> Unit) {
        val userId = getUserIdUseCase()
        val imgUrl = getStaticMapUrlUseCase(route, style)
        val routeWithImageUrl = route.copy(routeImgUrl = imgUrl, userId = userId)
        cloudStorageRouteRepository.saveRoute(routeWithImageUrl, onResult)
    }
}
