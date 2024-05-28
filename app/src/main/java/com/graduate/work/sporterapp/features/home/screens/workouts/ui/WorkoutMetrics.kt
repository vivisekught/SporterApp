package com.graduate.work.sporterapp.features.home.screens.workouts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.graduate.work.sporterapp.core.ext.parseSeconds
import com.graduate.work.sporterapp.core.ext.roundTo2
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.Workout

@Composable
fun ColumnScope.WorkoutMetrics(workout: Workout?) {
    workout?.let {
        Row {
            Row(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Icon(
                    Icons.Default.HorizontalRule,
                    contentDescription = "Distance",
                    modifier = Modifier.padding(2.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    it.distance.roundTo2().toString() + " km",
                    textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = "Time",
                    modifier = Modifier.padding(2.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    it.duration.parseSeconds(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = "Calories",
                    modifier = Modifier.padding(2.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    (it.calories.toInt()).toString() + " kcal",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Row {
            Row(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Icon(
                    Icons.Default.Speed,
                    contentDescription = "Avg. Speed",
                    modifier = Modifier.padding(2.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    it.avgSpeed.roundTo2().toString() + " km/h",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Icon(
                    Icons.Default.ArrowOutward,
                    contentDescription = "Climb",
                    modifier = Modifier.padding(2.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    (it.climb.toInt()).toString() + " m",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                Modifier
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = "Descent",
                    modifier = Modifier.padding(2.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    (it.descent.toInt()).toString() + " m",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}