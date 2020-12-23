package Utilities;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

public class ToXML {

    BufferedReader in;
    StreamResult out;
    TransformerHandler th;

    public static void main(String args[]) {
        new ToXML().begin();
    }

    public void begin() {
        try {
            in = new BufferedReader(new FileReader("data.txt"));
            out = new StreamResult("data.xml");
               openXml();
            String str;

            String patternString = "^\\s*$";
            String patternString2 = "\\s*Serial.*";

            Pattern pattern = Pattern.compile(patternString);
            Pattern pattern2 = Pattern.compile(patternString2);

            th.startElement(null, null, "inserts", null);
            th.startElement(null, null, "pre", null);

            int i=0;
            while ((str = in.readLine()) != null) {

                if(!pattern.matcher(str).matches()){
                    if(pattern2.matcher(str).matches()){
                          process(str,"SerialNumber");
                    }else if(i>5){
                        process(str,"option");
                    }else{
                        doNothing(str);
                    }
                }

                i++;

            }
            in.close();
            closeXml();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openXml() throws ParserConfigurationException, TransformerConfigurationException, SAXException {

        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        th = tf.newTransformerHandler();

        // pretty XML output
        Transformer serializer = th.getTransformer();
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");

        th.setResult(out);
        th.startDocument();

    }

    /**
     *
     * @param s string
     * @param qName node name. e.g. A:option
     * @throws SAXException
     */
    public void process(String s, String qName) throws SAXException {
         th.startElement(null, null, qName,null);
        th.characters(s.toCharArray(), 0, s.length());
        th.endElement(null, null, qName);
         }

    public void doNothing(String s) throws SAXException{

        th.characters(s.toCharArray(),0,s.length());



    }

    public void closeXml() throws SAXException {
        th.endElement(null, null, "inserts");
        th.endDocument();
    }
}