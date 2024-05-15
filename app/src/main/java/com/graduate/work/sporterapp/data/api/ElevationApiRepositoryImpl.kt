package com.graduate.work.sporterapp.data.api

import com.graduate.work.sporterapp.data.api.pojo.ElevationResponsePojo
import com.graduate.work.sporterapp.data.api.services.ElevationService
import com.graduate.work.sporterapp.domain.api.ElevationApiRepository
import com.mapbox.geojson.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ElevationApiRepositoryImpl @Inject constructor(
    private val elevationService: ElevationService,
) : ElevationApiRepository {

    override suspend fun getPointsWithElevationFromCoordinates(
        points: List<Point>?,
    ): List<Point>? = suspendCoroutine { continuation ->
        if (points.isNullOrEmpty()) {
            continuation.resume(null)
            return@suspendCoroutine
        }

        val pointsMutableList = points.toMutableList()
        val batchSize = 100
        val batches = pointsMutableList.chunked(batchSize)

        val allPointsWithAltitude = mutableListOf<Point>()

        fun processBatch(batch: List<Point>) {
            val pointsWithoutAltitude: MutableMap<Int, Point> = mutableMapOf()
            batch.forEachIndexed { index, point ->
                if (!point.hasAltitude()) {
                    pointsWithoutAltitude[index] = point
                }
            }

            if (pointsWithoutAltitude.isEmpty()) {
                allPointsWithAltitude.addAll(batch)
                if (allPointsWithAltitude.size == pointsMutableList.size) {
                    continuation.resume(allPointsWithAltitude)
                } else {
                    val remainingBatches = batches.drop(allPointsWithAltitude.size / batchSize)
                    remainingBatches.firstOrNull()?.let { processBatch(it) }
                }
                return
            }

            val latitudes = pointsWithoutAltitude.values.map { it.latitude() }
            val longitudes = pointsWithoutAltitude.values.map { it.longitude() }
            elevationService.getElevation(
                latitudes = latitudes,
                longitudes = longitudes
            ).enqueue(object : Callback<ElevationResponsePojo> {
                override fun onResponse(
                    call: Call<ElevationResponsePojo>,
                    response: Response<ElevationResponsePojo>,
                ) {
                    val altitudes = response.body()?.elevation
                    var altitudeIndex = 0
                    if (altitudes != null) {
                        pointsWithoutAltitude.forEach { (key, point) ->
                            pointsWithoutAltitude[key] = Point.fromLngLat(
                                point.longitude(),
                                point.latitude(),
                                altitudes[altitudeIndex]
                            )
                            altitudeIndex++
                        }
                    }
                    allPointsWithAltitude.addAll(pointsWithoutAltitude.values)
                    if (allPointsWithAltitude.size == pointsMutableList.size) {
                        continuation.resume(allPointsWithAltitude)
                    } else {
                        val remainingBatches = batches.drop(allPointsWithAltitude.size / batchSize)
                        remainingBatches.firstOrNull()?.let { processBatch(it) }
                    }
                }

                override fun onFailure(call: Call<ElevationResponsePojo>, t: Throwable) {
                    allPointsWithAltitude.addAll(pointsWithoutAltitude.values)
                    if (allPointsWithAltitude.size == pointsMutableList.size) {
                        continuation.resume(allPointsWithAltitude)
                    } else {
                        val remainingBatches = batches.drop(allPointsWithAltitude.size / batchSize)
                        remainingBatches.firstOrNull()?.let { processBatch(it) }
                    }
                }
            })
        }

        processBatch(batches.first())
    }
}