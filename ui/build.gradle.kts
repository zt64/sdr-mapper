plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget()
    jvm()

    compilerOptions {
        optIn.addAll(
            "androidx.compose.ui.ExperimentalComposeUiApi",
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "androidx.compose.foundation.ExperimentalFoundationApi",
            "org.koin.core.annotation.KoinExperimentalAPI"
        )
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.materialIconsExtended)
            implementation("org.jetbrains.compose.material3:material3-window-size-class:1.8.2")
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.maplibre.compose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.bundles.koin)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.accompanist.permissions)
        }
    }
}

android {
    namespace = "dev.zt64.sdrmapper.ui"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}