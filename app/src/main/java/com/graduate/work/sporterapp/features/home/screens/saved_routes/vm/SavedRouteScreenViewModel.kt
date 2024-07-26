package com.graduate.work.sporterapp.features.home.screens.saved_routes.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.SearchRouteParams
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.GetUserIdUseCase
import com.graduate.work.sporterapp.domain.firebase.storage.routes.CloudStorageRouteRepository
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedRouteScreenViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val cloudStorageRouteRepository: CloudStorageRouteRepository,
) : ViewModel() {

    var routes by mutableStateOf<Response<List<Route>>>(Response.Loading)
        private set

    fun getRoutesList(searchRouteParams: SearchRouteParams) = viewModelScope.launch {
        val userId = getUserIdUseCase()
        cloudStorageRouteRepository.getRoutes(
            userId.toString(),
            searchRouteParams,
        ).collect { response ->
            routes = response
        }
    }
}