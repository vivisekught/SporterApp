package com.graduate.work.sporterapp.features.login.sign_in.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignInWithEmailAndPasswordUseCase
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignInWithGoogleUseCase
import com.graduate.work.sporterapp.utils.core.auth.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInScreenState(
    val isLoading: Boolean = false,
    val shouldNavigateToOnBoarding: Boolean = false,
    val shouldNavigateToHomeScreen: Boolean = false,
    val isEmailAndPasswordError: Boolean = false,
    val email: String = "",
    val password: String = "",
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(SignInScreenState())
        private set

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun signInWithEmailAndPassword() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(isEmailAndPasswordError = true)
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val response = signInWithEmailAndPasswordUseCase(uiState.email, uiState.password)
            when (response) {
                is Response.Failure -> {
                    uiState = uiState.copy(isLoading = false, isEmailAndPasswordError = true)
                }

                is Response.Success -> {
                    uiState = uiState.copy(isLoading = false, shouldNavigateToHomeScreen = true)
                }

                Response.Loading -> Unit
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            when (val response = signInWithGoogleUseCase()) {
                is Response.Failure -> {
                    uiState = uiState.copy(isLoading = false, isEmailAndPasswordError = true)
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

//    fun signUpWithMailAndPassword(email: String, password: String) {
//        viewModelScope.launch {
//            uiState = SignInScreenUiState.Loading
//            val response = signUpWithMailAndPasswordUseCase(email, password)
//            uiState = when (response) {
//                is Response.Failure -> SignInScreenUiState.Error(response.message)
//                Response.Loading -> SignInScreenUiState.Loading
//                is Response.Success -> SignInScreenUiState.Success
//            }
//        }
//    }
}