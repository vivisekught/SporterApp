package com.graduate.work.sporterapp.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.GetCurrentAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentAuthStateUseCase: GetCurrentAuthStateUseCase,
): ViewModel() {

    val currentAuthState = getAuthState()

    init {
        getAuthState()
    }

    private fun getAuthState() = getCurrentAuthStateUseCase(viewModelScope)
}