plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

apply(from = "${rootDir}/gradle_configs/versions.gradle")

android {
    namespace = "com.mohamedrejeb.richeditor"

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

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.composeui)

    // Android Studio Preview support
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.ksoup.html)
    implementation(libs.ksoup.entities)
    implementation(libs.markdown)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}