package PostgresXMLdatabase;

import Utilities.Bill;
import Utilities.UnwantedWords;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExtractInfo {
    /** add committee to billxmlc */


    /** add sponsor and cosponsor */

    public List<String> getCoSponsor(){

        Connection c = null;
        Statement stmt = null;
        List<String> cosponsorInfo = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String queryStat = "SELECT filename,(xpath('//cosponsor ', data)) " +
                    " FROM  billxmlc where filename  in(" +
                    "select distinct filename from billcount2 where cosponsor isnull ) ";



            /** first get all the file name from the database */
            ResultSet rs1 = stmt.executeQuery(queryStat );
            while (rs1.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray1 =  rs1.getArray("xpath").toString();
                String cosponsor = rarray1.replaceAll("<.*?>\\s*","")
                        .replaceAll("[\\{\\}\"]","");

                String filename = rs1.getString("filename");
                cosponsor = filename + "#&#" + cosponsor;

                // System.out.println(speaker);
                System.out.println(cosponsor);
                cosponsorInfo.add(cosponsor);

            }



            rs1.close();
            stmt.close();
            c.close();





        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cosponsorInfo;

    }

    public List<String> getSponsor(){

        Connection c = null;
        Statement stmt = null;
        List<String> sponsorInfo = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String queryStat = "SELECT filename,(xpath('//sponsor ', data)) " +
                    " FROM  billxmlc where filename in (" +
                    "select filename from billcount2 where sponsor isnull)";



            /** first get all the file name from the database */
            ResultSet rs1 = stmt.executeQuery(queryStat );

            while (rs1.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray1 =  rs1.getArray("xpath").toString();
                String sponsor = rarray1.replaceAll("<.*?>\\s*","")
                        .replaceAll("[\\{\\}\"]","");

                String filename = rs1.getString("filename");
                sponsor = filename + "#&#" + sponsor;

                // System.out.println(speaker);
                System.out.println(sponsor);
                sponsorInfo.add(sponsor);

            }



            rs1.close();
            stmt.close();
            c.close();





        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sponsorInfo;

    }

    public void addsponsors(){



            List<String> sponsors = getSponsor();
            // List<String> cosponsors = getCoSponsor();

        for (String sponsor : sponsors){
            addsponsor(sponsor);
        }



    }

    public void addcosponsors(){



        //List<String> sponsors = getSponsor();
         List<String> cosponsors = getCoSponsor();

        for (String cosponsor : cosponsors){
            addcosponsor(cosponsor);
        }



    }



    public void addsponsor(String sponsor){

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

            stmt = c.createStatement();

            if (sponsor.isEmpty() || !sponsor.contains("#&#")) return;
            String[] info = sponsor.split("#&#");
            if (info.length < 2) return;
            String filename = info[0], sponsorName = info[1];
            sponsorName = sponsorName.replaceAll(",", "&&");
            //  sponsorName = StringUtils.normalizeSpace(sponsorName );

            String queryStat = "update postgres.public.billcount2 set sponsor = " +
                    "\'" + sponsorName + "\'" +
                    " where  filename =  " + "\'" + filename + "\';";
            System.out.println(queryStat);


            int count = stmt.executeUpdate(queryStat);
            System.out.println(count + " records being updated!");

            c.commit();
            stmt.close();
            c.close();




        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void addcosponsor(String cosponsor){

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

            stmt = c.createStatement();

            if (cosponsor.isEmpty() || !cosponsor.contains("#&#")) return;
            String[] info = cosponsor.split("#&#");
            if (info.length < 2) return;
            String filename = info[0], cosponsorName = info[1];
            cosponsorName = cosponsorName.replaceAll(",", "&&");
            //  sponsorName = StringUtils.normalizeSpace(sponsorName );

            String queryStat = "update postgres.public.billcount2 set cosponsor = " +
                    "\'" + cosponsorName + "\'" +
                    " where  filename =  " + "\'" + filename + "\';";
            System.out.println(queryStat);


            int count = stmt.executeUpdate(queryStat);
            System.out.println(count + " records being updated!");

            c.commit();
            stmt.close();
            c.close();




        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /** insert a single record into billcount2 table */
    public void insertRecord(String filename, String committee, String countinfo, int congressNumber){

        Connection c = null;
        Statement stmt = null;


        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String queryStat = "INSERT INTO postgres.public.billcount2 (filename, committee, countinfo, congressnumber) " +
                    " values (" + "\'" + filename + "\'," + "\'" + committee + "\'," +
                    "\'" + countinfo.replaceAll("\'","") + "\'," + congressNumber + ");";

            stmt.executeUpdate(queryStat );
            System.out.println(queryStat);

            c.commit();
            stmt.close();
            c.close();





        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }




    }


    public void insertRecord2(String filename, String committee, int congressNumber){

        Connection c = null;
        Statement stmt = null;


        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String queryStat = "INSERT INTO postgres.public.billtraining (filename, committee, congressnumber) " +
                    " values (" + "\'" + filename + "\'," + "\'" + committee + "\'," +
                      congressNumber + ");";

            stmt.executeUpdate(queryStat );
            System.out.println(queryStat);

            c.commit();
            stmt.close();
            c.close();





        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }




    }

    /** insert into billcount2 table: filename + committee + sponsor + cosponsor + countinfo */
    public void createbillcount2(){

        Connection c = null;
        Statement stmt = null;


        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
//            String queryStat = "SELECT filename,committee,countinfo " +
//                    " FROM  billcount where filename not in (" +
//                    "select distinct billcount2.filename from billcount2" +
//                    ")  ";

            /** add more samples from billxmlc */
            String queryStat = "SELECT filename,committee " +
                    " FROM  billxmlc where filename not in (" +
                    "select distinct billcount2.filename from billcount2" +
                    ")  ";

            ResultSet rs1 = stmt.executeQuery(queryStat );
            int count=0;
            while (rs1.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
           //     String countinfo = rs1.getString("countinfo");
                String filename = rs1.getString("filename");
                String committeeName = (rs1.getObject("committee")!=null) ? rs1.getString("committee") : "";
                int congressNumber = (filename.contains("BILLS-") ) ? Integer.parseInt(filename.replace("BILLS-","").substring(0,3)) : 0;

                if(!committeeName.isEmpty()){

                    if(!committeeName.trim().equals("Science, Space, and Technology") && committeeName.contains(",")){
                        String[] cInfo = committeeName.split(",");
                        if(cInfo.length>1){
                            for(String s : cInfo){

                                //insertRecord(filename,s,countinfo,congressNumber);
                                  insertRecord2(filename,s,congressNumber);

                            }
                        }
                    }else{
                       // insertRecord(filename,committeeName,countinfo,congressNumber);
                        insertRecord2(filename,committeeName,congressNumber);

                    }

                }


               count++;

            }



            rs1.close();
            stmt.close();
            c.close();





        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }





    /** add committee information to the table
     *
     */

    public void updateAllCommittee(){

        List<String> committeeInfoList = getCommittee();
        for(int i=0; i<committeeInfoList.size(); i++){

            addCommittee(committeeInfoList.get(i));
        }

    }


    public void addCommittee(String complexname){

        if(complexname.isEmpty() || !complexname.contains("#&#")) return;

        String[] info = complexname.split("#&#");
        if(info.length<2) return;
        String filename = info[0], committeeName = info[1];
        committeeName = committeeName
                .replaceAll("\n","")
                .replaceAll("(Committee[\n\\s]*[Oo]+n\\s*)","")
                .replaceAll("^\\s+","");
        committeeName = StringUtils.normalizeSpace(committeeName );

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


            stmt = c.createStatement();
            String queryStat = "update postgres.public.billcount set committee = " +
                    "\'" + committeeName + "\'" +
                    " where  filename =  " + "\'" + filename + "\';";


            /** update committee on billxmlc */
//            String queryStat = "update postgres.public.billxmlc set committee = " +
//                    "\'" + committeeName + "\'" +
//                    " where  filename =  " + "\'" + filename + "\';";
//

            System.out.println(queryStat);


            /** first get all the file name from the database */
            int count = stmt.executeUpdate(queryStat);
            System.out.println(count + " records being updated!");

            c.commit();
            stmt.close();
            c.close();



        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }



    public List<String> getCommittee(){

        Connection c = null;
        Statement stmt = null;
        List<String> committeeInfo = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();

            //get the not null committee record
//            String queryStat = "SELECT filename,(xpath('//committee-name ', data)) " +
//                    " FROM  billxmla where (select committee from billcount ) isnull ";

            String queryStat = "SELECT filename,(xpath('//committee-name ', data))\n" +
                    "                     FROM  billxmlc where filename not in (\n" +
                    "    select distinct filename\n" +
                    "from billcount\n" +
                    "where committee notnull\n" +
                    "    );";

            /** add committee to billxmlc */

//            String queryStat = "SELECT filename,(xpath('//committee-name ', data))\n" +
//                    "                     FROM  billxmlc where committee isnull ;";
//

            /** first get all the file name from the database */
            ResultSet rs1 = stmt.executeQuery(queryStat );
            while (rs1.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray1 =  rs1.getArray("xpath").toString();
                String committeeName = rarray1.replaceAll("<.*?>\\s*","")
                        .replaceAll("[\\{\\}\"]","");

                String filename = rs1.getString("filename");
                committeeName = filename + "#&#" + committeeName;

                // System.out.println(speaker);
                System.out.println(committeeName);
                committeeInfo.add(committeeName);

            }



            rs1.close();
            stmt.close();
            c.close();





        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

      return committeeInfo;

    }


    /** Extract information from billxmla table **/

    public BillInfo getInfoFromDatabase(){

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        List<String> shortTitleArray = new ArrayList<>();
        List<String> headerArray = new ArrayList<>();
        List<String> textArray = new ArrayList<>();


        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "beidan", "");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");



            stmt = c.createStatement();
            String queryStat = "SELECT filename,(xpath('//short-title ', data)) " +
                    " FROM  billxmla ";
            String queryStat2 = "SELECT filename,(xpath('//header ', data)) " +
                    " FROM  billxmla ";
            String queryStat3 = "SELECT filename,(xpath('//text ', data)) " +
                    " FROM  billxmla ";


            // get people information

            ResultSet rs1 = stmt.executeQuery(queryStat );
            while (rs1.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray1 =  rs1.getArray("xpath").toString();
                // System.out.println(rarray1);
                String name1 = rs1.getString("filename");
                    shortTitleArray.add(name1 + "#&#" + rarray1);

                // System.out.println(speaker);
                System.out.println(rarray1);

            }



            ResultSet rs2 = stmt.executeQuery(queryStat2 );
            while (rs2.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray2 =  rs2.getArray("xpath").toString();

                String name2 = rs2.getString("filename");
                headerArray.add(name2 + "#&#" + rarray2);


            }

            ResultSet rs3 = stmt.executeQuery(queryStat3 );
            while (rs3.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String rarray3 =  rs3.getArray("xpath").toString();


                String name3 = rs3.getString("filename");
                textArray.add(name3 + "#&#" + rarray3);
                System.out.println(name3 + "#&#" + rarray3);


            }

            rs1.close();
            stmt.close();
            c.close();


        }catch (Exception cf){
            System.out.println(cf.getMessage());
            System.out.println("ooop, something went wrong here");
        }

        return new BillInfo(shortTitleArray,headerArray,textArray);
    }

    public void uploadBillInfo(){

     BillInfo billInfo = getInfoFromDatabase();

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

            List<String> shortTitleArray = billInfo.getShortTitleArray();
            List<String> headerArray = billInfo.getHeaderArray();
            List<String> textArray = billInfo.getTextArray();


            List<String> unwanted = new UnwantedWords().getUnwlist();
            for(String st : shortTitleArray) {


                String[] info1 = st.trim().split("#&#");
                if (info1.length==2 && info1[1].trim().length()>3) {

                    //get rid of xml tags


                    stmt = c.createStatement();
                    String identity = "shortTitle";
                    String sql = "INSERT INTO billinfo (name,info,identity) "
                            + "VALUES (\'" + info1[0].replaceAll("\'", "`") + "\',"

                            + "\'" + info1[1].replaceAll("<.*?> ?", "").replaceAll("<.*?> ?", "").replaceAll("\'", "`") + "\',"
                             + "\'" + identity.replaceAll("\'", "`") + "\')";

                    System.out.println(sql);

                    stmt.executeUpdate(sql);
                    stmt.close();

                }
            }

            for(String he : headerArray) {


                String[] info2 = he.trim().split("#&#");
                if (info2.length==2 && info2[1].trim().length()>3) {

                    stmt = c.createStatement();
                    String identity = "header";
                    String sql = "INSERT INTO billinfo (name,info,identity) "
                            + "VALUES (\'" + info2[0].replaceAll("\'", "`") + "\',"

                            + "\'" + info2[1].replaceAll("<.*?> ?", "").replaceAll("\'", "`") + "\',"
                            + "\'" + identity.replaceAll("\'", "`") + "\')";

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

    public static void main(String[] args){

        ExtractInfo extractInfo = new ExtractInfo();
        /** get the information from database */
      //  extractInfo.getInfoFromDatabase();
        /** upload the info to billinfo table */
      //  extractInfo.uploadBillInfo();
        /** get the committee name from database using filename */
//        List<String> committeeInfoList = extractInfo.getCommittee();
//       for(String c :  committeeInfoList){
//           extractInfo.addCommittee(c);
//       }

        /** update all in one step */
     //  extractInfo.updateAllCommittee();

        /** insert into billcount2 table */
     //   extractInfo.createbillcount2();

        /** add sponsor and cosponsor information */
       // extractInfo.addsponsors();
        extractInfo.addcosponsors();
    }
}
