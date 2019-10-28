package LuxandAPI;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");

        System.setProperty("http.proxyHost", "113.160.234.147");
        System.setProperty("http.proxyPort","56570");
        
        try {
            URL url = new URL( "https://memorynotfound.com/configure-http-proxy-settings-java/");
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            System.out.println(connection.getResponseCode());
        } catch (Exception e) {
          e.printStackTrace();
        }
        

       /* API api = new LuxandAPI();
        System.out.println("Get UID and Cookei from Luxand");
        api.initiateConnection();
        File file1 = new File("./image/Ngoc-Trinh.jpg");
        System.out.println("Update image of partner 1");
        api.uploadImage(file1, 1);
        File file2 = new File("./image/testimage.jpg");
        System.out.println("Update image of partner 2");
        api.uploadImage(file2, 2);
        //TODO use asynchronous technique to update 2 file
        /*
            gender : 1 boy
                    0 girl
                    -1 either
        *//*
        System.out.println("make baby: ");
        String imageName = api.makeBaby(-1,-1);
        System.out.println("url baby: "+imageName);
        String fileOuputPath = "./image"+imageName;
        System.out.println("save image "+imageName+" to "+fileOuputPath);
        api.saveImage(imageName, fileOuputPath);*/
    }
}
