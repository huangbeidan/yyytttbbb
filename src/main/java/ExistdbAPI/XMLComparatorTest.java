package ExistdbAPI;

import BillsAPI.DiffTable;
import Utilities.CommentLines;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

public class XMLComparatorTest {

    public static void main(String args[]) throws FileNotFoundException, SAXException, IOException {


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

            String file1 = "/Users/beidan/yyytttbbb/test/example.xml";
            String file2 = "/Users/beidan/yyytttbbb/test/example2.xml";


            //If needed, comment dtd and stylesheet first
//            CommentLines cl = new CommentLines();
//            String file1_after = cl.CommentDtDANDSyleshhet(file1);
//            String file2_after = cl.CommentDtDANDSyleshhet(file2);

            //find the different
            XMLComparator xmlComparator = new XMLComparator();
            List<String> differences = xmlComparator.XMLCompare(file1, file2);

            if(differences.size()!=0){



                for(Object diff : differences){
                    //write the results to database



                    String[] split = diff.toString().split("Expected|but was|- comparing");
                    String attributeNode = split[1].trim().split("'")[0].trim();
                    String Doc1Value = split[1].trim().split("'")[1];
                    String Doc2Value = split[2].trim().replaceAll("'","");

                    String[] part3 = split[3].trim().split("\\sto\\s<");


                    String Doc1Loc = part3[0];
                    String Doc2Loc = (part3.length>1)? "<"+ part3[1] : "null";

                    //create a doc identifier

//                    String Doc1name = new DiffTable().getFileName(file1);
//                    String Doc2name = new DiffTable().getFileName(file2);
//                    String articleIndex = Doc1name.split("-")[1] + "-vs-" + Doc2name.split("-")[1];

                    String articleIndex = "example6";

                    //write the results to database
                    stmt = c.createStatement();

                    String sql = "INSERT INTO xmlcomparetest (articleindex,attributenode,comparedagainst,current,comparedagainstlocation," +
                            "currentlocation) "
                            + "VALUES (\'" + articleIndex + "\'," +
                            "\'" + attributeNode.replaceAll("\'", "`") + "\'," +
                            "\'" + Doc1Value.replaceAll("\'", "`") + "\'," +
                            "\'" + Doc2Value.replaceAll("\'", "`") + "\'," +
                            "\'" + Doc1Loc.replaceAll("\'", "`") + "\'," +
                            "\'" + Doc2Loc.replaceAll("\'", "`") + "\')";

                    //System.out.println(sql);
                    stmt.executeUpdate(sql);
                    System.out.println(sql);

                }

                stmt.close();
                c.commit();
                c.close();




            }








        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);


        }

    }



}
