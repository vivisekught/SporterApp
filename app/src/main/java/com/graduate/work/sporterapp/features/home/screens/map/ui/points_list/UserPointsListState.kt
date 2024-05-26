package com.graduate.work.sporterapp.features.home.screens.route_builder.ui.points_list

enum class UserPointsListState {
    NOT_OPENED_YET, OPENED, CLOSED;

    fun isOpened() = this == OPENED
}