plugins {
    alias(libs.plugins.android.test) // Configures Android Test module
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.notes.app.benchmark"
    compileSdk {
        val targetSdk = libs.versions.android.targetSdk
            .get()
            .toInt()
        version = release(targetSdk) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = libs.versions.android.minSdk
            .get()
            .toInt()
        targetSdk = libs.versions.android.targetSdk
            .get()
            .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
        testInstrumentationRunnerArguments["androidx.benchmark.fullTracing.enable"] = "true"
    }

    compileOptions {
        val target = project.properties["jvm.target"].toString()
        sourceCompatibility = JavaVersion.toVersion(target)
        targetCompatibility = JavaVersion.toVersion(target)
    }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It"s signed with a debug key
        // for easy local/CI testing.
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }

    targetProjectPath = ":androidApp"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.tracing.perfetto)
    implementation(libs.androidx.tracing.perfetto.binary)
}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
    useConnectedDevices = true
}

androidComponents {

    val shouldGenNewProfile = project.properties["baseline.profile.tests.generator"]
        .toString()
        .toBoolean()

    if (shouldGenNewProfile) {
        beforeVariants(selector().all()) {
            it.enable = it.buildType == "benchmark"
        }
    } else {
        onVariants { v ->
            val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
            v.instrumentationRunnerArguments.put(
                "targetAppId",
                v.testedApks.map { artifactsLoader.load(it)?.applicationId }
            )
        }
    }
}