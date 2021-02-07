package com.serhii.apps.notes.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.EventService;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.base.IAuthorizeUser;
import com.serhii.apps.notes.control.base.IUnlockKeystore;
import com.serhii.apps.notes.control.managers.BiometricAuthManager;
import com.serhii.apps.notes.control.managers.InactivityManager;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.fragments.NoteEditorFragment;
import com.serhii.apps.notes.ui.fragments.NoteViewFragment;
import com.serhii.apps.notes.ui.view_model.NotesViewModel;
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory;

import static com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY;

public class NotesViewActivity extends AppCompatActivity implements IAuthorizeUser,
        IUnlockKeystore, NoteViewFragment.NoteInteraction, NoteEditorFragment.EditorNoteInteraction {

    private static final String TAG = NotesViewActivity.class.getSimpleName();

    private NativeBridge nativeBridge = new NativeBridge();
    private NotesViewModel notesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        // Enable unsecured screen content settings
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        // Start authorization process
        authorizeUser(savedInstanceState);

        initNative(this);

        // Initialize lifecycle aware components
        getLifecycle().addObserver(EventService.getInstance());
        InactivityManager.getInstance().setLifecycle(this, getLifecycle());

        if (savedInstanceState == null) {
            addFragment();
        }

        Log.info(TAG, "onCreate()");
    }

    private void addFragment() {
        FragmentManager fm = getSupportFragmentManager();
        NoteViewFragment f = new NoteViewFragment();

        fm.beginTransaction().replace(R.id.main_layout, f, null)
                .setReorderingAllowed(true) // Needed for optimization
                .commit();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        InactivityManager.getInstance().onUserInteraction();
    }

    @Override
    protected void onResume() {
        super.onResume();

        notifyOnResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        notifyOnStop();
    }

    @Override
    protected void onDestroy() {
        Log.info(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (BiometricAuthManager.isUnlockActivityResult(requestCode, resultCode)) {
            Log.info(TAG, "onActivityResult() - Need to reload data");
            notesViewModel.retrieveData();
        }

    }

    /**
     *  Called when user is authorized
     */
    @Override
    public void onUserAuthorized() {
        Log.info(TAG, "onAuthorize() - User is authorized");

        // Update login limit
        nativeBridge.updateLoginLimit();

        notesViewModel = new ViewModelProvider(this, new NotesViewModelFactory(getApplication())).get(NotesViewModel.class);
    }

    @Override
    public void onUnlockKeystore() {
        Log.info(TAG, "notifyOnUnlockKeystore()");

        BiometricAuthManager.requestUnlockActivity(this);

        EventService.getInstance().notifyUnlockEventReceived();
    }

    @Override
    public void onOpenNote(NoteModel noteModel) {

        FragmentManager fm = getSupportFragmentManager();
        Bundle args = new Bundle();

        if (noteModel == null) {
            // Handle add note button click
            args.putString(NoteEditorFragment.ARG_NOTE_ID, NoteEditorFragment.ARG_NOTE_TEMPLATE);
            args.putString(NoteEditorFragment.ARG_ACTION, NoteEditorFragment.ACTION_NOTE_CREATE);
        } else {

            if (noteModel.isTemplate(this)) {
                args.putString(NoteEditorFragment.ARG_NOTE_ID, NoteEditorFragment.ARG_NOTE_TEMPLATE);
            } else {
                args.putString(NoteEditorFragment.ARG_NOTE_ID, noteModel.getId());
            }

            args.putString(NoteEditorFragment.ARG_ACTION, NoteEditorFragment.ACTION_NOTE_OPEN);
        }

        fm.beginTransaction().add(R.id.main_layout, NoteEditorFragment.class, args, null)
                .setReorderingAllowed(true) // Needed for optimization
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onNoteDeleted() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onBackPressedClicked() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    private void authorizeUser(Bundle bundle) {
        /**
         *  Activity is started by Android OS from launcher icon
         */
        if (bundle == null) {
            /*
                Start authorization activity
            */
            Intent intent = new Intent(this, AuthorizationActivity.class);
            startActivity(intent);
        } else {
            onUserAuthorized();
        }
    }

    /**
     *  Native interface
     *
     */
    private void initNative(Context context) {
        initNativeConfigs(GoodUtils.getFilePath(context));
    }

    private native void initNativeConfigs(String path);
    private native void notifyOnStop();
    private native void notifyOnResume();

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}
