package com.graduate.work.sporterapp.domain.firebase.storage.routes

import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route

interface CloudStorageRouteRepository {
    fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Route) -> Unit,
        onError: (Throwable) -> Unit,
    )

    fun removeListener()
    fun getRoute(routeId: String, onError: (Throwable) -> Unit, onSuccess: (Route) -> Unit)
    fun saveRoute(route: Route, onResult: (Throwable?) -> Unit)
    fun updateRoute(route: Route, onResult: (Throwable?) -> Unit)
    fun deleteRoute(routeId: String, onResult: (Throwable?) -> Unit)
}