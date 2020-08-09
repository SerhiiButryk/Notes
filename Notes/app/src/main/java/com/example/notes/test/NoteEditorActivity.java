package com.example.notes.test;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.core.common.log.Log;
import com.example.notes.test.control.EventService;
import com.example.notes.test.control.logic.IUnlockKeystore;
import com.example.notes.test.control.managers.BiometricAuthManager;
import com.example.notes.test.databinding.ActivityNoteEditorBinding;
import com.example.notes.test.db.LocalDataBase;
import com.example.notes.test.ui.data_model.NoteModel;
import com.example.core.utils.GoodUtils;
import com.example.notes.test.ui.utils.UserInactivityManager;

public class NoteEditorActivity extends AppCompatActivity implements IUnlockKeystore {

    private static final String TAG = NoteEditorActivity.class.getSimpleName();

    // Intent extra values
    public static final String ACTION_NOTE_OPENED = "action open note";
    public static final String UPDATE_NEEDED_EXTRA = "update needed extra";
    public static final String NOTE_TEMPLATE = "note template extra";
    public static final String NOTE_ID_EXTRA = "note key extra";
    public static final int EDITOR_ACTIVITY_INVALID_NOTE_ID = -1;
    public static final String EDITOR_ACTIVITY_NOTE_CANNOT_BE_DELETED = "can not be deleted";

    private  EditText titleNoteField;
    private  EditText noteFiled;

    private  Toolbar toolbar;

    private boolean isActionNoteOpened;
    private boolean isTemplateNoteOpened;
    private int noteID;

    private String checkNoteTitleContent = "";
    private String checkNoteContent = "";

    private boolean isPendingSave;
    private boolean isUpdateNeeded;

    // To overcome database issue
    private boolean cannotNoteBeDeleted;

    private Intent lastReceivedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable unsecured screen content settings
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        initBinding();
        initView();

        lastReceivedIntent = getIntent();

        handleIntent(lastReceivedIntent);

        // Lifecycle aware component
        UserInactivityManager.getInstance().setLifecycle(this, getLifecycle());

