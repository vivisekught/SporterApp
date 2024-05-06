package com.graduate.work.sporterapp.features.maps.screen

import android.graphics.Color
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.graduate.work.sporterapp.core.ext.getAlphabetLetterByIndex
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.maps.mapbox.domain.MapPoint
import com.graduate.work.sporterapp.features.maps.ui.AddPointAnnotation
import com.graduate.work.sporterapp.features.maps.ui.NewPointView
import com.graduate.work.sporterapp.features.maps.ui.points_list.RouteInfoPanel
import com.graduate.work.sporterapp.features.maps.utils.MapUtils
import com.graduate.work.sporterapp.features.maps.utils.MapUtils.LAYER_ID
import com.graduate.work.sporterapp.features.maps.utils.MapUtils.PITCH_OUTLINE
import com.graduate.work.sporterapp.features.maps.utils.MapUtils.SOURCE_ID
import com.graduate.work.sporterapp.features.maps.vm.MapScreenViewModel
import com.graduate.work.sporterapp.features.maps.vm.MapState
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
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

object HomeScreenConstants {
    const val NEW_POINT_DIALOG_ANIMATION_DURATION = 250
}

@Composable
fun MapScreen() {
    val viewModel: MapScreenViewModel = hiltViewModel<MapScreenViewModel>()
    MapBoxMap(viewModel.state) { event ->
        when (event) {
            MapScreenEvent.SetLastPointAsDestination -> {
                viewModel.setLastSelectedPointAsDestination()
            }

            MapScreenEvent.SetLastSelectedPointAsStart -> {
                viewModel.setLastSelectedPointAsStart()
            }

            is MapScreenEvent.ChangeLastSelectedPoint -> {
                viewModel.changeLastSelectedPoint(event.point)
            }

            MapScreenEvent.DeleteLastSelectedPoint -> {
                viewModel.deleteLastSelectedPoint()
            }

            MapScreenEvent.SetUserLocationAsStart -> {
                viewModel.setUserLocationAsStart()
            }

            is MapScreenEvent.OnPointClicked -> {
                TODO("Not yet implemented")
            }

            is MapScreenEvent.ChangeMapStyle -> {
                viewModel.changeMapStyle(event.style)
            }

            MapScreenEvent.ResetRouteError -> {
                viewModel.resetRouteError()
            }

            is MapScreenEvent.OnPointDeleteClick -> {
                viewModel.deletePoint(event.index)
            }

            is MapScreenEvent.OnPointIndexChanged -> {
                viewModel.changePointByIndex(event.from, event.to)
            }
        }
    }
}

enum class UserPointsListState {
    NOT_OPENED_YET, OPENED, CLOSED;

    fun isOpened() = this == OPENED
}

@OptIn(MapboxExperimental::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MapBoxMap(uiState: MapState, onEvent: (MapScreenEvent) -> Unit) {
    val context = LocalContext.current
    var polylineAnnotationManager: PolylineAnnotationManager? by remember { mutableStateOf(null) }
    var shouldShowLocationDisableDialog by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var pointOnCameraFocus: MapPoint? by remember { mutableStateOf(null) }
    var userPointsListOpenedState by remember { mutableStateOf(UserPointsListState.NOT_OPENED_YET) }
    val newPointSheetState = rememberModalBottomSheetState()

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
            enabled = false
        })
    }

    val scaleBarSetting: ScaleBarSettings by remember {
        mutableStateOf(ScaleBarSettings {
            enabled = true
        })
    }


    LaunchedEffect(Unit) {
        mapViewportState.flyToUserPosition()
        permissionRequest.launch(MapUtils.locationPermissions)
    }

    LaunchedEffect(uiState.route) {
        uiState.route?.let { route ->
            polylineAnnotationManager?.apply {
                deleteAll()
                val lineOptions = PolylineAnnotationOptions()
                    .withPoints(route.points ?: listOf())
                    .withLineColor(Color.RED)
                    .withLineWidth(5.0)
                update(create(lineOptions))
            }
        } ?: polylineAnnotationManager?.deleteAll()
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
            onEvent(MapScreenEvent.ResetRouteError)
        }
    }

    LaunchedEffect(pointOnCameraFocus) {
        pointOnCameraFocus?.let {
            mapViewportState.flyTo(
                cameraOptions {
                    center(it.point)
                }
            )
        }
    }

    LaunchedEffect(uiState.userPoints.size) {
        if (userPointsListOpenedState == UserPointsListState.NOT_OPENED_YET && uiState.userPoints.size > 1) {
            userPointsListOpenedState = UserPointsListState.OPENED
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (pointsListRef, mapRef, newPointDialogRef) = createRefs()
        MapboxMap(
            modifier = Modifier.constrainAs(mapRef) {
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
                onEvent(MapScreenEvent.ChangeLastSelectedPoint(point))
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
                AddPointAnnotation(
                    context, (uiState.numOfAllUserPoints).getAlphabetLetterByIndex(), it
                )
            }
            uiState.userPoints.forEachIndexed { index, mapPoint ->
                AddPointAnnotation(context, mapPoint.name, mapPoint.point) {
                    onEvent(MapScreenEvent.OnPointClicked(mapPoint))
                }
            }
        }
        AnimatedVisibility(
            visible = userPointsListOpenedState.isOpened(),
            modifier = Modifier.constrainAs(pointsListRef) {
                top.linkTo(parent.top)
                width = Dimension.matchParent
            }) {
            RouteInfoPanel(
                points = uiState.userPoints,
                onMove = { from, to ->
                    onEvent(MapScreenEvent.OnPointIndexChanged(from, to))
                },
                onPointClick = {
                    pointOnCameraFocus = uiState.userPoints[it]
                },
                onPointDeleteClick = {
                    onEvent(MapScreenEvent.OnPointDeleteClick(it))
                },
                onListClose = {
                    userPointsListOpenedState = UserPointsListState.CLOSED
                }
            )
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
        Column(
            modifier = Modifier
                .alpha(0.7f)
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
                .constrainAs(createRef()) {
                    bottom.linkTo(parent.bottom)
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
                                onEvent(MapScreenEvent.ChangeMapStyle(it))
                                showDropdownMenu = false
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
        if (uiState.isNewPointDialogOpened) {
            ModalBottomSheet(
                sheetState = newPointSheetState,
                onDismissRequest = {
                    onEvent(MapScreenEvent.DeleteLastSelectedPoint)
                },
            ) {
                NewPointView(
                    latitude = uiState.lastSelectedPoint?.latitude(),
                    longitude = uiState.lastSelectedPoint?.longitude(),
                    isSetAsDestinationBtnVisible = uiState.userPoints.isEmpty() && uiState.userLocationPoint != null,
                    setUserLocationAsStart = { onEvent(MapScreenEvent.SetUserLocationAsStart) },
                    setLastPointAsDestination = { onEvent(MapScreenEvent.SetLastPointAsDestination) },
                    setLastSelectedPointAsStart = { onEvent(MapScreenEvent.SetLastSelectedPointAsStart) }
                )
                Spacer(
                    Modifier.windowInsetsBottomHeight(
                        WindowInsets.navigationBarsIgnoringVisibility
                    )
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

@OptIn(MapboxExperimental::class)
fun MapViewportState.flyToUserPosition() {
    transitionToFollowPuckState(
        followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
            .pitch(null)
            .build(),
    )
}