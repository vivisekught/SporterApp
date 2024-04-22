package com.graduate.work.sporterapp.features.login.presentation.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.features.login.presentation.composables.EmailTextField
import com.graduate.work.sporterapp.features.login.presentation.composables.LoginIcon
import com.graduate.work.sporterapp.features.login.presentation.composables.PasswordTextField

sealed class SignInScreenEvent {
    data object ForgetPassword : SignInScreenEvent()
    data class SignIn(val email: String, val password: String) : SignInScreenEvent()
    data object GoogleSignIn : SignInScreenEvent()
    data object SignUp : SignInScreenEvent()
}

@Composable
fun SignInScreen(isError: Boolean = false, event: (SignInScreenEvent) -> Unit) {
    var emailField by rememberSaveable {
        mutableStateOf("")
    }
    var passwordField by rememberSaveable {
        mutableStateOf("")
    }
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
            emailField = emailField,
            onEmailFieldChange = { emailField = it },
            isError = isError,
            modifier = Modifier.constrainAs(emailFieldRef) {
                top.linkTo(signInTextRef.bottom, 64.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.85f)
            },
        )
        PasswordTextField(
            passwordField = passwordField,
            onPasswordFieldChange = {
                passwordField = it
            },
            isError = isError,
            modifier = Modifier.constrainAs(passwordFieldRef) {
                top.linkTo(emailFieldRef.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.85f)
            },
        )
        TextButton(
            onClick = { event(SignInScreenEvent.ForgetPassword) },
            modifier = Modifier.constrainAs(forgetPasswordRef) {
                top.linkTo(passwordFieldRef.bottom, margin = 8.dp)
                end.linkTo(passwordFieldRef.end)
            }) {
            Text(text = stringResource(R.string.forget_password))
        }
        Button(
            onClick = { event(SignInScreenEvent.SignIn(emailField, passwordField)) },
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
                description = stringResource(R.string.sign_in_with_google)
            ) {
                event(SignInScreenEvent.GoogleSignIn)
            }
        }

        Row(modifier = Modifier.constrainAs(signUpTextRef) {
            bottom.linkTo(parent.bottom, 16.dp)
            centerHorizontallyTo(parent)
        }, verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.don_t_have_an_account))
            TextButton(onClick = { event(SignInScreenEvent.SignUp) }) {
                Text(text = stringResource(R.string.sign_up))
            }
        }
    }
}