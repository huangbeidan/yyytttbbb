package ExistdbAPI;

import org.xmldb.api.base.*;
import org.xmldb.api.modules.*;
import org.xmldb.api.*;
import org.exist.xmldb.EXistResource;
public class XQueryExample {

    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    /**
     * args[0] Should be the name of the collection to access
     * args[1] Should be the XQuery to execute
     */
    public static void main(String args[]) throws Exception {

        final String driver = "org.exist.xmldb.DatabaseImpl";

        // initialize database driver
        Class cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        Collection col = null;
        try {
            col = DatabaseManager.getCollection(URI + "db/apps/billsAnalyzer/dataHearing");
            XQueryService xqs = (XQueryService) col.getService("XQueryService",
                    "1.0");
            xqs.setProperty("indent", "yes");

            CompiledExpression compiled = xqs.compile("let $query := \n" +
                    "    <query>\n" +
                    "        <bool><term occur=\"must\">chairman</term><term occur=\"must\">committee</term></bool>\n" +
                    "       <bool><term occur=\"should\">president</term><term occur=\"shoud\">poverty</term></bool>\n" +
                    "       <bool><term occur=\"phrase\">area designations</term></bool>\n" +
                    "       <bool><term occur=\"near\" slope=\"1\" >air pollution</term><term occur=\"near\">strategies</term></bool>\n" +
                    "        \n" +
                    "      </query>\n" +
                    "        \n" +
                    "\n" +
                    "\n" +
                    "for $group in //paragraph[ft:query(., $query)]\n" +
                    "        \n" +
                    "        let $hID := data($group/parent::Statement/parent::Hearing/@HearingID)\n" +
                    "(:        order by ft:score($group):)\n" +
                    "        order by $hID\n" +
                    "        return\n" +
                    "            \n" +
                    "        <p> \n" +
                    "            Hearing ID is: {$hID}\n" +
                    "           <ul>\n" +
                    "            \n" +
                    "               <p>Speaker:  {data($group/@speaker)} </p>\n" +
                    "                \n" +
                    "               <p>Said: \"{$group}</p>\n" +
                    "\n" +
                    "           </ul>\n" +
                    "        \n" +
                    "        </p>");
            ResourceSet result = xqs.execute(compiled);
            ResourceIterator i = result.getIterator();
            Resource res = null;
            while(i.hasMoreResources()) {
                try {
                    res = i.nextResource();
                    System.out.println(res.getContent());
                } finally {
                    //dont forget to cleanup resources
                    try { ((EXistResource)res).getContentLength(); } catch(XMLDBException xe) {xe.printStackTrace();}
                }
            }
        } finally {
            //dont forget to cleanup
            if(col != null) {
                try { col.close(); } catch(XMLDBException xe) {xe.printStackTrace();}
            }
        }
    }
}