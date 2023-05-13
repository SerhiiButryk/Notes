# Notes application for Android

# Overview

This application is developed for keeping and accessing all your notes securely on a device. 
The app encryptes your notes before saving it to storage. So, it couldn't be extracted and read easily. The app protects data with authentication
and it provides simple interface for use.

# Features

- Password and biometric authentication
- Basic text editing
- Limitation of user login attempts
- Application inactivity lock (idle lock timeout)
- Application block (after login attempts are exceeded)
- Secure information storage
- Backup/restore support

# Repository structure

- external-libs/ - libraries folder
- Notes/ - app root folder
- tools/ - helper scripts
- gradle_configs/ - common project gradle settings

# Used technologies

- Languages: Java/JNI, Kotlin, C++17
- Libraries: OpenSSL (1.1.1v), Boost
- Build tools: NDK, Gradle
- Android conponents/libraries: Jatpack libraries, fragments, view model, SQL database.

# Supported platforms

- Android version: Android 8 - Android 13
- Supported ABIs: x86, x86_64, armeabi-v7a

# Localization

- ua (Ukraine)
- ru (Russia)
- uk (English)

# Build instruction

Required tools:

1) NDK verions: 21.4.7075529
2) Java version: 11
2) Kotlin version: 1.6

Use gradlew or Android Studio ('Build' menu) to build this application

# Tests

Find tests under 'Notes/app/src/androidTest' folder.

# App designs

<img src="images/Screenshot_1.png" height="420" width="220"> <img src="images/Screenshot_2.png" height="420" width="220">
<img src="images/Screenshot_3.png" height="420" width="220"> <img src="images/Screenshot_4.png" height="420" width="220"> 
<img src="images/Screenshot_5.png" height="420" width="220"> <img src="images/Screenshot_6.png" height="420" width="220">
<img src="images/Screenshot_7.png" height="420" width="220"> <img src="images/Screenshot_8.png" height="420" width="220">
<img src="images/Screenshot_9.png" height="420" width="220">

# Contacts

Reach out to me for questions, help or collaboration - sergeybutr@gmail.com.
