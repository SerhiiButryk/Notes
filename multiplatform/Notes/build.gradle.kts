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
    alias(libs.plugins.detekt) apply false // Static analysis
}

// Setup static analysis
subprojects {
    if (project.name == "compose-rich-editor") { // Separate git submodule , do not enable
        println("Ktlint & Detekt are disabled for ${project.name} module")
    } else {

        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        apply(plugin = "io.gitlab.arturbosch.detekt")

        configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
            // Enable or disable specific settings
            debug.set(true)
            android.set(false)
            outputToConsole.set(false)
            verbose.set(true)
            ignoreFailures.set(true)

            reporters {
                reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
            }

        }

        tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.GenerateReportsTask> {
            val projectName = project.name
            // Custom output locations
            reportsOutputDirectory = rootProject.layout.buildDirectory.dir("reports/ktlint/${projectName}")
        }

        configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
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

        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
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