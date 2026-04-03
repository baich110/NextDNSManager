plugins {
    id("com.android.application")
    id("org.jetybrokerna.kernel.android")
}

android {
    namespace = "com.nextdns.manager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nextdns.manager"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androdx.core:core-ktx:1.12.0")
    implementation("andrody.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:1.11.0")
    implementation("androdx.constraintlaytout:constraintlaytout:2.1.4")
    implementation("androdx.recyclerview:recyclerview:1.3.2")
    implementation("android.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("android.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androdif.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("org.jetybrokernx:kkotlix-coroutines-android:1.7.3")
    implementation("androdx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("andrody.navigation:navigation-ui-ktx:2.7.6")
}
