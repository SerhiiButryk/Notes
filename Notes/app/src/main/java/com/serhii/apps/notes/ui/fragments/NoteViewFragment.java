package com.serhii.apps.notes.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.activities.SettingsActivity;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.utils.NotesRecyclerAdapter;
import com.serhii.apps.notes.ui.view_model.NotesViewModel;
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory;
import com.serhii.core.log.Log;

import java.util.ArrayList;
import java.util.List;

public class NoteViewFragment extends Fragment {

    private static final String TAG = "NoteViewFragment";

    private static final int NOTES_COLUMN_COUNT = 2;

    private FloatingActionButton actionButton;
    private RecyclerView notesRecyclerView;
    private NotesViewModel notesViewModel;
    private NotesRecyclerAdapter adapter;
    private NoteInteraction interaction;
    private Toolbar toolbar;
    private ImageView noNotesImage;
    private TextView noNotesText;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            interaction = (NoteInteraction) context;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Should implement " + NoteInteraction.class.getSimpleName() + " listener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = initView(inflater, container);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interaction != null) {
                    interaction.onOpenNote(null);
                }
            }
        });

        adapter = new NotesRecyclerAdapter(new NotesRecyclerAdapter.NoteViewHolder.ClickListener() {
            @Override
            public void onClick(NoteModel noteModel) {
                if (interaction != null) {
                    interaction.onOpenNote(noteModel);
                }
            }
        });

        notesRecyclerView.setAdapter(adapter);
        notesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), NOTES_COLUMN_COUNT));

        return v;
    }

    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_notes_view, container, false);
        // Set references
        actionButton = view.findViewById(R.id.fab);
        notesRecyclerView = view.findViewById(R.id.note_recycler_view);
        toolbar = view.findViewById(R.id.toolbar);
        noNotesImage = view.findViewById(R.id.placeholder_imv);
        noNotesText = view.findViewById(R.id.placeholder_txv);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        notesViewModel = new ViewModelProvider(getActivity(), new NotesViewModelFactory(getActivity().getApplication()))
                .get(NotesViewModel.class);

        notesViewModel.getNotes().observe(getActivity(), new Observer<List<NoteModel>>() {
            @Override
            public void onChanged(List<NoteModel> noteModels) {
                Log.info(TAG, "onChanged() new data received, size = " + noteModels.size());
                if (!noteModels.isEmpty()) {
                    // Hide "no notes" image and text
                    noNotesImage.setVisibility(View.GONE);
                    noNotesText.setVisibility(View.GONE);
                } else {
                    // Show "no notes" image and text
                    noNotesImage.setVisibility(View.VISIBLE);
                    noNotesText.setVisibility(View.VISIBLE);
                }
                adapter.setDataChanged(notesViewModel.getNotes().getValue());
            }
        });

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_view_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_item :
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return false;
    }

    public interface NoteInteraction {
        void onOpenNote(NoteModel note);
    }

}
