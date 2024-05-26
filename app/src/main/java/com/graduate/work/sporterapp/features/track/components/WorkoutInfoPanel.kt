package com.graduate.work.sporterapp.features.track.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp


@Composable
fun WorkoutInfoPanel(
    modifier: Modifier = Modifier,
    speed: Double,
    distance: Double,
    avgSpeed: Double,
    hours: String,
    minutes: String,
    seconds: String,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        {
            WorkoutPanelDetail(
                modifier = Modifier.weight(1f),
                title = "Speed",
                value = speed.toString(),
                units = "km/h",
                icon = Icons.Default.Speed
            )
            Spacer(modifier = Modifier.width(16.dp))
            WorkoutPanelDetail(
                modifier = Modifier.weight(1f),
                title = "Avg. Speed",
                value = avgSpeed.toString(),
                units = "km/h",
                icon = Icons.Default.ShutterSpeed
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        {
            WorkoutPanelDetail(
                modifier = Modifier.weight(1f),
                title = "Distance",
                value = distance.toString(),
                units = "km",
                icon = Icons.Default.Route
            )
            Spacer(modifier = Modifier.width(16.dp))
            WorkoutPanelDetail(
                modifier = Modifier.weight(1f),
                title = "Duration",
                value = "$hours:$minutes:$seconds",
                units = "",
                icon = Icons.Default.Timer
            )
        }
    }
}

@Composable
fun WorkoutPanelDetail(
    modifier: Modifier,
    title: String,
    value: String,
    units: String,
    icon: ImageVector,
    description: String? = null,
) {
    Row(modifier) {
        Icon(
            icon, contentDescription = description, modifier = Modifier.padding(8.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Thin)) {
                    append(title)
                }
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(value)
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Thin)) {
                    append(" $units")
                }
            },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }
}