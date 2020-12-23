package TwitterOpinionAnalysis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TwitterDBInterface {


    /** do the query, get the String array of top tag */

    public void doit(){

        /** save the response somewhere */
        Connection c = null;
        Statement stmt = null;

        List<String> tagList = new ArrayList<>();

        try {

            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://10.128.6.136:5432/postgres",
                            "b5huang", "pht8mCcW");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");



            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select name as tag, velocity as velocity, accl as accl, date as date from htvelocity " +
                    "where velocity>0 and date >= '2018-10-01' and date <= '2018-10-05'\n" +
                    "ORDER BY velocity desc\n" +
                    "limit 10;");



            while (rs.next()){
                //int id = rs.getInt("id");
                //System.out.println(id);
                String tag = rs.getString("tag");
                int velocity = rs.getInt("velocity");
                int accl = rs.getInt("accl");
               // System.out.println("tag is: " + tag + "v is: " +  velocity + "accl is: " + accl);

                tagList.add(tag);
            }


            for(String t : tagList){


                stmt = c.createStatement();
                ResultSet rs2 = stmt.executeQuery( "select * from htvelocity " +
                        "where name = " + "\'" + t + "\'");


                while(rs2.next()){

                    int vI = rs2.getInt("velocity");
                    Date date = rs2.getDate("date");
                    System.out.println(vI + "Date is: " + date);

                }







            }




            rs.close();
            stmt.close();
            c.close();



        }catch (Exception cf){
            System.out.println(cf.getMessage());
            System.out.println("ooop, something went wrong here");
        }



    }

    public static void main(String[] args){

        TwitterDBInterface twitterDBInterface = new TwitterDBInterface();
        twitterDBInterface.doit();


    }



}
