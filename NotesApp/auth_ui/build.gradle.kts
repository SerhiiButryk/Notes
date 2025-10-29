plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

apply(from = "${rootDir}/gradle_configs/versions.gradle")

android {
    namespace = "com.notes.auth_ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    // A compiler report for composable functions. Can be used for stable/unstable class/func checks.
    // Run 'assemble' task in release mode to find a report.
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}

dependencies {

    implementation(project(":ui"))
    implementation(project(":api"))
    implementation(project(":notes_ui"))
    implementation(project(":ext:services"))

    implementation(libs.bundles.android.core)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.composeui)

    // Compose view model support
    implementation(libs.androidx.lifecycle.viewmodel.compose)

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

    // Adaptive layout
    implementation(libs.androidx.adaptive)

    // UI Tests
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}