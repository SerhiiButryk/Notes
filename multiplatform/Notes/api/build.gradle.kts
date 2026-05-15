import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    id("kotlin-parcelize") // For 'kotlinx.parcelize.Parcelize'
    alias(libs.plugins.composeCompiler) // For 'androidx.compose.runtime.Immutable'
}

kotlin {

    androidLibrary {
        namespace = "com.api"

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
        commonMain.dependencies {
            implementation(libs.serializationJson)
            implementation(libs.kotlinx.coroutines.core)

            // Compose

            // For 'androidx.compose.runtime.Immutable'
            implementation(libs.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
