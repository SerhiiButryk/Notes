package com.notes.ui

import kotlinx.serialization.Serializable

/**
 * Define new screen using this base class.
 */
@Serializable
open class Screen(
    val route: String,
)
