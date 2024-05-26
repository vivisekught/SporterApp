package com.graduate.work.sporterapp.domain.maps.routes

import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route

abstract class RoutesFileService {

    abstract suspend fun importRoutes(routesPath: String): Route

    abstract suspend fun saveRouteInInternalStorage(route: Route): Boolean
}