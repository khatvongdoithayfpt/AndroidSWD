package com.example.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.connection.API;
import com.example.connection.LuxandAPI;
import com.example.constant.Const;
import com.example.model.BabyCharacteristic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static int REQUEST_CODE_IMAGE1 = 1;
    private static int REQUEST_CODE_IMAGE2 = 2;
    private ImageView imgView1;
    private ImageView imgView2;
    private ImageView imgView3;
    private Button btnUpdateLoad1;
    private Button btnUpdateLoad2;
    private ImageButton imageButton;
    private String realPath1 = "";
    private String realPath2 = "";
    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        imgView1 = findViewById(R.id.imageView);
        imgView2 = findViewById(R.id.imageView2);
        imgView3 = findViewById(R.id.loadImage);
        btnUpdateLoad1 = findViewById(R.id.upload1);
        btnUpdateLoad2 = findViewById(R.id.upload2);
        imageButton = findViewById(R.id.imageButton);
        api = LuxandAPI.getApi();

        //init connection
        new InitConnection().execute();

        btnUpdateLoad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventUpload1(v);
            }
        });
        btnUpdateLoad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventUpload2(v);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventPressMakeBaby();
            }
        });
    }

    public void eventUpload1(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE1);
    }

    public void eventUpload2(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE2);
    }

    private void eventPressMakeBaby() {
        //set input of user; gender and skin
        BabyCharacteristic babyCharacteristic = new BabyCharacteristic();
        new DoMakeBaby().execute(babyCharacteristic);
    }

    private void setImageView(ImageView imageView, Uri uri) {
        try {
            realPath1 = getRealPathFromURI(uri);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(contentUri,
                proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            File file = null;
            Uri uri;
            int action = 0;
            if (requestCode == REQUEST_CODE_IMAGE1 && data != null) {
                uri = data.getData();
                setImageView(imgView1, uri);
                realPath1 = getRealPathFromURI(uri);
                file = new File(realPath1);
                action = Const.UPLOAD_FILE_1;
            } else if (requestCode == REQUEST_CODE_IMAGE2 && data != null) {
                uri = data.getData();
                setImageView(imgView2, uri);
                realPath2 = getRealPathFromURI(uri);
                file = new File(realPath2);
                action = Const.UPLOAD_FILE_2;
            }
            new UploadImage(action).execute(file);
        }
    }

    private class LoadImageAPI extends AsyncTask<String, Void, Bitmap> {
        private Bitmap bitmap = null;

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                InputStream inputStream = url.openConnection().getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.d("LoadImage", "Đã đến");
            imgView3.setImageBitmap(bitmap);
        }
    }

    public class UploadImage extends AsyncTask<File, Void, Void> {
        private int action;

        public UploadImage(int action) {
            this.action = action;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(), "Update File successfully", Toast.LENGTH_LONG);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(File... files) {
            if (files != null && files.length > 0) {
                File file = files[0];
                api.uploadImage(file, action);
            }
            return null;
        }
    }

    private class InitConnection extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            api.initiateConnection();
            return null;
        }
    }

    private class DoMakeBaby extends AsyncTask<BabyCharacteristic, Void, String> {
        @Override
        protected String doInBackground(BabyCharacteristic... babyCharacteristics) {
            String nameImageChild = null;
            if (babyCharacteristics != null && babyCharacteristics.length > 0) {
                nameImageChild = api.makeBaby(-1, -1);
                Log.e("Image Link", nameImageChild);
            }
            return nameImageChild;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String url = api.getURLChildImage(s);
            new LoadImageAPI().execute(url);
        }
    }
}
