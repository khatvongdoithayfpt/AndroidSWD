package com.example.ui.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.example.callback.ConnectionPoolCallback;
import com.example.callback.ManageImageCallback;
import com.example.callback.UploadBabyCharacteristicCallback;
import com.example.connection.API;
import com.example.connection.LuxandAPI;
import com.example.model.BabyCharacteristic;
import com.example.ui.activity.R;

import java.util.List;

public class UpdateMultipleImageFragment extends Fragment {

    private ImageView imgView1;
    private ImageView imgView2;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupSkin;
    private Button btnUpload;
    private Button btnNext;
    private Context mContext;
    private List<API> connectionPool;
    private List<Uri> partner1Uris;
    private List<Uri> partner2Uris;

    public UpdateMultipleImageFragment(Context mContext, List<API> connectionPool, List<Uri> partner1Uris, List<Uri> partner2Uris) {
        this.mContext = mContext;
        this.connectionPool = connectionPool;
        this.partner1Uris = partner1Uris;
        this.partner2Uris = partner2Uris;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_update_multiple_image, container, false);
        imgView1 = rootView.findViewById(R.id.imageView);
        imgView2 = rootView.findViewById(R.id.imageView2);
        radioGroupGender = rootView.findViewById(R.id.radio_group_gender);
        radioGroupSkin = rootView.findViewById(R.id.radio_group_skin);
        btnUpload = rootView.findViewById(R.id.btnUpload);
        btnNext = rootView.findViewById(R.id.btnNext);
        setEvent(rootView);

        return rootView;
    }

    private void setEvent(final View rootView) {
        imgView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ManageImageCallback)mContext).openManageImage(1);
            }
        });
        imgView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ManageImageCallback)mContext).openManageImage(2);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BabyCharacteristic babyCharacteristic = new BabyCharacteristic();
                int value;

                RadioButton rb =rootView.findViewById(
                        radioGroupGender.getCheckedRadioButtonId());
                value = Integer.valueOf(rb.getTag().toString());
                babyCharacteristic.setSex(value);

                rb =rootView.findViewById(
                        radioGroupSkin.getCheckedRadioButtonId());
                value = Integer.valueOf(rb.getTag().toString());
                babyCharacteristic.setSkin(value);

                ((UploadBabyCharacteristicCallback)mContext).UploadBabyCharacteristic(babyCharacteristic);
                uploadImagetoRemote();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ConnectionPoolCallback)mContext).showResult();
            }
        });
    }


    private void uploadImagetoRemote(){
        int countConnection = partner1Uris.size()*partner2Uris.size();
        for(int i = 0 ; i < countConnection-1;i++){
            connectionPool.add(new LuxandAPI());
        }
        ((ConnectionPoolCallback)mContext).initPoolConnection();
        ((ConnectionPoolCallback)mContext).uploadImage();
    }


}
