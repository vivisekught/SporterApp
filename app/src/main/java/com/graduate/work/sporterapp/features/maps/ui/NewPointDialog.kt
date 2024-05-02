package com.graduate.work.sporterapp.features.maps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.graduate.work.sporterapp.R

@Composable
fun NewPointDialog(
    latitude: Double?,
    longitude: Double?,
    isSetAsDestinationBtnVisible: Boolean,
    deleteLastSelectedPoint: () -> Unit,
    setUserLocationAsStart: () -> Unit,
    setLastPointAsDestination: () -> Unit,
    setLastSelectedPointAsStart: () -> Unit,
) {
    Card {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        R.string.new_point, String.format("%.6f", latitude), String.format(
                            "%.6f",
                            longitude
                        )
                    )
                )
                IconButton(
                    onClick = { deleteLastSelectedPoint() },
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_dialog)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = {
                    setLastSelectedPointAsStart()
                }) {
                    Text(text = stringResource(R.string.start_here))
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (isSetAsDestinationBtnVisible) {
                    Button(onClick = { setUserLocationAsStart() }) {
                        Text(text = stringResource(R.string.set_as_destination))
                    }
                } else {
                    Button(onClick = { setLastPointAsDestination() }) {
                        Text(text = stringResource(R.string.add_new_point))
                    }
                }

            }
        }
    }
}