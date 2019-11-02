package com.example.swdproject;

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
import android.widget.ImageView;

import com.example.builder.URLBuilder;
import com.example.connection.API;
import com.example.connection.LuxandAPI;
import com.example.constant.Const;

import java.io.File;
import java.io.FileNotFoundException;
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
    private String realPath1= "";
    private String realPath2= "";
    private boolean checkLoad = false;

    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView1 = findViewById(R.id.imageView);
        imgView2 = findViewById(R.id.imageView2);
        imgView3 = findViewById(R.id.loadImage);
        api = new LuxandAPI();
        api.initiateConnection();
        Log.d("Cookies",((LuxandAPI) api).cookies.toString());
    }

    public void upload1(View view) {
        Log.d("Cookies",((LuxandAPI) api).cookies.toString());
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_IMAGE1);
    }

    public void upload2(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_IMAGE2);
    }
    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
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
            if (requestCode == REQUEST_CODE_IMAGE1 && data!=null ){
                try {
                    Uri uri = data.getData();
                    realPath1 = getRealPathFromURI(uri);
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgView1.setImageBitmap(bitmap);

                    File file = new File(realPath1);
                    api.uploadImage(file, Const.UPLOAD_FILE_1);
                    Log.d("UploadImage",""+realPath1);

                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_IMAGE2 && data!=null){
                try {
                    Uri uri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgView2.setImageBitmap(bitmap);
                    realPath2 = getRealPathFromURI(uri);
                    File file = new File(realPath2);
                    Log.d("UploadImage",""+realPath2);
                    api.uploadImage(file, Const.UPLOAD_FILE_2);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }


    public void LoadInternet(View view) {
        Log.d("Cookies",((LuxandAPI) api).cookies.toString());
        String nameImageChild = api.makeBaby(-1,-1);
        Log.d("LoadImageUrl",""+nameImageChild);
        String url = api.getURLChildImage(nameImageChild);
        Log.d("LoadImage","-"+url);
       new  LoadImageAPI().execute(url);
    }

    private class LoadImageAPI extends AsyncTask<String,Void, Bitmap> {
        private Bitmap bitmapHinh = null;

        @Override
        protected Bitmap doInBackground(String... strings) {
            try{
            URL url = new URL(strings[0]);
            InputStream inputStream = url.openConnection().getInputStream();
                bitmapHinh = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmapHinh;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.d("LoadImage","Đã đến");
            imgView3.setImageBitmap(bitmap);

        }
    }
}
