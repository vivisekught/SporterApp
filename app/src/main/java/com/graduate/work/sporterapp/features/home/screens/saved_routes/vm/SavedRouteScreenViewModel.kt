package com.graduate.work.sporterapp.features.home.screens.saved_routes.vm

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.GetUserIdUseCase
import com.graduate.work.sporterapp.domain.firebase.storage.routes.CloudStorageRouteRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedRouteScreenViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val cloudStorageRouteRepository: CloudStorageRouteRepository,
) : ViewModel() {

    var routes = mutableStateMapOf<String, Route>()
        private set

    fun addListener() {
        val userId = getUserIdUseCase()
        cloudStorageRouteRepository.addListener(
            userId.toString(),
            onDocumentEvent = ::onDocumentEvent,
            onError = {

            })
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, route: Route) {
        if (wasDocumentDeleted) {
            routes.remove(route.routeId)
        } else {
            routes[route.routeId] = route
        }
    }

    fun removeListener() {
        cloudStorageRouteRepository.removeListener()
    }
}