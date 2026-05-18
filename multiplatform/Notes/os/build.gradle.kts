plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
}

kotlin {

    android {
        namespace = "com.notes.os"
        compileSdk = libs.versions.android.targetSdk
            .get()
            .toInt()
        minSdk = libs.versions.android.minSdk
            .get()
            .toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api)
                implementation(projects.ui)
                // Kotlin coroutines
                implementation(libs.kotlinx.coroutines.core)
                // For some Compose APIs
                implementation(libs.runtime)
            }
        }

        androidMain {
            dependencies {
                implementation(projects.net)
                implementation(projects.ext.services)
                implementation(projects.localDb)
                implementation(projects.repo)

                // API to initialize components
                implementation(libs.androidx.startup.runtime)

                // DataStore APIs
                implementation(libs.androidx.datastore.preferences)
            }
        }

        jvmMain.dependencies {
            // Tracing APIs
            // https://developer.android.com/topic/performance/tracing/in-process-tracing
            implementation(libs.androidx.tracing.wire)
        }
    }

}