package com.serhii.apps.notes.activities;

import static com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.control.NativeBridge;
import com.serhii.apps.notes.control.base.IAuthorizeUser;
import com.serhii.apps.notes.control.broadcast.InactivityEventReceiver;
import com.serhii.apps.notes.control.managers.AppForegroundListener;
import com.serhii.apps.notes.control.managers.BackupManager;
import com.serhii.apps.notes.control.managers.BiometricAuthManager;
import com.serhii.apps.notes.control.managers.InactivityManager;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.fragments.NoteEditorFragment;
import com.serhii.apps.notes.ui.fragments.NoteViewFragment;
import com.serhii.apps.notes.ui.view_model.NotesViewModel;
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory;
import com.serhii.core.log.Log;
import com.serhii.core.security.impl.crypto.CryptoError;
import com.serhii.core.utils.GoodUtils;

public class NotesViewActivity extends AppCompatActivity implements IAuthorizeUser,
        NoteViewFragment.NoteInteraction, NoteEditorFragment.EditorNoteInteraction {

    private static final String TAG = "NotesViewActivity";

    private NotesViewModel notesViewModel;
    private final AppForegroundListener appForegroundListener = new AppForegroundListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.info(TAG, "onCreate() IN");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        // Enable unsecured screen content settings
        GoodUtils.enableUnsecureScreenProtection(this);

        // Need to call initNativeConfigs() before authorization process at the beginning.
        String filePath = GoodUtils.getFilePath(this);
        initNativeConfigs(filePath);

        if (savedInstanceState == null) {
            addFragment();
        }

        // Start authorization process.
        authorizeUser(savedInstanceState);

        // Initialize lifecycle aware components
        getLifecycle().addObserver(appForegroundListener);

        Log.info(TAG, "onCreate() OUT");
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
        // TODO: Need also check whether Authorize activity has already shown
        InactivityManager.getInstance().onUserInteraction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InactivityEventReceiver.checkIfInactivityTimeoutReceived(this);
        // Trigger time out
        InactivityManager.getInstance().scheduleAlarm();
        Log.info(TAG, "onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.info(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        Log.info(TAG, "onDestroy()");
        super.onDestroy();
        BackupManager.getInstance().clearNotesViewModelWeakReference();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (BiometricAuthManager.isUnlockActivityResult(requestCode, resultCode)) {
            Log.info(TAG, "onActivityResult(), reload data");
            notesViewModel.resetErrorState();
            notesViewModel.updateData();
        }

    }

    /**
     *  Called when user is authorized
     */
    @Override
    public void onUserAuthorized() {
        Log.info(TAG, "onAuthorize(), User is authorized");

        NativeBridge nativeBridge = new NativeBridge();
        nativeBridge.resetLoginLimitLeft(this);

        notesViewModel = new ViewModelProvider(this, new NotesViewModelFactory(getApplication())).get(NotesViewModel.class);
        notesViewModel.getErrorStateData().observe(this, new Observer<CryptoError>() {
            @Override
            public void onChanged(CryptoError cryptoError) {
                if (cryptoError == CryptoError.USER_NOT_AUTHORIZED) {
                    // Request KeyStore Unlock
                    Log.info(TAG, "onChanged() request keystore unlock");

                    BiometricAuthManager.requestUnlockActivity(NotesViewActivity.this);
                }
            }
        });

        notesViewModel.updateData();

        BackupManager.getInstance().setNotesViewModelWeakReference(notesViewModel);
    }

    @Override
    public void onOpenNote(NoteModel noteModel) {

        Log.info(TAG, "onOpenNote()");

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(NoteEditorFragment.FRAGMENT_TAG) != null) {
            Log.info(TAG, "onOpenNote() fragment is already opened, return");
            return;
        }

        Bundle args = new Bundle();

        if (noteModel == null) {
            // Handle add note button click
            args.putString(NoteEditorFragment.ARG_NOTE_ID, NoteEditorFragment.ARG_NOTE_TEMPLATE);
            args.putString(NoteEditorFragment.ARG_ACTION, NoteEditorFragment.ACTION_NOTE_CREATE);
        } else {
            // Handle open note button click
            if (noteModel.isTemplate(this)) {
                args.putString(NoteEditorFragment.ARG_NOTE_ID, NoteEditorFragment.ARG_NOTE_TEMPLATE);
            } else {
                args.putString(NoteEditorFragment.ARG_NOTE_ID, noteModel.getId());
            }

            args.putString(NoteEditorFragment.ARG_ACTION, NoteEditorFragment.ACTION_NOTE_OPEN);
        }

        fm.beginTransaction().add(R.id.main_layout, NoteEditorFragment.class, args, NoteEditorFragment.FRAGMENT_TAG)
                .setReorderingAllowed(true) // Needed for optimization
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onDeleteNote() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void onBackNavigation() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    // This will authorization activity for user to login
    private void authorizeUser(Bundle bundle) {
        /**
         *  Activity is launched first time
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
    private native void initNativeConfigs(String path);

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}
