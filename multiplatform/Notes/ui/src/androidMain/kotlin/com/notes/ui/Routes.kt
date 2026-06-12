package com.notes.ui

import kotlinx.serialization.Serializable

// Object: Use an object for routes without arguments.
// Class: Use a class or data class for routes with arguments.

@Serializable
open class Screen

@Serializable
class Auth : Screen()

@Serializable
class Access(
    val showChangePasswordUI: Boolean = false,
    val forceLoginUI: Boolean = false
) : Screen()

@Serializable
class OnBoardingScreen : Screen()

@Serializable
class EmailVerification : Screen()

@Serializable
class NotesPreview : Screen()

@Serializable
class MainContent : Screen()

@Serializable
class NotesSettings : Screen()

@Serializable
class NotesAccount : Screen()

@Serializable
class MediaPreview(val uri: String, val name: String) : Screen()