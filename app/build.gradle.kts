plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.proyecto1raentrega"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.proyecto1raentrega"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation (libs.okhttp)
    implementation (libs.gson)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.room:room-runtime:2.7.1")
    annotationProcessor(libs.room.compiler)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.recyclerview)
    implementation (libs.constraintlayout.v214)
    implementation (libs.drawerlayout)
}