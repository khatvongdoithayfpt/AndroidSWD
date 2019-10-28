package connection;

import java.io.File;
/**
 * API
 */
public interface API {
    void initiateConnection();
    void uploadImage(File file,int type);
    void saveImage(String nameImage,String saveFilePath);
    String makeBaby(int gender,int skin);
}
