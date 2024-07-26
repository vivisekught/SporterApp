package com.graduate.work.sporterapp.features.home.screens.saved_routes.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.SearchRouteParams
import com.graduate.work.sporterapp.core.SortDirection
import com.graduate.work.sporterapp.core.SortRouteType
import com.graduate.work.sporterapp.core.snackbar.LocalSnackbarController
import com.graduate.work.sporterapp.core.snackbar.SnackbarController
import com.graduate.work.sporterapp.features.home.screens.saved_routes.ui.SavedRoute
import com.graduate.work.sporterapp.features.home.screens.saved_routes.vm.SavedRouteScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SavedRoutesScreen(
    snackbarHostState: SnackbarHostState,
    snackbarController: SnackbarController = LocalSnackbarController.current,
    navToRoutePage: (String) -> Unit,
    navToWorkout: () -> Unit,
) {
    val viewModel = hiltViewModel<SavedRouteScreenViewModel>()
    var search by rememberSaveable {
        mutableStateOf("")
    }
    var isActiveSearch by rememberSaveable { mutableStateOf(false) }
    var searchParams by remember { mutableStateOf(SearchRouteParams()) }
    val searchDirectionSheetState = rememberModalBottomSheetState()
    val searchTypeSheetState = rememberModalBottomSheetState()
    var isSearchDirectionWindowOpen by remember { mutableStateOf(false) }
    var isSearchTypeWindowOpen by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navToWorkout() }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Start workout")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.saved_routes))
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
            val (searchBarRef, searchSettingsRef) = createRefs()
            LaunchedEffect(searchParams) {
                viewModel.getRoutesList(searchParams)
            }
            SearchBar(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(searchBarRef) {
                        top.linkTo(parent.top)
                        centerHorizontallyTo(parent)
                        width = Dimension.fillToConstraints
                    },
                query = search,
                onQueryChange = { search = it },
                onSearch = {
                    isActiveSearch = false
                    searchParams = searchParams.copy(searchText = search)
                },
                active = isActiveSearch,
                onActiveChange = {
                    isActiveSearch = it
                },
                placeholder = {
                    Text(text = "Search")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (isActiveSearch) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                isActiveSearch = false; search = "";
                                searchParams = searchParams.copy(
                                    searchText = ""
                                )
                            }
                        )
                    }
                },
                colors = SearchBarDefaults.colors()
            ) {

            }
            Row(modifier = Modifier
                .constrainAs(searchSettingsRef) {
                    top.linkTo(searchBarRef.bottom, margin = 8.dp)
                    centerHorizontallyTo(parent)
                }
            ) {
                Row(Modifier.clickable { isSearchDirectionWindowOpen = true }) {
                    Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sort")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(Modifier.clickable { isSearchTypeWindowOpen = true }) {
                    Icon(Icons.Default.FilterAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Filter")
                }
            }
            when (val response = viewModel.routes) {
                is Response.Failure -> {
                    Log.d("AAAAAA", "SavedRoutesScreen: ${response.message}")
                }

                Response.Loading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .constrainAs(createRef()) {
                                top.linkTo(searchBarRef.bottom)
                                centerHorizontallyTo(parent)
                            })
                }

                is Response.Success -> {
                    response.data?.let {
                        LazyColumn(Modifier.constrainAs(createRef()) {
                            top.linkTo(searchSettingsRef.bottom, 8.dp)
                            height = Dimension.fillToConstraints
                            bottom.linkTo(parent.bottom)
                        }) {
                            items(items = it, key = { it.routeId }) { route ->
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        start = 4.dp,
                                        end = 4.dp,
                                        top = 12.dp,
                                        bottom = 12.dp
                                    )
                                )
                                SavedRoute(route = route) {
                                    navToRoutePage(route.routeId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (isSearchDirectionWindowOpen) {
        ModalBottomSheet(
            sheetState = searchDirectionSheetState,
            onDismissRequest = {
                isSearchDirectionWindowOpen = false
            },
        ) {
            Column {
                SortDirection.entries.forEach { direction ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (searchParams.sortDirection == direction),
                                onClick = {
                                    searchParams =
                                        searchParams.copy(sortDirection = direction)
                                }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (searchParams.sortDirection == direction),
                            onClick = {
                                searchParams =
                                    searchParams.copy(sortDirection = direction)
                            }
                        )
                        Text(
                            text = direction.value,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
            Spacer(
                Modifier.windowInsetsBottomHeight(
                    WindowInsets.navigationBarsIgnoringVisibility
                )
            )
        }
    }
    if (isSearchTypeWindowOpen) {
        ModalBottomSheet(
            sheetState = searchTypeSheetState,
            onDismissRequest = {
                isSearchTypeWindowOpen = false
            },
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                SortRouteType.entries.forEach { type ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (type == searchParams.sortType),
                                onClick = {
                                    searchParams =
                                        searchParams.copy(sortType = type)
                                }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == searchParams.sortType),
                            onClick = {
                                searchParams =
                                    searchParams.copy(sortType = type)
                            }
                        )
                        Text(
                            text = type.userValue,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
            Spacer(
                Modifier.windowInsetsBottomHeight(
                    WindowInsets.navigationBarsIgnoringVisibility
                )
            )
        }
    }
}