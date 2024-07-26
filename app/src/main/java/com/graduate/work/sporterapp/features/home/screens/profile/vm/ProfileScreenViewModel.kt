package com.graduate.work.sporterapp.features.home.screens.profile.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.domain.api.StravaApiRepository
import com.graduate.work.sporterapp.domain.api.usecases.GetAndSaveStravaTokenUseCase
import com.graduate.work.sporterapp.domain.firebase.auth.FirebaseAuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel()
class ProfileScreenViewModel @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
): ViewModel() {

    fun signOut() {
        viewModelScope.launch {
            firebaseAuthRepository.signOut()
        }
    }
}