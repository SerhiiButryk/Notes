package com.serhii.apps.notes.ui.view_model;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.serhii.core.log.Log;

/**
 *  View view factory for {@link com.serhii.apps.notes.ui.view_model.NotesViewModel} class
 */

public class NotesViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private static final String TAG = "NotesViewModelFactory";

    public NotesViewModelFactory(@NonNull Application application) {
        super(application);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Log.info(TAG, "create(), IN");
        T viewModel = super.create(modelClass);
        Log.info(TAG, "create(), created: " + viewModel);
        return viewModel;
    }

}
