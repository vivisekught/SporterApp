package com.graduate.work.sporterapp.data.firebase.storage.mapper

import com.graduate.work.sporterapp.data.firebase.storage.pojo.FirestoreRoutePojo
import com.graduate.work.sporterapp.data.firebase.storage.pojo.RoutePoint
import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route
import com.mapbox.geojson.Point

class RouteMapper {

    fun mapFirestorePojoToEntity(entity: FirestoreRoutePojo) = Route(
        routeId = entity.routeId,
        name = entity.name,
        description = entity.description,
        userId = entity.userId,
        points = entity.points?.map { Point.fromLngLat(it.lng, it.lat, it.altitude) },
        distance = entity.distance,
        duration = entity.duration,
        climb = entity.climb,
        descent = entity.descent,
        geometry = entity.geometry,
        routeImgUrl = entity.routeImgUrl,
        timeStamp = entity.timeStamp
    )

    fun mapEntityToFirestorePojo(route: Route) = FirestoreRoutePojo(
        routeId = route.routeId,
        name = route.name,
        description = route.description,
        userId = route.userId,
        points = route.points?.map {
            RoutePoint(
                lat = it.latitude(),
                lng = it.longitude(),
                altitude = it.altitude()
            )
        },
        distance = route.distance,
        duration = route.duration,
        climb = route.climb,
        descent = route.descent,
        geometry = route.geometry,
        routeImgUrl = route.routeImgUrl,
        timeStamp = route.timeStamp
    )
}