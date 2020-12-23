package Utilities;

public class EnumInIfStatement {

    public String enumInIf(COMMITTEE committee) {

        switch (committee){
            case ENERGYANDCOMMERCE:
                return "type1";
            case AGRICULTURE:
                return "type1";
            case ARMEDSERVICES:
                return "type1";
            case VETERANSAFFAIRS:
                return "type1";

            case FOREIGNAFFAIRS:
                return  "type2";
            case EDUCATION:
                return "type2";
            case FINANCIALSERVICES:
                return "type2";
            case HOMELANDSECURITY:
                return "type2";
            case HOUSE:
                return "type2";
            case OVERSIGHT:
                return "type2";
            case NATURALRESOURCES:
                return "type3";
            case SCIENCESPACEANDTECHNOLOGY:
                return "type2";
            case SMALLBUSINESS:
                return "type2";
            case TRANSPORTATIONANDINFRASTRUCTURE:
                return "type2";
            case WAYSANDMEANS:
                return "type1";
            case THEBUDGET:
                return "type2";
            case THEJUDICIARY:
                return "type2";
            case APPROPRIATIONS:
                return "type2";
            case CONGRESSIONALEXECUTIVECOMMISSIONONCHINA:
                return "typex";



            default:
                return "type2";

        }

    }


    public String tableOfContentType(COMMITTEE committee) {

        switch (committee){

            // DEFALUT: first line no space

            //Statement of Members:
                       //XXXXXXXXXXXXXXXXXX
            case EDUCATION:
                return "111";

            case NATURALRESOURCES:
                return "111";

                //WITNESSES (in the center)
             // with dates below
            case FINANCIALSERVICES:
                return "222";

// OPENING STATEMENTS (in the center)
            //XXXXXXXXXXX
            case SMALLBUSINESS:
                return "333";
            case THEJUDICIARY:
                return "333";
            case FOREIGNAFFAIRS:
                return "333";



            //similar to 333, but with 2parts for the name
            case ARMEDSERVICES:
                return "444";
            case AGRICULTURE:
                return "444";

            //separated by "offered by"
            // balabalabalabal  .. offered by... wwww


            // no structure at all, should skip it
            case APPROPRIATIONS:
                return "XXX";

            default:
                return "type2";

        }

    }

    public static void main(String[] args){
        String str = "COMMITTEE ON ENERGY AND COMMERCE";
        String after = str.replaceAll("COMMITTEE ON ","").replaceAll(" ","");
        COMMITTEE committee_enum = COMMITTEE.valueOf(after);
        System.out.println(after);
        System.out.print(new EnumInIfStatement().enumInIf(committee_enum));
    }
}
