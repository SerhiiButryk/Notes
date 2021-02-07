package com.serhii.apps.notes.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.databinding.FragmentPlansViewBinding;
import com.serhii.apps.notes.ui.data_model.PlansModel;
import com.serhii.apps.notes.ui.fragments.base.IViewBindings;
import com.serhii.apps.notes.ui.utils.PlansRecyclerAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class PlansFragment extends Fragment implements IViewBindings {

    private static final int PLANS_LIST_COLUMN_NUMBER = 2;

    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView plansListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initBinding(inflater, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
    }

    @Override
    public View initBinding(LayoutInflater inflater, ViewGroup container) {

        FragmentPlansViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_plans_view, container, false);

        plansListView = binding.plansListLayout;

        return binding.getRoot();
    }

    private void initView() {
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);
        bottomSheetDialog.setContentView(R.layout.plans_dialog);

        List<PlansModel> plans = new ArrayList<>();
        plans.add(new PlansModel("Birthday John", "05/12/12"));
        plans.add(new PlansModel("Birthday John", "05/12/12"));
        plans.add(new PlansModel("Birthday John", "05/12/12"));
        plans.add(new PlansModel("Birthday John", "05/12/12"));

        plansListView.setLayoutManager(new GridLayoutManager(getContext(), PLANS_LIST_COLUMN_NUMBER));
        plansListView.setAdapter(new PlansRecyclerAdapter(plans, new PlansRecyclerAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                bottomSheetDialog.show();
            }
        }, getContext()));
    }


}
