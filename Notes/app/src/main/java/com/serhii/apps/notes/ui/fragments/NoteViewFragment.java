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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serhii.core.log.Log;
import com.serhii.apps.notes.R;
import com.serhii.apps.notes.activities.SettingsActivity;
import com.serhii.apps.notes.databinding.FragmentNotesViewBinding;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.fragments.base.IViewBindings;
import com.serhii.apps.notes.ui.utils.NotesRecyclerAdapter;
import com.serhii.apps.notes.ui.view_model.NotesViewModel;
import com.serhii.apps.notes.ui.view_model.NotesViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NoteViewFragment extends Fragment implements IViewBindings {

    private static final String TAG = NoteViewFragment.class.getSimpleName();

    private FloatingActionButton actionButton;
    private RecyclerView notesRecyclerView;
    private NotesViewModel notesViewModel;
    private NotesRecyclerAdapter adapter;
    private NoteInteraction interaction;
    private Toolbar toolbar;

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

        View v = initBinding(inflater, container);

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
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public View initBinding(LayoutInflater inflater, ViewGroup container) {
        FragmentNotesViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes_view, container, false);

        // Set references
        actionButton = binding.fab;
        notesRecyclerView = binding.noteListView.noteRecyclerView;
        toolbar = binding.toolbar;

        return binding.getRoot();
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

                // Add template note
                if (noteModels.isEmpty()) {
                    List<NoteModel> notes = new ArrayList<>();
                    notes.add(NoteModel.createTemplateNote(getContext()));

                    adapter.setDataChanged(notes);
                } else {
                    adapter.setDataChanged(notesViewModel.getNotes().getValue());
                }

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
            // TODO: uncomment when implemented
            //            case R.id.plans_item :
            //                startActivity(new Intent(this, PlansViewActivity.class));
            //                return true;
        }
        return false;
    }

    public interface NoteInteraction {
        void onOpenNote(NoteModel note);
    }

}
