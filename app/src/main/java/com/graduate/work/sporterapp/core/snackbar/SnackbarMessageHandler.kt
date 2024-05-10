package com.graduate.work.sporterapp.core.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SnackbarMessageHandler(
    snackbarMessage: SnackbarMessage?,
    onDismissSnackbar: () -> Unit,
    snackbarController: SnackbarController = LocalSnackbarController.current,
) {
    if (snackbarMessage == null) return

    when (snackbarMessage) {
        is SnackbarMessage.Text -> {
            val message = snackbarMessage.userMessage.asString()
            val actionLabel = snackbarMessage.actionLabelMessage?.asString()

            LaunchedEffect(snackbarMessage, onDismissSnackbar) {
                snackbarController.showMessage(
                    message = message,
                    actionLabel = actionLabel,
                    withDismissAction = snackbarMessage.withDismissAction,
                    duration = snackbarMessage.duration,
                    onSnackbarResult = snackbarMessage.onSnackbarResult
                )

                onDismissSnackbar()
            }
        }

        is SnackbarMessage.Visuals -> {
            LaunchedEffect(snackbarMessage, onDismissSnackbar) {
                snackbarController.showMessage(
                    snackbarVisuals = snackbarMessage.snackbarVisuals,
                    onSnackbarResult = snackbarMessage.onSnackbarResult
                )

                onDismissSnackbar()
            }
        }
    }
}