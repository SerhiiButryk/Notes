# Notes application for Android

# Overview

This application is developed for keeping and accessing all your notes on a device securely. 
The app encryptes your notes before saving it to storage. So, it couldn't be extracted and read easily. The app protects data with authentication. 
The interface is simple to use.

# Features

- Password and biometric authentication
- Basic text editing
- Limitation of User login attempts
- Application inactivity lock
- Apllication block (after login attempts are exceeded)
- Secure information storage
- Backup/restore support

# Repository structure

- external-libs/ - project libraries folder
- Notes/ - project root folder

# Used technologies

- Languages: Java/JNI, C++17
- Libraries: OpenSSL (1.1.1v), Boost
- Build tools: NDK, Gradle
- Android conponents / libraries: fragments, view model, view binding, SQL database.

# Supported platforms

- Android version: Android 8 - Android 11
- Supported ABIs: x86, x86_64, armeabi-v7a

# Build instraction

Required tools:

1) NDK verions: 21.4.7075529
2) Java veriosn: 11

Use .gradlew or Android Studio ('Build' menu) to build this application

# App designs

<img src="images/Screenshot_1.jpg" height="400" width="800"> <img src="images/Screenshot_2.jpg" height="400" width="800">
<img src="images/Screenshot_7.jpg" height="400" width="800"> <img src="images/Screenshot_4.jpg" height="400" width="800"> 
<img src="images/Screenshot_5.jpg" height="400"> <img src="images/Screenshot_6.jpg" height="400">
<img src="images/Screenshot_3.jpg" height="400"> <img src="images/Screenshot_8.jpg" height="400">
