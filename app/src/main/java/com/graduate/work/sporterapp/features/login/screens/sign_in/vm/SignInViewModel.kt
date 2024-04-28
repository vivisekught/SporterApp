package com.graduate.work.sporterapp.features.login.screens.sign_in.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.AuthWithGoogleUseCase
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignInWithEmailAndPasswordUseCase
import com.graduate.work.sporterapp.features.login.core.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInScreenState(
    val isLoading: Boolean = false,
    val shouldNavigateToEmailVerification: Boolean = false,
    val shouldNavigateToHomeScreen: Boolean = false,
    val isEmailAndPasswordError: Boolean = false,
    val isGoogleAuthError: Boolean = false,
    val errorMessage: String = "",
    val email: String = "",
    val password: String = "",
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val authWithGoogleUseCase: AuthWithGoogleUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(SignInScreenState())
        private set

    fun onEmailChange(email: String) {
        resetErrorState()
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        resetErrorState()
        uiState = uiState.copy(password = password)
    }

    private fun resetErrorState() {
        uiState = uiState.copy(isEmailAndPasswordError = false, errorMessage = "")
    }

    fun resetErrorGoogleAuthState() {
        uiState = uiState.copy(isGoogleAuthError = false, errorMessage = "")
    }

    fun signInWithEmailAndPassword() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(
                isEmailAndPasswordError = true,
                // TODO move to string resource
                errorMessage = "Please fill all fields"
            )
            return
        }
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            val authResponse: AuthResponse<AuthResult> =
                signInWithEmailAndPasswordUseCase(uiState.email, uiState.password)
            when (authResponse) {
                is AuthResponse.Failure -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isEmailAndPasswordError = true,
                        errorMessage = authResponse.message
                    )
                }

                is AuthResponse.Success -> {
                    uiState = if (authResponse.data?.user?.isEmailVerified == false) {
                        uiState.copy(isLoading = false, shouldNavigateToEmailVerification = true)
                    } else {
                        uiState.copy(isLoading = false, shouldNavigateToHomeScreen = true)
                    }
                }
            }
        }
    }

    fun authWithGoogle(credentialAuthResponse: AuthResponse<GetCredentialResponse>) {
        viewModelScope.launch {
            uiState = when (val response = authWithGoogleUseCase(credentialAuthResponse)) {
                is AuthResponse.Failure -> {
                    uiState.copy(
                        isLoading = false,
                        isGoogleAuthError = true,
                        errorMessage = response.message
                    )
                }

                is AuthResponse.Success -> {
                    uiState.copy(isLoading = false, shouldNavigateToHomeScreen = true)
                }
            }
        }
    }

    fun setLoading() {
        uiState = uiState.copy(isLoading = true)
    }
}