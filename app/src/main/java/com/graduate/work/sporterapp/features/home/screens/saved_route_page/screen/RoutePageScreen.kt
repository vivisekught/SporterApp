package com.graduate.work.sporterapp.features.home.screens.saved_route_page.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessageHandler
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.AddCheckPointAnnotation
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.AddPointAnnotation
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.ui.RouteMetrics
import com.graduate.work.sporterapp.features.home.screens.map.utils.MapUtils.transitionToGeometry
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.ui.rememberMarker
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.vm.RoutePageState
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.vm.RoutePageViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
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
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.fullWidth
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.HorizontalLayout
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener

@Composable
fun RoutePageScreenCompleteScreen(
    snackbarHostState: SnackbarHostState,
    routeId: String,
    onBack: () -> Unit,
) {
    val viewModel =
        hiltViewModel<RoutePageViewModel, RoutePageViewModel.RoutePageViewModelFactory> { factory ->
            factory.create(routeId)
        }
    RoutePageScreen(snackbarHostState, viewModel.state) {
        when (it) {
            RoutePageScreenEvent.Back -> {
                onBack()
            }

            RoutePageScreenEvent.Delete -> {

            }

            RoutePageScreenEvent.Export -> {
                viewModel.exportRoute()
            }

            RoutePageScreenEvent.DismissSnackbar -> {
                viewModel.dismissSnackbar()
            }

            RoutePageScreenEvent.HideMapPoint -> {
                viewModel.hideMapPoint()
            }

            is RoutePageScreenEvent.ShowMapPoint -> {
                viewModel.showMapPoint(it.distance)
            }
        }
    }
}

sealed class RoutePageScreenEvent {
    data object Back : RoutePageScreenEvent()
    data object Export : RoutePageScreenEvent()
    data class ShowMapPoint(val distance: Double) : RoutePageScreenEvent()
    data object HideMapPoint : RoutePageScreenEvent()
    data object DismissSnackbar : RoutePageScreenEvent()
    data object Delete : RoutePageScreenEvent()
}


//@Preview(showSystemUi = true)
//@Composable
//fun CustomPreview() {
//    AppTheme {
//        RoutePageScreen(RoutePageState(Route())) {
//
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun RoutePageScreen(
    snackbarHostState: SnackbarHostState,
    uiState: RoutePageState,
    onEvent: (RoutePageScreenEvent) -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = uiState.route?.name ?: "Route")
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(RoutePageScreenEvent.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(RoutePageScreenEvent.Export) }) {
                        Icon(
                            imageVector = Icons.Default.IosShare,
                            contentDescription = stringResource(R.string.export_gpx),
                        )
                    }
                }
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
    ) { innerPadding ->
        val context = LocalContext.current
        val modelProducer = remember { CartesianChartModelProducer.build() }
        val marker = rememberMarker()
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
                enabled = false
            })
        }

        val attributionSettings: AttributionSettings by remember {
            mutableStateOf(AttributionSettings {
                enabled = false
            })
        }

        val scaleBarSetting: ScaleBarSettings by remember {
            mutableStateOf(ScaleBarSettings {
                enabled = false
            })
        }
        LaunchedEffect(uiState.gpxFileIntent) {
            if (uiState.gpxFileIntent != null) {
                context.startActivity(uiState.gpxFileIntent)
            }
        }
        LaunchedEffect(uiState.route) {
            uiState.route?.points?.let { points ->
                mapViewportState.transitionToGeometry(points, padding = 50.0)
            }
        }
        LaunchedEffect(uiState.elevationProfile) {
            if (uiState.elevationProfile?.x != null && uiState.elevationProfile.y != null) {
                modelProducer.tryRunTransaction {
                    lineSeries {
                        series(
                            x = uiState.elevationProfile.x,
                            y = uiState.elevationProfile.y
                        )
                    }
                }
            }
        }
        SnackbarMessageHandler(
            snackbarMessage = uiState.snackbarMessage,
            onDismissSnackbar = { onEvent(RoutePageScreenEvent.DismissSnackbar) },
        )
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
            ) {
                MapboxMap(
                    mapInitOptionsFactory = { context ->
                        MapInitOptions(
                            context = context,
                            styleUri = MapBoxStyle.STREET.style,
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    compassSettings = compassSettings,
                    mapViewportState = mapViewportState,
                    scaleBarSettings = scaleBarSetting,
                    gesturesSettings = mapBoxUiSettings,
                    attributionSettings = attributionSettings,
                ) {
                    MapEffect(Unit) { mapView ->
                        with(mapView) {
                            location.locationPuck = createDefault2DPuck(withBearing = true)
                            location.enabled = true
                        }
                    }
                    uiState.route?.points?.let {
                        PolylineAnnotationGroup(
                            annotations = listOf(
                                PolylineAnnotationOptions()
                                    .withPoints(points = it)
                                    .withLineBorderWidth(2.0)
                                    .withLineBorderColor(context.getColor(R.color.light_blue))
                                    .withLineWidth(7.0)
                                    .withLineColor(context.getColor(R.color.dark_blue))
                            ),
                            lineCap = LineCap.ROUND
                        )
                        AddCheckPointAnnotation(context, text = "A", point = it.first())
                        AddCheckPointAnnotation(context, text = "B", point = it.last())
                    }
                    uiState.mapPoint?.let {
                        AddPointAnnotation(context, point = it)
                    }
                }
                IconButton(
                    onClick = {
                        uiState.route?.points?.let { points ->
                            mapViewportState.transitionToGeometry(points, padding = 50.0)
                        }
                    }, modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(0.7f),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = stringResource(id = R.string.move_camera_to_route)
                    )
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = uiState.route?.name ?: "Route",
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 5,
                    modifier = Modifier.padding(start = 4.dp, top = 16.dp)
                )
                Text(
                    text = uiState.route?.description ?: "Description",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 10,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    RouteMetrics(uiState.route)
                }
                Spacer(modifier = Modifier.height(16.dp))
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(
                            itemPlacer = remember {
                                AxisItemPlacer.Horizontal.default(
                                    spacing = 5,
                                    shiftExtremeTicks = false,
                                    addExtremeLabelPadding = false,
                                )
                            },
                        ),
                    ),
                    modelProducer = modelProducer,
                    modifier = Modifier.fillMaxWidth(),
                    marker = marker,
                    markerVisibilityListener = remember {
                        object : CartesianMarkerVisibilityListener {
                            override fun onHidden(marker: CartesianMarker) {
                                onEvent(RoutePageScreenEvent.HideMapPoint)
                            }

                            override fun onShown(
                                marker: CartesianMarker,
                                targets: List<CartesianMarker.Target>,
                            ) {
                                onEvent(RoutePageScreenEvent.ShowMapPoint(targets.first().x.toDouble()))
                            }
                        }
                    },
                    runInitialAnimation = true,
                    horizontalLayout = HorizontalLayout.fullWidth(),
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = {
                    val intentUri = Uri.parse("https://www.strava.com/oauth/mobile/authorize")
                        .buildUpon()
                        .appendQueryParameter("client_id", "126680")
                        .appendQueryParameter("redirect_uri", "strava://redirect")
                        .appendQueryParameter("response_type", "code")
                        .appendQueryParameter("approval_prompt", "auto")
                        .appendQueryParameter("scope", "activity:write,read")
                        .build()

                    val intent = Intent(Intent.ACTION_VIEW, intentUri)
                    context.startActivity(intent)
                }) {
                    Text(text = "Share route on Strava")
                }
            }
        }
    }
}