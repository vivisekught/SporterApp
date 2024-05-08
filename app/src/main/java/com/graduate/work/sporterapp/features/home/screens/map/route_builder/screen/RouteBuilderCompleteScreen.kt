package com.graduate.work.sporterapp.features.home.screens.map.route_builder.screen

import android.util.Log
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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.atMost
import androidx.hilt.navigation.compose.hiltViewModel
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.ext.getAlphabetLetterByIndex
import com.graduate.work.sporterapp.core.ext.metersToKms
import com.graduate.work.sporterapp.core.ext.parseSeconds
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.features.home.ScaffoldScreensState
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.AddPointAnnotation
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.NewPointView
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.points_list.RouteInfoPanel
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.vm.RouteBuilderScreenViewModel
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.vm.RouteBuilderState
import com.graduate.work.sporterapp.features.home.screens.map.ui.LocationDisableAlertDialog
import com.graduate.work.sporterapp.features.home.screens.map.utils.MapUtils
import com.graduate.work.sporterapp.features.home.screens.map.utils.MapUtils.flyToUserPosition
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotationGroup
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings

@Composable
fun RouteBuilderCompleteScreen(
    onScreenStateChange: (ScaffoldScreensState) -> Unit,
) {
    val viewModel: RouteBuilderScreenViewModel = hiltViewModel<RouteBuilderScreenViewModel>()
    LaunchedEffect(Unit) {
        onScreenStateChange(ScaffoldScreensState(
            screenNameId = R.string.home,
            actions = {
                IconButton(onClick = { Log.d("AAAAAA", "AAAAAA") }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(
                            R.string.more
                        )
                    )
                }
            }
        ))
    }
    RouteBuilderScreen(viewModel.state) { event ->
        when (event) {
            RouteBuilderScreenEvent.SetLastPointAsDestination -> {
                viewModel.setLastSelectedPointAsDestination()
            }

            RouteBuilderScreenEvent.SetLastSelectedPointAsStart -> {
                viewModel.setLastSelectedPointAsStart()
            }

            is RouteBuilderScreenEvent.ChangeLastSelectedPoint -> {
                viewModel.changeLastSelectedPoint(event.point)
            }

            RouteBuilderScreenEvent.DeleteLastSelectedPoint -> {
                viewModel.deleteLastSelectedPoint()
            }

            RouteBuilderScreenEvent.SetUserLocationAsStart -> {
                viewModel.setUserLocationAsStart()
            }

            is RouteBuilderScreenEvent.OnPointClicked -> {
                TODO("Not yet implemented")
            }

            is RouteBuilderScreenEvent.ChangeRouteBuilderStyle -> {
                viewModel.changeMapStyle(event.style)
            }

            RouteBuilderScreenEvent.ResetRouteError -> {
                viewModel.resetRouteError()
            }

            is RouteBuilderScreenEvent.OnPointDeleteClick -> {
                viewModel.deletePoint(event.index)
            }

            is RouteBuilderScreenEvent.OnPointIndexChanged -> {
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
fun RouteBuilderScreen(uiState: RouteBuilderState, onEvent: (RouteBuilderScreenEvent) -> Unit) {
    val context = LocalContext.current
    var shouldShowLocationDisableDialog by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var userPointsListOpenedState by remember { mutableStateOf(UserPointsListState.NOT_OPENED_YET) }
    val newPointSheetState = rememberModalBottomSheetState()

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
                shouldShowLocationDisableDialog = true
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
            onEvent(RouteBuilderScreenEvent.ResetRouteError)
        }
    }

    LaunchedEffect(uiState.userPoints.size) {
        if (userPointsListOpenedState == UserPointsListState.NOT_OPENED_YET && uiState.userPoints.size > 1) {
            userPointsListOpenedState = UserPointsListState.OPENED
        }
    }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (pointsListRef, mapRef, routeInfoRef) = createRefs()
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
            onMapClickListener = {
                Log.d("AAAAAA", "onMapClick")
                true
            },
            onMapLongClickListener = { point ->
                onEvent(RouteBuilderScreenEvent.ChangeLastSelectedPoint(point))
                true
            }
        ) {
            MapEffect(Unit) { mapView ->
                with(mapView) {
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
            uiState.route?.points?.let {
                PolylineAnnotationGroup(
                    annotations = listOf(
                        PolylineAnnotationOptions()
                            .withPoints(points = it)
                            .withLineBorderWidth(1.0)
                            .withLineBorderColor(context.getColor(R.color.yellow))
                            .withLineWidth(5.0)
                            .withLineColor(context.getColor(R.color.light_red))
                    ),
                    lineCap = LineCap.ROUND,
                    onClick = { polylineAnnotation ->
                        Log.d("AAAAAA", "OnPolylineClick")
                        true
                    })
            }
            uiState.lastSelectedPoint?.let {
                AddPointAnnotation(
                    context, (uiState.pointAlphabetIndex).getAlphabetLetterByIndex(), it
                )
            }
            uiState.userPoints.forEach { point ->
                AddPointAnnotation(
                    context,
                    point.name,
                    point.point
                ) {

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
                    onEvent(RouteBuilderScreenEvent.OnPointIndexChanged(from, to))
                },
                onPointClick = {
                    uiState.userPoints[it].let {
                        mapViewportState.flyTo(
                            cameraOptions {
                                center(it.point)
                            }
                        )
                    }
                },
                onPointDeleteClick = {
                    onEvent(RouteBuilderScreenEvent.OnPointDeleteClick(it))
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
        Row(
            modifier = Modifier
                .constrainAs(routeInfoRef) {
                    bottom.linkTo(parent.bottom)
                    width = Dimension.matchParent
                    height = Dimension.preferredWrapContent.atMost(36.dp)
                }
                .background(color = MaterialTheme.colorScheme.surface.copy(0.75f))
                .padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.Center
        ) {
            uiState.route?.let {
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_double_arrow),
                        contentDescription = "Distance",
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        it.distance.metersToKms().toString() + " km",
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_timer),
                        contentDescription = "Time",
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(it.duration.parseSeconds(), textAlign = TextAlign.Center)
                }
            }
        }
        Column(
            modifier = Modifier
                .alpha(0.7f)
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
                .constrainAs(createRef()) {
                    bottom.linkTo(routeInfoRef.top)
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
                                onEvent(RouteBuilderScreenEvent.ChangeRouteBuilderStyle(it))
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
                    onEvent(RouteBuilderScreenEvent.DeleteLastSelectedPoint)
                },
            ) {
                NewPointView(
                    latitude = uiState.lastSelectedPoint?.latitude(),
                    longitude = uiState.lastSelectedPoint?.longitude(),
                    isSetAsDestinationBtnVisible = uiState.userPoints.isEmpty() && uiState.userLocationPoint != null,
                    setUserLocationAsStart = { onEvent(RouteBuilderScreenEvent.SetUserLocationAsStart) },
                    setLastPointAsDestination = { onEvent(RouteBuilderScreenEvent.SetLastPointAsDestination) },
                    setLastSelectedPointAsStart = { onEvent(RouteBuilderScreenEvent.SetLastSelectedPointAsStart) }
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
        LocationDisableAlertDialog(
            onDialogDismiss = { shouldShowLocationDisableDialog = false },
            onRequestLocation = {
                shouldShowLocationDisableDialog = false
                permissionRequest.launch(MapUtils.locationPermissions)
            }
        )
    }
}
