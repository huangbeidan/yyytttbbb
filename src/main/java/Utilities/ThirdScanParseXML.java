package Utilities;

import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class ThirdScanParseXML {

    BufferedReader in;


    public void scanIt(String filename){

       // String outputname = "CHRGparse3/" + filename.split("/")[1].split("\\.")[0]+".xml";

        String outputname = "";

            outputname = "CHRGBulkparse3/" + filename.split("/")[1].split("\\.")[0]+".xml";


        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));



            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputname))

        )
        {
            String line = "";
            while((line=bufferedReader.readLine())!=null) {
                bufferedWriter.write(line.replaceAll("&lt;","<").replaceAll("&gt;",">"));
                bufferedWriter.newLine();

            }

        } catch (FileNotFoundException e) {
            // exception handling
        } catch (IOException e) {
            // exception handling
        }






    }

    public static void main(String[] args){
        ThirdScanParseXML thirdScanParseXML = new ThirdScanParseXML();

        String filename = "CHRG-parse2/CHRG-115hhrg25633.xml";
        thirdScanParseXML.scanIt(filename);
    }





}
