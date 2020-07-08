package com.example.notes.test;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.example.notes.test.databinding.ActivityNotesBinding;
import com.example.notes.test.ui.utils.UserInactivityManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.notes.test.control.NativeBridge;
import com.example.notes.test.db.LocalDataBase;
import com.example.notes.test.ui.data_model.NoteModel;
import com.example.notes.test.control.logic.IAuthorize;
import com.example.notes.test.ui.utils.NotesRecyclerAdapter;
import com.example.core.common.log.Log;
import com.example.core.utils.GoodUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.notes.test.NoteEditorActivity.EDITOR_ACTIVITY_INVALID_NOTE_ID;
import static com.example.notes.test.NoteEditorActivity.EDITOR_ACTIVITY_NOTE_CANNOT_BE_DELETED;
import static com.example.notes.test.common.AppUtils.RUNTIME_LIBRARY;

public class NotesViewActivity extends AppCompatActivity implements IAuthorize {

    private static final String TAG = "NotesViewActivity";

    // Intent extra values
    private static final int EDITOR_ACTIVITY_REQUEST_CODE = 199;

    private FloatingActionButton actionButton;
    private RecyclerView notesRecyclerView;

    private List<NoteModel> _uiData = new ArrayList<>();
    private NotesRecyclerAdapter adapter;

    private NativeBridge nativeBridge = new NativeBridge();

    private NotesRecyclerAdapter.ClickListener noteViewClickListener = new NotesRecyclerAdapter.ClickListener() {
        @Override
        public void onClick(int position) {

            NoteModel uiData = null;

            try {
                uiData = _uiData.get(position);
            } catch (IndexOutOfBoundsException exception) {
                Log.info(TAG, "no note in list, position [" + position + "] is invalid");
            }

            if (uiData != null) {
                /*
                    Indicates if this is a request to open the user note or the system template one
                */
                int noteID = position;

                Intent intent = NoteEditorActivity.newIntent(NotesViewActivity.this,
                        uiData.isTemplate(NotesViewActivity.this), uiData.isTemplate(NotesViewActivity.this)
                                ? EDITOR_ACTIVITY_INVALID_NOTE_ID : noteID);

                /**
                 *   Database issue:
                 *
                 *   If there is at least 2 notes in the list and position = 0
                 *   SQL database will return an error. To overcome this show a message to user
                 *   to inform about this case and prevent a user from a deletion the first note.
                 *
                 *   User still can remove all notes one by one starting from the last note.
                 *
                 */
                if (_uiData.size() >= 2 && position == 0) {
                    intent.putExtra(EDITOR_ACTIVITY_NOTE_CANNOT_BE_DELETED, true);
                }

                startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST_CODE);

            } else {
                throw new IllegalStateException("Corresponding uiData object was not found");
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable unsecure screen content settings
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        /**
         * Start authorization process
         */
        authorizeUser(savedInstanceState);

        initNative(this);

        UserInactivityManager.getInstance().initManager(this);

        Log.info(TAG, "onCreate()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start inactivity listener
        UserInactivityManager.getInstance().scheduleAlarm();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        // Reschedule
        UserInactivityManager.getInstance().cancelAlarm();
        UserInactivityManager.getInstance().scheduleAlarm();
    }

    @Override
    protected void onDestroy() {
        Log.info(TAG, "onDestroy()");

        _uiData.clear();
        LocalDataBase.getInstance().close();

        // Cancel alarm
        UserInactivityManager.getInstance().cancelAlarm();

        super.onDestroy();
    }

    /**
     *  View bindings
     */
    private void initBinding() {
        ActivityNotesBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_notes);

        // Set references
        actionButton = binding.fab;
        notesRecyclerView = binding.noteListView.noteRecyclerView;
    }

    private void initActivity() {
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = NoteEditorActivity.newIntent(NotesViewActivity.this);

                startActivityForResult(intent, EDITOR_ACTIVITY_REQUEST_CODE);
            }
        });

        LocalDataBase.getInstance().initDb(getApplicationContext());

        adapter = new NotesRecyclerAdapter(new ArrayList<NoteModel>(),this);
        adapter.setClickListener(noteViewClickListener);
        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_item :
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDITOR_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                if (NoteEditorActivity.isUpdateNotesNeeded(data)) {

                    prepareLayout();
                }

            }
        }
    }

    /**
     *  Called when user is authorized
     */
    @Override
    public void onAuthorization() {
        Log.info(TAG, "onAuthorize() - User is authorized");

        initBinding();
        initActivity();

        prepareLayout();

        // If user entered password several times
        // and have failed to login, login limit should be updated here
        int loginLimitFromSettings = nativeBridge.getAttemptLimit();
        nativeBridge.setLimitLeft(loginLimitFromSettings);
    }

    @Override
    public void onRegistration() {
        /**
         * not used
         */
    }

    @Override
    public void onBiometricLogin() {
        /**
         * not used
         */
    }

    private void prepareLayout() {
        List<NoteModel> uiData = LocalDataBase.getInstance().getRecords();

        _uiData.clear();

        if (uiData.isEmpty()) {

            NoteModel note = NoteModel.createTemplateNote(this);

            // Add to the list
            _uiData.add(note);

        } else {
            // Add to the list
            _uiData.addAll(uiData);
        }

        adapter.setDataChanged(_uiData);

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
            onAuthorization();
        }
    }

    /**
     *  Native interface
     *
     */
    private void initNative(Context context) {
        initFileSystem(GoodUtils.getFilePath(context));
    }

    private native void initFileSystem(String path);

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}
