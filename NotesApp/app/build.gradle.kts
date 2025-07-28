plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

apply(from = "${rootDir}/gradle_configs/versions.gradle")

android {
    namespace = "com.notes.app"

    signingConfigs {
        create("release") {
            keyAlias = "key0"
            storePassword = "password"
            keyPassword = "password"
            storeFile = file("test_only.jks")
        }
    }

    defaultConfig {
        applicationId = "com.notes.app"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":auth_ui"))
    implementation(project(":interfaces"))
    implementation(project(":ui"))
    implementation(project(":notes_ui"))

    implementation(libs.bundles.android.core)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.composeui)

    // Compose navigation
    implementation(libs.navigation)
    implementation(libs.serialization)

    // Hilt dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Android Studio Preview support
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    // DataStore APIs
    implementation(libs.androidx.datastore.preferences)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}