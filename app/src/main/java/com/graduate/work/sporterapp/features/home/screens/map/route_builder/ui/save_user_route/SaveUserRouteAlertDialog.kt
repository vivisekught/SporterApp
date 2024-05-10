package com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.save_user_route

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.graduate.work.sporterapp.R

@Composable
fun SaveUserRouteAlertDialog(
    onCancel: () -> Unit,
    onSave: (name: String, description: String) -> Unit,
) {
    var routeName: String by rememberSaveable { mutableStateOf("Route Name") }
    var routeDescription: String by rememberSaveable { mutableStateOf("Route Description") }
    AlertDialog(
        onDismissRequest = { onCancel() },
        confirmButton = {
            Button(onClick = { onSave(routeName, routeDescription) }) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        icon = {
            Icon(Icons.Default.Save, contentDescription = null)
        },
        title = {
            Text(text = stringResource(id = R.string.save_route))
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                TextField(
                    value = routeName,
                    onValueChange = {
                        routeName = it
                    },
                    singleLine = true,
                    minLines = 1,
                    isError = routeName.isBlank()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = routeDescription,
                    onValueChange = {
                        routeDescription = it
                    }
                )
            }
        })
}