package com.graduate.work.sporterapp.features.login.screens.forget_password.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.features.login.screens.forget_password.vm.ForgetPasswordScreenState
import com.graduate.work.sporterapp.features.login.ui.EmailTextField
import com.graduate.work.sporterapp.utils.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun CustomPreview() {
    AppTheme {
        ForgetPasswordScreen(
            ForgetPasswordScreenState()
        ) {}
    }
}

@Composable
fun ForgetPasswordScreen(
    uiState: ForgetPasswordScreenState,
    event: (ForgetPasswordScreenEvent) -> Unit,
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (
            forgetPasswordRef,
            dontWorryRef,
            emailFieldRef,
            sendCodeButtonRef,
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
        LaunchedEffect(uiState.shouldNavigateToSignIn) {
            if (uiState.shouldNavigateToSignIn) {
                event(ForgetPasswordScreenEvent.NavigateToSignInScreen)
            }
        }
        Text(
            text = stringResource(R.string.forget_password),
            modifier = Modifier
                .constrainAs(forgetPasswordRef) {
                    top.linkTo(parent.top, 64.dp)
                    centerHorizontallyTo(parent)
                },
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.please_enter_your_email_address_and_we_will_send),
            modifier = Modifier
                .constrainAs(dontWorryRef) {
                    top.linkTo(forgetPasswordRef.bottom, 8.dp)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
            style = MaterialTheme.typography.bodyMedium
        )
        EmailTextField(
            emailField = uiState.email,
            onEmailFieldChange = { event(ForgetPasswordScreenEvent.OnEmailChanged(it)) },
            isError = uiState.isEmailError,
            modifier = Modifier.constrainAs(emailFieldRef) {
                top.linkTo(dontWorryRef.bottom, 64.dp)
                bottom.linkTo(sendCodeButtonRef.top, 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.85f)
            },
        )
        Button(
            onClick = { event(ForgetPasswordScreenEvent.OnSendEmailClick) },
            enabled = !uiState.isLoading,
            modifier = Modifier.constrainAs(sendCodeButtonRef) {
                bottom.linkTo(parent.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.9f)
            }) {
            Text(
                text = stringResource(R.string.send_code),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}