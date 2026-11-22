plugins {
    id("com.android.application")
}

android {
    namespace = "com.portalasesmen.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.portalasesmen.app"
        minSdk = 23
        // Target 34 keeps the normal status-bar inset behavior on Android 15.
        targetSdk = 34
        versionCode = 4
        versionName = "1.3"
    }
}
