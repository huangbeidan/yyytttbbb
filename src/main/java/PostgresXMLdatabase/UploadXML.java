package PostgresXMLdatabase;

import Utilities.UnwantedWords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UploadXML {

    public String readLines(String filepath) throws IOException {
        FileReader reader = new FileReader(filepath);
        BufferedReader textReader = new BufferedReader(reader);
        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = textReader.readLine()) != null) {
            // I tried this:
            if (line.startsWith("<?xml-styleshee") || line.startsWith("<!DOCTYPE")) {
                continue;
            }
           sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    public static String readFileAsString(String fileName)throws Exception
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    public static void main(String[] args) throws Exception
    {
     //   String data = readFileAsString("HR/BILLS-115hr806ih.xml");
     //   System.out.println(data);
        UploadXML uploadXML = new UploadXML();
      // uploadXML.do_it(); /** do it: upload the xml files in folderpath to the databse */
      //  String docname = "CHRG-115hhrg25034.xml";
    //    PeopleInfo peopleInfo = uploadXML.select_doit(docname);
       // uploadXML.UploadPeopleInfo(peopleInfo);
       // uploadXML.select_doit();
     uploadXML.uploadOneStep();
    }

    /**
     * Upload the query result from xmlhearing to hearingpeopleinfo table in the database
     * @throws Exception
     */
    public void uploadOneStep() throws Exception{

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        List<String> hearingNameList = new ArrayList<>();

        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "beidan", "");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();

            //get name list from the database
            String queryStat = "SELECT * from xmlhearing";

            ResultSet rs1 = stmt.executeQuery(queryStat );
            while (rs1.next()){

                String name = (rs1.getString("name"));
                hearingNameList.add(name);
            }

            for(String name : hearingNameList){
                PeopleInfo peopleInfo = select_doit(name);
                UploadPeopleInfo(peopleInfo);
            }



            stmt.close();
            c.close();








        }catch (Exception cf){
            System.out.println(cf.getMessage());
            System.out.println("ooop, something went wrong here");
        }





        }

    public PeopleInfo select_doit(String docname) throws Exception{

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        List<String> peopleSpeakerArray = new ArrayList<>();
        List<String> WitnessesArray = new ArrayList<>();
        String Doc_title = "";
        String HearingID = "";
        String info = "";
        String committeeName = "";
        String subcommitteeName = "";


        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "beidan", "");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");



            stmt = c.createStatement();
            String queryStat = "SELECT (xpath('//peopleSpeaker ', data)) " +
                    " FROM  xmlhearing where name=\'" + docname + "\'";
            String queryStat2 = "SELECT (xpath('//Witness ', data)) " +
                    " FROM  xmlhearing where name=\'" + docname + "\'";
            String queryStat3 = "SELECT (xpath('//Doc-title ', data)) " +
                    " FROM  xmlhearing where name=\'" + docname + "\'";
            String queryStat4 = "SELECT (xpath('//Hearing/@HearingID ', data)) " +
                    " FROM  xmlhearing where name=\'" + docname + "\'";
            String queryStat5 = "SELECT (xpath('//info ', data)) " +
                    " FROM  xmlhearing where name=\'" + docname + "\'";
            String queryStat6 = "SELECT (xpath('//committee_name ', data)) " +
                    " FROM  xmlhearing where name=\'" + docname + "\'";
//            String queryStat7 = "SELECT (xpath('//subcomittee_name ', data)) " +
//                    " FROM  xmlhearing where name='CHRG-115hhrg24845.xml'";
            String queryStat7 = "SELECT (xpath('//subcomittee_name ', data)) " +
                    " FROM  xmlhearing where name=\'" + docname + "\'";


