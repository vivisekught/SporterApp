package com.graduate.work.sporterapp.domain.maps.mapbox.usecases

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.Workout
import com.graduate.work.sporterapp.domain.maps.mapbox.MapboxApiRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.mapbox.geojson.utils.PolylineUtils
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

    operator fun invoke(
        workout: Workout,
        style: MapBoxStyle?,
    ): String {
        val startPoint = workout.points.first().point
        val endPoint = workout.points.last().point
        val points = workout.points.map { workoutPoint ->
            workoutPoint.point
        }
        val geometry = PolylineUtils.encode(points, 5)
        val l = mapboxApiRepository.getStaticMapUrl(
            startPoint,
            endPoint,
            geometry,
            style ?: MapBoxStyle.STREET
        ).toString()
        return l
    }
}