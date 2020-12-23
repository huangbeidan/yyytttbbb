package ExistdbAPI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import Utilities.CommentLines;
import org.custommonkey.xmlunit.*;
import org.xml.sax.SAXException;


/** * * Java program to compare two XML files using XMLUnit example * @author Javin Paul */

/**
 * source: BILLS-115hr115eh.xml
 * target: BILLS-115hr115ih.xml
 */

public class XMLComparator {



    public List XMLCompare(String file1, String file2) throws FileNotFoundException, SAXException, IOException{

        // reading two xml file to compare in Java program
        List differences = new ArrayList();

        try{

        FileInputStream fis1 = new FileInputStream(file1);
        FileInputStream fis2 = new FileInputStream(file2);
        // using BufferedReader for improved performance
        BufferedReader source = new BufferedReader(new InputStreamReader(fis1));
        BufferedReader target = new BufferedReader(new InputStreamReader(fis2));
        //configuring XMLUnit to ignore white spaces
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreWhitespace(true); //comparing two XML using XMLUnit in Java
         differences = compareXML(source, target);
        //showing differences found in two xml files
        printDifferences(differences);




        }


        catch (FileNotFoundException fe){
            System.out.println("File don't exist!");
        }

        return differences;


    }



    public static void main(String args[]) throws FileNotFoundException, SAXException, IOException {

        String file1 = "/Users/beidan/yyytttbbb/HR/BILLS-115hr510eh.xml";
        String file2 = "/Users/beidan/yyytttbbb/HR/BILLS-115hr806rfs.xml";

        //If needed, comment dtd and stylesheet first
        CommentLines cl = new CommentLines();
        String file1_after = cl.CommentDtDANDSyleshhet(file1);
        String file2_after = cl.CommentDtDANDSyleshhet(file2);

        //find the different
        XMLComparator xmlComparator = new XMLComparator();
        xmlComparator.XMLCompare(file1_after,file2_after);

         }


        public static List compareXML(Reader source, Reader target) throws SAXException, IOException{
        //creating Diff instance to compare two XML files
            Diff xmlDiff = new Diff(source, target);

            //ignore element order
           // xmlDiff.overrideElementQualifier(new ElementNameAndTextQualifier());

            //for getting detailed differences between two xml files
            DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff);

            return detailXmlDiff.getAllDifferences(); }



            public static void printDifferences(List differences){
                 int totalDifferences = differences.size();

                  System.out.println("===============================");
                  System.out.println("Total differences : " + totalDifferences);
                  System.out.println("================================");

        for(Object difference : differences){
            System.out.println(difference);
        }
    }
}

