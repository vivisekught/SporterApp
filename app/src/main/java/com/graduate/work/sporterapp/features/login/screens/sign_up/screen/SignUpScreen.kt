package com.graduate.work.sporterapp.features.login.screens.sign_up.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.features.login.screens.sign_up.vm.SignUpScreenState
import com.graduate.work.sporterapp.features.login.ui.EmailTextField
import com.graduate.work.sporterapp.features.login.ui.LoginIcon
import com.graduate.work.sporterapp.features.login.ui.PasswordTextField
import com.graduate.work.sporterapp.features.login.ui.UserNameTextField
import com.graduate.work.sporterapp.utils.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun CustomPreview() {
    AppTheme {
        SignUpScreen(
            uiState = SignUpScreenState(
                isGoogleAuthError = true,
            ),
            event = {}
        )
    }
}

@Composable
fun SignUpScreen(uiState: SignUpScreenState, event: (SignUpScreenEvent) -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (
            getStartedTextRef,
            creatingAnAccountTextRef,
            userNameFieldRef,
            emailFieldRef,
            passwordFieldRef,
            nextButtonRef,
            otherSignInVariantsRef,
            orSignUpWithTextRef,
            signInTextRef,
        ) = createRefs()

        AnimatedVisibility(
            visible = uiState.isLoading,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(createRef()) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                },
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {
            LinearProgressIndicator()
        }

        LaunchedEffect(uiState.isGoogleAuthError, uiState.isEmailAndPasswordError) {
            if (uiState.isGoogleAuthError || uiState.isEmailAndPasswordError) {
                snackbarHostState.showSnackbar(
                    message = uiState.errorMessage,
                    duration = SnackbarDuration.Short
                )
                event(SignUpScreenEvent.ResetGoogleAuthErrorState)
            }
        }

        LaunchedEffect(uiState) {
            if (uiState.shouldNavigateToOnBoarding) {
                event(SignUpScreenEvent.NavigateToOnBoarding)
            } else if (uiState.shouldNavigateToHomeScreen) {
                event(SignUpScreenEvent.NavigateToHome)
            } else if (uiState.shouldNavigateToEmailVerification) {
                event(SignUpScreenEvent.NavigateToEmailVerification)
            }
        }

        Text(
            text = stringResource(R.string.get_started),
            modifier = Modifier
                .constrainAs(getStartedTextRef) {
                    top.linkTo(parent.top, 64.dp)
                    centerHorizontallyTo(parent)
                },
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.by_creating_a_free_account),
            modifier = Modifier
                .constrainAs(creatingAnAccountTextRef) {
                    top.linkTo(getStartedTextRef.bottom, 8.dp)
                    centerHorizontallyTo(parent)
                },
            style = MaterialTheme.typography.bodyMedium
        )
        UserNameTextField(
            usernameField = uiState.userName,
            onUserNameFieldChange = { event(SignUpScreenEvent.OnUserNameChanged(it)) },
            isError = uiState.isUserNameError,
            modifier = Modifier.constrainAs(userNameFieldRef) {
                top.linkTo(creatingAnAccountTextRef.bottom, 64.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.85f)
            },
        )
        EmailTextField(
            emailField = uiState.email,
            onEmailFieldChange = { event(SignUpScreenEvent.OnEmailChanged(it)) },
            isError = uiState.isEmailAndPasswordError,
            modifier = Modifier.constrainAs(emailFieldRef) {
                top.linkTo(userNameFieldRef.bottom, 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.85f)
            },
        )
        PasswordTextField(
            passwordField = uiState.password,
            onPasswordFieldChange = {
                event(SignUpScreenEvent.OnPasswordChanged(it))
            },
            isError = uiState.isEmailAndPasswordError,
            modifier = Modifier.constrainAs(passwordFieldRef) {
                top.linkTo(emailFieldRef.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.85f)
            },
        )
        Button(
            onClick = {
                event(
                    SignUpScreenEvent.SignUpWithEmailAndPassword
                )
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.constrainAs(nextButtonRef) {
                top.linkTo(passwordFieldRef.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.9f)
            }) {
            Text(
                text = stringResource(R.string.next),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.constrainAs(createRef()) {
            bottom.linkTo(orSignUpWithTextRef.top, margin = 8.dp)
            start.linkTo(parent.start, 32.dp)
            end.linkTo(parent.end, 32.dp)
            width = Dimension.fillToConstraints
        }, thickness = 2.dp)
        Text(
            text = stringResource(R.string.or_sign_up_with),
            modifier = Modifier.constrainAs(orSignUpWithTextRef) {
                bottom.linkTo(otherSignInVariantsRef.top, margin = 32.dp)
                centerHorizontallyTo(parent)
            })
        Row(
            modifier = Modifier.constrainAs(otherSignInVariantsRef) {
                bottom.linkTo(signInTextRef.top, margin = 16.dp)
                centerHorizontallyTo(parent)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val localContext = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            LoginIcon(
                painter = painterResource(id = R.drawable.ic_google),
                enabled = !uiState.isLoading,
                description = stringResource(R.string.sign_in_with_google)
            ) {
                event(SignUpScreenEvent.AuthWithGoogle(coroutineScope, localContext))
            }
        }

        Row(modifier = Modifier.constrainAs(signInTextRef) {
            bottom.linkTo(parent.bottom, 16.dp)
            centerHorizontallyTo(parent)
        }, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.already_have_an_account))
            TextButton(onClick = { event(SignUpScreenEvent.NavigateToSignIn) }) {
                Text(text = stringResource(R.string.sign_in))
            }
        }
        SnackbarHost(modifier = Modifier.constrainAs(createRef()) {
            bottom.linkTo(parent.bottom, margin = 16.dp)
            centerHorizontallyTo(parent)
        }, hostState = snackbarHostState)
    }
}