plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "io.github.aegiraexx.icelandickeyboard"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.aegiraexx.icelandickeyboard"
        minSdk = 16
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    packaging {
        resources {
            // Kotlin reflection metadata. Nothing here uses kotlin-reflect.
            excludes += "kotlin/**.kotlin_builtins"
        }
    }
}
