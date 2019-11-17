package com.example.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.callback.TransitionFragmentCallback;
import com.example.callback.UploadBabyCharacteristicCallback;
import com.example.callback.UploadImageRemoteCallback;
import com.example.callback.UploadSavedDataCallback;
import com.example.constant.Constant;
import com.example.model.BabyCharacteristic;
import com.example.ui.activity.R;
import com.example.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadImageFragment extends Fragment {
    private static int REQUEST_CODE_IMAGE1 = 1;
    private static int REQUEST_CODE_IMAGE2 = 2;

    private ImageView imgView1;
    private ImageView imgView2;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupSkin;
    private Button btnNext;
    private Context mContext;
    private Map<String,Uri> map;

    public UploadImageFragment(Context mContext) {
       this.mContext = mContext;
        map = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_upload_image, container, false);
        imgView1 = rootView.findViewById(R.id.imageView);
        imgView2 = rootView.findViewById(R.id.imageView2);
        radioGroupGender = rootView.findViewById(R.id.radio_group_gender);
        radioGroupSkin = rootView.findViewById(R.id.radio_group_skin);
        btnNext = rootView.findViewById(R.id.btnNext);

        if(map.size() > 0){
            if(map.containsKey("uri1")){
                setImageView(imgView1,map.get("uri1"));
            }
            if(map.containsKey("uri2")){
                setImageView(imgView2,map.get("uri2"));
            }
        }
        setEvent(rootView);
        return rootView;
    }

    private void setEvent(final View rootView){
        imgView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventUpload(v,REQUEST_CODE_IMAGE1);
            }
        });
        imgView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventUpload(v,REQUEST_CODE_IMAGE2);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
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
                ((TransitionFragmentCallback)mContext).openResultFragment();
            }
        });

    }

    public void eventUpload(View view,int action) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, action);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            default:
                if (resultCode == RESULT_OK) {
                    File file = null;
                    Uri uri;
                    int action = 0;
                    if (requestCode == REQUEST_CODE_IMAGE1 && data != null) {
                        uri = data.getData();
                        setImageView(imgView1,uri);
                        map.put("uri1",uri);
                        String realPath1 = Utils.getRealPathFromURI(uri,mContext);
                        ((UploadSavedDataCallback)mContext).uploadPartner1Path(realPath1);
                        file = new File(realPath1);
                        action = Constant.UPLOAD_FILE_1;
                    } else if (requestCode == REQUEST_CODE_IMAGE2 && data != null) {
                        uri = data.getData();
                        map.put("uri2",uri);
                        setImageView(imgView2,uri);
                        String realPath2 = Utils.getRealPathFromURI(uri,mContext);
                        ((UploadSavedDataCallback)mContext).uploadPartner2Path(realPath2);
                        file = new File(realPath2);
                        action = Constant.UPLOAD_FILE_2;
                    }
                    ((UploadImageRemoteCallback)mContext).uploadDataToRemote(action,file);
                }
                break;
        }
    }

    private void setImageView(ImageView imageView, Uri uri) {
        try {
            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
