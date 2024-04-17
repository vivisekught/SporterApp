package com.graduate.work.sporterapp.features.login.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.graduate.work.sporterapp.features.login.presentation.model.LoginScreenViewModel

@Composable
fun LoginScreen() {
    val viewModel = hiltViewModel<LoginScreenViewModel>()
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

    }
}