import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
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