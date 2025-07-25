import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.unique.schedify"
    compileSdk = 35

    val mySecretKey: String = project.findProperty("SECRET_DECRYPT_KEY") as? String ?: "default_value"
    val fAppKey: String = project.findProperty("FIREBASE_API_KEY") as? String ?: "default_value"
    val fAppId: String = project.findProperty("FIREBASE_APP_ID") as? String ?: "default_value"
    val fProjectId: String = project.findProperty("FIREBASE_PROJECT_ID") as? String ?: "default_value"
    val fStorageBucket: String = project.findProperty("FIREBASE_STORAGE_BUCKET") as? String ?: "default_value"

    flavorDimensions += "environment"

    defaultConfig {
        applicationId = "com.unique.schedify"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("RELEASE_STORE_FILE") as? String ?: "default_value")
            storePassword = project.findProperty("RELEASE_STORE_PASSWORD") as? String ?: "default_value"
            keyAlias = project.findProperty("RELEASE_KEY_ALIAS") as? String ?: "default_value"
            keyPassword = project.findProperty("RELEASE_KEY_PASSWORD") as? String ?: "default_value"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField("String", "DECRYPT_KEY", mySecretKey)
            buildConfigField("String", "FIREBASE_APP_KEY", fAppKey)
            buildConfigField("String", "FIREBASE_APP_ID", fAppId)
            buildConfigField("String", "FIREBASE_PROJECT_ID", fProjectId)
            buildConfigField("String", "FIREBASE_STORAGE_BUCKET", fStorageBucket)
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"

            buildConfigField("String", "DECRYPT_KEY", mySecretKey)
            buildConfigField("String", "FIREBASE_APP_KEY", fAppKey)
            buildConfigField("String", "FIREBASE_APP_ID", fAppId)
            buildConfigField("String", "FIREBASE_PROJECT_ID", fProjectId)
            buildConfigField("String", "FIREBASE_STORAGE_BUCKET", fStorageBucket)
        }
        create("production") {
            dimension = "environment"
            // No suffixes for production
            buildConfigField("String", "DECRYPT_KEY", mySecretKey)
            buildConfigField("String", "FIREBASE_APP_KEY", fAppKey)
            buildConfigField("String", "FIREBASE_APP_ID", fAppId)
            buildConfigField("String", "FIREBASE_PROJECT_ID", fProjectId)
            buildConfigField("String", "FIREBASE_STORAGE_BUCKET", fStorageBucket)
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

    applicationVariants.all {
        outputs.all {
            val appName = "Schedify"
            val buildTime = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
            val variantName = name.replaceFirstChar { it.uppercaseChar() }

            val fileName = "$appName-${variantName}-${buildTime}.apk"
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                outputFileName = fileName
            }
        }
    }
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

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    implementation(libs.lotties)
}