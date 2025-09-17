package com.notes.app.net

import android.content.Context
import com.notes.app.R
import com.notes.api.NetSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocalNetSettings @Inject constructor(@ApplicationContext context: Context) : NetSettings {

    private val domain = context.getString(R.string.server_domain)

    override val loginUrl: String = "http://$domain/auth/login"
    override val registerUrl: String = "http://$domain/auth/register"
    override val refreshTokenUrl: String = "http://$domain/auth/refresh"
}