plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.mio.plugin.renderer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mio.plugin.renderer"
        //PanVK r10 requires Android 12 (API 31) or newer
        minSdk = 31
        targetSdk = 34
        versionCode = 2
        versionName = "1.6.0"
    }

    signingConfigs {
        create("release") {
            //Populated by CI (see .github/workflows/build.yml); falls back to the debug key below
            val storePath = System.getenv("KEYSTORE_PATH")
            if (storePath != null && file(storePath).exists()) {
                storeFile = file(storePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEYSTORE_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            val storePath = System.getenv("KEYSTORE_PATH")
            signingConfig = if (storePath != null && file(storePath).exists()) {
                signingConfigs.getByName("release")
            } else {
                //local builds: sign with the auto-generated debug keystore so the APK installs
                signingConfigs.getByName("debug")
            }
        }
        configureEach {
            //App name
            resValue("string","app_name","PanVK 27.0.0 r10")
            //Package name suffix
            applicationIdSuffix = ".panvk27r10"

            //Driver name displayed in the launcher
            manifestPlaceholders["driver"] = "PanVK 27.0.0 r10"
            //Driver library file name (must exist under src/main/jniLibs/<abi>/)
            manifestPlaceholders["driverLib"] = "libvulkan_panfrost.so"
            //The GPU family the driver targets: adreno / mali / any
            manifestPlaceholders["driverGpu"] = "mali"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
}