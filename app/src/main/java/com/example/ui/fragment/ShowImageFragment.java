package com.example.ui.fragment;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.ImageAdapter;
import com.example.decoration.SpacesItemDecoration;
import com.example.ui.activity.R;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowImageFragment extends Fragment {

    private Context mContext;
    private List<Uri> uris;
    private List<String> links;
    private final int SELECT_PICTURES = 102;
    private   RecyclerView recyclerView;
    private   ImageAdapter adapter;

    public ShowImageFragment(Context context) {
        this.mContext = context;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<Uri> getUris() {
        return uris;
    }

    public void setUris(List<Uri> uris) {
        this.uris = uris;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_image, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.nav_add).setVisible(true);
        menu.findItem(R.id.nav_add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.nav_add){
                    eventAddImage();
                    return true;
                }
                return false;
            }
        });
    }

    public void init(View rootView) {
        recyclerView = rootView.findViewById(R.id.image_container);
        GridLayoutManager layoutManager = new GridLayoutManager(rootView.getContext(), 2);
        adapter = new ImageAdapter(uris,links, mContext);
        int spanCount = 2;
        int spacing = 5;
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new SpacesItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PICTURES){
            if(resultCode == Activity.RESULT_OK){
                if(data.getClipData() != null){
                   for(int i = 0; i < data.getClipData().getItemCount();i++){
                        uris.add(data.getClipData().getItemAt(i).getUri());
                   }
                }else if(data.getData() != null){
                    uris.add(data.getData());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void eventAddImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURES);
    }
}
