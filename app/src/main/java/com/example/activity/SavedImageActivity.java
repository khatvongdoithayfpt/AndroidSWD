package com.example.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.adapter.SavedImageAdapter;
import com.example.model.SavedInformation;
import com.example.utils.SavedInformationRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedImageActivity extends AppCompatActivity {

    private List<File> files;
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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        repository = new SavedInformationRepository(this);
        files = new ArrayList<>();

        //Fake data
//        String path = "/storage/sdcard/Download/76688923_1136885503173834_3508330137223430144_n.jpg";
            repository.fetchAllData().observe(this, new Observer<List<SavedInformation>>() {
            @Override
            public void onChanged(List<SavedInformation> savedInformations) {
                for (SavedInformation information:savedInformations){
                    File file = new File(information.getChild());
                    files.add(file);
                }
                init();
            }
        });
//        String path = bundle.getString("path");
        files = new ArrayList<>();
//        for (int i = 0 ; i < 10;i++){
//            files.add(new File(path));
//        }

//        init();

    }

    public void init(){
        RecyclerView recyclerView = findViewById(R.id.image_container);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        SavedImageAdapter adapter = new SavedImageAdapter(files);
        int spanCount = 3; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new SavedImageAdapter.SpacesItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
