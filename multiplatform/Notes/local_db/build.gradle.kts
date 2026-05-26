import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.ksp)
}

kotlin {

    android {
        namespace = "com.notes.db"

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
            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.serializationJson)
        }
        commonMain.dependencies {
            implementation(projects.api)
        }
    }
}

dependencies {
    // Apply to an Android-specific source set if needed
    add("kspAndroid", libs.androidx.room.compiler)
}
