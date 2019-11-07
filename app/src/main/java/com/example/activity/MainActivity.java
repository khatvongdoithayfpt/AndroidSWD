package com.example.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;

import com.example.connection.API;
import com.example.connection.LuxandAPI;
import com.example.constant.Const;
import com.example.model.BabyCharacteristic;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static int REQUEST_CODE_IMAGE1 = 1;
    private static int REQUEST_CODE_IMAGE2 = 2;
    CallbackManager callbackManager;
    Bitmap bitmapChild;
    ShareDialog shareDialog;
    private ImageView imgView1;
    private ImageView imgView2;
    private ImageView imgView3;
    private String realPath1 = "";
    private String realPath2 = "";
    private API api;
    String userName;
    MenuItem item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        item=menu.findItem(R.id.nav_login);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_login:
                facebookLogin();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void facebookLogin() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    userName = object.getString("name");
                                    item.setTitle(userName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i("MainActivity", "@@@onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("MainActivity", "@@@onError: " + error.getMessage());
            }
        });
    }

    void prinHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.swdproject", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                System.out.println(Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        imgView1 = findViewById(R.id.imageView);
        imgView2 = findViewById(R.id.imageView2);
        imgView3 = findViewById(R.id.loadImage);
        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Predict Child");
        api = LuxandAPI.getApi();
        new InitConnection().execute();
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

    public void eventPressMakeBaby(View view) {
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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

    public Bitmap readBitmapAndScale(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //Chỉ đọc thông tin ảnh, không đọc dữ liwwuj
        BitmapFactory.decodeFile(path, options); //Đọc thông tin ảnh
        options.inSampleSize = 4; //Scale bitmap xuống 4 lần
        options.inJustDecodeBounds = false; //Cho phép đọc dữ liệu ảnh ảnh
        return BitmapFactory.decodeFile(path, options);
    }

    public void test(View v) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        System.out.println(accessToken);
        System.out.println(isLoggedIn);
        if (!isLoggedIn) {
            facebookLogin();
        }
        if (bitmapChild == null) return;
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmapChild)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        if (shareDialog.canShow(content)) {

            shareDialog.show(content);
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
            bitmapChild = bitmap;
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

    private void useLoginInformation(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    System.out.println("aaaaaaaa");
                    String name = object.getString("name");
                    item.setTitle(name);
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        request.executeAsync();
    }
}
