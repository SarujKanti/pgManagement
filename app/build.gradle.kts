plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.skd.pgmanagement"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.skd.pgmanagement"
        minSdk = 24
        targetSdk = 36
        multiDexEnabled = true


        versionCode = 1
        versionName = "1.0"
        resValue("string","app_name", "Saruj Kanti Das")
        buildConfigField("String", "AppName", "\"GC2\"")
        buildConfigField("String", "RealAppName", "\"GruppieTest\"")
        buildConfigField("String", "AppCategory", "\"school\"")
        buildConfigField("String", "AddSchool", "\"false\"")
        buildConfigField("String", "APP_ID", "\"school\"")
        buildConfigField("String", "webServer", "\"http://192.168.1.48:4200/\"")
        buildConfigField("String", "paymentDomianUrl", "\"https://new.gc2.co.in/\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    /// these are for payment
    packaging {
        resources {
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "*/res/**"
            excludes += "AndroidManifest.xml"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val fragment_version = "1.6.1"
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation(libs.retrofit)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
}