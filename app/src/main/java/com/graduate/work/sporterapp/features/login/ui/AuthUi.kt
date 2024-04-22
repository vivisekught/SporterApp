package com.graduate.work.sporterapp.features.login.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.graduate.work.sporterapp.R

@Composable
fun UserNameTextField(
    modifier: Modifier = Modifier,
    isError: Boolean,
    usernameField: String,
    onUserNameFieldChange: (String) -> Unit,
) {
    TextField(
        value = usernameField,
        onValueChange = {
            onUserNameFieldChange(it)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier,
        supportingText = {
            Text(text = stringResource(R.string.username))
        },
        trailingIcon = {
            Icon(Icons.Default.Person, contentDescription = stringResource(id = R.string.username))
        },
        isError = isError,
        singleLine = true,
        maxLines = 1,
    )
}


@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    isError: Boolean,
    emailField: String,
    onEmailFieldChange: (String) -> Unit,
) {
    TextField(
        value = emailField,
        onValueChange = {
            onEmailFieldChange(it)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = modifier,
        supportingText = {
            Text(text = stringResource(R.string.email))
        },
        trailingIcon = {
            Icon(Icons.Default.Email, contentDescription = stringResource(id = R.string.email))
        },
        isError = isError,
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    isError: Boolean,
    passwordField: String,
    onPasswordFieldChange: (String) -> Unit,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    TextField(
        value = passwordField,
        onValueChange = {
            onPasswordFieldChange(it)
        },
        modifier = modifier,
        supportingText = {
            Text(text = stringResource(R.string.password))
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            val description =
                if (passwordVisible) {
                    stringResource(R.string.hide_password)
                } else stringResource(
                    R.string.show_password
                )
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        },
        isError = isError,
        singleLine = true,
        maxLines = 1
    )
}

@Composable
fun LoginIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    enabled: Boolean,
    description: String,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = { onClick() }
    ) {
        Icon(
            painter = painter,
            contentDescription = description,
            tint = Color.Unspecified
        )
    }
}