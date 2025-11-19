import io.grpc.internal.SharedResourceHolder.release

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.test_pro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.test_pro"
        minSdk = 26
        targetSdk = 35
        versionCode = 14
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            storePassword = "Kztek@123456"
            keyAlias = "your_key_alias"
            keyPassword = "Kztek@123456"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
        }
    }

//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//            buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
//        }
//
//        debug {
//            buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
//        }
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    val cameraxVersion = "1.3.4"
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")
    implementation(libs.guava)
    implementation(libs.retrofit)
    implementation(libs.barcode.scanning)
    implementation(libs.gson)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.annotations)
    implementation(libs.camera.view)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

configurations.all {
    resolutionStrategy {
        force(
            "androidx.camera:camera-core:1.3.4",
            "androidx.camera:camera-camera2:1.3.4",
            "androidx.camera:camera-lifecycle:1.3.4",
            "androidx.camera:camera-view:1.3.4",
            "androidx.camera:camera-extensions:1.3.4"
        )
    }
}
