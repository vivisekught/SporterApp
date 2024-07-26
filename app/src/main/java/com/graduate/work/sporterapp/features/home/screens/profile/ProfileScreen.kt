package com.graduate.work.sporterapp.features.home.screens.profile

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.ImageColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.ui.theme.AppTheme
import com.graduate.work.sporterapp.features.home.screens.profile.vm.ProfileScreenViewModel
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.ui.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import kotlin.random.Random

private val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val bottomAxisValueFormatter = CartesianValueFormatter { x, _, _ ->
    daysOfWeek[x.toInt() % daysOfWeek.size]
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    snackbarHostState: SnackbarHostState,
    onSignOut: () -> Unit
) {
    val vm = hiltViewModel<ProfileScreenViewModel>()
    val producer = remember { CartesianChartModelProducer.build() }
    val context = LocalContext.current
    var routeColor by remember {
        mutableStateOf(Color(0xFF466810))
    }
    var workoutColor by remember {
        mutableStateOf(Color(0xFF466810))
    }
    val controllerRoute = rememberColorPickerController()
    val controllerWorkout = rememberColorPickerController()
    LaunchedEffect(Unit) {
        producer.tryRunTransaction {
            columnSeries {
                series(
                    x = (0..6).toList(),
                    y = buildList {
                        repeat(7) { add(Random.nextInt(0, 30)) }
                    },
                )
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.profile))
                },
                actions = {
                    IconButton(onClick = {
                        vm.signOut()
                        onSignOut()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            var lineWidthSliderValue by remember { mutableFloatStateOf(5f) }
            var cameraZoomSliderValue by remember { mutableFloatStateOf(15f) }
//            SnackbarMessageHandler(
//                snackbarMessage = uiState.snackbarMessage,
//                onDismissSnackbar = { onEvent(RouteBuilderScreenEvent.DismissSnackbar) },
//            )
            Text(text = "Hello, Nikita", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(48.dp))
            Text(text = "Week Statistics", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            CartesianChartHost(
                chart =
                rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
                ),
                modelProducer = producer,
                marker = rememberMarker(),
                runInitialAnimation = true,
                zoomState = rememberVicoZoomState(zoomEnabled = false),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Personal data", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Age",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = "25", onValueChange = {

            })
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Weight, kg",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = "73", onValueChange = {

            })
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Height, cm",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = "181", onValueChange = {

            })
            Spacer(modifier = Modifier.height(48.dp))
            Text(text = "General", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Line width",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = lineWidthSliderValue,
                onValueChange = { lineWidthSliderValue = it },
                valueRange = 1f..12f,
                steps = 11
            )
            Text(
                text = lineWidthSliderValue.toInt().toString(),
                Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Camera zoom",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = cameraZoomSliderValue,
                onValueChange = { cameraZoomSliderValue = it },
                valueRange = 5f..18f,
                steps = 13
            )
            Text(
                text = cameraZoomSliderValue.toInt().toString(),
                Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pick route color",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HsvColorPicker(
                        modifier = Modifier
                            .width(96.dp)
                            .height(96.dp),
                        controller = controllerRoute,
                        initialColor = routeColor,
                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                            Log.d("AAAAAA", "onColorChanged: $colorEnvelope");
                            routeColor = colorEnvelope.color
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        Modifier
                            .width(64.dp)
                            .height(64.dp)
                            .background(routeColor, shape = CircleShape)
                            .align(Alignment.CenterHorizontally),
                    )
                }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pick workout color",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HsvColorPicker(
                        modifier = Modifier
                            .width(96.dp)
                            .height(96.dp),
                        controller = controllerWorkout,
                        initialColor = workoutColor,
                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                            Log.d("AAAAAA", "onColorChanged: $colorEnvelope");
                            workoutColor = colorEnvelope.color
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        Modifier
                            .width(64.dp)
                            .height(64.dp)
                            .background(workoutColor, shape = CircleShape)
                            .align(Alignment.CenterHorizontally),
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Account", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
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
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.strava))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_strava),
                    contentDescription = "Strava",
                    tint = Color.Unspecified,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log in with Strava")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}