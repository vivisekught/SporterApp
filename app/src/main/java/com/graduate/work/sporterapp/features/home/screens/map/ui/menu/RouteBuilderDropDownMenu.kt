package com.graduate.work.sporterapp.features.home.screens.route_builder.ui.menu

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.vector.ImageVector
import com.graduate.work.sporterapp.R

enum class RouteBuilderDropDownMenu(
    val state: RouteBuilderDropDownMenuState,
    @StringRes val titleId: Int,
    val icon: ImageVector,
) {
    USER_POINTS_LIST(
        RouteBuilderDropDownMenuState.OpenUserPointsList,
        R.string.selected_points,
        Icons.Default.Menu
    ),
    MOVE_TO_CURRENT_GEOMETRY(
        RouteBuilderDropDownMenuState.MoveCameraToCurrentGeometry,
        R.string.move_camera_to_route,
        Icons.Default.Map
    ),
    SET_FIRST_POINT_AS_FINISH(
        RouteBuilderDropDownMenuState.SetFirstPointAsFinish,
        R.string.set_first_point_as_finish,
        Icons.Default.RestartAlt
    ),
    SAVE_ROUTE(RouteBuilderDropDownMenuState.SaveRoute, R.string.save_route, Icons.Default.Save),
}