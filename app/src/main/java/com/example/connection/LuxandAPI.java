package com.example.connection;

import com.example.builder.URLBuilder;
import com.example.constant.Const;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * LuxandAPI
 */
public class LuxandAPI implements API {

    public  Map<String, String> cookies;

    @Override
    public void initiateConnection() {
        try {
            cookies = new HashMap<>();
            URL url = new URL(Const.URL_LUXAND);

            CookieManager manager = new CookieManager();
            CookieHandler.setDefault(manager);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                List<HttpCookie> raw_cookies = manager.getCookieStore().getCookies();
                for (HttpCookie httpCookie : raw_cookies) {
                    cookies.put(httpCookie.getName(), httpCookie.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadImage(File file, int type) {
        URLBuilder builder = new URLBuilder();
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        String charset = "UTF-8";

        try {
            URL url = builder.create(Const.URL_UPLOAD).setParameter("file", type == 1 ? "1" : "2").getUrl();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("Cookie", getStringCookies());
            System.out.println(getStringCookies());

            OutputStream os = conn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, charset), true);
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\"" + file.getName() + "\"")
                    .append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(file.toPath(), os);
            os.flush();
            writer.append(CRLF).flush();
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
            // ------------------------------------------------------
            int responseCode = conn.getResponseCode();
            System.out.println(responseCode);
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (type == 1) {
                    cookies.put(Const.PATNER_PHOTO_1, line);
                } else {
                    cookies.put(Const.PATNER_PHOTO_2, line);
                }
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * gender : 1 boy 0 girl -1 either 
     * skin: 0 light 1 medium 2 dark 3 asian -1 auto
     * detect
     */
    @Override
    public String makeBaby(int gender, int skin) {
        StringBuilder image = new StringBuilder();
        try {
            URLBuilder builder = new URLBuilder();
            builder.create(Const.URL_LUXAND).setPath("1").setPath("m").setParameter("sex", String.valueOf(gender))
                    .setParameter("skin", String.valueOf(skin));
            for (Entry<String, String> entry : cookies.entrySet()) {
                switch (entry.getKey()) {
                case Const.PATNER_PHOTO_1:
                    builder.setParameter(Const.FILE1, entry.getValue()+Const.IMAGE_EXTENSION);
                    break;
                case Const.PATNER_PHOTO_2:
                    builder.setParameter(Const.FILE2, entry.getValue()+Const.IMAGE_EXTENSION);
                    break;
                default:
                    builder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            URL url = builder.getUrl();
            System.out.println(url.toExternalForm());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", getStringCookies());

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                image.append(line);
            }
            image.delete(0, image.lastIndexOf("/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image.toString();
    }

    @Override
    public void saveImage(String nameImage,String saveFilePath) {
        try {
            
            URLBuilder builder = new URLBuilder();
            URL url = builder.create(Const.URL_IMAGE).setPath(nameImage).getUrl();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", getStringCookies());

            byte []buffer = new byte[1024];
            InputStream is = connection.getInputStream();
            FileOutputStream fos = new  FileOutputStream(saveFilePath);
            int bytereader = -1;
            while((bytereader = is.read(buffer))!= -1){
                fos.write(buffer, 0, bytereader);
            }
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getURLChildImage(String nameImageChild) {
        URLBuilder builder = new URLBuilder();
        URL url = builder.create(Const.URL_IMAGE).setPath(nameImageChild).getUrl();
        return url.toString();
    }

    private String getStringCookies() {
        StringBuilder sc = new StringBuilder();
        for (Entry<String, String> entry : cookies.entrySet()) {
            sc.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
        }
        // sc.delete(sc.lastIndexOf(";"), sc.length());
        sc.append(Const.COOKIES_WARNING).append("=").append("true");
        return sc.toString();
    }
}