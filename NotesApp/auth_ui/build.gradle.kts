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

    // A compiler report for composable functions. Can be used for stable/unstable class/func checks.
    // Run 'assemble' task in release mode to find a report.
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}

dependencies {

    implementation(project(":ui"))
    implementation(project(":auth"))
    implementation(project(":net"))
    implementation(project(":interfaces"))
    implementation(project(":notes_ui"))

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
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Android Studio Preview support
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    // UI Tests
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}