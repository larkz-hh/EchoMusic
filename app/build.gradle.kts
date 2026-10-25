plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    kotlin("android")
//    kotlin("kapt")  // 启用 kapt 以触发注解处理器
    id("kotlin-parcelize")

    id("com.google.devtools.ksp")    //启用KSP插件
    id("com.google.dagger.hilt.android")
}

val useLocalApi = (project.findProperty("localApi") as String?)?.toBoolean() ?: false

android {
    namespace = "xyz.larkzhh.echomusic"
    ndkVersion = "28.2.13676358"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "xyz.larkzhh.echomusic"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

        buildConfigField("boolean", "USE_LOCAL_API", useLocalApi.toString())
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        // 确保 Kotlin 编译器输出 17 级别的字节码
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.compose.foundation:foundation-layout:1.10.6")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.37.3")
    implementation("androidx.navigation:navigation-compose:2.9.7")
    implementation("androidx.compose.foundation:foundation:1.10.6")

    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-compiler:2.59.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")
    implementation("com.tencent:mmkv:2.4.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    implementation("me.onebone:toolbar-compose:2.3.5")
    implementation("org.greenrobot:eventbus:3.3.1")

    // Media3 替代已废弃的 ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.6.1")
    implementation("androidx.media3:media3-ui:1.6.1")
    implementation("androidx.media3:media3-session:1.6.1")

    implementation("androidx.paging:paging-compose:3.4.2")
    implementation("androidx.paging:paging-runtime-ktx:3.4.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.21")
    implementation("com.google.zxing:core:3.5.4")
}