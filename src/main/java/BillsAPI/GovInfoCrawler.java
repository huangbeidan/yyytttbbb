package BillsAPI;


import ExistdbAPI.XMLComparatorToFile;
import PostgresXMLdatabase.UploadXML;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GovInfoCrawler {

    /**
     *
     * @param stage    there are 4 stage: ih, rh, eh, rfs
     * @param CongressNumber
     * @param BillNumber
     * @param filetype  xml, pdf, mod, htm
     * @return
     */
    public String urlGeneratorHR(String stage, int CongressNumber, int BillNumber, String filetype){

        String apiKey = "hL7pm92KFBUCDZfXer1DHuOggpVcDa4XYk6rsMhb";
        String url = String.format("https://api.govinfo.gov/packages/BILLS-%shr%s"+stage + "/" + filetype + "?api_key=" + apiKey,CongressNumber,BillNumber);

        return url;
    }


    /**
     *
     * @param CongressNumber
     * @param BillNumber
     * @param filetype  MODs, HTM(TXT)
     * @return
     */
    public String urlGeneratorCHRG(int CongressNumber, int BillNumber, String filetype){

        String apiKey = "hL7pm92KFBUCDZfXer1DHuOggpVcDa4XYk6rsMhb";
        String detail = String.format("CHRG-%shhrg%s",CongressNumber,BillNumber);
        String url = "https://api.govinfo.gov/packages/"+detail+"/granules/"+detail+"/"+filetype+"?api_key="+apiKey;

        return url;
    }

    public List<String> urlGeneratorCHRGBulk(int CongressNumber, int lowerlimit, int upperlimit) throws IOException {
        List<String> urlList = new ArrayList<>();
        String apiKey = "hL7pm92KFBUCDZfXer1DHuOggpVcDa4XYk6rsMhb";
        CrunchifyFetchURLData cf = new CrunchifyFetchURLData();

        for(int i=lowerlimit; i<upperlimit;i++){


            String detail = String.format("CHRG-%shhrg%s",CongressNumber,i);
            String link = "https://api.govinfo.gov/packages/"+detail+"/granules/"+detail+"/"+"htm"+"?api_key="+apiKey;


            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();

            if(code==200){
                urlList.add(link);
                System.out.println(link);
                cf.fetchURLData(link,"CHRGBulk");

            }

        }

        return urlList;


    }

    public String urlGeneratorCRPT(int CongressNumber, int BillNumber, String filetype){

        String apiKey = "hL7pm92KFBUCDZfXer1DHuOggpVcDa4XYk6rsMhb";
        String detail = String.format("CRPT-%shrpt%s",CongressNumber,BillNumber);
        String url = "https://api.govinfo.gov/packages/"+detail+"/granules/"+detail+"/"+filetype+"?api_key="+apiKey;

        return url;
    }



    /**
     *  there are 4 stage: ih, rh, eh, rfs
     *  there are 4 file formats: xml, pdf, mod, htm
     */

    public void cleanReport(String folderPath) throws IOException {
        UploadXML uploadXML = new UploadXML();

        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();




        try{
        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {

                String filename = listOfFiles[i].getName();

                if(filename.endsWith(".xml") && !filename.endsWith("-nodtd.xml") ){

                      String cleanReport = uploadXML.readLines(folderPath + filename);

                    BufferedWriter writer = new BufferedWriter(new FileWriter("cleanHR/" + filename));

                    writer.write(cleanReport);
                    writer.close();

                }



            }
        }

        }catch(NullPointerException ne){
            System.out.println(ne.getMessage());
            }


    }

    /**
     * One step crawler for Bills
     * @param CongressNumber
     * @param BillNumber
     * @param filetype
     */
    public void oneStepCrawler(int CongressNumber, int BillNumber, String filetype){
        CrunchifyFetchURLData cf = new CrunchifyFetchURLData();

        String url11 = urlGeneratorHR("ih",CongressNumber,BillNumber,filetype);
        cf.fetchURLData(url11,"HR");

        String url21 = urlGeneratorHR("rh",CongressNumber,BillNumber,filetype);
        cf.fetchURLData(url21,"HR");

        String url31 = urlGeneratorHR("eh",CongressNumber,BillNumber,filetype);
        cf.fetchURLData(url31,"HR");

        String url51 = urlGeneratorHR("rds",CongressNumber,BillNumber,filetype);
        cf.fetchURLData(url51,"HR");

        String url41 = urlGeneratorHR("rfs",CongressNumber,BillNumber,filetype);
        cf.fetchURLData(url41,"HR");


    }

    public void oneStepHtmCrawler(int CongressNumber, int BillNumber, String filetype){
        CrunchifyFetchURLData cf = new CrunchifyFetchURLData();

        String url11 = urlGeneratorHR("ih",CongressNumber,BillNumber,filetype);
        cf.fetchURLData(url11,"BillXML");

//        String url21 = urlGeneratorHR("rh",CongressNumber,BillNumber,filetype);
//        cf.fetchURLData(url21,"BillXML");
//
//        String url31 = urlGeneratorHR("eh",CongressNumber,BillNumber,filetype);
//        cf.fetchURLData(url31,"BillXML");
//
//        String url51 = urlGeneratorHR("rds",CongressNumber,BillNumber,filetype);
//        cf.fetchURLData(url51,"BillXML");
//
//        String url41 = urlGeneratorHR("rfs",CongressNumber,BillNumber,filetype);
//        cf.fetchURLData(url41,"BillXML");


    }

    public void crawlBulkBillHtm(int congressN, int start, int end){

        for(int i=start;i<=end;i++){
            oneStepHtmCrawler(congressN,i,"htm");
        }

    }



    public void crawlBulkBillXML(int congressN, int start, int end){





        for(int i=start;i<=end;i++){
            oneStepHtmCrawler(congressN,i,"xml");
        }

    }






    public void crawlFromCSV() throws IOException {

        BufferedReader csvReader = new BufferedReader(new FileReader("list_bills.csv"));
        CSVWriter csvWriter = new CSVWriter(new FileWriter("crawl_billList.csv"), ',');
        csvReader.readLine();
        String row = "";
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            // do something with the data
            if(data[0].matches("[0-9]+") && data[1].replaceAll("^(HR\\s)","").matches("[0-9]+")) {
                int congressN = Integer.parseInt(data[0]);
                int billN = Integer.parseInt(data[1].replaceAll("^(HR\\s)", ""));

                oneStepCrawler(congressN,billN,"xml");

            }


        }
        csvReader.close();
    }

    public  Map<String,List<String>> generateCrawlList(){

        File folder = new File("HR/");
        File[] listOfFiles = folder.listFiles();

        Map<String,List<String>> map = new HashMap<>();

        for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {

            String filename = listOfFiles[i].getName();

            if(filename.endsWith(".xml")){

                String num;
                String regex ="(\\d+)";

                Matcher matcher = Pattern.compile( regex ).matcher( filename);

                //add helper -- count
                int count = 0;
                String congressN = "";
                String billN = "";

                while (matcher.find( ))
                {


                    if(count==0){
                        // we're only looking for one group, so get it
                         congressN = matcher.group(1);
                        count++;
                    }else if(count==1){
                        billN = matcher.group(1);
                        count = 0;
                    }


                }

               String billKey = (congressN + "-" + billN);

                if(!map.containsKey(billKey)){
                    map.put(billKey,new ArrayList<String>());
                }
                map.get(billKey).add(filename);
                System.out.println(billKey + "," + filename);


            }



            // System.out.println("File " + listOfFiles[i].getName());
        } else if (listOfFiles[i].isDirectory()) {
            //   System.out.println("Directory " + listOfFiles[i].getName());
        }
    }
    return  map;
    }

    public static void main(String[] args) throws Exception {
        GovInfoCrawler gov = new GovInfoCrawler();
        CrunchifyFetchURLData cf = new CrunchifyFetchURLData();
        XMLComparatorToFile xmlComparatorToFile = new XMLComparatorToFile();



        int i=1;

        while(i<6000){

            /**generate random numbers for crawling */
            double randomDouble = Math.random();
            randomDouble = randomDouble * 6000 + 1;
            int randomInt = (int) randomDouble;

            /**crawl bulk bill.xml file*/
//            gov.oneStepHtmCrawler(105,randomInt,"xml");
//            gov.oneStepHtmCrawler(106,randomInt,"xml");
//            gov.oneStepHtmCrawler(107,randomInt,"xml");
//            gov.oneStepHtmCrawler(108,randomInt,"xml");
//            gov.oneStepHtmCrawler(109,randomInt,"xml");
//            gov.oneStepHtmCrawler(110,randomInt,"xml");
//            gov.oneStepHtmCrawler(111,randomInt,"xml");
//            gov.oneStepHtmCrawler(112,randomInt,"xml");
//            gov.oneStepHtmCrawler(113,randomInt,"xml");
//            gov.oneStepHtmCrawler(114,randomInt,"xml");
//            gov.oneStepHtmCrawler(115,randomInt,"xml");
            gov.oneStepHtmCrawler(114,i,"xml");


            i++;
        }




       // gov.oneStepCrawler(113,6,"xml");
      //  gov.cleanReport("HR/");

        //crawl a given list of bills from list_bills.csv file
        //gov.crawlFromCSV();

        //upload the files under cleanHR/ folder to the database
//        UploadXML uploadXML = new UploadXML();
//        uploadXML.upload_billXML();

//        Map<String,List<String>> billMap = gov.generateCrawlList();
//
//        for (String billInfo : billMap.keySet()){
//
//            List<String> bills = billMap.get(billInfo);
//            if(bills.size()>1){
//
//                String[] billsS = billInfo.split("-");
//
//                xmlComparatorToFile.oneStepReport(Integer.parseInt(billsS[0]),Integer.parseInt(billsS[1]));
//
//
//            }
//        }




//        String url11 = gov.urlGeneratorHR("ih",115,806,"xml");
//        cf.fetchURLData(url11,"HR");
//
//        String url12 = gov.urlGeneratorHR("ih",115,806,"mods");
//        cf.fetchURLData(url12,"HR");
//
//        String url21 = gov.urlGeneratorHR("rh",115,806,"xml");
//        cf.fetchURLData(url21,"HR");
//
//        String url22 = gov.urlGeneratorHR("rh",115,806,"mods");
//        cf.fetchURLData(url22,"HR");
//
//        String url31 = gov.urlGeneratorHR("eh",115,806,"xml");
//        cf.fetchURLData(url31,"HR");
//
//        String url32 = gov.urlGeneratorHR("eh",115,806,"mods");
//        cf.fetchURLData(url32,"HR");
//
//        String url41 = gov.urlGeneratorHR("rfs",115,806,"xml");
//        cf.fetchURLData(url41,"HR");
//
//        String url42 = gov.urlGeneratorHR("rfs",115,806,"mods");
//        cf.fetchURLData(url42,"HR");



//
//        String urlHearing = gov.urlGeneratorCHRG(115,24676,"htm");
//        cf.fetchURLData(urlHearing,"CHRG");

     //   List<String> urlHearingBatch = gov.urlGeneratorCHRGBulk(115,25782,34000);

//        for(String url : urlHearingBatch){
//            cf.fetchURLData(url,"CHRGBulk");
//        }



////
//        String urlCRPT = gov.urlGeneratorCRPT(115,1,"htm");
//        cf.fetchURLData(urlCRPT,"CRPT");




    }


}
