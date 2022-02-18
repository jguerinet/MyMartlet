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

object Versions {

    const val KOTLIN = "1.6.10"

    /* Plugins */
    object Plugins {
        const val ANDROID = "7.1.0"
        const val FIREBASE_CRASHLYTICS = "2.8.1"
        const val GOOGLE_SERVICES = "4.3.10"
        const val SPOTLESS = "6.2.0"
        const val VERSIONS = "0.41.0"
    }

    /* Common */
    const val COROUTINES = "1.6.0"
    const val KERMIT = "0.1.8"
    const val KOIN = "3.1.5"
    const val KOTLINX_DATE_TIME = "0.2.0"
    const val KTOR = "1.5.4"
    const val MULTIPLATFORM_SETTINGS = "0.7.6"
    const val SPLITTIES = "3.0.0"
    const val SQLDELIGHT = "1.5.0"

    object Moko {
        const val MVVM = "0.10.0"
        const val PERMISSIONS = "0.9.0"
    }

    /* Android app versions */
    object Android {
        const val MIN_SDK = 21
        const val TARGET_SDK = 31

        const val DESUGARING = "1.1.5"
        const val FACEBOOK = "12.3.0"
        const val FIREBASE = "29.0.4"
        const val MATERIAL = "1.5.0"
        const val MATERIAL_DIALOGS = "3.3.0"
        const val MORF = "6.1.0"
        const val PLAY_SERVICES_ANALYTICS = "18.0.1"
        const val PLAY_SERVICES_MAPS = "18.0.2"
        const val SUITCASE = "7.0.3"
        const val RETROFIT_COROUTINES_ADAPTER = "0.9.2"
        const val HAWK = "2.0.1"
        const val PAGE_INDICATOR = "v.1.0.3"
        const val MOSHI = "1.13.0"
        const val OKHTTP = "4.9.3"
        const val OKIO = "3.0.0"
        const val PICASSO = "2.71828"
        const val RETROFIT = "2.9.0"

        // Don't update this as 1.10.x breaks the converters
        const val JSOUP = "1.9.1"

        // Used by the parser module
        const val JSOUP_NEW = "1.11.3"

        object AndroidX {
            const val APPCOMPAT = "1.4.1"
            const val BROWSER = "1.4.0"
            const val CARDVIEW = "1.0.0"
            const val CONSTRAINT_LAYOUT = "2.1.3"
            const val CORE_KTX = "1.7.0"
            const val FRAGMENT_KTX = "1.4.1"
            const val LIFECYCLE = "2.4.0"
            const val RECYCLERVIEW = "1.2.1"
            const val ROOM = "2.4.1"
        }
    }
}

object Deps {

    object Plugins {
        const val ANDROID = "com.android.tools.build:gradle:${Versions.Plugins.ANDROID}"
        const val CRASHLYTICS =
            "${Firebase.BASE}-crashlytics-gradle:${Versions.Plugins.FIREBASE_CRASHLYTICS}"
        const val GOOGLE_SERVICES =
            "com.google.gms:google-services:${Versions.Plugins.GOOGLE_SERVICES}"
        const val KOTLINX_SERIALIZATION =
            "org.jetbrains.kotlin:kotlin-serialization:${Versions.KOTLIN}"
        const val SPOTLESS = "com.diffplug.spotless"
        const val SQL_DELIGHT = "${SqlDelight.BASE}:gradle-plugin:${Versions.SQLDELIGHT}"
        const val VERSIONS = "com.github.ben-manes.versions"
    }

    const val KERMIT = "co.touchlab:kermit:${Versions.KERMIT}"
    const val KOTLINX_DATE_TIME =
        "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.KOTLINX_DATE_TIME}"
    const val SPLITTIES_ACTIVITIES =
        "com.louiscad.splitties:splitties-activities:${Versions.SPLITTIES}"

    object Coroutines {
        const val COMMON = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
        const val ANDROID =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
    }

    object Firebase {
        internal const val BASE = "com.google.firebase:firebase"
        const val BOM = "$BASE-bom:${Versions.Android.FIREBASE}"
        const val ANALYTICS = "$BASE-analytics-ktx"
        const val CRASHLYTICS = "$BASE-crashlytics-ktx"
        const val FIRESTORE = "$BASE-firestore"
    }

    object Koin {
        private const val BASE = "io.insert-koin:koin"
        const val ANDROID = "$BASE-android:${Versions.KOIN}"
        const val CORE = "$BASE-core:${Versions.KOIN}"
    }

    object Ktor {
        const val COMMON_CORE = "io.ktor:ktor-client-core:${Versions.KTOR}"
        const val COMMON_JSON = "io.ktor:ktor-client-json:${Versions.KTOR}"
        const val COMMON_LOGGING = "io.ktor:ktor-client-logging:${Versions.KTOR}"
        const val COMMON_SERIALIZATION = "io.ktor:ktor-client-serialization:${Versions.KTOR}"
        const val ANDROID = "io.ktor:ktor-client-okhttp:${Versions.KTOR}"
        const val IOS = "io.ktor:ktor-client-ios:${Versions.KTOR}"
    }

    object Moko {
        private const val BASE = "dev.icerock.moko"
        const val MVVM = "$BASE:mvvm:${Versions.Moko.MVVM}"
        const val PERMISSIONS = "$BASE:permissions:${Versions.Moko.PERMISSIONS}"
    }

    object MultiplatformSettings {
        const val CORE = "com.russhwolf:multiplatform-settings:${Versions.MULTIPLATFORM_SETTINGS}"
        const val TEST =
            "com.russhwolf:multiplatform-settings-test:${Versions.MULTIPLATFORM_SETTINGS}"
    }

