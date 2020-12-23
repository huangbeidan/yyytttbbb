package Utilities;

import java.util.ArrayList;
import java.util.List;

public class UnwantedWords {

    List<String> unwlist = new ArrayList<>();

    public UnwantedWords() {

        generateList();

    }

    public List<String> getUnwlist() {
        return unwlist;
    }

    public void generateList(){
        unwlist.add("January");
        unwlist.add("February");
        unwlist.add("March");
        unwlist.add("April");
        unwlist.add("May");
        unwlist.add("June");
        unwlist.add("July");
        unwlist.add("August");
        unwlist.add("September");
        unwlist.add("October");
        unwlist.add("November");
        unwlist.add("December");
        unwlist.add("HR");
        unwlist.add("H Res ");
        unwlist.add("H Res");
        unwlist.add("Monday");
        unwlist.add("Tuesday");
        unwlist.add("Wednesday");
        unwlist.add("Thursday");
        unwlist.add("Friday");
        unwlist.add("from the State of California");
        unwlist.add("MD");
        unwlist.add("__________  Advisory of July");
        unwlist.add("__________  Advisory of March");
        unwlist.add("__________  Advisory of January");
        unwlist.add("__________  Advisory of February");
        unwlist.add("__________  Advisory of March");
        unwlist.add("__________  Advisory of April");
        unwlist.add("__________  Advisory of May");
        unwlist.add("__________  Advisory of June");
        unwlist.add("__________  Advisory of August");
        unwlist.add("__________  Advisory of September");
        unwlist.add("__________  Advisory of October");
        unwlist.add("__________  Advisory of November");
        unwlist.add("__________  Advisory of December");
        unwlist.add("Technology");
        unwlist.add("Space");
        unwlist.add("Hearing held on Thursday");
        unwlist.add("Hearing held on Monday");
        unwlist.add("Hearing held on Tuesday");
        unwlist.add("Hearing held on Wednesday");
        unwlist.add("Hearing held on Friday");


    }
}
