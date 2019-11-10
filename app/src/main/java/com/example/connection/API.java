package com.example.connection;

import java.io.File;
/**
 * API
 */
public interface API {
    String initiateConnection();
    String uploadImage(File file,int type);
    String saveImage(String nameImage,String dir);
    String makeBaby(int gender,int skin);
    String getURLChildImage();
}
