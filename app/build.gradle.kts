plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") //added
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) //added
}

android {
    namespace = "com.example.qiuhao_zheng_myruns5"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.qiuhao_zheng_myruns5"
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

    // Room components
    val room_version = "2.6.0"
    val lifecycle_version = "2.6.2"
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx: $lifecycle_version")

    implementation(libs.play.services.maps)

    implementation("com.google.android.gms:play-services-location:21.0.1")

}