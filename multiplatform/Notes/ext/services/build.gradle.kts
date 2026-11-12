import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
}

kotlin {

    android {
        namespace = "com.notes.services"

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

            implementation(projects.repo)

            // Firebase SDK
            implementation(project.dependencies.platform(libs.firebase.bom))
            // Firebase AI Logic
            implementation(libs.firebase.ai)
            // Firebase auth
            implementation(libs.firebase.auth)
            // Firestore APIs
            implementation(libs.firebase.firestore)

            // For Google sing in
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)

            // Google Drive APIs and dependencies
            implementation(libs.play.services.auth)
            implementation(libs.google.api.services.drive)
            implementation(libs.google.http.client.jackson2)
            implementation(libs.google.auth.library.oauth2.http)

            // To fix undef grpc class issue.
            implementation(libs.grpc.okhttp)
            implementation(libs.grpc.android)
            implementation(libs.grpc.stub)
            implementation(libs.grpc.protobuf.lite)
        }
        commonMain.dependencies {
            implementation(projects.api)

            // Firebase SDK for KMP
            implementation(libs.firebase.app )
            implementation(libs.gitlive.firebase.firestore)
            implementation(libs.devfirebase.auth)
        }
        jvmMain.dependencies {
        }
    }
}
