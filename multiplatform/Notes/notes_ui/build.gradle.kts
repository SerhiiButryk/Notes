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
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    sourceSets {
        androidMain.dependencies {

            implementation(projects.api)
            implementation(projects.data)
            implementation(projects.ui)
            implementation(projects.composeRichEditor)

            implementation(libs.bundles.android.core)
            // For style attributes like attr/colorControlNormal
            implementation(libs.androidx.appcompat)

            // Compose UI
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.bundles.composeui)

            // Compose navigation
            implementation(libs.navigation)
            implementation(libs.serializationJson)

            // For currentWindowAdaptiveInfo() & List Detail composable
            implementation(libs.androidx.adaptive)
            implementation(libs.androidx.adaptive.navigation)
            implementation(libs.androidx.adaptive.layout)

            // Preview support
            implementation(compose.preview)
        }
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
