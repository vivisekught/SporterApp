package com.graduate.work.sporterapp.features.home.screens.route_builder.ui.menu

sealed class RouteBuilderDropDownMenuState {
    data object OpenUserPointsList : RouteBuilderDropDownMenuState()
    data object MoveCameraToCurrentGeometry : RouteBuilderDropDownMenuState()
    data object SetFirstPointAsFinish : RouteBuilderDropDownMenuState()
    data object SaveRoute : RouteBuilderDropDownMenuState()

}
