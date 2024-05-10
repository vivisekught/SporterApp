package com.graduate.work.sporterapp.features.home.screens.saved_routes.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route


@Preview(showSystemUi = true)
@Composable
fun CustomPreview() {
    SavedRoute(modifier = Modifier, route = Route())
}

@Composable
fun SavedRoute(modifier: Modifier = Modifier, route: Route) {

}