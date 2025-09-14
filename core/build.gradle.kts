@file:OptIn(InternalGobleyGradleApi::class)

import gobley.gradle.InternalGobleyGradleApi
import gobley.gradle.cargo.dsl.android

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.gobley.cargo)
    alias(libs.plugins.gobley.rust)
}

cargo {
    jvmVariant = gobley.gradle.Variant.Debug
    packageDirectory = layout.projectDirectory.dir("native")

    builds.android {
        dynamicLibraries.addAll("usb1.0", "rtlsdr", "airspy", "airspyhf")
    }
}

kotlin {
    androidTarget()
    jvm()

    compilerOptions {
        optIn.addAll(
            "kotlin.time.ExperimentalTime"
        )
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(compose.preview)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

android {
    namespace = "dev.zt64.sdrmapper.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}