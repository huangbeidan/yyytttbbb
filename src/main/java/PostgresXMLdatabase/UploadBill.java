package PostgresXMLdatabase;

import BillsAPI.GovInfoCrawler;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class UploadBill {


    /**
     * only upload rfs file, namely, the final stage of a bill
     * @throws FileNotFoundException
     */
    public void upload_it() throws FileNotFoundException {


        //get files from the folder and upload to database
        File folder = new File("BillXML/");
        File[] listOfFiles = folder.listFiles();

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            //read the file, get name + content
            //upload to the database

            for (int i = 0; i < listOfFiles.length; i++) {

                try {

                    if (listOfFiles[i].isFile()) {

                        String filename = listOfFiles[i].getName();

                        if (filename.endsWith(".xml") && filename.contains("ih")) {

                            String data = new UploadXML().readLines("BillXML/" + filename);


                            System.out.println("This is data" + data);


                            stmt = c.createStatement();
                            String sql = "INSERT INTO billxmlc (filename,data) "
                                    + "VALUES (\'" + filename + "\'," +
                                    "\'" + data.replaceAll("\'", "`")
                                    .replaceAll("(\\*+[a-zA-Z0-9\\*]*\\*+)", "")
                                    .replaceAll("&", "ampS") + "\') " +
                                    "on conflict (filename) do nothing ;";


                            System.out.println(sql);
                            stmt.executeUpdate(sql);


                        }


                        // System.out.println("File " + listOfFiles[i].getName());
                    } else if (listOfFiles[i].isDirectory()) {
                        //   System.out.println("Directory " + listOfFiles[i].getName());
                    }

                }catch (Exception e){
                    System.out.println(e.getMessage());
                    continue;
                }


            }

            stmt.close();
            c.commit();
            c.close();


        } catch (NullPointerException ne){
            System.out.println(ne.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }


    /**upload bill htm file to awesome database */
    public void upload_it_htm() throws FileNotFoundException {


        //get files from the folder and upload to database
        File folder = new File("BillHtm/");
        File[] listOfFiles = folder.listFiles();

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            //read the file, get name + content
            //upload to the database

            for (int i = 0; i < listOfFiles.length; i++) {

                if (listOfFiles[i].isFile()) {

                    String filename = listOfFiles[i].getName();

                    if(filename.endsWith(".htm") && (filename.contains("rfs")
                    || filename.contains("rds"))) {

                        String data = readfromFile("BillHtm/" + filename);


                        System.out.println("This is data" + data);

                        stmt = c.createStatement();
                        String sql = "INSERT INTO billhtm (name,content) "
                                + "VALUES (\'" + filename + "\'," +
                                "\'" + data.replaceAll("\'", "`")
                                .replaceAll("(\\*+[a-zA-Z0-9]*\\*+)","")
                                .replaceAll("&","ampS")+ "\')";

                        System.out.println(sql);


                        stmt.executeUpdate(sql);


                    }


                    // System.out.println("File " + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    //   System.out.println("Directory " + listOfFiles[i].getName());
                }
            }

            stmt.close();
            c.commit();
            c.close();


        } catch (NullPointerException ne){
            System.out.println(ne.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public String readfromFile(String filepath) throws IOException {

        FileReader reader = new FileReader(filepath);
        BufferedReader textReader = new BufferedReader(reader);
        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = textReader.readLine()) != null) {
            // I tried this:
            line = line.trim();
            //get rid of html tags
            line = line. replaceAll("<.*?>\\s*","")
                    .replaceAll("(?<=\\[).*?(?=\\])", "")
            .replaceAll("\\[\\]\\s*","")
            .replaceAll("&lt;","")
            .replaceAll("&gt;","")
            .replaceAll("[_]+","")
            .replaceAll("^//s+","");
            sb.append(line).append(" ");

        }
        System.out.println(sb.toString());
        reader.close();


        return sb.toString();
    }

    public void crawlBills(){

        GovInfoCrawler govInfoCrawler = new GovInfoCrawler();

        govInfoCrawler.oneStepHtmCrawler(115,115,"htm");



    }





    public static void main(String[] args) throws IOException {

        //new UploadBill().crawlBills();
    //
        //
        //   new GovInfoCrawler().crawlBulkBillHtm(115,201,400);
        new UploadBill().upload_it();
       // new UploadBill().upload_it_htm();
    }

}

