package com.graduate.work.sporterapp.features.home.screens.map.home_map.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.maps.mapbox.domain.MapPoint
import com.graduate.work.sporterapp.features.home.ScaffoldScreensState
import com.graduate.work.sporterapp.features.home.screens.map.home_map.vm.HomeMapState
import com.graduate.work.sporterapp.features.home.screens.map.home_map.vm.HomeMapViewModel
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
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings

@Composable
fun HomeMapCompleteScreen(
    onScreenStateChange: (ScaffoldScreensState) -> Unit,
    navigateToRouteBuilder: () -> Unit,
) {
    val viewModel = hiltViewModel<HomeMapViewModel>()
    LaunchedEffect(Unit) {
        onScreenStateChange(ScaffoldScreensState(
            screenNameId = R.string.home,
            fabIcon = {
                FloatingActionButton(onClick = navigateToRouteBuilder) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(
                            R.string.add_route
                        )
                    )
                }
            },
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
    HomeMapScreen(uiState = viewModel.state) {
        when (it) {
            is HomeMapScreenEvent.ChangeLastSelectedPoint -> {
                viewModel.changeLastSelectedPoint(it.point)
            }

            is HomeMapScreenEvent.ChangeMapStyle -> {
                viewModel.changeMapStyle(it.style)
            }

            HomeMapScreenEvent.DeleteLastSelectedPoint -> {
                viewModel.deleteLastSelectedPoint()
            }

            is HomeMapScreenEvent.OnPointClicked -> {
                // TODO
            }
        }
    }
}

sealed class HomeMapScreenEvent {
    data class ChangeLastSelectedPoint(val point: Point) : HomeMapScreenEvent()
    data class OnPointClicked(val mapPoint: MapPoint) : HomeMapScreenEvent()
    data object DeleteLastSelectedPoint : HomeMapScreenEvent()
    data class ChangeMapStyle(val style: MapBoxStyle) : HomeMapScreenEvent()
}

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class, ExperimentalLayoutApi::class)
@Composable
fun HomeMapScreen(uiState: HomeMapState, onEvent: (HomeMapScreenEvent) -> Unit) {
    val context = LocalContext.current
    var polylineAnnotationManager: PolylineAnnotationManager? by remember { mutableStateOf(null) }
    var shouldShowLocationDisableDialog by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    val newPointSheetState = rememberModalBottomSheetState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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

    LaunchedEffect(uiState.listOfUserMarkersState) {
        if (drawerState.isOpen) {
            drawerState.close()
        } else {
            drawerState.open()
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

    ModalNavigationDrawer(
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                for (i in 0..10) {
                    Text(text = "Item $i", modifier = Modifier.padding(8.dp))
                }
            }
        }, drawerState = drawerState
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (mapRef) = createRefs()
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
                    onEvent(HomeMapScreenEvent.ChangeLastSelectedPoint(point))
                    true
                }
            ) {
                MapEffect(Unit) { mapView ->
                    with(mapView) {
                        polylineAnnotationManager = annotations.createPolylineAnnotationManager(
                            annotationConfig = AnnotationConfig(
                                MapUtils.PITCH_OUTLINE,
                                MapUtils.LAYER_ID,
                                MapUtils.SOURCE_ID
                            )
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

                }
                uiState.userPoints.forEachIndexed { index, mapPoint ->

                }
            }
            Column(
                modifier = Modifier
                    .alpha(0.7f)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(8.dp)
                    .constrainAs(createRef()) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
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
                        onEvent(HomeMapScreenEvent.DeleteLastSelectedPoint)
                    },
                ) {
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
}