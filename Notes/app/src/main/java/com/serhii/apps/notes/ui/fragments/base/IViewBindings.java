package com.serhii.apps.notes.ui.fragments.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface IViewBindings {
    /**
     * Binds a view using a fragment inflater
     * @return a reference to a root view
     */
    View initBinding(LayoutInflater inflater, ViewGroup container);
}
