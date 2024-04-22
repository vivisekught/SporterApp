buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        dependencies {
            classpath(libs.gradle)
        }
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.googleHiltAndroid) apply false
    alias(libs.plugins.gmsGoogleServices) apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}