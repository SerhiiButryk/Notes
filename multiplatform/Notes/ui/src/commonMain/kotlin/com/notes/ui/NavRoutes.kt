package com.notes.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object LoginScreen : NavKey

@Serializable
data object RegistrationScreen : NavKey

@Serializable
data object SettingsScreen : NavKey

@Serializable
data object AccountInfoScreen : NavKey

@Serializable
data object PreviewScreen : NavKey

@Serializable
data object OnBoardingNoteScreen : NavKey

val destinations = arrayOf(LoginScreen::class, RegistrationScreen::class,
    SettingsScreen::class, AccountInfoScreen::class,
    PreviewScreen::class, OnBoardingNoteScreen::class)
