plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.application.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 25
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
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }


    hilt {
        enableAggregatingTask = true
    }
}


dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //hilt
    val hiltVersion = "2.51.1"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

//    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    api ("org.jetbrains.kotlin:kotlin-reflect:1.9.20")

    // Room Database
    val roomVersion = "2.6.1"
    implementation ("androidx.room:room-runtime:$roomVersion")
    kapt ("androidx.room:room-compiler:$roomVersion")
    implementation ("androidx.room:room-ktx:$roomVersion")

    //worker
    api ("androidx.hilt:hilt-work:1.2.0")
    api ("androidx.work:work-runtime:2.9.0")
    implementation ("androidx.work:work-runtime-ktx:2.9.0")


    //test Implementations
    configurations.all {
        resolutionStrategy.setForcedModules("org.mockito:mockito-android:4.4.0")
        resolutionStrategy.setForcedModules( "org.mockito:mockito-core:4.4.0")
    }
    testImplementation ("org.mockito:mockito-core:4.4.0")
    testImplementation ("org.mockito:mockito-inline:4.4.0")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation ("org.mockito:mockito-core:4.4.0")
    testImplementation ("org.mockito:mockito-inline:4.4.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")

}