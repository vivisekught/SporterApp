package com.graduate.work.sporterapp.features.login.screens.sign_up.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.AuthWithGoogleUseCase
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignUpWithMailAndPasswordUseCase
import com.graduate.work.sporterapp.features.login.core.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SignUpScreenState(
    val isLoading: Boolean = false,
    val shouldNavigateToHome: Boolean = false,
    val shouldNavigateToEmailVerification: Boolean = false,
    val isEmailAndPasswordError: Boolean = false,
    val isGoogleAuthError: Boolean = false,
    val isUserNameError: Boolean = false,
    val errorMessage: String = "",
    val isPolicyAccepted: Boolean = false,
    val isPolicyAcceptedError: Boolean = false,
    val email: String = "",
    val password: String = "",
    val userName: String = "",
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpWithMailAndPasswordUseCase: SignUpWithMailAndPasswordUseCase,
    private val authWithGoogleUseCase: AuthWithGoogleUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(SignUpScreenState())
        private set

    fun onEmailChange(email: String) {
        resetErrorFieldsState()
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        resetErrorFieldsState()
        uiState = uiState.copy(password = password)
    }


    fun onUserNameChange(userName: String) {
        resetUserNameErrorState()
        uiState = uiState.copy(userName = userName)
    }

    private fun resetUserNameErrorState() {
        uiState = uiState.copy(isUserNameError = false)
    }

    private fun resetErrorFieldsState() {
        uiState = uiState.copy(isEmailAndPasswordError = false)
    }

    fun resetErrors() {
        uiState = uiState.copy(isGoogleAuthError = false, errorMessage = "")
    }


    fun setLoading() {
        uiState = uiState.copy(isLoading = true)
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
                    uiState.copy(isLoading = false, shouldNavigateToHome = true)
                }
            }
        }
    }

    fun signUpWithMailAndPassword() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(
                isEmailAndPasswordError = true,
                // TODO move to string resource
                errorMessage = "Please fill all fields"
            )
            return
        }
        if (!uiState.isPolicyAccepted) {
            uiState = uiState.copy(isPolicyAcceptedError = true)
            return
        }
        if (uiState.userName.isBlank() || uiState.userName.length > MAX_NICKNAME_LENGTH) {
            uiState = uiState.copy(isUserNameError = true)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val response = signUpWithMailAndPasswordUseCase(uiState.email, uiState.password)
            uiState = when (response) {
                is AuthResponse.Failure -> {
                    uiState.copy(
                        isLoading = false,
                        isEmailAndPasswordError = true,
                        errorMessage = response.message
                    )
                }

                is AuthResponse.Success -> {
                    uiState.copy(isLoading = false, shouldNavigateToEmailVerification = true)
                }
            }
        }
    }

    fun onAgreePolicy(agree: Boolean) {
        uiState = uiState.copy(isPolicyAccepted = agree)
        uiState = uiState.copy(isPolicyAcceptedError = false)
    }

    companion object {
        private const val MAX_NICKNAME_LENGTH = 25
    }
}