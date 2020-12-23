package Utilities;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class XMLValidate {


    public void validate() throws SAXException, IOException, ParserConfigurationException {

        // parse an XML document into a DOM tree
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(new File("/Users/beidan/yyytttbbb/BillXML/BILLS-110hr1307ih.xml"));

        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(new File("mySchema.xsd"));
        Schema schema = factory.newSchema(schemaFile);

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();

        // validate the DOM tree
        try {
            validator.validate(new DOMSource(document));
        } catch (SAXException e) {

            // instance document is invalid!
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        XMLValidate xmlValidate = new XMLValidate();
        xmlValidate.validate();
    }
}
