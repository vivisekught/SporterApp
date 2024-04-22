package com.graduate.work.sporterapp.features.login.sign_in.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.features.login.sign_in.vm.SignInScreenState
import com.graduate.work.sporterapp.features.login.ui.EmailTextField
import com.graduate.work.sporterapp.features.login.ui.LoginIcon
import com.graduate.work.sporterapp.features.login.ui.PasswordTextField

@Composable
fun SignInScreen(uiState: SignInScreenState, event: (SignInScreenEvent) -> Unit) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (
            welcomeTextRef,
            signInTextRef,
            emailFieldRef,
            passwordFieldRef,
            forgetPasswordRef,
            nextButtonRef,
            signUpTextRef,
            otherSignInVariantsRef,
            orLoginWithTextRef,
        ) = createRefs()

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(createRef()) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                    }
            )
        }

        LaunchedEffect(uiState.shouldNavigateToOnBoarding) {
            if (uiState.shouldNavigateToOnBoarding) {
                event(SignInScreenEvent.NavigateToOnBoarding)
            }
        }

        LaunchedEffect(uiState.shouldNavigateToHomeScreen) {
            if (uiState.shouldNavigateToHomeScreen) {
                event(SignInScreenEvent.NavigateToHomeScreen)
            }
        }

        Text(
            text = stringResource(R.string.welcome_back),
            modifier = Modifier
                .constrainAs(welcomeTextRef) {
                    top.linkTo(parent.top, 64.dp)
                    centerHorizontallyTo(parent)
                },
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.sign_in_to_your_account),
            modifier = Modifier
                .constrainAs(signInTextRef) {
                    top.linkTo(welcomeTextRef.bottom, 8.dp)
                    centerHorizontallyTo(parent)
                },
            style = MaterialTheme.typography.bodyMedium
        )
        EmailTextField(
            emailField = uiState.email,
            onEmailFieldChange = { event(SignInScreenEvent.OnEmailChanged(it)) },
            isError = uiState.isEmailAndPasswordError,
            modifier = Modifier.constrainAs(emailFieldRef) {
                top.linkTo(signInTextRef.bottom, 64.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.85f)
            },
        )
        PasswordTextField(
            passwordField = uiState.password,
            onPasswordFieldChange = {
                event(SignInScreenEvent.OnPasswordChanged(it))
            },
            isError = uiState.isEmailAndPasswordError,
            modifier = Modifier.constrainAs(passwordFieldRef) {
                top.linkTo(emailFieldRef.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.85f)
            },
        )
        TextButton(
            onClick = { event(SignInScreenEvent.NavigateToForgetPassword) },
            modifier = Modifier.constrainAs(forgetPasswordRef) {
                top.linkTo(passwordFieldRef.bottom, margin = 8.dp)
                end.linkTo(passwordFieldRef.end)
            }) {
            Text(text = stringResource(R.string.forget_password))
        }
        Button(
            onClick = {
                event(
                    SignInScreenEvent.SignInWithEmailAndPassword
                )
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.constrainAs(nextButtonRef) {
                top.linkTo(forgetPasswordRef.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.9f)
            }) {
            Text(
                text = stringResource(R.string.next),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
        Text(
            text = stringResource(R.string.or_login_with),
            modifier = Modifier.constrainAs(orLoginWithTextRef) {
                top.linkTo(nextButtonRef.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
            })
        Row(
            modifier = Modifier.constrainAs(otherSignInVariantsRef) {
                top.linkTo(orLoginWithTextRef.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoginIcon(
                painter = painterResource(id = R.drawable.ic_google),
                enabled = !uiState.isLoading,
                description = stringResource(R.string.sign_in_with_google)
            ) {
                event(SignInScreenEvent.SignInWithGoogle)
            }
        }

        Row(modifier = Modifier.constrainAs(signUpTextRef) {
            bottom.linkTo(parent.bottom, 16.dp)
            centerHorizontallyTo(parent)
        }, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.don_t_have_an_account))
            TextButton(onClick = { event(SignInScreenEvent.NavigateToSignUp) }) {
                Text(text = stringResource(R.string.sign_up))
            }
        }
    }
}