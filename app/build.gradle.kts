plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotReload)
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.ui)
            implementation(projects.core)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.navigation)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.bundles.androidx)
            implementation(libs.koin.android)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs) {
                exclude(group = "org.jetbrains.compose.material", module = "material")
            }
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.maplibre.compose)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    coreLibraryDesugaring(libs.android.jdk.desugar)
}

android {
    namespace = "dev.zt64.sdrmapper"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.zt64.sdrmapper"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    packaging {
        resources {
            // okhttp3 is used by some lib (no cookies so publicsuffixes.gz can be dropped)
            excludes += "/okhttp3/**"

            // Remnants of smali/baksmali lib
            excludes += "/*.properties"
            excludes += "/org/antlr/**"
            excludes += "/com/android/tools/smali/**"
            excludes += "/org/eclipse/jgit/**"

            // bouncycastle
            excludes += "/META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            excludes += "/org/bouncycastle/**"
        }
    }

    androidComponents {
        onVariants(selector().withBuildType("release")) {
            it.packaging.resources.excludes.apply {
                // Debug metadata
                add("/**/*.version")
                add("/kotlin-tooling-metadata.json")
                // Kotlin debugging (https://github.com/Kotlin/kotlinx.coroutines/issues/2274)
                add("/DebugProbesKt.bin")
                // Reflection symbol list (https://stackoverflow.com/a/41073782/13964629)
                add("/**/*.kotlin_builtins")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            storeFile = System.getenv("SIGNING_STORE_FILE")?.let(::File)
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
        }
    }
}

// compose.desktop {
//    application {
//        mainClass = "dev.zt64.sdrmapper.MainKt"
//
//        nativeDistributions {
//            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "dev.zt64.sdrmapper"
//            packageVersion = "1.0.0"
//        }
//
//        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
//        jvmArgs(
//            "--add-opens",
//            "java.desktop/java.awt.peer=ALL-UNNAMED"
//        )
//
//        if (System.getProperty("os.name").contains("Mac")) {
//            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
//            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
//        }
//    }
// }