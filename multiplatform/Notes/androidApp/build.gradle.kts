import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.google.gms.google-services") // Google firebase
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    compilerOptions {
        val target = project.properties["jvm.target"].toString()
        jvmTarget = JvmTarget.fromTarget(target)
    }
}

dependencies {

    implementation(projects.api)
    implementation(projects.ui)
    implementation(projects.notesUi)
    implementation(projects.authUi)
    implementation(projects.os)

    // Compose UI
    implementation(project.dependencies.platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)

    // Compose navigation
    implementation(libs.navigation)

    // Initialization during app launch
    implementation(libs.androidx.startup.runtime)

    // Compose
    implementation(libs.runtime)
    implementation(libs.foundation)
    implementation(libs.material3)
    implementation(libs.ui)
    implementation(libs.components.resources)
    // Preview support
    //debugImplementation(libs.jetbrains.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    // Serialization
    implementation(libs.serializationJson)

    // Navigation 3
    implementation(libs.jetbrains.navigation3.ui)
    implementation(libs.androidx.profileinstaller)

    // The consumer app ('androidApp') knows where to get the generated profile from.
    baselineProfile(project(":benchmark"))

    // Perfetto tracing
    implementation(libs.androidx.runtime.tracing)

    // Test modules
    androidTestImplementation(projects.localDb)
    androidTestImplementation(projects.repo)
    // Test deps
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.truth)
    androidTestImplementation(kotlin("reflect"))
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.kotlinx.coroutines.core)
}

android {
    namespace = "com.notes.app"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.notes.app"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
            // When generating baseline profile makes sure that the app is not obfuscated
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        val target = project.properties["jvm.target"].toString()
        sourceCompatibility = JavaVersion.toVersion(target)
        targetCompatibility = JavaVersion.toVersion(target)
    }

    packaging {
        resources {
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            // Exclude debug info from Android apk
            resources.excludes += "DebugProbesKt.bin"
        }
    }

    buildFeatures {
        buildConfig = true
    }
}