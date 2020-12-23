package ExistdbAPI;

import BillsAPI.DiffTable;
import SimilarityScore.SMITHW;
import Utilities.SmithWatermanWords;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.NGram;
import org.xml.sax.SAXException;
import wagu.Block;
import wagu.Board;
import wagu.Table;

import javax.print.Doc;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class XMLComparatorToFile {
    int maxLenLoc;
    int maxReasonChange;
    String currFileName;
    String comparedAgainstFileName;

    public String paraphraseReason(String TypeOfAttribute){
        switch (TypeOfAttribute){
            case "text value":
                return "content change";
            case "presence of child node":
                return "insertion/deletion";
            case "sequence of child nodes":
                return "order change";
            default:
                return TypeOfAttribute;
        }
    }

    public List<List<String>> getAllDifferencesTable(String filepath1, String filepath2) throws FileNotFoundException, SAXException, IOException{

        List<List<String>> res = new ArrayList<>();


        //first get the difference
//         filepath1 = "/Users/beidan/yyytttbbb/test/example.xml";
//         filepath2 = "/Users/beidan/yyytttbbb/test/example2.xml";


        //If needed, comment dtd and stylesheet first
//            CommentLines cl = new CommentLines();
//            String file1_after = cl.CommentDtDANDSyleshhet(file1);
//            String file2_after = cl.CommentDtDANDSyleshhet(file2);


        //get the document name first
        DiffTable diffTable = new DiffTable();
        String filename1 = diffTable.getFileName(filepath1);
        String filename2 = diffTable.getFileName(filepath2);



        //find the different
        XMLComparator xmlComparator = new XMLComparator();
        List<String> differences = xmlComparator.XMLCompare(filepath1, filepath2);

        //add NGram method to calculate similarity
        Jaccard jaccard = new Jaccard();
        SmithWatermanWords smithWatermanWords = new SmithWatermanWords();





        if (differences.size() != 0) {



            for (Object diff : differences) {
                //write the results to database


                String[] split = diff.toString().split("Expected|but was|- comparing");
                String attributeNode = split[1].trim().split("'")[0].trim();
                attributeNode = paraphraseReason(attributeNode);
                maxReasonChange = Math.max(maxReasonChange,attributeNode.length());



                String Doc1Value = split[1].trim().split("'")[1].replaceAll("\\n","");
                String Doc2Value = split[2].trim().replaceAll("'", "").replaceAll("\\n","");



                //calculate similarity scores here
                double JaccardIndex = 0.0;
                String JaccardIndexString;
                double SmithWatermanScore = 0.0;
                String SmithWatermanString;

                float SmithWatermanMax = 0.0f;
                float[][] SmithWatermanMatrix = new float[0][0];
                String SmithDetailedReport = "";

                if(attributeNode.equals("content change")){
                    JaccardIndex = jaccard.similarity(Doc1Value,Doc2Value);
                    JaccardIndex = Math.round(JaccardIndex * 100.0)/100.0;
                    if(JaccardIndex>0.2){

                        String[] a = Doc1Value.replaceAll("[^a-zA-Z0-9’'\\s]","").split(" ");
                        String[] b = Doc2Value.replaceAll("[^a-zA-Z0-9’'\\s]","").split(" ");

                        for(String str: a){
                            System.out.println("str is nagea : "+ str);
                        }

                        for(String str: b){
                            System.out.println("str is nageb : "+ str);
                        }


                        SMITHW smithw = smithWatermanWords.smithWaterman(a,b);
                        SmithWatermanScore = smithWatermanWords.compare(a,b);
                        SmithWatermanScore = Math.round(SmithWatermanScore*100.0)/100.0;

                        SmithWatermanMax = smithw.getMax();
                        SmithWatermanMatrix = smithw.getMatrix();

                        LinkedList<int[]> route = smithWatermanWords.getRoute(SmithWatermanMatrix,SmithWatermanMax);
                        SmithDetailedReport = smithWatermanWords.getStringReport(route);

                        System.out.println(SmithDetailedReport);


                    }else{
                        SmithDetailedReport = "Similarity score is too low here. It's probably because of a position shift";
                    }
                }

                JaccardIndexString = (JaccardIndex==0.0) ? " ":String.valueOf(JaccardIndex);
                SmithWatermanString = (SmithWatermanScore==0.0) ? " ":String.valueOf(SmithWatermanScore);


                Doc1Value = Doc1Value.length()>30 ? Doc1Value.substring(0,30) + "..." : Doc1Value;
                Doc2Value = Doc2Value.length()>30 ? Doc2Value.substring(0,30) + "..." : Doc2Value;

                String[] part3 = split[3].trim().split("\\sto\\s+(?:<|null|at)");

                String[] loc = part3[0].split("at\\s+(?:/|null)");
                String Doc1Loc = (loc.length>1)?loc[1]:"null";

                //record maxLen
                maxLenLoc = Math.max(maxLenLoc,Doc1Loc.length());

                String Doc2Loc = "";
                if(part3.length>1){
                    String[] loc2 = part3[1].split("at\\s+(?:/|null)");
                    Doc2Loc = (loc2.length>1)?loc2[1]:"null";

                }else{
                    Doc2Loc = "null";
                }


                res.add(new ArrayList<>(Arrays.asList(Doc1Loc,Doc2Loc,attributeNode,Doc1Value,Doc2Value,JaccardIndexString,SmithWatermanString, SmithDetailedReport)));




            }
        }

        return res;

    }


    /**
     *
     *
     * @param filepath1 "/Users/beidan/yyytttbbb/test/example.xml"
     * @param filepath2 "/Users/beidan/yyytttbbb/test/example2.xml"
     * @return
     * @throws FileNotFoundException
     * @throws SAXException
     * @throws IOException
     */
    public List<List<String>> getAllDifferences(String filepath1, String filepath2) throws FileNotFoundException, SAXException, IOException{

        List<List<String>> res = new ArrayList<>();


          //first get the difference
//         filepath1 = "/Users/beidan/yyytttbbb/test/example.xml";
//         filepath2 = "/Users/beidan/yyytttbbb/test/example2.xml";


        //If needed, comment dtd and stylesheet first
//            CommentLines cl = new CommentLines();
//            String file1_after = cl.CommentDtDANDSyleshhet(file1);
//            String file2_after = cl.CommentDtDANDSyleshhet(file2);


        //get the document name first
        DiffTable diffTable = new DiffTable();
        String filename1 = diffTable.getFileName(filepath1);
        String filename2 = diffTable.getFileName(filepath2);



        //find the different
        XMLComparator xmlComparator = new XMLComparator();
        List<String> differences = xmlComparator.XMLCompare(filepath1, filepath2);

        //add NGram method to calculate similarity
        Jaccard jaccard = new Jaccard();
        SmithWatermanWords smithWatermanWords = new SmithWatermanWords();





        if (differences.size() != 0) {



            for (Object diff : differences) {
                //write the results to database



                String[] split = diff.toString().split("Expected|but was|- comparing");

                if(split.length>3){


                    String attributeNode = split[1].trim().split("'")[0].trim();
                    attributeNode = paraphraseReason(attributeNode);
                    maxReasonChange = Math.max(maxReasonChange,attributeNode.length());


                    String Doc1Value = (split[1].trim().split("'").length > 1) ? split[1].trim().split("'")[1].replaceAll("\\n","") : "";
                    String Doc2Value = split[2].trim().replaceAll("'", "").replaceAll("\\n","");



                    //calculate similarity scores here
                    double JaccardIndex = 0.0;
                    String JaccardIndexString;
                    double SmithWatermanScore = 0.0;
                    String SmithWatermanString;

                    float SmithWatermanMax = 0.0f;
                    float[][] SmithWatermanMatrix = new float[0][0];
                    String SmithDetailedReport = "";

                    if(attributeNode.equals("content change")){
                        JaccardIndex = jaccard.similarity(Doc1Value,Doc2Value);
                        JaccardIndex = Math.round(JaccardIndex * 100.0)/100.0;
                        if(JaccardIndex>0.2){

                            String[] a = Doc1Value.replaceAll("[^a-zA-Z0-9’'\\s]","").split(" ");
                            String[] b = Doc2Value.replaceAll("[^a-zA-Z0-9’'\\s]","").split(" ");

                            for(String str: a){
                                System.out.println("str is nagea : "+ str);
                            }

                            for(String str: b){
                                System.out.println("str is nageb : "+ str);
                            }


                            SMITHW smithw = smithWatermanWords.smithWaterman(a,b);
                            SmithWatermanScore = smithWatermanWords.compare(a,b);
                            SmithWatermanScore = Math.round(SmithWatermanScore*100.0)/100.0;

                            SmithWatermanMax = smithw.getMax();
                            SmithWatermanMatrix = smithw.getMatrix();

                            LinkedList<int[]> route = smithWatermanWords.getRoute(SmithWatermanMatrix,SmithWatermanMax);
                            SmithDetailedReport = smithWatermanWords.getStringReport(route);

                            System.out.println(SmithDetailedReport);


                        }else{
                            SmithDetailedReport = "Similarity score is too low here. It's probably because of a position shift";
                        }
                    }

                    JaccardIndexString = (JaccardIndex==0.0) ? " ":String.valueOf(JaccardIndex);
                    SmithWatermanString = (SmithWatermanScore==0.0) ? " ":String.valueOf(SmithWatermanScore);


                    Doc1Value = Doc1Value.length()>30 ? Doc1Value.substring(0,30) + "..." : Doc1Value;
                    Doc2Value = Doc2Value.length()>30 ? Doc2Value.substring(0,30) + "..." : Doc2Value;

                    String[] part3 = split[3].trim().split("\\sto\\s+(?:<|null|at)");

                    String[] loc = part3[0].split("at\\s+(?:/|null)");
                    String Doc1Loc = (loc.length>1)?loc[1]:"null";

                    //record maxLen
                    maxLenLoc = Math.max(maxLenLoc,Doc1Loc.length());

                    String Doc2Loc = "";
                    if(part3.length>1){
                        String[] loc2 = part3[1].split("at\\s+(?:/|null)");
                        Doc2Loc = (loc2.length>1)?loc2[1]:"null";

                    }else{
                        Doc2Loc = "null";
                    }


                    res.add(new ArrayList<>(Arrays.asList(Doc1Loc,Doc2Loc,attributeNode,Doc1Value,Doc2Value,JaccardIndexString,SmithWatermanString, SmithDetailedReport, filename1, filename2)));







                }




            }
        }

        return res;

    }

    public void generateReport(List<List<String>> t1rowList, String filename) throws IOException {


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

            BufferedWriter writer = new BufferedWriter(new FileWriter("report/" + filename));


            // add some decoration
            writer.write("===================================================\n");
            writer.newLine();

            //get the summary String
            String summary = "total difference is: " + t1rowList.size() + "\n"
                    + "comparing document: " + currFileName + "\n"
                    + "to document: " + comparedAgainstFileName + "\n";
            writer.write(summary);
            writer.newLine();

            writer.write("===================================================");
            writer.newLine();


            for (List<String> line : t1rowList) {

                //skip unimportant part
                //a. skip attribute value
                if (line.get(0).matches(".*(/@).*") && !line.get(0).matches(".*(/@bill-stage)")) {
                    continue;
                }
                //b. skip number of child nodes change
                if (line.get(2).equals("number of child nodes") || line.get(2).equals("number of element attributes")
                        || line.get(2).equals("attribute name")
                        || line.get(2).equals("presence of child nodes to be")
                        || line.get(2).equals("order change")) {
                    continue;
                }


                StringBuilder ReportLoc = new StringBuilder();
                StringBuilder ReportReason = new StringBuilder();
                StringBuilder ReportVal = new StringBuilder();


                ReportLoc.append("We are at location: ").append(line.get(0))
                        .append("   Compared with location: ").append(line.get(1));


                ReportReason.append("The reason of change is: ");


                //get file names
                String filename1 = line.get(line.size()-2);
                String filename2 = line.get(line.size()-1);


                //add reportVal
                ReportVal.append("The value is expected to be: ").append(line.get(3))
                        .append(" But actually it is: ").append(line.get(4));



                // if it is insertion/deletion, display the content
                if (line.get(2).equals("insertion/deletion") && line.get(0).equals("null")) {
                    ReportReason.append("insertion");

                    //append reportVal for insertion, query the database again to get the content
                    stmt = c.createStatement();

                    //get the information for xPath

                    String pathString = String.format("SELECT xpath('//%s ', data) ",line.get(1));
                    String attString = String.format("FROM xmldatabills WHERE name='%s' ", filename2);


                    ResultSet rs = stmt.executeQuery( pathString +
                            attString);

                    System.out.println(pathString + attString);

                    while (rs.next()){
                        //int id = rs.getInt("id");
                        //System.out.println(id);
                        String rarray =  rs.getArray("xpath").toString()
                                .replaceAll("\\<.*?\\>", "");
                        //System.out.println(rarray);
                        ReportVal.append("\n Inserted paragraph content is: ").append(rarray);
                    }




                } else if (line.get(2).equals("insertion/deletion") && line.get(1).equals("null")) {
                    ReportReason.append("deletion");


                    stmt = c.createStatement();

                    //get the information for xPath

                    String pathString = String.format("SELECT xpath('//%s ', data) ",line.get(0));
                    String attString = String.format("FROM xmldatabills WHERE name='%s' ", filename1);


                    ResultSet rs = stmt.executeQuery( pathString +
                            attString);

                    System.out.println(pathString + attString);

                    while (rs.next()){
                        //int id = rs.getInt("id");
                        //System.out.println(id);
                        String rarray =  rs.getArray("xpath").toString()
                                .replaceAll("\\<.*?\\>", "");
                        //System.out.println(rarray);
                        ReportVal.append("\n Deleted paragraph content is: ").append(rarray);
                    }


                } else {
                    ReportReason.append(line.get(2));



                }



                if (line.get(2).equals("content change")) {
                    ReportVal.append("\n").append("The Jaccard Index is: ").append(line.get(5));

                    ReportVal.append("\n").append("And the Smith Waterman Score is: ").append(line.get(6)).append("\n")
                            .append("The Detailes are shown as followings: ").append("\n").append(line.get(7));
                }

                writer.write(ReportLoc.toString());
                writer.newLine();
                writer.write(ReportReason.toString());
                writer.newLine();
                writer.write(ReportVal.toString());
                writer.write("\n");
                writer.write("\n");


            }


            writer.close();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    public String do_it(List<List<String>> t1rowsList) throws FileNotFoundException, SAXException, IOException  {

//        for(List<String> list : t1rowsList){
//            System.out.println(list.get(4) + " " + list.get(5));
//        }

        //get the summary String
        String summary = "total difference is: " + t1rowsList.size() + "\n"
                + "comparing document: " + currFileName + "\n"
                + "to document: " + comparedAgainstFileName + "\n";

        // create table
        List<String> t2headers = Arrays.asList("CurrLoc", "ComparedAgainstLoc", "ReasonOfChange", "CurrVal", "ComparedAgainstVal","Jaccard","SmithW","SmithDetailedReport");
        List<Integer> t2ColWidths = Arrays.asList(maxLenLoc,maxLenLoc, maxReasonChange, 33, 33,7,7,100);


       // String tableString = board.setInitialBlock(new Table(board, 250, t2headers, t1rowsList, t2ColWidths).tableToBlocks()).build().getPreview();




        //bookmark 1
        Board board = new Board(500);
        board.setInitialBlock(new Block(board, 60, 7, summary).allowGrid(false).setBlockAlign(Block.BLOCK_LEFT).setDataAlign(Block.DATA_BOTTOM_LEFT));
        board.appendTableTo(0, Board.APPEND_BELOW, new Table(board, 250, t2headers, t1rowsList,t2ColWidths));

        String boardString = (board.invalidate().build().getPreview());
        System.out.println(boardString);


          return boardString;

    }

    public void writeStringToFile(String boardString, String filename) throws IOException{

        //write the table to txt file
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        writer.write(boardString);

        String footer = "" + "\n" +
                "\n"+
                "San Diego Supercomputer Center";
        writer.newLine();
        writer.write(footer);

        writer.close();

    }

    public void oneStepReport(int congressNumber, int billNumber) throws FileNotFoundException, SAXException, IOException{



        String filepath1 =  String.format("/Users/beidan/yyytttbbb/cleanHR/BILLS-%1$shr%2$sih.xml",congressNumber,billNumber);
        String filepath2 =  String.format("/Users/beidan/yyytttbbb/cleanHR/BILLS-%1$shr%2$srh.xml",congressNumber,billNumber);
        String filepath3 =  String.format("/Users/beidan/yyytttbbb/cleanHR/BILLS-%1$shr%2$seh.xml",congressNumber,billNumber);
        String filepath4 =  String.format("/Users/beidan/yyytttbbb/cleanHR/BILLS-%1$shr%2$srfs.xml",congressNumber,billNumber);
        String filepath5 =  String.format("/Users/beidan/yyytttbbb/cleanHR/BILLS-%1$shr%2$srds.xml",congressNumber,billNumber);
//

//
        File file1 = new File(filepath1);
        File file2 = new File(filepath2);
        File file3 = new File(filepath3);
        File file4 = new File(filepath4);
        File file5 = new File(filepath5);


        List<List<String>> t1rowsList = new ArrayList<>();
        List<List<String>> t2rowsList = new ArrayList<>();
        List<List<String>> t3rowsList = new ArrayList<>();
        List<List<String>> t4rowsList = new ArrayList<>();

        if(file1.isFile() && file2.isFile()){
        t1rowsList = this.getAllDifferences(filepath1,filepath2);}

        if(file2.isFile() && file3.isFile()) {
             t2rowsList = this.getAllDifferences(filepath2, filepath3);
        }
        if(file3.isFile() && file4.isFile()) {
           t3rowsList = this.getAllDifferences(filepath3, filepath4);
        }
        if(file3.isFile() && file5.isFile()){
            t4rowsList = this.getAllDifferences(filepath3,filepath5);
        }


        if(t1rowsList.size()>0){

            DiffTable diffTable = new DiffTable();
            this.currFileName = diffTable.getFileName(filepath1);
            this.comparedAgainstFileName = diffTable.getFileName(filepath2);

            String reportName = String.format("report%1$s-%2$sIHvsRH",congressNumber,billNumber);
            this.generateReport(t1rowsList,reportName + ".txt");



        }

        if(t2rowsList.size()>0){

            DiffTable diffTable = new DiffTable();
            this.currFileName = diffTable.getFileName(filepath2);
            this.comparedAgainstFileName = diffTable.getFileName(filepath3);

            String reportName = String.format("report%1$s-%2$sRHvsEH",congressNumber,billNumber);
            this.generateReport(t2rowsList,reportName + ".txt");
        }


        if(t3rowsList.size()>0){

            DiffTable diffTable = new DiffTable();
            this.currFileName = diffTable.getFileName(filepath3);
            this.comparedAgainstFileName = diffTable.getFileName(filepath4);

            String reportName = String.format("report%1$s-%2$sRHvsRFS",congressNumber,billNumber);
            this.generateReport(t3rowsList,reportName + ".txt");

        }

        if(t4rowsList.size()>0){

            DiffTable diffTable = new DiffTable();
            this.currFileName = diffTable.getFileName(filepath3);
            this.comparedAgainstFileName = diffTable.getFileName(filepath5);

            String reportName = String.format("report%1$s-%2$sRHvsRDS",congressNumber,billNumber);
            this.generateReport(t4rowsList,reportName + ".txt");

        }

    }

    public static void main(String[] args) throws FileNotFoundException, SAXException, IOException{


        //test documents: /HR/BILLS-115hr806rfs-nodtd.xml /HR/BILLS-115hr806eh-nodtd.xml

        String filepath1 =  "/Users/beidan/yyytttbbb/cleanHR/BILLS-115hr806rfs.xml";
               String filepath2  = "/Users/beidan/yyytttbbb/cleanHR/BILLS-115hr806eh.xml";

//        String filepath1 = "/Users/beidan/yyytttbbb/test/example.xml";
//        String filepath2 = "/Users/beidan/yyytttbbb/test/example2.xml";


        XMLComparatorToFile xmlComparatorToFile = new XMLComparatorToFile();
       List<List<String>> t1rowsList = xmlComparatorToFile.getAllDifferences(filepath1,filepath2);

        //add Diff Table method to get the filename

        DiffTable diffTable = new DiffTable();
        xmlComparatorToFile.currFileName = diffTable.getFileName(filepath1);
        xmlComparatorToFile.comparedAgainstFileName = diffTable.getFileName(filepath2);


        /** Generate the difference table */


        List<List<String>> t2rowsList = xmlComparatorToFile.getAllDifferencesTable(filepath1,filepath2);
        String boardString = xmlComparatorToFile.do_it(t2rowsList);

           xmlComparatorToFile.writeStringToFile(boardString,"exampleDifference.txt");
       // xmlComparatorToFile.generateReport(t1rowsList,"reportTest2.txt");


        xmlComparatorToFile.oneStepReport(115,510);

    }





}
