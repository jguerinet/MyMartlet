# MyMartlet - Android

[![Dev Build](https://github.com/jguerinet/MyMartlet/workflows/Dev%20Build/badge.svg?branch=develop)](https://github.com/jguerinet/MyMartlet/actions?query=workflow%3A%22Dev+Build%22)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6764bc77236c4209a230db91bb2cf441)](https://app.codacy.com/app/jguerinet/MyMartlet?utm_source=github.com&utm_medium=referral&utm_content=jguerinet/MyMartlet&utm_campaign=Badge_Grade_Dashboard)

## Summary

The MyMartlet app provides a mobile portal to McGill University students to McGill's MyMcGill. It gives the user access to all of the essential MyMcGill features, such as schedule, transcript, ebill, adding/dropping courses, a campus map, and more.
This is an unofficial McGill app, and is not affiliated with McGill University whatsoever. [Download it from the Play Store](https://play.google.com/store/apps/details?id=ca.appvelopers.mcgillmobile).

## Contributions

Have a feature request? Make an issue! If you want to implement it yourself, just fork the repo, make the changes, and submit a PR.

If you do fork the repo, make sure to copy signing.properties.sample to signing.properties and google-services-sample.json to google-services.json or else the project will not build.

All contributions are welcome!

## Branches

This repository follows [Git Flow](https://nvie.com/posts/a-successful-git-branching-model/?).

-   `develop`: Current development code
-   `master`: Current production code
-   `release-*`: A new release, where \* is the new version number.
-   `hotfix-*`: A new hotfix for a released version, where \* is the new version number.
-   `appvelopers`: Code for the last version released under the old Google Play Account
-   Any other branches are either feature branches or bugfix branches.

## Packages

-   model: All models used throughout the app
-   ui: All UI classes, including activities, fragments, adapters etc.
-   util: Utility classes used throughout the app

## Gradle Dependencies (Needs to be updated)

-   [Android AppCompat](http://developer.android.com/tools/support-library/features.html#v7-appcompat)
-   [Android Design](http://developer.android.com/tools/support-library/features.html#design)
-   [Android Support v4](http://developer.android.com/tools/support-library/features.html#v4)
-   Android Support Vector Drawable
-   [Android RecyclerView](http://developer.android.com/tools/support-library/features.html#v7-recyclerview)
-   [Crashlytics](http://try.crashlytics.com/sdk-android/)
-   [Facebook](https://github.com/facebook/facebook-android-sdk)
-   [GPS - Analytics](https://developers.google.com/analytics/devguides/collection/android/v4/)
-   [GPS - Maps](https://developers.google.com/maps/documentation/android-api/)
-   [Dagger](http://google.github.io/dagger)
-   [Android Utils](https://github.com/jguerinet/android-utils)
-   [FormGenerator](https://github.com/jguerinet/form-generator)
-   [ButterKnife](https://github.com/JakeWharton/butterknife)
-   [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP)
-   [Timber](https://github.com/JakeWharton/timber)
-   [okhttp](https://github.com/square/okhttp)
-   [okio](https://github.com/square/okio)
-   [Picasso](https://github.com/square/picasso)
-   [Retrofit](https://github.com/square/retrofit)
-   [Twitter](https://dev.twitter.com/mopub/android)
-   [ViewPagerIndicator](https://github.com/JakeWharton/ViewPagerIndicator)
-   [JSoup](https://github.com/jhy/jsoup)

## Contributors

-   [Joshua David Alfaro](https://github.com/JDAlfaro) - Lead Designer
-   [Julien Guerinet](https://github.com/jguerinet) - Project Leader
-   [Rafi Uddin](https://github.com/AdnanUddin) - Android Support
-   [Ryan Singzon](https://github.com/rsingzon) - Android Developer
-   [Shabbir Hussain](https://github.com/shabbir-hussain) - Android Developer
-   [Quang Dao](https://github.com/nqdao) - Android Developer

## Version History

See the [Change Log](docs/Changelog.md).

## Copyright

     Copyright 2014-2019 Julien Guerinet

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
