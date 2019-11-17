package com.example.ui.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.model.HistoryRecord;
import com.example.ui.activity.R;
import com.example.utils.Utils;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedChildFragment extends Fragment {

    private Context mContext;
    private HistoryRecord historyRecord;
    private ImageView ivPartner1;
    private ImageView ivPartner2;
    private ImageView ivChild;

    public SavedChildFragment(Context mContext, HistoryRecord historyRecord) {
        this.mContext = mContext;
        this.historyRecord = historyRecord;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_saved_child, container, false);
       ivChild = rootView.findViewById(R.id.img_saved_child);
       ivPartner1 = rootView.findViewById(R.id.img_par_1);
       ivPartner2 = rootView.findViewById(R.id.img_par_2);
       setImageToImageView();
        return rootView;
    }

    private void setImageToImageView(){
        Utils.setImageView(ivChild,new File(historyRecord.getChild()));
        Utils.setImageView(ivPartner1,new File(historyRecord.getPartner1()));
        Utils.setImageView(ivPartner2,new File(historyRecord.getPartner2()));
    }
}
