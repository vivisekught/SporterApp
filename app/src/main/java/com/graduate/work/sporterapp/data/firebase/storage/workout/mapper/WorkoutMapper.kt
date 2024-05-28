package com.graduate.work.sporterapp.data.firebase.storage.workout.mapper

import com.graduate.work.sporterapp.data.firebase.storage.workout.pojo.WorkoutFirestorePojo
import com.graduate.work.sporterapp.data.firebase.storage.workout.pojo.WorkoutFirestoreRoutePoint
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.Workout
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.WorkoutRoutePoint
import com.mapbox.geojson.Point

class WorkoutMapper {

    fun mapFirestorePojoToEntity(pojo: WorkoutFirestorePojo): Workout {
        return Workout(
            workoutId = pojo.workoutId,
            name = pojo.name,
            userId = pojo.userId,
            points = pojo.points?.map {
                WorkoutRoutePoint(
                    point = Point.fromLngLat(it.lng, it.lat, it.altitude),
                    distanceFromStart = it.distanceFromStart,
                    speed = it.speed,
                    timeStamp = it.timeStamp
                )
            },
            distance = pojo.distance,
            duration = pojo.duration,
            climb = pojo.climb,
            descent = pojo.descent,
            routeImgUrl = pojo.routeImgUrl,
            avgSpeed = pojo.avgSpeed,
            maxSpeed = pojo.maxSpeed,
            calories = pojo.calories,
            timeStamp = pojo.timeStamp
        )
    }

    fun mapEntityToFirestorePojo(workout: Workout) = WorkoutFirestorePojo(
        workoutId = workout.workoutId,
        name = workout.name,
        userId = workout.userId,
        points = workout.points?.map {
            WorkoutFirestoreRoutePoint(
                lat = it.point.latitude(),
                lng = it.point.longitude(),
                altitude = it.point.altitude(),
                distanceFromStart = it.distanceFromStart,
                speed = it.speed,
                timeStamp = it.timeStamp
            )
        },
        distance = workout.distance,
        duration = workout.duration,
        climb = workout.climb,
        descent = workout.descent,
        routeImgUrl = workout.routeImgUrl,
        avgSpeed = workout.avgSpeed,
        maxSpeed = workout.maxSpeed,
        calories = workout.calories,
        timeStamp = workout.timeStamp
    )
}