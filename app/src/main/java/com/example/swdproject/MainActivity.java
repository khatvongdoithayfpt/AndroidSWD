package com.example.swdproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView1 = findViewById(R.id.imageView);
        imgView2 = findViewById(R.id.imageView2);
        imgView3 = findViewById(R.id.loadImage);
    }

    public void upload1(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_IMAGE1);
    }

    public void upload2(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_CODE_IMAGE2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE1 && data!=null ){
                try {
                    Uri uri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgView1.setImageBitmap(bitmap);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CODE_IMAGE2 && data!=null){
                try {
                    Uri uri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgView2.setImageBitmap(bitmap);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void LoadInternet(View view) {
       new  LoadImageAPI().execute("");
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
            imgView3.setImageBitmap(bitmap);

        }
    }

}
