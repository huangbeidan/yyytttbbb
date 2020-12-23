package ExistdbAPI;



import org.w3c.dom.Node;
import org.xmlunit.xpath.JAXPXPathEngine;

import javax.xml.transform.Source;
import java.util.Iterator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class XPathTest {


    @org.junit.Test
    public void test(){


        Source source = new Source() {
            @Override
            public void setSystemId(String systemId) {

            }

            @Override
            public String getSystemId() {
                return null;
            }
        };

        Iterable<Node> i = new JAXPXPathEngine().selectNodes("//li", source);
        assertNotNull(i);
        int count = 0;
        for (Iterator<Node> it = i.iterator(); it.hasNext(); ) {
            count++;
            assertEquals("li", it.next().getNodeName());
        }
        assertEquals(4, count);
    }


}
