package BillsAPI;

import ExistdbAPI.XMLComparator;
import Utilities.CommentLines;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class DiffTable {


    /**
     *
     * @param map  CongressNumber - Bill Nmber Map
     *             First generate the URLs for crawling
     *             Then get all the files
     *             Get the differences and write them to database
     */
    public void Onestep(Map<Integer,Integer> map){

        GovInfoCrawler gov = new GovInfoCrawler();
        CrunchifyFetchURLData cf = new CrunchifyFetchURLData();

        /**
         *  there are 4 stage: ih, rh, eh, rfs
         *  there are 4 file formats: xml, pdf, mod, htm
         */

        for(int con_number : map.keySet()){

            int bill_number = map.get(con_number);

            String url11 = gov.urlGeneratorHR("ih",con_number,bill_number,"xml");
            String name1 = cf.fetchURLData(url11,"HR");

            String url21 = gov.urlGeneratorHR("rh",con_number,bill_number,"xml");
            String name2 = cf.fetchURLData(url21,"HR");

            String url31 = gov.urlGeneratorHR("eh",con_number,bill_number,"xml");
            String name3 = cf.fetchURLData(url31,"HR");

            String url41 = gov.urlGeneratorHR("rfs",con_number,bill_number,"xml");
            String name4 = cf.fetchURLData(url41,"HR");



            CommentLines cl = new CommentLines();
            String prefix = "/Users/beidan/yyytttbbb/HR/";
            name1 = cl.CommentDtDANDSyleshhet(prefix,name1);
            name2 = cl.CommentDtDANDSyleshhet(prefix,name2);
            name3 = cl.CommentDtDANDSyleshhet(prefix,name3);
            name4 = cl.CommentDtDANDSyleshhet(prefix,name4);

            System.out.println("print name after comment is: " + name1);

            WriteDiff2DB(name1,name2);
            WriteDiff2DB(name2,name3);
            WriteDiff2DB(name3,name4);

        }


    }


    /** get file name without path */
    public String getFileName(String docPath){



        int index = docPath.lastIndexOf('/');
        if(index>0){
            return docPath.substring(index +1);
        }

        return docPath;

    }


    public void WriteDiff2DB(String doc1Path, String doc2Path) {


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



            XMLComparator xmlComparator = new XMLComparator();
            List<String> differences = xmlComparator.XMLCompare(doc1Path,doc2Path);

            stmt = c.createStatement();

            for(Object diff : differences){
                //write the results to database



                String[] split = diff.toString().split("Expected|but was|- comparing");
                String attributeNode = split[1].trim().split("'")[0].trim();
                String Doc1Value = split[1].trim().split("'")[1];
                String Doc2Value = split[2].trim().replaceAll("'","");

                String[] part3 = split[3].trim().split("\\sto\\s");


                String Doc1Loc = part3[0];
                String Doc2Loc = part3[1];

                   //create a doc identifier

                String Doc1name = getFileName(doc1Path);
                String Doc2name = getFileName(doc2Path);
                String articleIndex = Doc1name.split("-")[1] + "-vs-" + Doc2name.split("-")[1];

                //write the results to database
                stmt = c.createStatement();

                String sql = "INSERT INTO billscomparison (articleindex,attributenode,comparedagainst,current,comparedagainstlocation," +
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

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);


        }
    }




        public static void main(String[] args){


//
//        String test = "Expected attribute value 'Engrossed-in-House' but was 'Referred-in-Senate' - comparing <bill bill-stage=\"Engrossed-in-House\"...> at /bill[1]/@bill-stage to <bill bill-stage=\"Referred-in-Senate\"...> at /bill[1]/@bill-stage\n";
//
//        String[] split = test.split("Expected|but was|- comparing");
//        String attributeNode = split[1].trim().split("'")[0];
//        String Doc1Value = split[1].trim().split("'")[1];
//        String Doc2Value = split[2].trim().replaceAll("'","");
//        String[] part3 = split[3].trim().split("\\sat\\s");
//        String DiffSection = part3[0];
//        String Doc1Loc = part3[1];
//        String Doc2Loc = part3[2];


//       DiffTable dt = new DiffTable();
//       String file1 = "/Users/beidan/yyytttbbb/HR/BILLS-115hr806ih.xml";
//       String file2 = "/Users/beidan/yyytttbbb/HR/BILLS-115hr806rh.xml";
//
//            CommentLines cl = new CommentLines();
//            String file1_after = cl.CommentDtDANDSyleshhet(file1);
//            String file2_after = cl.CommentDtDANDSyleshhet(file2);
//       dt.WriteDiff2DB(file1_after,file2_after);


            DiffTable diffTable = new DiffTable();
            Map<Integer,Integer> map = new HashMap<>();

            /** For House Bills (HR) specifically: MAP<Congress Number, Bill Number> */
            map.put(115,115);

            diffTable.Onestep(map);


        }
}
