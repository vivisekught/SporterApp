package com.graduate.work.sporterapp.domain.maps.mapbox.usecases

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route
import com.graduate.work.sporterapp.domain.maps.mapbox.MapboxApiRepository
import javax.inject.Inject

class GetStaticMapUrlUseCase @Inject constructor(
    private val mapboxApiRepository: MapboxApiRepository,
) {
    operator fun invoke(
        route: Route,
        style: MapBoxStyle?,
    ): String {
        val startPoint = route.points?.first()
        val endPoint = route.points?.last()
        val geometry = route.geometry
        return mapboxApiRepository.getStaticMapUrl(
            startPoint,
            endPoint,
            geometry,
            style ?: MapBoxStyle.STREET
        ).toString()
    }
}