//            String queryStat = "SELECT\n" +
//                    "    (xpath('//peopleSpeaker',item))::text,\n" +
//                    "    unset(xpath('//peopleinfo',item))::text\n" +
//                    "FROM (\n" +
//                    "    SELECT\n" +
//                    "        unnest(xpath('//Hearing',data)) AS item\n" +
//                    "    FROM xmlhearing\n" +
//
//                    "    ) s";



           // get people information

            ResultSet rs1 = stmt.executeQuery(queryStat );
      while (rs1.next()){
               //int id = rs.getInt("id");
               //System.out.println(id);
               String rarray1 =  rs1.getArray("xpath").toString();
              // System.out.println(rarray1);
               String[] speakers = rarray1.replaceAll("<.*?> ?", "").replaceAll("[\"\\{}]","").
                       replaceAll("^[\\s+]","").split(",");
               for (String speaker : speakers){

                  // System.out.println(speaker);

                   peopleSpeakerArray.add(speaker);
               }
           }

            // get Witness information

            ResultSet rs2 = stmt.executeQuery(queryStat2 );
            while (rs2.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray2 =  rs2.getArray("xpath").toString();
                System.out.println(rarray2);
                String[] witnesses = rarray2.replaceAll("<.*?> ?", "").replaceAll("[\"\\{}]","").
                        replaceAll("^[\\s+]","").split(",");
                for (String witness : witnesses){

                    //System.out.println(witness);
                    if(!witness.isEmpty()){
                        WitnessesArray.add(witness);
                    }
                    //System.out.println(witness.trim().replaceAll("<.*?> ?", "").replaceAll("^[\\s\\t]+",""));
                }

            }

            // get Doc_title

            ResultSet rs3 = stmt.executeQuery(queryStat3 );
            while (rs3.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray3 =  rs3.getArray("xpath").toString();


                System.out.println(rarray3);

                Doc_title = (rarray3.contains("\",\"") ? rarray3.split("\",\"")[0] :
                        rarray3);
                Doc_title = Doc_title.replaceAll("<.*?> ?", "").replaceAll("[\"\\{}]","");

                System.out.println(Doc_title);
            }

            // get Hearing ID

            ResultSet rs4 = stmt.executeQuery(queryStat4 );
            while (rs4.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                HearingID =  rs4.getArray("xpath").toString().replaceAll("<.*?> ?", "").replaceAll("[\"\\{}]","");
                //System.out.println(HearingID);

            }

            // get Info

            ResultSet rs5 = stmt.executeQuery(queryStat5 );
            while (rs5.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray5 =  rs5.getArray("xpath").toString();
                System.out.println(rarray5);

                info = (rarray5.length()>1) ?
                        rarray5.split("<.*?> ?")[1] : rarray5.replaceAll("<.*?> ?","");

                //System.out.println(info);
            }

            // get committeeName

            ResultSet rs6 = stmt.executeQuery(queryStat6 );
            while (rs6.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray6 =  rs6.getArray("xpath").toString().replaceAll("<.*?> ?", "").replaceAll("[\"\\{}]","");

                committeeName = (rarray6.contains(",") ? rarray6.split(",")[0] :
                        rarray6);
                System.out.println(committeeName);

            }

            // get SubcommitteeName

            ResultSet rs7 = stmt.executeQuery(queryStat7 );
            while (rs7.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray7 =  rs7.getArray("xpath").toString().replaceAll("<.*?> ?", "").replaceAll("[\"\\{}]","");

                subcommitteeName = (rarray7.contains(",") ? rarray7.split(",")[0] :
                        rarray7);
                System.out.println(subcommitteeName);

            }




            rs1.close();
            stmt.close();
            c.close();








        }catch (Exception cf){
            System.out.println(cf.getMessage());
            System.out.println("ooop, something went wrong here");
        }

        return new PeopleInfo(peopleSpeakerArray,WitnessesArray,Doc_title,HearingID,info,committeeName,subcommitteeName);


    }

    public void UploadPeopleInfo(PeopleInfo peopleInfo) throws Exception{

        File folder = new File("cleanHearing/");
        File[] listOfFiles = folder.listFiles();

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "beidan", "");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            //read the file, get name + content
            //upload to the database

            List<String> peopleSpeakerArray = peopleInfo.getPeopleSpeakerArray();
            List<String> WitnessesArray = peopleInfo.getWitnessesArray();
            String Doc_title = peopleInfo.getDoc_title();
            String HearingID = peopleInfo.getHearingID();
            String info = peopleInfo.getInfo();
            String committeeName = peopleInfo.getCommitteeName();
            String subcommitteeName = peopleInfo.getSubcommitteeName();


            List<String> unwanted = new UnwantedWords().getUnwlist();
            for(String peoplespeaker : peopleSpeakerArray) {

                peoplespeaker = peoplespeaker.trim();
                if (!peoplespeaker.isEmpty() && !unwanted.contains(peoplespeaker)) {

                    stmt = c.createStatement();
                    String identity = "speaker";
                    String sql = "INSERT INTO hearingpeopleinfo2 (people,identity,doctitle,hearingid,info,committeename,subcommitteename) "
                            + "VALUES (\'" + peoplespeaker.replaceAll("\'", "`") + "\',"
                            + "\'" + identity + "\',"
                            + "\'" + Doc_title.replaceAll("\'", "`") + "\',"
                            + "\'" + HearingID.replaceAll("\'", "`") + "\',"
                            + "\'" + info.replaceAll("\'", "`") + "\',"
                            + "\'" + committeeName.replaceAll("\'", "`") + "\',"
                            + "\'" + subcommitteeName.replaceAll("\'", "`") + "\')";

                    System.out.println(sql);

                    stmt.executeUpdate(sql);
                    stmt.close();

                }
            }

            for(String witness : WitnessesArray) {

                if (!witness.trim().isEmpty()) {

                    stmt = c.createStatement();
                    String identity = "witness";
                    String sql = "INSERT INTO hearingpeopleinfo2 (people,identity,doctitle,hearingid,info,committeename,subcommitteename) "
                            + "VALUES (\'" + witness.replaceAll("\'", "`") + "\',"
                            + "\'" + identity + "\',"
                            + "\'" + Doc_title.replaceAll("\'", "`") + "\',"
                            + "\'" + HearingID.replaceAll("\'", "`") + "\',"
                            + "\'" + info.replaceAll("\'", "`") + "\',"
                            + "\'" + committeeName.replaceAll("\'", "`") + "\',"
                            + "\'" + subcommitteeName.replaceAll("\'", "`") + "\')";

                    System.out.println(sql);

                    stmt.executeUpdate(sql);
                    stmt.close();

                }
            }


            c.commit();
            c.close();

        } catch (NullPointerException ne){
            System.out.println(ne.getMessage());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }







    }
    public void upload_billXML() throws Exception{

        File folder = new File("cleanHR/");
        File[] listOfFiles = folder.listFiles();

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        String fileNameDisplay = "";

        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "beidan", "");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            //read the file, get name + content
            //upload to the database

            for (int i = 0; i < listOfFiles.length; i++) {

                if (listOfFiles[i].isFile()) {

                    String filename = listOfFiles[i].getName();
                    fileNameDisplay = filename;

                    if(filename.endsWith(".xml") ){

                        String data = readLines("cleanHR/" + filename);


                        System.out.println("This is data" + data);

                        stmt = c.createStatement();
                        String sql = "INSERT INTO xmldatabills (name,data) "
                                + "VALUES (\'" + filename + "\'," +
                                "\'" + data.replaceAll("\'", "`")
                                .replaceAll("&","ampS")+ "\')";

                        System.out.println(sql);


                        stmt.executeUpdate(sql);

                        System.out.println("upload successfully");


                      //  stmt.executeUpdate(sql);


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
            System.out.println("Somthing went wrong with " + fileNameDisplay);
            System.out.println(e.getMessage());
        }



    }


    /** upload all the parsed xml hearing documents in cleanHearing folder to database */
    public void do_it() throws Exception{

        File folder = new File("CHRGBulkparse3/115/");
        File[] listOfFiles = folder.listFiles();

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "beidan", "");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            //read the file, get name + content
            //upload to the database

            for (int i = 0; i < listOfFiles.length; i++) {

                if (listOfFiles[i].isFile()) {

                    String filename = listOfFiles[i].getName();

                    if(filename.endsWith(".xml") && !filename.endsWith("-nodtd.xml") ){

                        String data = readLines("CHRGBulkparse3/115/" + filename);


                        System.out.println("This is data" + data);

                        stmt = c.createStatement();
                        String sql = "INSERT INTO xmlhearing (name,data) "
                                + "VALUES (\'" + filename + "\'," +
                                "\'" + data.replaceAll("\'", "`") + "\')";

                        System.out.println(sql);


//                        try{
//                        stmt.executeUpdate(sql);}
//                        catch (Exception e){
//                            System.out.println(filename + "something wrong here! check");
//                        }
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






}
