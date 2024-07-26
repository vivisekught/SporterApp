package com.graduate.work.sporterapp.domain.firebase.storage.routes

import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.SearchRouteParams
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import kotlinx.coroutines.flow.Flow

interface CloudStorageRouteRepository {
    fun getRoutes(
        userId: String,
        searchRouteParams: SearchRouteParams,
    ): Flow<Response<List<Route>>>

    fun getRoute(routeId: String, onError: (Throwable) -> Unit, onSuccess: (Route) -> Unit)
    fun saveRoute(route: Route, onResult: (Throwable?) -> Unit)
    fun updateRoute(route: Route, onResult: (Throwable?) -> Unit)
    fun deleteRoute(routeId: String, onResult: (Throwable?) -> Unit)
}