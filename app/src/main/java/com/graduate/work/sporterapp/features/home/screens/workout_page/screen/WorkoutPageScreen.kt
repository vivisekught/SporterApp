package com.graduate.work.sporterapp.features.home.screens.workout_page.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessageHandler
import com.graduate.work.sporterapp.features.home.screens.map.ui.RouteMetrics
import com.graduate.work.sporterapp.features.home.screens.route_builder.utils.MapUtils.transitionToGeometry
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.ui.rememberMarker
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.vm.RoutePageState
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.vm.RoutePageViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
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
    startWorkout: () -> Unit,
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

            RoutePageScreenEvent.ExportAsGpx -> {
                viewModel.exportWorkoutAsGpx()
            }

            RoutePageScreenEvent.ExportAsTcx -> {
                viewModel.exportWorkoutAsTcx()
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

            RoutePageScreenEvent.ExportWorkoutToStrava -> {
                startWorkout()
            }
        }
    }
}

sealed class RoutePageScreenEvent {
    data object Back : RoutePageScreenEvent()
    data object ExportAsGpx : RoutePageScreenEvent()
    data object ExportAsTcx : RoutePageScreenEvent()
    data class ShowMapPoint(val distance: Double) : RoutePageScreenEvent()
    data object HideMapPoint : RoutePageScreenEvent()
    data object DismissSnackbar : RoutePageScreenEvent()
    data object ExportWorkoutToStrava : RoutePageScreenEvent()
    data object Delete : RoutePageScreenEvent()
}

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun RoutePageScreen(
    snackbarHostState: SnackbarHostState,
    uiState: RoutePageState,
    onEvent: (RoutePageScreenEvent) -> Unit,
) {
    var isRouteDialogOpen by rememberSaveable { mutableStateOf(false) }
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
                    IconButton(onClick = { isRouteDialogOpen = true }) {
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
        LaunchedEffect(uiState.routeFileIntent) {
            if (uiState.routeFileIntent != null) {
                context.startActivity(uiState.routeFileIntent)
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
                .verticalScroll(rememberScrollState())
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.route?.routeImgUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Route image",
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator()
                },
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.0F)
            )
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
            Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                onEvent(RoutePageScreenEvent.ExportWorkoutToStrava)
            }) {
                Text(text = stringResource(R.string.start_a_workout_with_a_route))
            }
        }
        if (isRouteDialogOpen) {
            AlertDialog(
                icon = {
                    Icon(Icons.Default.Map, contentDescription = "Export Route")
                },
                title = {
                    Text(text = "Export Route")
                },
                text = {
                    Text(text = "Choose route file format")
                },
                onDismissRequest = {
                    isRouteDialogOpen = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEvent(RoutePageScreenEvent.ExportAsGpx)
                            isRouteDialogOpen = false
                        }
                    ) {
                        Text("Export as GPX")
                    }
                    TextButton(
                        onClick = {
                            onEvent(RoutePageScreenEvent.ExportAsTcx)
                            isRouteDialogOpen = false
                        }
                    ) {
                        Text("Export as TCX")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isRouteDialogOpen = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}