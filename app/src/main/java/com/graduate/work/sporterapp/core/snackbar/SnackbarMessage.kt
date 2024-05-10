package com.graduate.work.sporterapp.core.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals

sealed interface SnackbarMessage {
    data class Text(
        val userMessage: UserMessage,
        val actionLabelMessage: UserMessage? = null,
        val withDismissAction: Boolean = false,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val onSnackbarResult: (SnackbarResult) -> Unit = {},
    ) : SnackbarMessage

    data class Visuals(
        val snackbarVisuals: SnackbarVisuals,
        val onSnackbarResult: (SnackbarResult) -> Unit = {},
    ) : SnackbarMessage

    companion object {
        fun from(
            userMessage: UserMessage,
            actionLabelMessage: UserMessage? = null,
            withDismissAction: Boolean = false,
            duration: SnackbarDuration = SnackbarDuration.Short,
            onSnackbarResult: (SnackbarResult) -> Unit = {},
        ) = Text(
            userMessage = userMessage,
            actionLabelMessage = actionLabelMessage,
            withDismissAction = withDismissAction,
            duration = duration,
            onSnackbarResult = onSnackbarResult
        )

        fun from(
            snackbarVisuals: SnackbarVisuals,
            onSnackbarResult: (SnackbarResult) -> Unit,
        ) = Visuals(snackbarVisuals = snackbarVisuals, onSnackbarResult = onSnackbarResult)
    }
}