import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    android {
        namespace = "com.notes.app"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            val target = project.properties["jvm.target"].toString()
            jvmTarget.set(JvmTarget.fromTarget(target))
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    jvm()

    sourceSets {

        androidMain.dependencies {
        }

        commonMain.dependencies {
            // Compose
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.jetbrains.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
        }
    }
}

dependencies {
}
