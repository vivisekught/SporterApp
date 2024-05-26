import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
}

val gradleProperties = Properties()
val gradlePropertiesFile: File = rootProject.file("local.properties")
if (gradlePropertiesFile.exists()) {
    gradleProperties.load(gradlePropertiesFile.inputStream())
}


android {
    namespace = "com.graduate.work.sporterapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.graduate.work.sporterapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "WEB_API_KEY",
            "\"${gradleProperties.getProperty("WEB_API_KEY")}\""
        )

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.lifecycle.runtime.compose)
    kapt(libs.hilt.android.compiler)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)

    // google auth
    implementation(libs.androidx.credentials.v122)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.play.services.auth)
    implementation(libs.googleid)


    // mapbox
    implementation(libs.android.maps)
    implementation(libs.mapbox.sdk.services)
    implementation(libs.maps.compose)
    implementation(libs.mapbox.sdk.turf)

    // splash screen
    implementation(libs.androidx.core.splashscreen)


    // google location services
    implementation(libs.play.services.location)

    implementation(libs.android.gpx.parser)

    // koil
    implementation(libs.coil.compose)

    // Charts
    implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.19")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.19")
}