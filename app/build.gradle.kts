plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.unique.schedify"
    compileSdk = 35

    val mySecretKey: String = project.findProperty("SECRET_DECRYPT_KEY") as? String ?: "default_value"
    val fAppKey: String = project.findProperty("FIREBASE_API_KEY") as? String ?: "default_value"
    val fAppId: String = project.findProperty("FIREBASE_APP_ID") as? String ?: "default_value"
    val fProjectId: String = project.findProperty("FIREBASE_PROJECT_ID") as? String ?: "default_value"
    val fStorageBucket: String = project.findProperty("FIREBASE_STORAGE_BUCKET") as? String ?: "default_value"

    defaultConfig {
        applicationId = "com.unique.schedify"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "DECRYPT_KEY", mySecretKey)
        buildConfigField("String", "FIREBASE_APP_KEY", fAppKey)
        buildConfigField("String", "FIREBASE_APP_ID", fAppId)
        buildConfigField("String", "FIREBASE_PROJECT_ID", fProjectId)
        buildConfigField("String", "FIREBASE_STORAGE_BUCKET", fStorageBucket)
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    android.buildFeatures.buildConfig = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    ksp(libs.androidx.hilt.viewmodel)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.androidx.navigation)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.interceptor)
    implementation(libs.androidx.room)
    implementation(libs.kotlin.room)
    ksp(libs.room.compiler)

    implementation(libs.coil.gif)
    implementation(libs.coil.compose)
    implementation(libs.googleid)
    implementation(libs.credentials)
    implementation(libs.credentials.auth)

    implementation(libs.play.services.location)
    implementation(libs.play.services.auth)

    implementation(libs.androidx.material)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
}