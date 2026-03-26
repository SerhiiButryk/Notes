package com.notes.app

import android.app.Application
import api.AppServices
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
import com.notes.ui.previewIcon

class Notes : Application() {

    override fun onCreate() {
        super.onCreate()
        initRes()
    }

    private fun initRes() {

        // Icons: https://icons8.com/icons/set/cloud
        iconsCollection[h1FormatIcon] = CommonIcon(R.drawable.format_h1)
        iconsCollection[h2FormatIcon] = CommonIcon(R.drawable.format_h2)
        iconsCollection[h3FormatIcon] = CommonIcon(R.drawable.format_h3)
        iconsCollection[h4FormatIcon] = CommonIcon(R.drawable.format_h4)
        iconsCollection[h5FormatIcon] = CommonIcon(R.drawable.format_h5)
        iconsCollection[h6FormatIcon] = CommonIcon(R.drawable.format_h6)
        iconsCollection[googleIcon] = CommonIcon(R.drawable.google)
        iconsCollection[googleDriveIcon] = CommonIcon(R.drawable.googledrive)
        iconsCollection[firebaseIcon] = CommonIcon(R.drawable.firebase)
        iconsCollection[cloudSyncIcon] = CommonIcon(R.drawable.cloud_sync_icon)
        iconsCollection[previewIcon] = CommonIcon(R.drawable.image_preview)

        AppServices.serverClientId = getString(R.string.server_client_id)
    }
}
