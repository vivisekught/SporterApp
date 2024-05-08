package com.graduate.work.sporterapp.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.GetCurrentAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentAuthStateUseCase: GetCurrentAuthStateUseCase,
): ViewModel() {

    val currentAuthState = getAuthState()

    var isSplashLoading = mutableStateOf(true)
        private set

    init {
        getAuthState()
        viewModelScope.launch {
            currentAuthState.collect {
                isSplashLoading.value = false
            }
        }
    }

    private fun getAuthState() = getCurrentAuthStateUseCase(viewModelScope)
}