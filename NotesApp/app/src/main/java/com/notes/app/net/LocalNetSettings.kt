package com.notes.app.net

import android.content.Context
import com.notes.app.R
import com.notes.interfaces.NetSettings

class LocalNetSettings(context: Context) : NetSettings {

    val domain = context.getString(R.string.server_domain)

    override val loginUrl: String = "http://$domain/auth/login"
    override val registerUrl: String = "http://$domain/auth/register"
    override val refreshTokenUrl: String = "http://$domain/auth/refresh"
}