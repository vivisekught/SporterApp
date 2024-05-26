package com.graduate.work.sporterapp.navigation

enum class AuthScreens(val screenName: String) {
    SIGN_UP("signUp"),
    SIGN_IN("signIn"),
    FORGET_PASSWORD("forgetPassword"),
    EMAIL_VERIFICATION("emailVerification")
}

sealed class AppNavigation {

    sealed class Auth(val route: String) : AppNavigation() {
        data object SignInScreen : Auth(AuthScreens.SIGN_IN.screenName)

        data object SignUpScreen : Auth(AuthScreens.SIGN_UP.screenName)

        data object ForgetPasswordScreen : Auth(AuthScreens.FORGET_PASSWORD.screenName)

        data object EmailVerificationScreen : Auth(AuthScreens.EMAIL_VERIFICATION.screenName)
        companion object {
            const val AUTH_FEATURE_SCREEN_ROUTE = "auth"
        }
    }

    sealed class Home(val route: String) : AppNavigation() {
        data object SavedRoutesScreen : Home("savedRoute")
        data object HomeMapScreen : Home("homeMap")
        data object ProfileScreen : Home("profile")
        data object RoutePageScreen : Home("routePage/{routeId}") {
            const val ROUTE_ID_ARG = "routeId"
            fun createRoutePageScreen(routeId: String) = "routePage/$routeId"
        }

        data object TrackScreen : Workout("track/{routeId}") {
            const val ROUTE_ID_ARG = "routeId"
            fun createTrackScreen(routeId: String?) = "track/$routeId"
        }

        companion object {
            const val HOME_FEATURE_SCREEN_ROUTE = "home"
        }
    }

    sealed class Workout(val route: String) : AppNavigation() {
        data object TrackScreen : Workout("track/{routeId}") {
            const val ROUTE_ID_ARG = "routeId"
            fun createTrackScreen(routeId: String?) = "track/$routeId"
        }

        companion object {
            const val TRACK_FEATURE_SCREEN_ROUTE = "track"
        }
    }
}