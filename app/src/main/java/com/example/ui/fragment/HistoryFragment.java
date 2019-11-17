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
import com.example.decoration.SpacesItemDecoration;
import com.example.model.HistoryRecord;
import com.example.ui.activity.R;
import com.example.dao.HistoryRecordRepository;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private List<HistoryRecord> data;
    private HistoryRecordRepository repository;
    private Context mContext;

    public HistoryFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        repository = new HistoryRecordRepository(mContext);
        data = new ArrayList<>();

        repository.fetchAllData().observe(this, new Observer<List<HistoryRecord>>() {
            @Override
            public void onChanged(List<HistoryRecord> historyRecords) {
                data = historyRecords;
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
        int spacing = 1;
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new SpacesItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
