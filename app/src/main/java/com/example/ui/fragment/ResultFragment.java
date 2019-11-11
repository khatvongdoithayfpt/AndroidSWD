package com.example.ui.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.callback.GetResultBabyCallback;
import com.example.callback.TransitionFragmentCallback;
import com.example.callback.UpdateViewCallback;
import com.example.ui.activity.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


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
        setEvent();
        return rootView;
    }

    private void setEvent(){
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GetResultBabyCallback)mContext).GetResultBaby();
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TransitionFragmentCallback)mContext).previousFragment();
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
        if(param == null){
            progressBar.setVisibility(View.VISIBLE);
            isLoading = true;
            btnResult.setClickable(false);
            btnResult.setText("Loading....");
            btnShare.setVisibility(View.INVISIBLE);
            process();
        }else{
            String url = param.toString();
            new LoadImageAPI().execute(url);
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
}
