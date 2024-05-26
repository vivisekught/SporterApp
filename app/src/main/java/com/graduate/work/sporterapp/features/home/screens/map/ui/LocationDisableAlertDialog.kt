package com.graduate.work.sporterapp.features.home.screens.route_builder.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.graduate.work.sporterapp.R

@Composable
fun LocationDisableAlertDialog(onDialogDismiss: () -> Unit, onRequestLocation: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, stringResource(R.string.info))
        },
        onDismissRequest = { onDialogDismiss() },
        title = {
            Text(text = stringResource(R.string.location_permission_required))
        },
        text = {
            Text(text = stringResource(R.string.to_ensure_the_app_works_properly_enable_location_permission))
        },
        confirmButton = {
            Button(onClick = {
                onRequestLocation()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDialogDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}