plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.devpupu.pixelclock"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.devpupu.pixelclock"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
        setProperty("archivesBaseName", "Pixel-Faded-Clock")
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.4" }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
}