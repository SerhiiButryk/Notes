package com.notes.notes_ui.di

import com.notes.notes_ui.Repository
import com.notes.notes_ui.data.OfflineRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideRepo(): Repository {
        return OfflineRepository()
    }

}