import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML
import org.jlleitschuh.gradle.ktlint.tasks.GenerateReportsTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.androidLint) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false // Google firebase
    alias(libs.plugins.ktlint) apply false // Static analysis
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.androidx.room) apply false
}

subprojects {

    // Apply static analysis tools to all subprojects
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    // Configure compose compiler reports dynamically
    // Run this task to see a report:
    // '$ ./gradlew clean'
    // '$ ./gradlew :composeApp:compileDebugKotlinAndroid --rerun-tasks'
    plugins.withId("org.jetbrains.kotlin.plugin.compose") {

        extensions.configure<ComposeCompilerGradlePluginExtension>(
            "composeCompiler"
        ) {

            reportsDestination.set(
                rootProject.layout.buildDirectory.dir("compose_reports/${project.name}")
            )

            metricsDestination.set(
                rootProject.layout.buildDirectory.dir("compose_compiler/${project.name}/")
            )

            // TODO Add stability configuration if needed
//            stabilityConfigurationFiles.add(
//                rootProject.layout.projectDirectory.file(
//                    "compose_compiler/stability.conf"
//                )
//            )
        }
    }

    // Configure Kotlin lint dynamically
    // Run this task to see a report:
    // '$ ./gradlew ktlintchec'
    // '$ ./gradlew ktlintFormat' - to fix
    plugins.withId("org.jlleitschuh.gradle.ktlint") {

        extensions.configure<KtlintExtension> {
            // Enable or disable specific settings
            debug.set(true)
            android.set(false)
            outputToConsole.set(false)
            verbose.set(true)
            ignoreFailures.set(true)
            reporters {
                reporter(HTML)
            }
        }

        tasks.withType<GenerateReportsTask> {
            val projectName = project.name
            // Custom output locations
            reportsOutputDirectory =
                rootProject.layout.buildDirectory.dir("reports/ktlint/${projectName}")
        }
    }

    // Configure detekt dynamically
    // Run this task to see a report:
    // '$ ./gradlew detekt'
    plugins.withId("io.gitlab.arturbosch.detekt") {

        configure<DetektExtension> {
            // Enable or disable specific settings
            buildUponDefaultConfig = true
            allRules = false
            debug = true
            ignoreFailures = true
            // Enables parallel analysis (false by default)
            parallel = true
            // Specify the base path for file paths in the formatted reports.
            // If not set, all file paths reported will be absolute file path.
            basePath = projectDir.path
        }

        tasks.withType<Detekt>().configureEach {
            reports {
                // Enable or disable specific formats
                html.required.set(true)
                xml.required.set(false)
                txt.required.set(false)
                sarif.required.set(false)

                val projectName = project.name
                val outputFile = rootProject.layout.buildDirectory
                    .dir("reports/detekt/${projectName}/detekt-report.html")
                    .get().asFile

                // Custom output locations
                html.outputLocation.set(outputFile)
            }
        }
    }

}