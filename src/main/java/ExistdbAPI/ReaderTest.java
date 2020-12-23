package ExistdbAPI;

import info.debatty.java.stringsimilarity.Damerau;
import info.debatty.java.stringsimilarity.NGram;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.List;

import static ExistdbAPI.XMLComparator.compareXML;
import static ExistdbAPI.XMLComparator.printDifferences;
import static java.awt.SystemColor.info;



import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;

public class ReaderTest {


    /** JaroWinkler distance */
    public double compareStrings(String stringA, String stringB) {
        JaroWinkler algorithm = new JaroWinkler();
        return algorithm.getSimilarity(stringA, stringB);
    }




    public static void main(String[] args) throws FileNotFoundException, SAXException, IOException {

          ReaderTest readerTest = new ReaderTest();
          String A = "The shootings that killed at least 49 people at two mosques in New Zealand on Friday have placed new scrutiny on New Zealand’s gun laws and sparked a fervent debate about whether they were a factor in the gunman’s decision to carry out his attack there.\n";
          String B = "The shootings that killed at least 49 people at two mosques in New Zealand on Friday have placed new scrutiny on New Zealand’s gun laws and sparked a serious debate about whether they were a factor in the gunman’s decision to carry out his attack there.\n ";
          String C = "The police have described the gunman as a man in his late 20s and officials have said he was an Australian citizen, which has led to comparisons between gun laws in that country and in New Zealand.\n";
          String D = "In the years since a gunman killed 35 people in Port Arthur, Tasmania, in 1996, Australia has embarked on one of the world’s most expansive efforts to rid a society of gun violence. Officials significantly strengthened gun laws, severely restricted semiautomatic weapons and engaged in a buyback program that took more than 650,000 firearms off the streets.\n";
          System.out.println(readerTest.compareStrings(C,D));

        // produces 0.416666
        NGram twogram = new NGram(2);
        System.out.println(twogram.distance("ABCD", "ABTUIO"));

        // produces 0.97222
        String s1 = "Adobe CreativeSuite 5 Master Collection from cheap 4zp";
        String s2 = "Adobe CreativeSuite 5 Master Collection from cheap d1x";
        NGram ngram = new NGram(4);
        System.out.println(ngram.distance(A, B));



    }



}
