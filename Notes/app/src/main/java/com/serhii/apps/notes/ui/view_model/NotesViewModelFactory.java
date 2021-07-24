package com.serhii.apps.notes.ui.view_model;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.serhii.core.log.Log;

/**
 *  Model view factory for NotesViewActivity
 */

public class NotesViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private static final String TAG = NotesViewModelFactory.class.getSimpleName();

    private Context context;

    public NotesViewModelFactory(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        ViewModel viewModel = new NotesViewModel(context);

        Log.info(TAG, "create(), created: " + viewModel);

        return (T) viewModel;
    }

}
