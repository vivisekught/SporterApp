package com.graduate.work.sporterapp.data.api.elevation

import com.graduate.work.sporterapp.data.api.elevation.pojo.ElevationResponsePojo
import com.graduate.work.sporterapp.data.api.elevation.services.ElevationService
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

        // process first batch
        fun processBatch(batch: List<Point>) {
            // get points without altitude
            val pointsWithoutAltitude: MutableMap<Int, Point> = mutableMapOf()
            batch.forEachIndexed { index, point ->
                // filter points without altitude
                if (!point.hasAltitude()) {
                    pointsWithoutAltitude[index] = point
                }
            }
            // get points with altitude
            if (pointsWithoutAltitude.isEmpty()) {
                allPointsWithAltitude.addAll(batch)
                // check if all points are processed
                if (allPointsWithAltitude.size == pointsMutableList.size) {
                    continuation.resume(allPointsWithAltitude)
                } else {
                    val remainingBatches = batches.drop(allPointsWithAltitude.size / batchSize)
                    remainingBatches.firstOrNull()?.let { processBatch(it) }
                }
                return
            }
            // get points latitudes for api request
            val latitudes = pointsWithoutAltitude.values.map { it.latitude() }
            // get points longitudes for api request
            val longitudes = pointsWithoutAltitude.values.map { it.longitude() }
            // get points altitudes
            elevationService.getElevation(
                latitudes = latitudes,
                longitudes = longitudes
            ).enqueue(object : Callback<ElevationResponsePojo> {
                // on success response
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

                // on failure
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
        // process first batch
        processBatch(batches.first())
    }
}