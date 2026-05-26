import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    android {
        namespace = "com.notes.ui"

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
            val target = project.properties["jvm.target"].toString()
            jvmTarget.set(JvmTarget.fromTarget(target))
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {

            implementation(libs.bundles.android.core)

            // Compose UI
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.bundles.composeui)

            // Compose preview
            implementation(libs.ui.tooling)
            implementation(libs.ui.tooling.preview)

            // Compose navigation
            implementation(libs.navigation)

            // Adaptive layout
            implementation(libs.androidx.adaptive)

            // Player
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ui.compose)
        }

        commonMain.dependencies {

            implementation(projects.api)

            // Compose
            implementation(libs.runtime)
            implementation(libs.jetbrains.material.icons.extended)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Preview support
            // implementation(libs.jetbrains.ui.tooling.preview)

            // Resources
            implementation(libs.components.resources)

            // Serialization
            implementation(libs.serializationJson)

            // Navigation 3
            implementation(libs.jetbrains.navigation3.ui)
        }
    }
}
