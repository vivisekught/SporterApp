package com.graduate.work.sporterapp.core

enum class SortDirection(val value: String) {
    DESC("Descending"),
    ASC("Ascending")
}

enum class SortWorkoutType(val userValue: String, val dbValue: String) {
    DATE("Date", "timeStamp"),
    DISTANCE("Distance", "distance"),
    DURATION("Duration", "duration"),
    CALORIES("Calories", "calories"),
    AVG_SPEED("Avg. Speed", "avgSpeed"),
}

enum class SortRouteType(val userValue: String, val dbValue: String) {
    DATE("Date", "timeStamp"),
    DISTANCE("Distance", "distance"),
    DURATION("Duration", "duration"),
    CLIMB("Climb", "climb"),
}


data class SearchWorkoutParams(
    val searchText: String = "",
    val sortDirection: SortDirection = SortDirection.DESC,
    val sortWorkoutType: SortWorkoutType = SortWorkoutType.DATE
)

data class SearchRouteParams(
    val searchText: String = "",
    val sortDirection: SortDirection = SortDirection.DESC,
    val sortType: SortRouteType = SortRouteType.DATE
)