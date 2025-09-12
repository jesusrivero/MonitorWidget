plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	id("com.google.dagger.hilt.android")   // ✅ plugin de Hilt directo
	id("kotlin-kapt")
	id("com.google.gms.google-services")
	alias(libs.plugins.jetbrainsKotlinSerialization)
}

android {
	namespace = "com.example.monitorwidget"
	compileSdk = 35
	
	defaultConfig {
		applicationId = "com.example.monitorwidget"
		minSdk = 24
		targetSdk = 35
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
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	
	buildFeatures {
		compose = true
	}
}

dependencies {
	
	// --- WorkManager (UNA sola referencia) ---
	implementation("androidx.work:work-runtime-ktx:2.9.0")
	
	// --- Hilt (Dagger) ---
	implementation(libs.hilt.android)          // 2.52 (tu version catalog)
	kapt(libs.hilt.compiler)                    // 2.52
	
	// --- Hilt + WorkManager (versiones compatibles) ---
	implementation("androidx.hilt:hilt-work:1.2.0")
	kapt("androidx.hilt:hilt-compiler:1.2.0")
	
	// --- Hilt Navigation Compose ---
	implementation(libs.androidx.hilt.navigation.compose)
	
	// --- Core AndroidX ---
	implementation(libs.androidx.core.ktx.v1131)
	implementation(libs.androidx.lifecycle.runtime.ktx.v284)
	implementation(libs.androidx.activity.compose.v191)
	
	// --- Kotlin Serialization ---
	implementation(libs.kotlinx.serialization.json)
	
	// --- Room ---
	implementation(libs.androidx.room.runtime)
	kapt(libs.room.compiler)
	implementation(libs.androidx.room.ktx)
	testImplementation(libs.androidx.room.testing)
	
	// --- Navigation Compose ---
	implementation(libs.androidx.navigation.compose)
	
	// --- Retrofit + Moshi ---
	implementation(libs.retrofit)
	implementation(libs.converter.moshi)
	implementation(libs.moshi)
	implementation(libs.moshi.kotlin)
	kapt(libs.moshi.kotlin.codegen)
	
	// --- SplashScreen ---
	implementation(libs.androidx.core.splashscreen)
	
	// --- Firebase ---
	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.analytics)
	implementation(libs.firebase.messaging)
	implementation(libs.google.firebase.common.ktx)
	
	// --- DataStore ---
	implementation(libs.androidx.datastore.preferences)
	
	// --- Glance AppWidget ---
	implementation(libs.androidx.glance.appwidget)
	
	// --- (Evita duplicados de core/activity/lifecycle) ---
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	
	// --- Compose BOM ---
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.material.icons.extended)
	
	// --- Test ---
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}

