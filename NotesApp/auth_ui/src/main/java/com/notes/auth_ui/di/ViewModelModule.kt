package com.notes.auth_ui.di

import com.notes.auth.AuthService
import com.notes.api.PlatformAPIs
import com.notes.net.NetHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideAuthService(): AuthService {
        return AuthService(netSettings = PlatformAPIs.netSettings, httpClient = NetHttpClient())
    }

}