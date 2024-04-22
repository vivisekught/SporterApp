package com.graduate.work.sporterapp.features.login.presentation.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignInWithEmailAndPasswordUseCase
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignInWithGoogleUseCase
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignUpWithMailAndPasswordUseCase
import com.graduate.work.sporterapp.utils.core.auth.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthScreenUiState {
    data object Initial : AuthScreenUiState()
    data object Loading : AuthScreenUiState()
    data object Success : AuthScreenUiState()
    data class Error(val error: String) : AuthScreenUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val signUpWithMailAndPasswordUseCase: SignUpWithMailAndPasswordUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    var uiState by mutableStateOf<AuthScreenUiState>(AuthScreenUiState.Initial)
        private set

    fun signInWithGoogle() {
        viewModelScope.launch {
            uiState = AuthScreenUiState.Loading
            val response = signInWithGoogleUseCase()
            uiState = when (response) {
                is Response.Failure -> AuthScreenUiState.Error(response.message)
                Response.Loading -> AuthScreenUiState.Loading
                is Response.Success -> AuthScreenUiState.Success
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            uiState = AuthScreenUiState.Loading
            val response = signInWithEmailAndPasswordUseCase(email, password)
            uiState = when (response) {
                is Response.Failure -> AuthScreenUiState.Error(response.message)
                Response.Loading -> AuthScreenUiState.Loading
                is Response.Success -> AuthScreenUiState.Success
            }
        }
    }

    fun signUpWithMailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            uiState = AuthScreenUiState.Loading
            val response = signUpWithMailAndPasswordUseCase(email, password)
            uiState = when (response) {
                is Response.Failure -> AuthScreenUiState.Error(response.message)
                Response.Loading -> AuthScreenUiState.Loading
                is Response.Success -> AuthScreenUiState.Success
            }
        }
    }
}