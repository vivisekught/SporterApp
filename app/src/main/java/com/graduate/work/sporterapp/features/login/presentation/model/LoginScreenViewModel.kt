package com.graduate.work.sporterapp.features.login.presentation.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.SignInAnonymouslyUseCase
import com.graduate.work.sporterapp.utils.helpers.auth.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginScreenUiState {
    data object Initial : LoginScreenUiState()
    data object Loading : LoginScreenUiState()
    data object Success : LoginScreenUiState()
    data class Error(val error: String) : LoginScreenUiState()
}

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
) : ViewModel() {

    var uiState by mutableStateOf<LoginScreenUiState>(LoginScreenUiState.Initial)
        private set

    fun signInAnonymously() {
        viewModelScope.launch {
            uiState = LoginScreenUiState.Loading
            signInAnonymouslyUseCase().let { response ->
                when (response) {
                    is Response.Failure -> uiState =
                        LoginScreenUiState.Error(response.e.message.toString())

                    is Response.Success -> uiState = LoginScreenUiState.Success
                    else -> Unit
                }
            }
        }
    }
}