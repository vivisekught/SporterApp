package com.graduate.work.sporterapp.features.login.screens.forget_password.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SendPasswordResetEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgetPasswordScreenState(
    val isLoading: Boolean = false,
    val shouldNavigateToSignIn: Boolean = false,
    val isEmailError: Boolean = false,
    val email: String = "",
)


@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(ForgetPasswordScreenState())
        private set

    fun onEmailChanged(email: String) {
        resetErrorState()
        uiState = uiState.copy(email = email)
    }

    private fun resetErrorState() {
        uiState = uiState.copy(isEmailError = false)
    }

    fun sendPasswordResetEmail() {
        if (uiState.email.isBlank()) {
            uiState = uiState.copy(isEmailError = true)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val response = sendPasswordResetEmailUseCase(uiState.email)
            uiState = when (response) {
                is Response.Success -> {
                    uiState.copy(isLoading = false, shouldNavigateToSignIn = true)
                }

                is Response.Failure -> {
                    uiState.copy(isLoading = false, isEmailError = true)
                }
            }
        }
    }
}