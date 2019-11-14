package com.example.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.callback.GetResultBabyCallback;
import com.example.callback.TransitionFragmentCallback;
import com.example.callback.UpdateViewCallback;
import com.example.ui.activity.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment implements UpdateViewCallback {


    private Context mContext;
    private Button btnResult;
    private Button btnShare;
    private Button btnPrevious;
    private ImageView imageView;
    private ProgressBar progressBar;
    private boolean isLoading;
    //share function
    private String userName;
    private MenuItem item;
    private CallbackManager callbackManager;
    //    Bitmap bitmapChild;
    private ShareDialog shareDialog;

    public ResultFragment(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        btnResult = rootView.findViewById(R.id.btn_result);
        btnShare = rootView.findViewById(R.id.fb_share_button);
        btnPrevious = rootView.findViewById(R.id.btnPrevious);
        imageView = rootView.findViewById(R.id.loadImage);
        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        btnShare = rootView.findViewById(R.id.fb_share_button);
        btnShare.setVisibility(View.INVISIBLE);
        setEvent();
        prinHash();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setEvent() {
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GetResultBabyCallback) mContext).GetResultBaby();
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TransitionFragmentCallback) mContext).previousFragment();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareOnFacebook(v);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
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
            imageView.setImageBitmap(bitmap);
            isLoading = false;
            btnResult.setClickable(true);
            btnResult.setText("Result");
            btnShare.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void doUpdateView(Object param) {
        if (param == null) {
            progressBar.setVisibility(View.VISIBLE);
            isLoading = true;
            btnResult.setClickable(false);
            btnResult.setText("Loading....");
            btnShare.setVisibility(View.INVISIBLE);
            process();
        } else {
            String url = param.toString();
            new LoadImageAPI().execute(url);
            btnShare.setVisibility(View.VISIBLE);
        }
    }

    void process() {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                while (isLoading) {
                    try {
                        sleep(100);
                        progressBar.incrementProgressBy(10);
                        if (progressBar.getProgress() == 100)
                            progressBar.setProgress(0);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        };
        t.start();
    }

    public void shareOnFacebook(View v) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (!isLoggedIn) {
            facebookLogin();
        }
        Bitmap bitmapChild = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
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

    public void facebookLogin() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(ResultFragment.this,Arrays.asList("public_profile"));
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
            PackageInfo info = mContext
                    .getPackageManager()
                    .getPackageInfo("com.example.swdproject", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                System.out.println(Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
