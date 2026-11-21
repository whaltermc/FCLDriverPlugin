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

    buildTypes {
        release {
            isMinifyEnabled = false
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