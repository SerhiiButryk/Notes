# Notes

# Overview

The app is developed for keeping all your notes in one place (passwords, plannings, film list ets). Notes are saved encrypted in app database.
It provides simple interface and pleasant design.

# Features

- Password and biometric authentication
- Basic editing features
- Login password limit
- Idle Lock
- Apllication Block

# Implementation details

- Separated UI logic in fragments
- Separated presentation and bisness logic with MVVM pattern
- Separeted reusable modules into library
- Low level logic is written in native code -> improves code structure and flexibility
- Java and native calls are made from single place -> improves code maintainability
- Applided decouple strategies to separate a receiver and handler of an event
- Separated code into logical levels of abstraction

# Repo directory structure

- external-libs/ - reusable components
- sdk/ - reusable native interfaces
- Notes/ - application root directory

# Demo video

[![Watch the video](https://img.youtube.com/vi/C2zxFzp1pFk/0.jpg)](https://www.youtube.com/watch?v=C2zxFzp1pFk&feature=youtu.be)
