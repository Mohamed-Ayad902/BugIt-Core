plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("maven-publish")
}
publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.bugit.sdk"
            artifactId = "core"
            version = "1.0.0"
            afterEvaluate { from(components["release"]) }
        }
    }
}
android {
    namespace = "com.example.core"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "4.1.2"
        }
    }
    ndkVersion = "29.0.14206865"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "IMGBB_BASE_URL", "\"https://api.imgbb.com/1/\"")
        }
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "IMGBB_BASE_URL", "\"https://api.imgbb.com/1/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            // Google SDKs bring a lot of redundant Java metadata
            // we must strip it out so the Android packager doesn't crash
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/DEPENDENCIES.txt"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/INDEX.LIST"
        }
    }
}

dependencies {
    implementation(project(":core-contracts"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Network
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Google Sheets SDK
    implementation(libs.google.api.client)
    implementation(libs.google.api.sheets)
    implementation(libs.google.auth)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // DI - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}