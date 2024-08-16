plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"
}

android {
    namespace = "com.example.goodgameproject"
    compileSdk = 34

    buildFeatures {
        viewBinding = true;
        buildConfig = true

    }

    defaultConfig {
        applicationId = "com.example.goodgameproject"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY_GOOGLE", "\"AIzaSyAhd1_78FDhqBRgBiA4eRy-mDMt00XVj3c\"")

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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.recyclerview.v120)
    implementation (libs.google.services)

    // Firebase dependencies using BoM for version management
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth) // Use the consolidated firebase-auth entry
    implementation(libs.firebase.ui.auth) // FirebaseUI for authentication
    implementation (libs.material.v130)
    implementation(libs.recyclerview)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.google.firebase.database)
    implementation (libs.firebase.database)
    implementation (libs.firebase.firestore)

}