package HTTPClient;


import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class HttpGet {



    public static void main(String[] args){

        String url = "http://localhost:8080/exist/rest/db/bills/BILLS-115hr115rfs.xml";


        try {
            InputStream response = new URL(url).openStream();

            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            System.out.println(responseBody);



        } catch (Exception ex) {
            ex.printStackTrace();
        }



    }





}
