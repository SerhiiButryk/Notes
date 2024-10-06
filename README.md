# Notes application for Android

# Overview

This application is developed for keeping and accessing all your notes securely on a device. 
The app encryptes your notes before saving it to the storage. So, it couldn't be extracted and read easily. The app protects data with authentication and provides simple and beautiful interface.

# Get on Google Play

[<img src="https://www.logo.wine/a/logo/Google_Play/Google_Play-Logo.wine.svg" width="400" />](https://play.google.com/store/apps/details?id=com.serhii.apps.notes)

# Features

- Password & biometric authentication
- Basic text editing
- Application screen lock (the app gets locked after some timeout)
- Secure data storage
- Backup/restore support
- A search across all your notes

# Repository structure

- external-libs/ - additinal libraries which the app is using
- Notes/ - the app project folder
- tools/ - utility scripts
- gradle_configs/ - utility project gradle settings

# Used technologies

- Languages: Kotlin, JNI, C++17

- Libraries: 

1) OpenSSL (1.1.1v) - https://www.openssl.org/source/
2) Boost - https://www.boost.org/ 
3) CSCRYPTO - https://www.copperspice.com/docs/cs_crypto/basic_what.html

- Build tools: NDK, Gradle
- Android conponents/libraries: Jatpack Compose, MVVM, SQL database.

# Supported platforms

- Android OS: Android 8 - Android 14
- ABIs: x86, x86_64, armeabi-v7a, arm64-v8a

# Localization

- ua (Ukraine)
- ru (Russia)
- uk (English)
- it (Italian)
- fr (France)
- es (Espana)
- de (Germany)
- zh-rcn (China)

# Build instructions

Required tools:

1) NDK verions: 26
2) Java version: 17
2) Kotlin version: 2.0

Use gradlew or Android Studio 'Build' menu to build this project

# Unit Tests

Find tests under 'Notes/app/src/androidTest' folder.

# App designs [This is not up-to-date]

<img src="images/Screenshot_1.png" height="420" width="220"> <img src="images/Screenshot_2.png" height="420" width="220">
<img src="images/Screenshot_3.png" height="420" width="220"> <img src="images/Screenshot_4.png" height="420" width="220"> 
<img src="images/Screenshot_5.png" height="420" width="220"> <img src="images/Screenshot_6.png" height="420" width="220">
<img src="images/Screenshot_7.png" height="420" width="220"> <img src="images/Screenshot_8.png" height="420" width="220">
<img src="images/Screenshot_9.png" height="420" width="220">

# Questions or help

Reach out to me: sergeybutr@gmail.com.