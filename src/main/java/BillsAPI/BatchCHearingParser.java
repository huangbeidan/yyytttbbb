package BillsAPI;

import Utilities.SecondScanParseXML;
import Utilities.ThirdScanParseXML;
import Utilities.ToXMLdom;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BatchCHearingParser {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException{
//        String filename = "CHRG/CHRG-115hhrg24676.htm";
//        new BatchCHearingParser().doit(filename);
//
        String path = "115/";
       new BatchCHearingParser().parseFolder(path);

           String filename = "115/CHRG-115hhrg24032.htm";
       // new BatchCHearingParser().doit(filename);


    }

    /**
     *
     * @param path   CHRGBulk/115/
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void parseFolder(String path) throws IOException, ParserConfigurationException {

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {

                String filename = listOfFiles[i].getName();

                if(filename.endsWith(".htm")){

                    try{
                    doitBulk(path + filename);


                    }
                    catch (IOException e){
                        System.out.println(e.getMessage());
                    }

                }


            }
        }



    }


    /**
     *
     * @param filename  the file name has to be in specific format. e.g. CHRG/CHRG-115hhrg25633.htm
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public void doit(String filename) throws FileNotFoundException, IOException, ParserConfigurationException {


        String filename1 = new ToXMLdom().doit(filename);
        String filename2 = new SecondScanParseXML().do_it(filename1);
        new ThirdScanParseXML().scanIt(filename2);


    }

    public void doitBulk(String filename) throws FileNotFoundException, IOException, ParserConfigurationException {


        String filename1 = new ToXMLdom().doit(filename);
        String filename2 = new SecondScanParseXML().do_it(filename1);
        new ThirdScanParseXML().scanIt(filename2);


    }


}
