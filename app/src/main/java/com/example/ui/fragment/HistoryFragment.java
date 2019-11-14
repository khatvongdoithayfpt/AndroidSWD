package com.example.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.SavedImageAdapter;
import com.example.model.SavedInformation;
import com.example.ui.activity.R;
import com.example.utils.SavedInformationRepository;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private List<SavedInformation> data;
    private SavedInformationRepository repository;
    private Context mContext;

    public HistoryFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        repository = new SavedInformationRepository(mContext);
        data = new ArrayList<>();

        repository.fetchAllData().observe(this, new Observer<List<SavedInformation>>() {
            @Override
            public void onChanged(List<SavedInformation> savedInformations) {
                data = savedInformations;
                init(rootView);
            }
        });
        return rootView;
    }

    public void init( View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.image_container);
        GridLayoutManager layoutManager = new GridLayoutManager(rootView.getContext(), 2);
        SavedImageAdapter adapter = new SavedImageAdapter(data,mContext);
        int spanCount = 2;
        int spacing = 10;
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new SavedImageAdapter.SpacesItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
