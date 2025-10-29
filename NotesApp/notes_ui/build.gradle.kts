plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
}

apply(from = "${rootDir}/gradle_configs/versions.gradle")

android {
    namespace = "com.notes.notes_ui"

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
}

dependencies {

    implementation(project(":ui"))
    implementation(project(":data"))
    implementation(project(":api"))
    implementation(project(":ext:richeditor-compose"))

    implementation(libs.bundles.android.core)
    // For style attributes like attr/colorControlNormal
    implementation(libs.androidx.appcompat)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.composeui)

    // Android Studio Preview support
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

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

    // For currentWindowAdaptiveInfo() & List Detail composable
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.androidx.adaptive.layout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}