    object SqlDelight {
        const val BASE = "com.squareup.sqldelight"
        const val RUNTIME = "$BASE:runtime:${Versions.SQLDELIGHT}"
        const val RUNTIME_JDK = "$BASE:runtime-jvm:${Versions.SQLDELIGHT}"
        const val DRIVER_IOS = "$BASE:native-driver:${Versions.SQLDELIGHT}"
        const val DRIVER_ANDROID = "$BASE:android-driver:${Versions.SQLDELIGHT}"
    }

    /* Dependencies for the Android app */
    object Android {

        const val DESUGARING = "com.android.tools:desugar_jdk_libs:${Versions.Android.DESUGARING}"
        const val FACEBOOK =
            "com.facebook.android:facebook-android-sdk:${Versions.Android.FACEBOOK}"
        const val MATERIAL = "com.google.android.material:material:${Versions.Android.MATERIAL}"
        const val MATERIAL_DIALOGS =
            "com.afollestad.material-dialogs:core:${Versions.Android.MATERIAL_DIALOGS}"
        const val MORF = "com.guerinet:morf:${Versions.Android.MORF}"
        const val RETROFIT_COROUTINES_ADAPTER =
            "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.Android.RETROFIT_COROUTINES_ADAPTER}"
        const val HAWK = "com.orhanobut:hawk:${Versions.Android.HAWK}"
        const val PAGE_INDICATOR =
            "com.github.romandanylyk:PageIndicatorView:${Versions.Android.PAGE_INDICATOR}"
        const val MOSHI = "com.squareup.moshi:moshi:${Versions.Android.MOSHI}"
        const val OKHTTP_LOGGING =
            "com.squareup.okhttp3:logging-interceptor:${Versions.Android.OKHTTP}"
        const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.Android.OKHTTP}"
        const val OKIO = "com.squareup.okio:okio:${Versions.Android.OKIO}"
        const val PICASSO = "com.squareup.picasso:picasso:${Versions.Android.PICASSO}"
        const val RETROFIT_MOSHI =
            "com.squareup.retrofit2:converter-moshi:${Versions.Android.RETROFIT}"
        const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.Android.RETROFIT}"
        const val JSOUP = "org.jsoup:jsoup:${Versions.Android.JSOUP}"
        const val JSOUP_NEW = "org.jsoup:jsoup:${Versions.Android.JSOUP_NEW}"


        object AndroidX {
            private const val BASE = "androidx"
            private const val LIFECYCLE_BASE = "$BASE.lifecycle:lifecycle"
            const val APPCOMPAT =
                "androidx.appcompat:appcompat:${Versions.Android.AndroidX.APPCOMPAT}"
            const val BROWSER = "$BASE.browser:browser:${Versions.Android.AndroidX.BROWSER}"
            const val CARDVIEW = "$BASE.cardview:cardview:${Versions.Android.AndroidX.CARDVIEW}"
            const val CONSTRAINT_LAYOUT =
                "$BASE.constraintlayout:constraintlayout:${Versions.Android.AndroidX.CONSTRAINT_LAYOUT}"
            const val CORE_KTX = "$BASE.core:core-ktx:${Versions.Android.AndroidX.CORE_KTX}"
            const val FRAGMENT_KTX =
                "$BASE.fragment:fragment-ktx:${Versions.Android.AndroidX.FRAGMENT_KTX}"
            const val LIFECYCLE_LIVEDATA =
                "$LIFECYCLE_BASE-livedata-ktx:${Versions.Android.AndroidX.LIFECYCLE}"
            const val LIFECYCLE_RUNTIME =
                "$LIFECYCLE_BASE-runtime-ktx:${Versions.Android.AndroidX.LIFECYCLE}"
            const val LIFECYCLE_VIEWMODEL =
                "$LIFECYCLE_BASE-viewmodel-ktx:${Versions.Android.AndroidX.LIFECYCLE}"
            const val RECYCLERVIEW =
                "$BASE.recyclerview:recyclerview:${Versions.Android.AndroidX.RECYCLERVIEW}"
            const val ROOM = "$BASE.room:room-runtime:${Versions.Android.AndroidX.ROOM}"
            const val ROOM_COMPILER = "$BASE.room:room-compiler:${Versions.Android.AndroidX.ROOM}"
        }

        object PlayServices {
            const val ANALYTICS =
                "com.google.android.gms:play-services-analytics:${Versions.Android.PLAY_SERVICES_ANALYTICS}"
            const val MAPS =
                "com.google.android.gms:play-services-maps:${Versions.Android.PLAY_SERVICES_MAPS}"
        }

        object Suitcase {
            private const val BASE = "com.guerinet.Suitcase"
            const val COROUTINES = "$BASE:coroutines:${Versions.Android.SUITCASE}"
            const val DATE = "$BASE:date:${Versions.Android.SUITCASE}"
            const val DATE_ANDROID = "$BASE:date-android:${Versions.Android.SUITCASE}"
            const val DIALOG = "$BASE:dialog:${Versions.Android.SUITCASE}"
            const val FIREBASE_ANALYTICS = "$BASE:firebase-analytics:${Versions.Android.SUITCASE}"
            const val IO = "$BASE:io:${Versions.Android.SUITCASE}"
            const val LIFECYCLE = "$BASE:lifecycle:${Versions.Android.SUITCASE}"
            const val LOG = "$BASE:log:${Versions.Android.SUITCASE}"
            const val SETTINGS = "$BASE:settings:${Versions.Android.SUITCASE}"
            const val ROOM = "$BASE:room:${Versions.Android.SUITCASE}"
            const val UI = "$BASE:ui:${Versions.Android.SUITCASE}"
            const val UTIL = "$BASE:utils:${Versions.Android.SUITCASE}"
        }
    }
}
