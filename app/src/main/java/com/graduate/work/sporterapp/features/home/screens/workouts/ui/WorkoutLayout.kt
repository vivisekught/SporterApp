package com.graduate.work.sporterapp.features.home.screens.workouts.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.graduate.work.sporterapp.core.ext.getDateTime
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.Workout

@Composable
fun WorkoutLayout(modifier: Modifier = Modifier, workout: Workout, onClick: () -> Unit) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp),
    ) {
        val (staticMapRef, nameRef, routeMetricsRef) = createRefs()
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(workout.routeImgUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Route image",
            contentScale = ContentScale.Crop,
            loading = {
                CircularProgressIndicator()
            },
            alignment = Alignment.TopCenter,
            modifier = Modifier.constrainAs(staticMapRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                width = Dimension.percent(0.35f)
                height = Dimension.ratio("1:1")
            }
        )
        Text(
            text = workout.name,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 3,
            modifier = Modifier
                .constrainAs(nameRef) {
                    top.linkTo(parent.top)
                    start.linkTo(staticMapRef.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 12.dp)
        )
        Column(modifier = Modifier.constrainAs(routeMetricsRef) {
            top.linkTo(staticMapRef.bottom, margin = 8.dp)
            width = Dimension.fillToConstraints
            centerHorizontallyTo(parent)
        }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                WorkoutMetrics(workout)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = workout.timeStamp.getDateTime(),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}