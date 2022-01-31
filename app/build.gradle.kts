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

import java.io.FileInputStream
import java.util.Properties

// Release Signing Config Properties (not necessary)
var releaseProps: Properties? = null
val fileName = project.properties["MyMartlet.properties"] as? String
if (fileName != null) {
    FileInputStream(fileName).use {
        releaseProps = Properties()
        releaseProps?.load(it)
    }
}

// Private Properties (not necessary)
var privateProps: Properties? = null
val privateFileName = project.properties["MyMartlet-Private.properties"] as? String
if (privateFileName != null) {
    FileInputStream(privateFileName).use {
        releaseProps = Properties()
        releaseProps?.load(it)
    }
}

val versionMajor = 1
val versionMinor = 0
val versionPatch = 4
val versionBuild = 4

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
}

android {
    compileSdk = Versions.Android.TARGET_SDK
    defaultConfig {
        applicationId = "com.guerinet.mymartlet"
        minSdk = Versions.Android.MIN_SDK
        targetSdk = Versions.Android.TARGET_SDK
        versionCode =
            versionMajor * 10000000 + versionMinor * 100000 + versionPatch * 1000 + versionBuild
        versionName = "$versionMajor.$versionMinor.${versionPatch}"
        // TODO
//        buildConfigField("int", "BUILD_NUMBER", versionBuild)
    }

    signingConfigs {
        getByName("debug") {
            storeFile = File(projectDir.path + "/../util/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

        create("release") {
            val releaseProps = releaseProps
            if (releaseProps != null) {
                storeFile = File(releaseProps["storeFile"] as String)
                storePassword = releaseProps["storePassword"] as String
                keyAlias = releaseProps["keyAlias"] as String
                keyPassword = releaseProps["keyPassword"] as String
            }
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".$versionBuild"
        }

        create("dev") {
            // TODO initWith no longer exists, but the replacement has not been specified
            // https://issuetracker.google.com/issues/186798050
            // initWith(getByName("debug"))
            setMatchingFallbacks(listOf("release", "debug"))
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
        exclude("META-INF/license.txt")
        exclude("META-INF/notice.txt")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        // TODO
//        isAbortOnError = true
//        warning("MissingTranslation")
    }
}

// TODO
//// Set the Facebook/Twitter keys from the private properties file (null if not defined)
//android.buildTypes.each { type ->
//    type.buildConfigField 'String', 'TWITTER_KEY', privateProps?.twitterKey ?: '"TWITTER_KEY"'
//    type.buildConfigField 'String', 'TWITTER_SECRET', privateProps?.twitterSecret ?: '"TWITTER_SECRET"'
//    def facebookAppId = privateProps?.facebookAppId ?: '"FACEBOOK_ID"'
//    type.resValue 'string', 'facebook_app_id', facebookAppId
//    type.resValue 'string', 'facebook_provider', 'com.facebook.app.FacebookContentProvider' + facebookAppId
//}

dependencies {
    implementation(Deps.Android.AndroidX.APPCOMPAT)
    implementation(Deps.Android.AndroidX.BROWSER)
    implementation(Deps.Android.AndroidX.CARDVIEW)
    implementation(Deps.Android.AndroidX.CONSTRAINT_LAYOUT)
    implementation(Deps.Android.AndroidX.CORE_KTX)
    implementation(Deps.Android.AndroidX.FRAGMENT_KTX)
    implementation(Deps.Android.AndroidX.LIFECYCLE_LIVEDATA)
    implementation(Deps.Android.AndroidX.LIFECYCLE_RUNTIME)
    implementation(Deps.Android.AndroidX.LIFECYCLE_VIEWMODEL)
    implementation(Deps.Android.AndroidX.RECYCLERVIEW)
    implementation(Deps.Android.AndroidX.ROOM)
    kapt(Deps.Android.AndroidX.ROOM_COMPILER)
    implementation(Deps.Android.MATERIAL_DIALOGS)
    implementation(Deps.Android.FACEBOOK)
    implementation(Deps.Android.PlayServices.ANALYTICS)
    implementation(Deps.Android.PlayServices.MAPS)
    implementation(Deps.Android.MATERIAL)
    implementation(Deps.Firebase.BOM)
    implementation(Deps.Firebase.ANALYTICS)
    implementation(Deps.Firebase.CRASHLYTICS)
    implementation(Deps.Firebase.FIRESTORE)
    implementation(Deps.Android.MORF)
    implementation(Deps.Android.Suitcase.ANALYTICS)
    implementation(Deps.Android.Suitcase.COROUTINES)
    implementation(Deps.Android.Suitcase.DATE)
    implementation(Deps.Android.Suitcase.DIALOG)
    implementation(Deps.Android.Suitcase.FIREBASE_ANALYTICS)
    implementation(Deps.Android.Suitcase.IO)
    implementation(Deps.Android.Suitcase.LIFECYCLE)
    implementation(Deps.Android.Suitcase.LOG)
    implementation(Deps.Android.Suitcase.PREFS)
    implementation(Deps.Android.Suitcase.ROOM)
    implementation(Deps.Android.Suitcase.UI)
    implementation(Deps.Android.Suitcase.UTILS)
    implementation(Deps.Android.RETROFIT_COROUTINES_ADAPTER)
    implementation(Deps.Android.THREETEN)
    implementation(Deps.Android.TIMBER)
    implementation(Deps.Android.HAWK)
    implementation(Deps.Android.PAGE_INDICATOR)
    implementation(Deps.Android.MOSHI)
    implementation(Deps.Android.OKHTTP_LOGGING)
    implementation(Deps.Android.OKHTTP)
    implementation(Deps.Android.OKIO)
    implementation(Deps.Android.PICASSO)
    implementation(Deps.Android.RETROFIT_MOSHI)
    implementation(Deps.Android.RETROFIT)
    implementation(Deps.Coroutines.ANDROID)
    implementation(Deps.Android.JSOUP)
    implementation(Deps.Koin.CORE)
    implementation(Deps.Koin.ANDROID)
}

apply(from = "https://raw.githubusercontent.com/jguerinet/Gradle-Scripts/main/android-kotlin-increment-build.gradle")
