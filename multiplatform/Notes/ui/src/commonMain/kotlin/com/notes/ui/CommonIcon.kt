package com.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

expect class CommonIcon(resource: Any) {

    val resource: Any

    @Composable
    fun painter(): Painter
}

val iconsCollection = mutableMapOf<Int, CommonIcon>()

@Composable
fun getIconByKey(key: Int): Painter {
    return iconsCollection[key]!!.painter()
}

const val h1FormatIcon: Int = 1
const val h2FormatIcon: Int = 2
const val h3FormatIcon: Int = 3
const val h4FormatIcon: Int = 4
const val h5FormatIcon: Int = 5
const val h6FormatIcon: Int = 6
const val googleIcon: Int = 7
const val firebaseIcon: Int = 8
const val googleDriveIcon: Int = 9
const val cloudSyncIcon: Int = 10
const val previewIcon: Int = 11
