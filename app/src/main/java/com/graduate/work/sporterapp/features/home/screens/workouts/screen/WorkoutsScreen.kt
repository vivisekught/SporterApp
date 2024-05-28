package com.graduate.work.sporterapp.features.home.screens.workouts.screen

import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.snackbar.LocalSnackbarController
import com.graduate.work.sporterapp.core.snackbar.SnackbarController
import com.graduate.work.sporterapp.features.home.screens.workouts.ui.WorkoutLayout
import com.graduate.work.sporterapp.features.home.screens.workouts.vm.WorkoutsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    snackbarHostState: SnackbarHostState,
    snackbarController: SnackbarController = LocalSnackbarController.current,
    navToWorkoutPage: (String) -> Unit,
) {
    val viewModel = hiltViewModel<WorkoutsScreenViewModel>()
    DisposableEffect(viewModel) {
        viewModel.addListener()
        onDispose { viewModel.removeListener() }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.workouts))
                },
                actions = {

                }
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets)
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(viewModel.workouts.values.toList(), key = { it.workoutId }) { workout ->
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            start = 4.dp,
                            end = 4.dp,
                            top = 12.dp,
                            bottom = 12.dp
                        )
                    )
                    WorkoutLayout(workout = workout) {
                        navToWorkoutPage(workout.workoutId)
                    }
                }
            }
        }
    }
}