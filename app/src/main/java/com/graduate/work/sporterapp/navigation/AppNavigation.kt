package com.graduate.work.sporterapp.navigation

enum class AuthScreens(val screenName: String) {
    SIGN_UP("signUp"),
    SIGN_IN("signIn"),
    FORGOT_PASSWORD("forgotPassword"),
    EMAIL_VERIFICATION("emailVerification")
}

sealed class AppNavigation {

    sealed class Auth(val route: String) : AppNavigation() {
        data object SignInScreen : Auth(AuthScreens.SIGN_IN.screenName)

        data object SignUpScreen : Auth(AuthScreens.SIGN_UP.screenName)

        data object ForgotPasswordScreen : Auth(AuthScreens.FORGOT_PASSWORD.screenName)

        data object EmailVerificationScreen : Auth(AuthScreens.EMAIL_VERIFICATION.screenName)
        companion object {
            const val AUTH_FEATURE_SCREEN_ROUTE = "auth"
        }
    }
}