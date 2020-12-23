package PostgresXMLdatabase;

import java.util.ArrayList;
import java.util.List;

public class BillInfo {

    List<String> shortTitleArray;
    List<String> headerArray;
    List<String> textArray;

    public BillInfo(List<String> shortTitleArray, List<String> headerArray, List<String> textArray) {
        this.shortTitleArray = shortTitleArray;
        this.headerArray = headerArray;
        this.textArray = textArray;
    }

    public List<String> getHeaderArray() {
        return headerArray;
    }

    public List<String> getShortTitleArray() {
        return shortTitleArray;
    }

    public List<String> getTextArray() {
        return textArray;
    }
}
