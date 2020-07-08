package com.example.notes.test;

import android.content.Context;
import android.content.Intent;

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
import com.example.notes.test.databinding.ActivityNoteEditorBinding;
import com.example.notes.test.db.LocalDataBase;
import com.example.notes.test.ui.data_model.NoteModel;
import com.example.core.utils.GoodUtils;
import com.example.notes.test.ui.utils.UserInactivityManager;

import static com.example.notes.test.NotesViewActivity.EDITOR_ACTIVITY_INVALID_NOTE_ID;

public class NoteEditorActivity extends AppCompatActivity {

    // Intent extra values
    public static final String ACTION_NOTE_OPENED = "action open note";
    public static final String UPDATE_NEEDED_EXTRA = "update needed extra";
    public static final String NOTE_TEMPLATE = "note template extra";
    public static final String NOTE_ID_EXTRA = "note key extra";

    private  EditText titleNote;
    private  EditText note;

    private  Toolbar toolbar;

    private boolean isActionNoteOpened;
    private boolean isTemplateNoteOpened;
    private int noteID;

    private String checkTitle = "";
    private String checkNoteContent = "";

    private boolean isNoteOnBackPressedSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable unsecure screen content settings
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        initBinding();
        initView();

        final Intent intent = getIntent();

        if (intent != null) {
            handleIntent(intent);
        }

        UserInactivityManager.getInstance().initManager(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start inactivity listener
        UserInactivityManager.getInstance().scheduleAlarm();
    }

    @Override
    protected void onDestroy() {

        // Save in case user updated a note
        if (!isNoteOnBackPressedSaved) {
            saveUserNote(false);
        }

        // Cancel alarm
        UserInactivityManager.getInstance().cancelAlarm();

        super.onDestroy();
    }

    /**
     *  View bindings
     */
    private void initBinding() {
        ActivityNoteEditorBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_note_editor);

        // Set references
        titleNote = binding.titleNote;
        note = binding.bodyNote;
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

                if (TextUtils.isEmpty(titleNote.getText()) && TextUtils.isEmpty(note.getText())) {
                    Toast.makeText(this, R.string.toast_action_delete_error_note, Toast.LENGTH_SHORT).show();
                    return true;
                }

                LocalDataBase.getInstance().deleteRecord(noteID);

                sendResult(true, RESULT_OK);

                Toast.makeText(this, R.string.toast_action_deleted_message, Toast.LENGTH_SHORT).show();
                finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        // Reschedule
        UserInactivityManager.getInstance().cancelAlarm();
        UserInactivityManager.getInstance().scheduleAlarm();
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
        saveUserNote(true);

        isNoteOnBackPressedSaved = true;

        finish();
    }

    private void handleIntent(Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals(ACTION_NOTE_OPENED)) {

            noteID = intent.getIntExtra(NOTE_ID_EXTRA, EDITOR_ACTIVITY_INVALID_NOTE_ID);
            boolean isTemplateNote = intent.getBooleanExtra(NOTE_TEMPLATE, false);

            /*
                If it's a request to open already existed note, then
                retrieve available data from local database
            */
            if (noteID != EDITOR_ACTIVITY_INVALID_NOTE_ID) {

                final NoteModel note = LocalDataBase.getInstance().getRecord(noteID);

                if (note != null) {
                    checkTitle = note.getNoteTitle();
                    checkNoteContent = note.getNote();

                    titleNote.setText(note.getNoteTitle());
                    this.note.setText(note.getNote());
                }

                isActionNoteOpened = true;
            }

            /*
                If it's a request to open template note, then use default values
            */
            if (isTemplateNote) {
                titleNote.setHint(getResources().getString(R.string.template_note_title));
                note.setHint(getResources().getString(R.string.template_short_note));

                isTemplateNoteOpened = true;
            }

        }

    }

    private void saveUserNote(boolean showToastMessage) {

        /*
            Checks if note needs update
        */
        if (checkIfNoteChanged()) {
            return;
        }

        String title = GoodUtils.getText(titleNote);
        String note = GoodUtils.getText(this.note);

        /*
            If action is  ACTION_NOTE_OPENED then update current record
            else create a new record in the app database
        */
        if (isActionNoteOpened) {
            LocalDataBase.getInstance().updateRecord(noteID, new NoteModel(note, title));
        } else {
            LocalDataBase.getInstance().addRecord(new NoteModel(note, title));
        }

        sendResult(true, RESULT_OK);

        /*
            Display info toast message
        */
        if (showToastMessage) {
            Toast.makeText(this, R.string.toast_action_message, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     *  Compare notes before and after editing operation
     *
     */
    private boolean checkIfNoteChanged() {

        String title = GoodUtils.getText(titleNote);
        String note = GoodUtils.getText(this.note);

        if (isEmptyNote() || (checkTitle.equals(title) && checkNoteContent.equals(note)) ) {
            sendResult(false, RESULT_OK);
            return true;
        }

        return false;
    }

    private boolean isEmptyNote() {
        return TextUtils.isEmpty(GoodUtils.getText(titleNote))
                && TextUtils.isEmpty(GoodUtils.getText(note));
    }

    private void sendResult(boolean needUpdate, int success) {
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
}
