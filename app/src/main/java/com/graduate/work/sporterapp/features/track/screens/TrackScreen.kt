package com.graduate.work.sporterapp.features.track.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.ext.roundTo2
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.data.maps.location.TrackingUserWorkoutService
import com.graduate.work.sporterapp.features.home.screens.route_builder.utils.MapUtils
import com.graduate.work.sporterapp.features.home.screens.route_builder.utils.MapUtils.flyToUserPosition
import com.graduate.work.sporterapp.features.track.components.WorkoutInfoPanel
import com.graduate.work.sporterapp.features.track.vm.TrackScreenUiState
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotationGroup
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings

sealed class TrackScreenEvent {
    data object OnBack : TrackScreenEvent()
    data class ChangeMapStyle(val style: MapBoxStyle) : TrackScreenEvent()
    data class StopWorkout(val name: String) : TrackScreenEvent()
}

@OptIn(MapboxExperimental::class)
@Composable
fun TrackScreen(
    trackingService: TrackingUserWorkoutService,
    uiState: TrackScreenUiState,
    onEvent: (TrackScreenEvent) -> Unit,
) {
    val context = LocalContext.current
    var showDropdownStyleMenu by remember { mutableStateOf(false) }
    var isStopWorkoutDialogOpen by remember { mutableStateOf(false) }
    var workoutName by remember { mutableStateOf("") }
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(0.0, 0.0))
            zoom(0.0)
            pitch(0.0)
        }
        MapAnimationOptions.mapAnimationOptions {
            duration(3000)
        }
    }

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (!permissions.values.all { it }) {
                onEvent(TrackScreenEvent.OnBack)
            } else {
                mapViewportState.flyToUserPosition()
            }
        }
    )

    val mapBoxUiSettings: GesturesSettings by remember {
        mutableStateOf(GesturesSettings {
            rotateEnabled = true
            pinchToZoomEnabled = true
            pitchEnabled = true
        })
    }

    val compassSettings: CompassSettings by remember {
        mutableStateOf(CompassSettings {
            enabled = true
        })
    }

    val attributionSettings: AttributionSettings by remember {
        mutableStateOf(AttributionSettings {
            enabled = false
        })
    }

    val scaleBarSetting: ScaleBarSettings by remember {
        mutableStateOf(ScaleBarSettings {
            enabled = true
        })
    }
    LaunchedEffect(Unit) {
        permissionRequest.launch(MapUtils.locationPermissions)
    }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (mapRef, workoutInfoRef) = createRefs()
        WorkoutInfoPanel(
            modifier = Modifier.constrainAs(workoutInfoRef) {
                top.linkTo(parent.top)
                width = Dimension.matchParent
                height = Dimension.fillToConstraints
            },
            speed = trackingService.speed,
            distance = trackingService.distance.roundTo2(),
            avgSpeed = trackingService.avgSpeed,
            hours = trackingService.hours,
            minutes = trackingService.minutes,
            seconds = trackingService.seconds,
        )
        MapboxMap(
            modifier = Modifier.constrainAs(mapRef) {
                width = Dimension.matchParent
                height = Dimension.fillToConstraints
                top.linkTo(workoutInfoRef.bottom)
                bottom.linkTo(parent.bottom)
            },
            mapInitOptionsFactory = { context ->
                MapInitOptions(
                    context = context,
                    styleUri = MapBoxStyle.STREET.style,
                )
            },
            compassSettings = compassSettings,
            mapViewportState = mapViewportState,
            scaleBarSettings = scaleBarSetting,
            gesturesSettings = mapBoxUiSettings,
            attributionSettings = attributionSettings,
        ) {
            MapEffect(Unit) { mapView ->
                with(mapView) {
                    location.locationPuck = createDefault2DPuck(withBearing = true)
                    location.pulsingEnabled = false
                    location.enabled = true
                }
            }
            MapEffect(uiState.currentMapStyle) { mapView ->
                with(mapView) {
                    if (mapboxMap.isValid()) {
                        mapboxMap.loadStyle(uiState.currentMapStyle.style)
                    }
                }
            }
            trackingService.preparedRoute?.points?.let {
                PolylineAnnotationGroup(
                    annotations = listOf(
                        PolylineAnnotationOptions()
                            .withPoints(points = it)
                            .withLineBorderWidth(1.0)
                            .withLineBorderColor(context.getColor(R.color.light_blue))
                            .withLineWidth(3.0)
                            .withLineColor(context.getColor(R.color.dark_blue))
                    ),
                    annotationConfig = AnnotationConfig(
                        belowLayerId = "track-layer",
                        layerId = "prepared_track-layer",
                    ),
                    lineCap = LineCap.ROUND,
                )
            }
            PolylineAnnotationGroup(
                annotations = listOf(
                    PolylineAnnotationOptions()
                        .withPoints(points = trackingService.points.map { it.point })
                        .withLineBorderWidth(2.0)
                        .withLineBorderColor(context.getColor(R.color.yellow))
                        .withLineWidth(7.0)
                        .withLineColor(context.getColor(R.color.light_red))
                ),
                annotationConfig = AnnotationConfig(
                    layerId = "track-layer",
                ),
                lineCap = LineCap.ROUND,
            )
        }
        Row(
            Modifier
                .constrainAs(createRef()) {
                    start.linkTo(parent.start, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }
                .background(MaterialTheme.colorScheme.surface.copy(0.5f))) {
            IconButton(onClick = {
                with(context) {
                    val intent = if (trackingService.isWorkoutStarted) {
                        TrackingUserWorkoutService.createToggleIntent(
                            trackingService.isTracking,
                            this
                        )
                    } else {
                        TrackingUserWorkoutService.createStartIntent(this)
                    }
                    startService(intent)
                }
            }) {
                Icon(
                    if (trackingService.isTracking) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Toggle tracking"
                )
            }
            IconButton(onClick = {
                isStopWorkoutDialogOpen = true
            }) {
                Icon(
                    Icons.Default.Stop,
                    contentDescription = "Stop tracking"
                )
            }
        }
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(0.7f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp)
                .constrainAs(createRef()) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                },
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box {
                IconButton(onClick = { showDropdownStyleMenu = true }) {
                    Icon(
                        Icons.Default.Layers,
                        contentDescription = stringResource(R.string.map_layers)
                    )
                }
                DropdownMenu(
                    expanded = showDropdownStyleMenu,
                    onDismissRequest = { showDropdownStyleMenu = false }) {
                    MapBoxStyle.entries.forEach {
                        DropdownMenuItem(
                            modifier = Modifier.padding(4.dp),
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = it.resId),
                                        contentDescription = stringResource(id = it.textId)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = stringResource(id = it.textId))
                                }
                            }, onClick = {
                                onEvent(TrackScreenEvent.ChangeMapStyle(it))
                                showDropdownStyleMenu = false
                            })
                        HorizontalDivider()
                    }
                }
            }
            IconButton(onClick = {
                mapViewportState.flyToUserPosition()
            }) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.my_location)
                )
            }
        }
    }
    if (isStopWorkoutDialogOpen) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Warning, contentDescription = "Warning Icon")
            },
            title = {
                Text(text = "Stop workout?")
            },
            text = {
                TextField(
                    modifier = Modifier
                        .padding(16.dp),
                    value = workoutName,
                    onValueChange = {
                        workoutName = it
                    },
                    singleLine = true,
                    minLines = 1,
                    isError = workoutName.isBlank()
                )
            },
            onDismissRequest = {
                isStopWorkoutDialogOpen = false
            },
            confirmButton = {
                TextButton(
                    enabled = workoutName.isNotBlank(),
                    onClick = {
                        onEvent(TrackScreenEvent.StopWorkout(workoutName))
                        with(context) {
                            val intent = TrackingUserWorkoutService.createStopIntent(this)
                            startService(intent)
                        }
                        isStopWorkoutDialogOpen = false
                    }
                ) {
                    Text("Save workout")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isStopWorkoutDialogOpen = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}