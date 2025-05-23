plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
}

apply(from = "${rootDir}/gradle_configs/versions.gradle")

android {
    namespace = "com.notes.app"

    defaultConfig {
        applicationId = "com.notes.app"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Attention !!!
            // Obfuscation, shrinking and minification should be enabled only in the top app module.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":auth_ui"))
    implementation(project(":api"))
    implementation(project(":ui"))
    implementation(project(":notes_ui"))
    implementation(project(":ext:services"))

    implementation(libs.bundles.android.core)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.composeui)

    // Compose navigation
    implementation(libs.navigation)
    implementation(libs.serialization)

    // Hilt dependency injection
    // Dependencies notes:
    // 'libs.hilt.android' has Hilt source dependencies like hilt annotations
    // 'libs.hilt.compiler' is needed to presses Hilt source annotations and generate code
    // 'libs.androidx.hilt.navigation.compose' is needed to properly create a view model
    // when compose navigation is used
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Android Studio Preview support
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    // DataStore APIs
    implementation(libs.androidx.datastore.preferences)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Modules under the test
    androidTestImplementation(project(":data"))

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.truth)
    androidTestImplementation(kotlin("reflect"))
}