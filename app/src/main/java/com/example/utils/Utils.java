package com.example.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.example.connection.API;
import com.example.constant.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class Utils {

    public static String saveImage(API api,String nameFile){
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        rootPath += "/"+ Constant.DIR_SAVE_IMAGE;
        File file = new File(rootPath);
        if(!file.exists()){
            file.mkdirs();
        }
        new TaskSaveImage(api,nameFile,rootPath).execute();
        return rootPath+nameFile;
    }
    private static class TaskSaveImage extends AsyncTask<Void,Void,Void>{

        private API api;
        private String nameFile;
        private String dir;

        public TaskSaveImage(API api, String nameFile, String dir) {
            this.api = api;
            this.nameFile = nameFile;
            this.dir = dir;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            api.saveImage(nameFile,dir);
            return null;
        }
    }

    public static void setImageView(ImageView imageView, File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRealPathFromURI(Uri contentUri, Context mContext) {
        String path = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(contentUri,
                proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    public static String getImageFilePath(Uri uri,Context mContext) {
        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = mContext.getContentResolver().query(contentUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

}
