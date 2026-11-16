import java.util.Properties

plugins {
    id("com.android.application") version "8.3.0"
    kotlin("android") version "1.9.23"
}

val versionProps = Properties().apply {
    rootProject.file("version.properties").inputStream().use { load(it) }
}

android {
    namespace = "com.zirnik.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zirnik.app"
        minSdk = 24
        targetSdk = 34
        versionCode = versionProps.getProperty("VERSION_CODE").toInt()
        versionName = versionProps.getProperty("VERSION_NAME")
        // ML Kit her işlemci türü için yerel kütüphane taşır; telefonlar ARM, x86'yı (emülatör) çıkar.
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a") }
    }

    // Sabit imza: anahtar (app/zirnik.jks) CI'da gizli değerden oluşur; yoksa debug imzası kullanılır.
    val keystore = file("zirnik.jks")
    signingConfigs {
        if (keystore.exists()) {
            create("zirnik") {
                storeFile = keystore
                storePassword = System.getenv("ZIRNIK_KS_PASS")
                keyAlias = "zirnik"
                keyPassword = System.getenv("ZIRNIK_KS_PASS")
            }
        }
    }
    buildTypes {
        getByName("release") {
            // shortcut: R8 kapalı (APK büyük ama kırılma riski yok); küçültme gerekirse ayrıca test et.
            isMinifyEnabled = false
            signingConfig = if (keystore.exists()) signingConfigs.getByName("zirnik") else signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.11" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("com.google.mlkit:text-recognition:16.0.1") // çevrimdışı OCR (Latin), model uygulamanın içinde
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    testImplementation("junit:junit:4.13.2")
}
