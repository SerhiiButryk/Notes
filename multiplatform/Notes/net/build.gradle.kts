import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
}

kotlin {

    android {
        namespace = "com.notes.net"

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

    sourceSets {
        androidMain.dependencies {
            // OkHttp
            implementation(libs.okhttp)
            // Coroutines
            implementation(libs.kotlinx.coroutines.android)
        }
        commonMain.dependencies {
            implementation(projects.api)
        }
    }
}
