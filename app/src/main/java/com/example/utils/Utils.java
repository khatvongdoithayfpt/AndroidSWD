package com.example.utils;

import android.os.AsyncTask;
import android.os.Environment;

import com.example.connection.API;
import com.example.constant.Constant;

import java.io.File;


public class Utils {

    public static String saveImage(API api,String nameFile){
        String rootPath = Environment.getExternalStorageDirectory().getPath();
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



}
