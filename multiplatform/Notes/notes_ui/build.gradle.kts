import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize") // For 'kotlinx.parcelize.Parcelize'
}

kotlin {

    androidLibrary {
        namespace = "com.notes.notes_ui"

        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {

            implementation(projects.data)

            implementation(libs.bundles.android.core)
            // For style attributes like attr/colorControlNormal
            implementation(libs.androidx.appcompat)

            // Compose UI
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.bundles.composeui)

            // Compose navigation
            implementation(libs.navigation)

            // For currentWindowAdaptiveInfo() & List Detail composable
            implementation(libs.androidx.adaptive)
            implementation(libs.androidx.adaptive.navigation)
            implementation(libs.androidx.adaptive.layout)

            // Android coroutines
            implementation(libs.kotlinx.coroutines.android)

            // File storage
            implementation(libs.androidx.documentfile)

            // Compose tracing
            // implementation(libs.androidx.compose.runtime.tracing)
        }
        commonMain.dependencies {

            implementation(projects.api)
            implementation(projects.ui)
            implementation(projects.composeRichEditor)

            // Kotlin coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Compose
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.jetbrains.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.material.icons.extended)
            implementation(libs.components.splitpane)
            implementation(libs.jetbrains.ui.tooling.preview)

            // Serialization
            implementation(libs.serializationJson)
            // Navigation 3
            implementation(libs.jetbrains.navigation3.ui)
            // View Model
            implementation(libs.androidx.lifecycle.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
