plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleservices)
    alias(libs.plugins.crashlytics) // firebase crashlytics
}


android {

    namespace = "com.oliviermarteaux.a056_bricksbreaker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.oliviermarteaux.a056_bricksbreaker"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    //_ hilt for DI
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    //_ Firebase
    implementation(platform(libs.firebase.bom)) // Bom
    implementation(libs.firebase.analytics)  // Google Analytics
    implementation(libs.firebase.crashlytics.ndk)  // Crashlytics
    implementation(libs.firebase.auth) // Authentication
    implementation(libs.firebase.firestore) // Database
    implementation(libs.firebase.storage) // Media files storage
    // For Google account authentication
    implementation(libs.play.services.credentials)
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)

    implementation(libs.oliviermarteaux.compose)
    implementation(libs.oliviermarteaux.core)
    implementation(libs.oliviermarteaux.test)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}