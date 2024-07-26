package com.graduate.work.sporterapp.features.home.screens.workouts.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.SearchWorkoutParams
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.GetUserIdUseCase
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.CloudStorageWorkoutRepository
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutsScreenViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val cloudStorageWorkoutRepository: CloudStorageWorkoutRepository,
) : ViewModel() {

    var workoutsResponse by mutableStateOf<Response<List<Workout>>>(Response.Loading)
        private set

    fun getWorkoutList(searchWorkoutParams: SearchWorkoutParams = SearchWorkoutParams()) = viewModelScope.launch {
        val userId = getUserIdUseCase()
        cloudStorageWorkoutRepository.getWorkouts(
            searchWorkoutParams,
            userId.toString()
        ).collect { response ->
            workoutsResponse = response
        }
    }
}