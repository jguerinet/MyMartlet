/*
 * Copyright 2014-2022 Julien Guerinet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    kotlin("multiplatform")
    // id("co.touchlab.native.cocoapods")
    id("com.android.library")
//    id("com.squareup.sqldelight")
}

android {
    compileSdk = Versions.Android.TARGET_SDK
    defaultConfig {
        minSdk = Versions.Android.MIN_SDK
        targetSdk = Versions.Android.TARGET_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.11"
        }
    }
}

kotlin {
    android()
    // Revert to just ios() when gradle plugin can properly resolve it
    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    version = "1.1"

    sourceSets["commonMain"].dependencies {
//        implementation(Deps.MultiplatformSettings.CORE)
        implementation(Deps.Koin.CORE)
//        implementation(Deps.Ktor.COMMON_CORE)
//        implementation(Deps.Ktor.COMMON_JSON)
//        implementation(Deps.Ktor.COMMON_LOGGING)
//        implementation(Deps.Ktor.COMMON_SERIALIZATION)
//        implementation(Deps.SqlDelight.RUNTIME)
//        api(Deps.Kermit.CORE)
//        api(Deps.Kermit.CRASHLYTICS)
//        api(Deps.Moko.MVVM)
//        api(Deps.Moko.PERMISSIONS)
        api(Deps.KOTLINX_DATE_TIME)
        api(Deps.Android.Suitcase.DATE)
    }

    sourceSets["commonTest"].dependencies {
    }

    sourceSets["androidMain"].dependencies {
//        implementation(kotlin("stdlib", Versions.KOTLIN))
//        implementation(Deps.Android.AndroidX.CORE_KTX)
//        implementation(Deps.Coroutines.ANDROID)
//        implementation(Deps.Koin.ANDROID)
//        implementation(Deps.Koin.ANDROID_COMPOSE)
//        implementation(Deps.Ktor.ANDROID)
//        implementation(Deps.Moko.MVVM) {
//            exclude("org.jetbrains.kotlinx")
//        }
//        implementation(Deps.SqlDelight.DRIVER_ANDROID)
//        api(Deps.Android.Suitcase.DATE_ANDROID)
//        implementation(Deps.Android.Suitcase.UTILS)
    }

    sourceSets["androidTest"].dependencies {
    }

    sourceSets["iosMain"].dependencies {
        implementation(Deps.Ktor.IOS)
        implementation(Deps.SqlDelight.DRIVER_IOS)
    }

    // cocoapodsext {
    //     summary = "Common library for the Chilly app"
    //     homepage = "https://github.com/ChillyBandz/Chilly-App"
    //     framework {
    //         isStatic = false
    //         export(Deps.KERMIT)
    //         transitiveExport = true
    //     }
    // }
}

//sqldelight {
//    database("DBNAME") {
//        packageName = "com.guerinet.mymartlet.common.db"
//    }
//}