package com.example.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.adapter.SavedImageAdapter;
import com.example.constant.Constant;
import com.example.model.SavedInformation;
import com.example.utils.CallBack;
import com.example.utils.SavedInformationRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SavedImageActivity extends AppCompatActivity implements CallBack {

    private List<SavedInformation> data;
    private SavedInformationRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_image);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        repository = new SavedInformationRepository(this);
        data = new ArrayList<>();

        repository.fetchAllData().observe(this, new Observer<List<SavedInformation>>() {
            @Override
            public void onChanged(List<SavedInformation> savedInformations) {
                data = savedInformations;
                init();
            }
        });
    }

    public void init() {
        RecyclerView recyclerView = findViewById(R.id.image_container);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        SavedImageAdapter adapter = new SavedImageAdapter(data,this);
        int spanCount = 2;
        int spacing = 10;
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new SavedImageAdapter.SpacesItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void doCallback(Object param) {
        SavedInformation savedInformation = (SavedInformation) param;
        Intent intent = new Intent();
        intent.putExtra(Constant.RESULT_PARAM,savedInformation);
        setResult(RESULT_OK,intent);
        finish();
    }
}
