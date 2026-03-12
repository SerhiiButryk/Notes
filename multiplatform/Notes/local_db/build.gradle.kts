import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

// TODO: Uncomment when migarting to Room3
// Required by Room 3
//extensions.configure<androidx.room3.gradle.RoomExtension>("room3") {
//    schemaDirectory("$projectDir/schemas")
//}

room {
    schemaDirectory("$projectDir/schemas")
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
            val target = libs.versions.javaversion.get()
            jvmTarget.set(JvmTarget.fromTarget(target))
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            // Room
            implementation(libs.androidx.room.runtime)
        }
        commonMain.dependencies {
            implementation(projects.api)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.serializationJson)
        }
        jvmMain.dependencies {
            // TODO Room database for JVM
            // Room3 drops support for mac x64 in the latest releases,
            // so we must use old Room2 library for now.
            // However, if we don't mac x64 builds, then we can switch to Room3
            implementation(libs.androidx.sqlite.bundled.jvm)
            implementation(libs.androidx.sqlite.jvm)
            implementation(libs.androidx.room.common.jvm)
            implementation(libs.androidx.room.runtime.jvm)
        }
    }
}

dependencies {
    // Applying Room compiler
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}
