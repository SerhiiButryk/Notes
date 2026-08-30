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

    android {
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
            val target = libs.versions.javaversion.get()
            jvmTarget.set(JvmTarget.fromTarget(target))
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {

            implementation(projects.localDb)

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

            // Type-safe Rich Text Editor Engine for Compose Multiplatform
            // Github: https://github.com/mkeeda/arranger
            implementation(libs.arranger.richtext.editor)
            implementation(libs.arranger.richtext.editor.material3)
            // Html parsing
            implementation(libs.ksoup)

            // Kotlin coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Image loading
            implementation(libs.coil.compose)

            // Serialization
            implementation(libs.serializationJson)

            // Navigation 3
            implementation(libs.navigation3.ui)

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
        }
        jvmMain.dependencies {
        }
    }
}
