package com.notes.ui

import notes.ui.generated.resources.Res
import notes.ui.generated.resources.cloud_sync_icon
import notes.ui.generated.resources.firebase
import notes.ui.generated.resources.format_h1
import notes.ui.generated.resources.format_h2
import notes.ui.generated.resources.format_h3
import notes.ui.generated.resources.format_h4
import notes.ui.generated.resources.format_h5
import notes.ui.generated.resources.format_h6
import notes.ui.generated.resources.google
import notes.ui.generated.resources.googledrive

fun initResources() {
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