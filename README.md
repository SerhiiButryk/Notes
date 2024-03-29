# Notes application for Android

# Overview

This application is developed for keeping and accessing all your notes securely on a device. 
The app encryptes your notes before saving it to storage. So, it couldn't be extracted and read easily. The app protects data with authentication
and it provides simple interface for use.

# Get on Google Play

[<img src="https://www.logo.wine/a/logo/Google_Play/Google_Play-Logo.wine.svg" width="400" />](https://play.google.com/store/apps/details?id=com.serhii.apps.notes)

# Features

- User authentication
- Basic text editing
- Limitation of user login attempts
- Application inactivity lock (UI app lock after a timeout)
- Secure data storage
- Backup/restore support
- A search across all your notes

# Repository structure

- external-libs/ - libraries folder
- Notes/ - app root folder
- tools/ - helper scripts
- gradle_configs/ - common project gradle settings

# Used technologies

- Languages: Kotlin, JNI, C++17

- Libraries: 

1) OpenSSL (1.1.1v) - https://www.openssl.org/source/
2) Boost - https://www.boost.org/ 
3) CSCRYPTO - https://www.copperspice.com/docs/cs_crypto/basic_what.html

- Build tools: NDK, Gradle
- Android conponents/libraries: Jatpack libraries, fragments, view model, SQL database.

# Supported platforms

- Android version: Android 8 - Android 13
- Supported ABIs: x86, x86_64, armeabi-v7a, arm64-v8a

# Localization support

- ua (Ukraine)
- ru (Russia)
- uk (English)

# Build instructions

Required tools:

1) NDK verions: 21.4.7075529
2) Java version: 11
2) Kotlin version: 1.6

Use gradlew or Android Studio ('Build' menu) to build this application

# Unit Tests

Find tests under 'Notes/app/src/androidTest' folder.

# App designs

<img src="images/Screenshot_1.png" height="420" width="220"> <img src="images/Screenshot_2.png" height="420" width="220">
<img src="images/Screenshot_3.png" height="420" width="220"> <img src="images/Screenshot_4.png" height="420" width="220"> 
<img src="images/Screenshot_5.png" height="420" width="220"> <img src="images/Screenshot_6.png" height="420" width="220">
<img src="images/Screenshot_7.png" height="420" width="220"> <img src="images/Screenshot_8.png" height="420" width="220">
<img src="images/Screenshot_9.png" height="420" width="220">

# Contacts or questions

Reach out to me: sergeybutr@gmail.com.