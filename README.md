# Notes

# Overview

The app is developed for keeping all your notes in one place (passwords, plannings, film lists ets). Notes are saved encrypted in app database.
It provides simple interface and pleasant design.

# Features

- Password and biometric authentication
- Basic editing
- Login password limitation
- Idle lock
- Apllication block
- User authentication enforcement for the key material usage

# Implementation details

- Separated UI logic to fragments
- Separated presentation and bisness logic with MVVM pattern
- Separeted code into android/native libraries and modules
- Low level logic is written in native code -> improves code structure and code flexibility
- Single entry point to native layer
- Applided decouple strategies to separate a receiver and handler of an event/request

# Repo directory structure

- external-libs/ - reusable components
- Notes/ - application root directory
- Notes/app/src/main/cpp - native code

# Used technologies

- Languages: Java/JNI, C++17
- External native libraryies: OpenSSL, Boost
- Android framework features: androidX, view's bindings, fragments, secure key store, SQL database, biomatric APIs  

# Screenshots

<img src="images/screenshot_1.png" height="400"> <img src="images/screenshot_2.png" height="400">
<img src="images/screenshot_3.png" height="400"> <img src="images/screenshot_4.png" height="400"> 
<img src="images/screenshot_5.png" height="400">

# UML class diagram

Represents implemented sender/handler of an event model between Java and Cpp code

<img src="images/diagram.png" height="400">