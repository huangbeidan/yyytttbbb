package Utilities;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SecondScanParseXML {


    BufferedReader in;
    StreamResult out;

    Document xmldoc;
    Element root;

    int key;

    String HearingId;


    public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException{

        SecondScanParseXML secondScanParseXML = new SecondScanParseXML();

        String filename = "CHRG-parse1/CHRG-115hhrg25633.xml";
        secondScanParseXML.do_it(filename);


    }

    public String do_it(String filename) throws FileNotFoundException, IOException, ParserConfigurationException {
       // String outputname = "CHRGparse2/" + filename.split("/")[1].split("\\.")[0]+".xml";

        String outputname = "";

            outputname = "CHRGBulkparse2/" + filename.split("/")[1].split("\\.")[0]+".xml";




        try {
            in = new BufferedReader(new FileReader(filename));



            out = new StreamResult(outputname);

            HearingId = filename.split("[/\\-.]")[2].split("[a-z]+")[1];

            String str;
            StringBuilder sb = new StringBuilder();
            int i = 0;



            initXML();

            while ((str = in.readLine()) != null) {

                if (i > 1) {

                    if (changeSection(str)) {


                        if (key == 1) {
                            if (!sb.toString().equals("")) {
                                process_general(sb.toString(), "intro");
                            }
                        } else if (key == 2) {

                            if (!sb.toString().equals("")) {
                                process_general(sb.toString(), "previewHearing");
                            }

                        } else if (key == 3) {

                            if (!sb.toString().equals("")) {
                                process_general(sb.toString(), "committeeComposition");
                            }

                        } else if (key == 4) {

                            if (!sb.toString().equals("")) {
                                process_general(sb.toString(), "subcomitteeComposition");

                            }

                        } else if (key == 5) {

                            if (!sb.toString().equals("")) {
                                process_general(sb.toString(), "Directory");
                            }

                        } else if (key == 6) {

                            if (!sb.toString().equals("")) {
                                process_general(sb.toString(), "Overview");
                            }

                        } else if (key == 7) {

                            if (!sb.toString().equals("")) {
                                process_general(sb.toString(), "Statement");
                            }

                        } else {
                            if (!sb.toString().equals("")) {
                                process_general(sb.toString(), "biggerNode");
                            }
                        }

                        System.out.println("old key is: " + key);

                        sb = new StringBuilder().append("\n");

                    }

                    getKey(str);
                    System.out.println("new key is" + key);


                    sb.append(str).append("\n");

                }

                i++;

            }

            in.close();
            writeXML();


        } catch (Exception e) { e.printStackTrace(); }

        return outputname;

    }

    public void process_general(String s, String nodeName){
        Element e0 = xmldoc.createElement(nodeName);
        Node n0 = xmldoc.createTextNode(s.replaceAll("<","\\<").replaceAll(">","\\>"));
        e0.appendChild(n0);
        root.appendChild(e0);
    }




    public boolean changeSection(String str){



        List<Pattern> patternList = new ArrayList<>();



        patternList.add(Pattern.compile("^(<hearing-title>).*"));
        patternList.add(Pattern.compile("^(<comitteeM_name>).*"));
        patternList.add(Pattern.compile("^(<subcomittee_name>).*"));
        patternList.add(Pattern.compile("^(<table-of-content>).*"));
        patternList.add(Pattern.compile("^(<overview-title>).*"));
        patternList.add(Pattern.compile("^(<opening>).*"));
        patternList.add(Pattern.compile("^(</Hearing>).*"));


        for(Pattern pattern : patternList){

            if(pattern.matcher(str.trim()).matches()){
                return true;
            }
        }

        return false;

    }


    public void getKey(String str){

        if(Pattern.compile("^(<info>).*").matcher(str.trim()).matches()){
            key=1;
        }
        if(Pattern.compile("^(<hearing-title>).*").matcher(str.trim()).matches()){
            key=2;
        }
        if(Pattern.compile("^(<comitteeM_name>).*").matcher(str.trim()).matches()){
            key=3;
        }
        if(Pattern.compile("^(<subcomittee_name>).*").matcher(str.trim()).matches()){
            key=4;
        }
        if(Pattern.compile("^(<table-of-content>).*").matcher(str.trim()).matches()){
            key=5;
        }
        if(Pattern.compile("^(<overview-title>).*").matcher(str.trim()).matches()){
            key=6;
        }
        if(Pattern.compile("^(<opening>).*").matcher(str.trim()).matches()){
            key=7;
        }

    }

    public void initXML() throws ParserConfigurationException {
        // JAXP + DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();


        xmldoc = impl.createDocument(null, "Hearing", null);


        root = xmldoc.getDocumentElement();
        root.setAttribute("HearingID",HearingId);

    }

    public void writeXML() throws TransformerConfigurationException,
            TransformerException {
        DOMSource domSource = new DOMSource(xmldoc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
        // we want to pretty format the XML output
        // note : this is broken in jdk1.5 beta!
        transformer.setOutputProperty
                ("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //
        transformer.transform(domSource, out);
    /*
      get the XML in a String
          java.io.StringWriter sw = new java.io.StringWriter();
          StreamResult sr = new StreamResult(sw);
          transformer.transform(domSource, sr);
          return sw.toString();
    */
    }




}
