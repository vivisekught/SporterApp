package com.graduate.work.sporterapp.features.login.screens.sign_up.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.AuthWithGoogleUseCase
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignUpWithMailAndPasswordUseCase
import com.graduate.work.sporterapp.utils.core.auth.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SignUpScreenState(
    val isLoading: Boolean = false,
    val shouldNavigateToOnBoarding: Boolean = false,
    val shouldNavigateToHomeScreen: Boolean = false,
    val shouldNavigateToEmailVerification: Boolean = false,
    val isEmailAndPasswordError: Boolean = false,
    val isGoogleAuthError: Boolean = false,
    val isUserNameError: Boolean = false,
    val errorMessage: String = "",
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
        resetErrorState()
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        resetErrorState()
        uiState = uiState.copy(password = password)
    }

    fun onUserNameChange(userName: String) {
        resetUserNameErrorState()
        uiState = uiState.copy(userName = userName)
    }

    private fun resetUserNameErrorState() {
        uiState = uiState.copy(isUserNameError = false)
    }

    private fun resetErrorState() {
        uiState = uiState.copy(isEmailAndPasswordError = false)
    }

    fun resetErrorGoogleAuthState() {
        uiState = uiState.copy(isGoogleAuthError = false, errorMessage = "")
    }


    fun setLoading() {
        uiState = uiState.copy(isLoading = true)
    }

    fun authWithGoogle(credentialResponse: Response<GetCredentialResponse>) {
        viewModelScope.launch {
            when (val response = authWithGoogleUseCase(credentialResponse)) {
                is Response.Failure -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isGoogleAuthError = true,
                        errorMessage = response.message
                    )
                }

                is Response.Success -> {
                    uiState = if (response.data?.additionalUserInfo?.isNewUser == true) {
                        uiState.copy(isLoading = false, shouldNavigateToOnBoarding = true)
                    } else {
                        uiState.copy(isLoading = false, shouldNavigateToHomeScreen = true)
                    }
                }

                Response.Loading -> Unit
            }
        }
    }

    fun signUpWithMailAndPassword() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(isEmailAndPasswordError = true)
            return
        }
        if (uiState.userName.isBlank() || uiState.userName.length > 25) {
            uiState = uiState.copy(isUserNameError = true)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val response = signUpWithMailAndPasswordUseCase(uiState.email, uiState.password)
            when (response) {
                is Response.Failure -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        isEmailAndPasswordError = true,
                        errorMessage = response.message
                    )
                }

                is Response.Success -> {
                    uiState = uiState.copy(isLoading = false, shouldNavigateToHomeScreen = true)
                }

                Response.Loading -> Unit
            }
        }
    }
}