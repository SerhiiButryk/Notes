package com.notes.app.notes

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.notes.ui.CommonIcon
import com.notes.ui.cloudSyncIcon
import com.notes.ui.firebaseIcon
import com.notes.ui.googleDriveIcon
import com.notes.ui.googleIcon
import com.notes.ui.h1FormatIcon
import com.notes.ui.h2FormatIcon
import com.notes.ui.h3FormatIcon
import com.notes.ui.h4FormatIcon
import com.notes.ui.h5FormatIcon
import com.notes.ui.h6FormatIcon
import com.notes.ui.iconsCollection
import notes.composeapp.generated.resources.Res
import notes.composeapp.generated.resources.cloud_sync_icon
import notes.composeapp.generated.resources.firebase
import notes.composeapp.generated.resources.format_h1
import notes.composeapp.generated.resources.format_h2
import notes.composeapp.generated.resources.format_h3
import notes.composeapp.generated.resources.format_h4
import notes.composeapp.generated.resources.format_h5
import notes.composeapp.generated.resources.format_h6
import notes.composeapp.generated.resources.google
import notes.composeapp.generated.resources.googledrive
import java.awt.Dimension
import kotlin.collections.set

fun main() =
    application {

        initApplication()

        Window(
            onCloseRequest = ::exitApplication,
            title = "notes",
        ) {

            // Set min width & height
            window.minimumSize = Dimension(800, 600)

            EntryScreen()
        }
    }

fun initApplication() {
    // Icons: https://icons8.com/icons/set/cloud
    iconsCollection[h1FormatIcon] = CommonIcon(Res.drawable.format_h1)
    iconsCollection[h2FormatIcon] = CommonIcon(Res.drawable.format_h2)
    iconsCollection[h3FormatIcon] = CommonIcon(Res.drawable.format_h3)
    iconsCollection[h4FormatIcon] = CommonIcon(Res.drawable.format_h4)
    iconsCollection[h5FormatIcon] = CommonIcon(Res.drawable.format_h5)
    iconsCollection[h6FormatIcon] = CommonIcon(Res.drawable.format_h6)
    iconsCollection[googleIcon] = CommonIcon(Res.drawable.google)
    iconsCollection[googleDriveIcon] = CommonIcon(Res.drawable.googledrive)
    iconsCollection[firebaseIcon] = CommonIcon(Res.drawable.firebase)
    iconsCollection[cloudSyncIcon] = CommonIcon(Res.drawable.cloud_sync_icon)
}