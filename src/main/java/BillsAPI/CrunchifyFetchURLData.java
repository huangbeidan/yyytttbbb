package BillsAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Crunchify.com
 */

public class CrunchifyFetchURLData {


    /** the url here is the link to download the data for H.R. files
     * e.g.https://api.govinfo.gov/packages/BILLS-115hr115rfs/xml?api_key=hL7pm92KFBUCDZfXer1DHuOggpVcDa4XYk6rsMhb
     * @param url
     * @param File_type HR CHRG CRPT
     */
    public String fetchURLData(String url, String File_type){


      String[] coms = url.split("/");
      String folderPath = "";
      String billName="";

      if(File_type.equals("HR")){

          billName = coms[4];
          billName = billName + "." + coms[5].split("\\?")[0];
          System.out.println(billName);
          folderPath = "HR/";

      } else if(File_type.equals("CHRG")){
           billName = coms[4];
          billName = billName + "." + coms[7].split("\\?")[0];
          System.out.println(billName);
          folderPath = "CHRG/";
      } else if (File_type.equals("CRPT")) {
          billName = coms[4];
          billName = billName + "." + coms[7].split("\\?")[0];
          System.out.println(billName);
          folderPath = "CRPT/";

      } else if (File_type.equals("CHRGBulk")) {
          billName = coms[4];
          billName = billName + "." + coms[7].split("\\?")[0];
          System.out.println(billName);
          folderPath = "CHRGBulk/";
      } else if(File_type.equals("BillHtm")){
          billName = coms[4];
          billName = billName + "." + coms[5].split("\\?")[0];
          System.out.println(billName);
          folderPath = "BillHtm/";
      }else if(File_type.equals("BillXML")) {
          billName = coms[4];
          billName = billName + "." + coms[5].split("\\?")[0];
          System.out.println(billName);
          folderPath = "BillXML/";
      }





          Path path = Paths.get(folderPath);
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {


            //add some filter here, if the status is OK, then get it
            URL link = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)link.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();

            if(code==200){

                InputStream in = new URL(url).openStream();
                Files.copy(in, Paths.get(folderPath+billName), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File saved in: "+folderPath+billName);


            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return billName;

    }


    public static void main(String[] args) {

        String FILE_URL = "https://api.govinfo.gov/packages/BILLS-115hr1625enr/xml?api_key=hL7pm92KFBUCDZfXer1DHuOggpVcDa4XYk6rsMhb\n";
         String CHRG_URL = "https://api.govinfo.gov/packages/CHRG-107shrg82483/granules/CHRG-107shrg82483/mods?api_key=hL7pm92KFBUCDZfXer1DHuOggpVcDa4XYk6rsMhb";
        new CrunchifyFetchURLData().fetchURLData(FILE_URL,"HR");
        new CrunchifyFetchURLData().fetchURLData(CHRG_URL,"CHRG");

    }
}