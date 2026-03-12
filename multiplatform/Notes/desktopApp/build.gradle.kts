import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

configurations.all {
    resolutionStrategy {
        // Force protobuf-java down to 3.25.3 to match gRPC and Firestore
        // and fix runtime version errors
        force("com.google.protobuf:protobuf-java:3.25.3")
        force("com.google.protobuf:protobuf-javalite:3.25.3")
    }
}

dependencies {

    implementation(projects.api)
    implementation(projects.ui)
    implementation(projects.notesUi)
    implementation(projects.authUi)
    implementation(projects.os)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    // Compose
    implementation(libs.runtime)
    implementation(libs.foundation)
    implementation(libs.material3)
    implementation(libs.ui)
    implementation(libs.components.resources)
    implementation(libs.jetbrains.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    // Serialization
    implementation(libs.serializationJson)

    // Navigation 3
    implementation(libs.jetbrains.navigation3.ui)

    // Testing modules
    testImplementation(projects.localDb)
    testImplementation(projects.repo)

    // Tests
    testImplementation(libs.kotlin.test)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.coroutines.core)
}

compose.desktop {
    application {
        mainClass = "com.notes.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Deb)
            packageName = "com.notes.app"
            packageVersion = "1.0.0"
        }
    }
}
