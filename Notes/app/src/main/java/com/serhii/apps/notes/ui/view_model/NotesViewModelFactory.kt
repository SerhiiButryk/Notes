/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.view_model

import com.serhii.core.log.Log.Companion.info
import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.ViewModel
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory

/**
 * View view factory for [com.serhii.apps.notes.ui.view_model.NotesViewModel] class
 */
class NotesViewModelFactory(application: Application) : AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        info(TAG, "create(), IN")
        val viewModel = super.create(modelClass)
        info(TAG, "create(), created: $viewModel")
        return viewModel
    }

    companion object {
        private const val TAG = "NotesViewModelFactory"
    }
}