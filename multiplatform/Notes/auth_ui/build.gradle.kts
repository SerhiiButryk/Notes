import com.android.build.api.dsl.androidLibrary
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
        namespace = "com.notes.auth_ui"

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

    sourceSets {
        androidMain.dependencies {

            implementation(projects.notesUi)
            implementation(projects.ui)

            implementation(libs.bundles.android.core)

            // Compose UI
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.bundles.composeui)

            // Compose preview
            implementation(libs.ui.tooling)
            implementation(libs.ui.tooling.preview)

            // Compose navigation
            implementation(libs.navigation)
            implementation(libs.serializationJson)

            // Adaptive layout
            implementation(libs.androidx.adaptive)
        }
        commonMain.dependencies {

            implementation(projects.api)

            // Kotlin coroutines
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
