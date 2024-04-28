package com.graduate.work.sporterapp.features.login.screens.email_verification.screen


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.graduate.work.sporterapp.R

@Composable
fun EmailVerificationScreen(email: String, navigateToSignIn: () -> Unit) {

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (
            welcomeTextRef,
            verifyEmailTextRef,
        ) = createRefs()

        Text(
            text = stringResource(R.string.almost_there),
            modifier = Modifier
                .constrainAs(welcomeTextRef) {
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start, 32.dp)
                },
            style = MaterialTheme.typography.headlineSmall
        )
        val annotatedString = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append(stringResource(R.string.please_verify_your_email))
            }
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(email)
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append(stringResource(R.string.to_continue))
            }
        }
        Text(
            text = annotatedString,
            modifier = Modifier
                .constrainAs(verifyEmailTextRef) {
                    top.linkTo(welcomeTextRef.bottom, 8.dp)
                    start.linkTo(parent.start, 32.dp)
                    end.linkTo(parent.end, 32.dp)
                    width = Dimension.fillToConstraints
                },
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = { navigateToSignIn() },
            modifier = Modifier.constrainAs(createRef()) {
                bottom.linkTo(parent.bottom, margin = 32.dp)
                centerHorizontallyTo(parent)
                width = Dimension.percent(0.9f)
            }) {
            Text(
                text = stringResource(R.string.sign_in),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}