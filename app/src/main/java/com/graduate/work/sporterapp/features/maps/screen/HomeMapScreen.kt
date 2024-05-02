package com.graduate.work.sporterapp.features.maps.screen

import android.graphics.Color
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.features.maps.ui.AddPointAnnotation
import com.graduate.work.sporterapp.features.maps.ui.NewPointDialog
import com.graduate.work.sporterapp.features.maps.utils.MapUtils
import com.graduate.work.sporterapp.features.maps.utils.MapUtils.LAYER_ID
import com.graduate.work.sporterapp.features.maps.utils.MapUtils.PITCH_OUTLINE
import com.graduate.work.sporterapp.features.maps.utils.MapUtils.SOURCE_ID
import com.graduate.work.sporterapp.features.maps.vm.HomeMapState
import com.graduate.work.sporterapp.features.maps.vm.HomeMapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions

@Composable
fun HomeMapScreen() {
    val viewModel: HomeMapViewModel = hiltViewModel<HomeMapViewModel>()
    MapBoxMap(viewModel.state) { event ->
        when (event) {
            HomeMapScreenEvent.SetLastPointAsDestination -> {
                viewModel.setLastSelectedPointAsDestination()
            }

            HomeMapScreenEvent.SetLastSelectedPointAsStart -> {
                viewModel.setLastSelectedPointAsStart()
            }

            is HomeMapScreenEvent.ChangeLastSelectedPoint -> {
                viewModel.changeLastSelectedPoint(event.point)
            }

            HomeMapScreenEvent.DeleteLastSelectedPoint -> {
                viewModel.deleteLastSelectedPoint()
            }

            HomeMapScreenEvent.SetUserLocationAsStart -> {
                viewModel.setUserLocationAsStart()
            }

            is HomeMapScreenEvent.OnPointClicked -> {
                TODO("Not yet implemented")
            }

            is HomeMapScreenEvent.ChangeMapStyle -> {
                viewModel.changeMapStyle(event.style)
            }

            HomeMapScreenEvent.ResetRouteError -> {
                viewModel.resetRouteError()
            }
        }
    }
}

@OptIn(MapboxExperimental::class)
@Composable
fun MapBoxMap(uiState: HomeMapState, onEvent: (HomeMapScreenEvent) -> Unit) {
    val context = LocalContext.current
    var shouldShowUserLocation by remember { mutableStateOf(true) }
    var shouldShowLocationDisableDialog by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var polylineAnnotationManager: PolylineAnnotationManager? by remember { mutableStateOf(null) }

    val permissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (!permissions.values.all { it }) {
                shouldShowLocationDisableDialog = true
            }
        }
    )

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
            enabled = true
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

    LaunchedEffect(uiState.route) {
        uiState.route?.let { route ->
            polylineAnnotationManager?.apply {
                val lineOptions = PolylineAnnotationOptions()
                    .withPoints(route)
                    .withLineColor(Color.RED)
                    .withLineWidth(5.0)
                update(create(lineOptions))
            }
        }
    }

    LaunchedEffect(shouldShowUserLocation) {
        if (shouldShowUserLocation) {
            mapViewportState.transitionToFollowPuckState(
                followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                    .pitch(null)
                    .build(),
            ) {
                shouldShowUserLocation = false
            }
        }
    }

    LaunchedEffect(uiState.lastSelectedPoint) {
        uiState.lastSelectedPoint?.let { point ->
            mapViewportState.flyTo(
                cameraOptions {
                    center(point)
                },
            )
        }
    }

    LaunchedEffect(uiState.routeNotFoundError) {
        if (uiState.routeNotFoundError) {
            Toast.makeText(context, "Route not found", Toast.LENGTH_SHORT).show()
            onEvent(HomeMapScreenEvent.ResetRouteError)
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.constrainAs(createRef()) {
                width = Dimension.matchParent
                height = Dimension.matchParent
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
            onMapLongClickListener = { point ->
                onEvent(HomeMapScreenEvent.ChangeLastSelectedPoint(point))
                true
            }
        ) {
            MapEffect(Unit) { mapView ->
                with(mapView) {
                    polylineAnnotationManager = annotations.createPolylineAnnotationManager(
                        annotationConfig = AnnotationConfig(PITCH_OUTLINE, LAYER_ID, SOURCE_ID)
                    )
                    location.locationPuck = createDefault2DPuck(withBearing = true)
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
            uiState.lastSelectedPoint?.let {
                AddPointAnnotation(context, (uiState.userPoints.size + 1).toString(), it)
            }
            uiState.userPoints.forEachIndexed { index, it ->
                AddPointAnnotation(context, (index + 1).toString(), it) {
                    onEvent(HomeMapScreenEvent.OnPointClicked(it))
                }
            }
        }
        AnimatedVisibility(
            visible = uiState.isRouteLoading,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(createRef()) {
                    centerHorizontallyTo(parent)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                },
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {
            LinearProgressIndicator()
        }
        AnimatedVisibility(
            visible = uiState.lastSelectedPoint != null,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(createRef()) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }
            )
        ) {
            NewPointDialog(
                latitude = uiState.lastSelectedPoint?.latitude(),
                longitude = uiState.lastSelectedPoint?.longitude(),
                isSetAsDestinationBtnVisible = uiState.userPoints.isEmpty() && uiState.userLocationPoint != null,
                deleteLastSelectedPoint = { onEvent(HomeMapScreenEvent.DeleteLastSelectedPoint) },
                setUserLocationAsStart = { onEvent(HomeMapScreenEvent.SetUserLocationAsStart) },
                setLastPointAsDestination = { onEvent(HomeMapScreenEvent.SetLastPointAsDestination) },
                setLastSelectedPointAsStart = { onEvent(HomeMapScreenEvent.SetLastSelectedPointAsStart) }
            )
        }
        Column(
            modifier = Modifier
                .alpha(0.7f)
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
                .constrainAs(createRef()) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box {
                IconButton(onClick = { showDropdownMenu = true }) {
                    Icon(
                        Icons.Default.Layers,
                        contentDescription = stringResource(R.string.map_layers)
                    )
                }
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }) {
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
                                onEvent(HomeMapScreenEvent.ChangeMapStyle(it))
                                showDropdownMenu = false
                            })
                        HorizontalDivider()
                    }
                }
            }

            IconButton(onClick = { shouldShowUserLocation = true }) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.my_location)
                )
            }
        }
    }

    if (shouldShowLocationDisableDialog) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Info, stringResource(R.string.info))
            },
            onDismissRequest = { shouldShowLocationDisableDialog = false },
            title = {
                Text(text = stringResource(R.string.location_permission_required))
            },
            text = {
                Text(text = stringResource(R.string.to_ensure_the_app_works_properly_enable_location_permission))
            },
            confirmButton = {
                Button(onClick = {
                    shouldShowLocationDisableDialog = false
                    permissionRequest.launch(MapUtils.locationPermissions)
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { shouldShowLocationDisableDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}