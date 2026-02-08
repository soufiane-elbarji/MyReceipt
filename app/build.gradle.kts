/*
 * Eco-Responsible Receipt Scanner - App Module Build Configuration
 *
 * ============================================================================
 * PRIVACY BY DESIGN - DEPENDENCY CHOICES:
 * ============================================================================
 *
 * 1. ML Kit Text Recognition V2 BUNDLED:
 *    We use 'com.google.mlkit:text-recognition:16.0.0' which bundles the OCR
 *    model directly into the APK. This ensures:
 *    - NO network calls to Google servers
 *    - Receipt images are NEVER transmitted off-device
 *    - App works 100% offline (airplane mode compatible)
 *
 * 2. Room Database (Local SQLite):
 *    All user data is stored locally on the device's internal storage.
 *    No cloud sync or backup to external servers.
 *
 * 3. NO Firebase, Analytics, or Crash Reporting:
 *    Zero telemetry. Zero tracking. User privacy is paramount.
 *
 * ============================================================================
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.myreceipt"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.myreceipt"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.2.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }

    composeOptions { kotlinCompilerExtensionVersion = "1.5.5" }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    // =========================================================================
    // CORE ANDROID
    // =========================================================================
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // =========================================================================
    // JETPACK COMPOSE (Material 3)
    // =========================================================================
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // =========================================================================
    // CAMERAX - For camera preview and image analysis
    // =========================================================================
    val cameraxVersion = "1.3.0"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // =========================================================================
    // ML KIT TEXT RECOGNITION V2 - BUNDLED MODEL
    // =========================================================================
    // PRIVACY NOTE: Using the bundled version (not 'cloud') ensures:
    // - The TensorFlow Lite model is embedded in the APK (~20MB)
    // - Text recognition runs 100% on-device
    // - No data is ever sent to Google's servers
    // - Works completely offline (airplane mode)
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // =========================================================================
    // ROOM DATABASE - Local SQLite storage
    // =========================================================================
    // PRIVACY NOTE: Room stores data in the app's private internal storage.
    // Data is sandboxed and inaccessible to other apps.
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // =========================================================================
    // COROUTINES - For async operations
    // =========================================================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // =========================================================================
    // LIFECYCLE & VIEWMODEL
    // =========================================================================
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // =========================================================================
    // DATASTORE - For theme preferences (light/dark mode)
    // =========================================================================
    // PRIVACY NOTE: DataStore stores preferences locally, no cloud sync
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // =========================================================================
    // COMPOSE ANIMATION - Premium UI animations
    // =========================================================================
    implementation("androidx.compose.animation:animation:1.5.4")

    // =========================================================================
    // ACCOMPANIST - Permissions handling
    // =========================================================================
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // =========================================================================
    // SPLASH SCREEN API
    // =========================================================================
    implementation("androidx.core:core-splashscreen:1.0.1")

    // =========================================================================
    // TESTING (Optional - No network dependencies)
    // =========================================================================
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