        initNative();

    }

    @Override
    protected void onDestroy() {

        notifyOnDestroy();

        super.onDestroy();
    }

    /**
     *  View bindings
     */
    private void initBinding() {
        ActivityNoteEditorBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_note_editor);

        // Set references
        titleNoteField = binding.titleNote;
        noteFiled = binding.bodyNote;
        toolbar = binding.toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_note_item :

                if (isTemplateNoteOpened) {
                    Toast.makeText(this, R.string.toast_action_delete_template_note, Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (TextUtils.isEmpty(titleNoteField.getText()) && TextUtils.isEmpty(noteFiled.getText())) {
                    Toast.makeText(this, R.string.toast_action_delete_error_note, Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (cannotNoteBeDeleted) {
                    Toast.makeText(this, R.string.toast_action_delete_error_first_note, Toast.LENGTH_SHORT).show();
                    return true;
                }

                LocalDataBase.getInstance().deleteRecord(noteID);

                setResult(true, RESULT_OK);

                Toast.makeText(this, R.string.toast_action_deleted_message, Toast.LENGTH_SHORT).show();

                finish();

            return true;

            case R.id.save_note:

                saveUserNote();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        UserInactivityManager.getInstance().onUserInteraction();
    }

    @Override
    public void onUnlockKeystore() {

        Log.info(TAG, "onUnlockKeystore()");

        BiometricAuthManager.requestUnlockActivity(this);

        EventService.getInstance().notifyEventReceived();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (BiometricAuthManager.isUnlockActivityResult(requestCode, resultCode)) {
            if (isPendingSave) {
                saveUserNote();
            } else {
                handleIntent(lastReceivedIntent);
            }
        }

    }

    private void initView() {

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_toolbar);
        }

    }

    @Override
    public void onBackPressed() {

        setResult(isUpdateNeeded, isUpdateNeeded ? RESULT_OK : RESULT_CANCELED);

        finish();
    }

    private void handleIntent(Intent intent) {

        if (intent == null)
            return;

        if (intent.getAction() != null && intent.getAction().equals(ACTION_NOTE_OPENED)) {

            noteID = intent.getIntExtra(NOTE_ID_EXTRA, EDITOR_ACTIVITY_INVALID_NOTE_ID);
            cannotNoteBeDeleted = intent.getBooleanExtra(EDITOR_ACTIVITY_NOTE_CANNOT_BE_DELETED, false);

            boolean isTemplateNote = intent.getBooleanExtra(NOTE_TEMPLATE, false);

            /*
                If it's a request to open already existed note, then
                retrieve available data from local database
            */
            if (noteID != EDITOR_ACTIVITY_INVALID_NOTE_ID) {

                final NoteModel note = LocalDataBase.getInstance().getRecord(noteID);

                if (note != null) {
                    checkNoteTitleContent = note.getNoteTitle();
                    checkNoteContent = note.getNote();

                    titleNoteField.setText(checkNoteTitleContent);
                    noteFiled.setText(checkNoteContent);
                }

                isActionNoteOpened = true;
            }

            /*
                If it's a request to open template note, then use default values
            */
            if (isTemplateNote) {
                titleNoteField.setHint(getResources().getString(R.string.template_note_title));
                noteFiled.setHint(getResources().getString(R.string.template_short_note));

                isTemplateNoteOpened = true;
            }

        }

    }

    private void saveUserNote() {

        String title = GoodUtils.getText(titleNoteField);
        String note = GoodUtils.getText(noteFiled);

        if (title.isEmpty() && note.isEmpty()) {

            Toast.makeText(this, R.string.toast_action_error_note_is_empty, Toast.LENGTH_SHORT).show();

            return;
        }

        /*
            Checks if note needs update
        */
        if (checkIfNoteChanged()) {

            Toast.makeText(this, R.string.toast_action_error_note_is_not_changed, Toast.LENGTH_SHORT).show();

            return;
        }

        /*
            If action is  ACTION_NOTE_OPENED then update current record
            else create a new record in the app database
        */

        boolean result;

        isPendingSave = true;

        if (isActionNoteOpened) {
            result = LocalDataBase.getInstance().updateRecord(noteID, new NoteModel(note, title));
        } else {
            result = LocalDataBase.getInstance().addRecord(new NoteModel(note, title));
        }

        /*
            Display info toast message
        */
        if (result) {
            Toast.makeText(this, R.string.toast_action_message, Toast.LENGTH_SHORT).show();

            isPendingSave = false;
            isUpdateNeeded = true;
        }

    }

    /**
     *  Compare notes before and after editing operation
     *
     */
    private boolean checkIfNoteChanged() {

        String title = GoodUtils.getText(titleNoteField);
        String note = GoodUtils.getText(noteFiled);

        return  isEmptyNote() || (checkNoteTitleContent.equals(title) && checkNoteContent.equals(note));
    }

    private boolean isEmptyNote() {
        return TextUtils.isEmpty(GoodUtils.getText(titleNoteField))
                && TextUtils.isEmpty(GoodUtils.getText(noteFiled));
    }

    private void setResult(boolean needUpdate, int success) {
        Intent extraData = new Intent();
        extraData.putExtra(UPDATE_NEEDED_EXTRA, needUpdate);

        setResult(success, extraData);
    }

    /**
     *  Helper function to create an Intent for starting activity
     *
     */

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, NoteEditorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        return intent;
    }

    public static Intent newIntent(Context context, boolean isTemplateNote, int noteID) {
        Intent intent = new Intent(context, NoteEditorActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction(NoteEditorActivity.ACTION_NOTE_OPENED);
        intent.putExtra(NoteEditorActivity.NOTE_ID_EXTRA, noteID);
        intent.putExtra(NoteEditorActivity.NOTE_TEMPLATE, isTemplateNote);

        return intent;
    }

    /**
     *  Helper function to check a result for the caller activity
     */
    public static boolean isUpdateNotesNeeded(Intent intent) {
        if (intent == null) {
            return false;
        }

        return intent.getBooleanExtra(NoteEditorActivity.UPDATE_NEEDED_EXTRA, false);
    }

    private void initNative() { initNativeConfigs(); }

    private native void initNativeConfigs();
    private native void notifyOnDestroy();
}
