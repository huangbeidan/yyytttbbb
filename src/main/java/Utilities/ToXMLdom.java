package Utilities;



import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

public class ToXMLdom {


    BufferedReader in;
    StreamResult out;

    String HearingId;

    Document xmldoc;
    Element root;

    String title;


    int key;

    int repeatedC;
    int repeatedS;
    int repeatedTitle;
    /** on means a paragraph starts*/
    Boolean on=true;

    int helper_6;
    int countRow;

    int helperTitle;

    String speakerNameAttr;

    int paraID;

    String openString;
    int openCount;

    int titleCount;

    boolean key2on;

    String Htype="typeother";
    String Ttype = "000";

    List<String> unwlist;

    public ToXMLdom() {
        unwlist = new UnwantedWords().getUnwlist();
    }

    public static void main (String args[]) {

        String filename = "CHRG/CHRG-115hhrg24324.htm";
        new ToXMLdom().doit(filename);
    }

    public String doit (String filename) {

        String outputname = "";

        outputname = "CHRGBulkparse1/" + filename.split("/")[1].split("\\.")[0]+".xml";



        try{



            in = new BufferedReader(new FileReader(filename));




            out = new StreamResult(outputname);
            initXML();
            String str;
            int i=0;
            StringBuilder sb = new StringBuilder();
            int count=0;
            StringBuilder smallSB = new StringBuilder();
            StringBuilder statementSB = new StringBuilder();
            StringBuilder tableSB = new StringBuilder();
            StringBuilder tableSB2 = new StringBuilder();


            HearingId = filename.split("[-.]")[1].split("[a-z]+")[1];


            while ((str = in.readLine()) != null) {


                if(str.trim().matches(".*(GRAPHIC\\(S\\) NOT AVAILABLE IN TIFF FORMAT).*")){
                    continue;
                }


                if(i==1) {

                    // title=str.split("-")[1].split("</title>")[0].trim();
                    title=str.
                            replaceAll("<.*?>\\s*","").
                            replaceAll("^[-]\\s*","").
                            replaceAll("(?<=\\[).*?(?=\\])", "").
                            replaceAll("\\[\\]\\s*","").
                            replaceAll("^//s+","").toUpperCase();

                    System.out.println("title before: " + title);
                }

                if(str.trim().isEmpty()){continue;}


                if(i>1 && changeSection(str)){

                    on=true;
                    System.out.println("Session changed! ");

                    if(!sb.toString().equals("")) {
                        process_general(sb.toString(), "option");
                        // System.out.println("Session change line: " + sb.toString());
                    }
                    // System.out.println(sb.toString());
                    sb=new StringBuilder();
                    count=-1;

                    if((key==2 || key==14 || key==15) && !tableSB.toString().isEmpty()){
                        String[] info = tableSB.toString().split(",");
                        if(info.length>=2){
                            tableParserHelper(info);}
                        tableSB = new StringBuilder();
                    }




                    getKey(str);

                    if(key==1){
                        if(Htype.equals("type2")&&!sb.toString().isEmpty()){
                            process_general(sb.toString(),"option");
                            sb=new StringBuilder();
                            process_general(title,"opening");
                        }else{
                            process_general(title,"Doc-title");}
                        //sb = new StringBuilder().append(str).append("\n");

                    }

                    if(key==5){


                        String after = str.replaceAll("COMMITTEE ON ","").replaceAll("HOUSE","").replaceAll("[,'\\-]","").replaceAll(" ","");
                        //     COMMITTEE committee_enum = (str.trim().replaceAll("-","").replaceAll("COMMISSION ON CHINA","").replaceAll("COMMITTEE ON","").isEmpty()) ? COMMITTEE.valueOf("TRANSPORTATIONANDINFRASTRUCTURE") : (after.equals(""))? COMMITTEE.valueOf("HOUSE") : COMMITTEE.valueOf(after);
                        COMMITTEE committee_enum = (str.trim().replaceAll("-","").replaceAll("COMMITTEE ON","").isEmpty()) ? COMMITTEE.valueOf("TRANSPORTATIONANDINFRASTRUCTURE") : (after.equals(""))? COMMITTEE.valueOf("HOUSE") : COMMITTEE.valueOf(after);

                        System.out.println("committee is:" + committee_enum.toString());
                        Htype = new EnumInIfStatement().enumInIf(committee_enum);
                        Ttype = new EnumInIfStatement().tableOfContentType(committee_enum);
                        System.out.println("Htype is: "+Htype);
                        System.out.println("Ttype is: "+Ttype);



                    }

                    if(key==2){
                        key2on=true;

                    }else if(key==1){
                        key2on=false;

                    }





                    System.out.println("the key is: "+key);



                    countRow=0;
                    helper_6=0;


                }

                /** for title line only */
                if(countRow==0){

                    if((key==5)){

                        process_general(str.trim(),"committee_name");

                    } else if(key==6){
                        process_general(str.trim(),"comitteeM_name");
                    } else if(key==9){

                        if(repeatedS>0 && Htype.equals("type2")){
                            process_general(str.trim(),"overview");
                        }

                        else{
                            process_general(str.trim(),"subcomittee_name");}

                    }
                    else if(key==12){
                        process_general(str.trim(),"subcomittee_name");
                    }


                    else if(key==2){

                        process_general(str.trim(),"table-of-content");

                    }else if(key==1){
//                        if(helperTitle==0 && titleCount==0){
//                        process_general(str.trim(),"hearing-title");
//                        }
//                        else{
//                            process_general(str.trim(),"overview-title");
//                        }
//                        helperTitle++;
//                        titleCount=1;





                        titleCount=0;
                        helperTitle++;

                        System.out.println("Helpertitle is: "+helperTitle);

                    } else if(((key==13 || key==3 || key==4||key==8 || key==41 || key==11))){
                        // process_general(str.trim(),"opening");
                        openString = str.trim();
                        openCount = 1;
                        speakerNameAttr = extractName(str);
                        paraID=1;
                    } else if(key==14){
                        if(Ttype.equals("333") || Ttype.equals("444")){


                            //write the previous result first
                            if (!tableSB.toString().isEmpty() && tableSB.toString().contains(",") && !str.trim().matches(".*[0-9]+$")) {

                                String[] info = tableSB.toString().split(",");

                                if (info.length >= 2) {
                                    tableParserHelper(info);
                                }


                                tableSB = new StringBuilder();
                            }

//                            if(!tableSB.toString().isEmpty()){
//                                process_general(tableSB.toString().trim(),"Speaker");
//                            }
                            process_general(str.trim(),"witnesses");

                        }else{
                            process_general(str.trim(),"witnesses");

                        }
                    }else if(key==15){
                        process_general(str.trim(),"submittedMaterial");
                    } else if(key==16){
                        process_general(str.trim(),"Speakers");
                    } else if(key==17){
                        process_general(str.trim(),"Members");
                    } else if(key==20){
                        process_general(str.trim(),"Speakers");
                    }


                    else if(key!=0){
                        process_general(str.trim(),"option");
                    }

                    countRow++;
                }



                //new session begins
                if(i>2 && !changeSection(str)){


                    /** for table of content section */


                    if(!Htype.equals("typex") && !Ttype.equals("XXX") && (key==2 || key==14 || key==15 || key==16 ||  key==17 || key==18 ||key==20)) {
//                        if (str.trim().equals("Witnesses") || str.trim().equals("Submitted Material")){
//                            process_general(str.trim(),str.trim().replaceAll(" ",""));
//                        }

                        if (str.matches("^[a-zA-Z].*") || (str.matches("[\\s]{4}[a-zA-Z].*") && Ttype.equals("111")) || (str.matches("[\\s]{4}[a-zA-Z].*") && Ttype.equals("333")) ) {

                            if(str.matches("^[\\s]{4}[a-zA-Z].*")){
                                System.out.println("start with spaces: "+str);
                            }


                            //write the previous result first
                            if (!tableSB.toString().isEmpty() && tableSB.toString().contains(",") && !str.trim().matches(".*[0-9]+$")) {

                                String[] info = tableSB.toString().split(",");

                                if (info.length >= 2) {
                                    tableParserHelper(info);
                                }


                                tableSB = new StringBuilder();

                            } else if(!tableSB.toString().isEmpty() && Ttype.equals("333")&& !str.trim().matches(".*[0-9]+$")){

                                tableSB = new StringBuilder();

                            }
                        }


                        tableSB.append(" ").append(str.trim().replaceAll("[.0-9\\-]", "").replaceAll("(Page)", "").trim());

                    }else if(key==21){
                        String nn = str.replaceAll("[.0-9]","").trim();

                        if(nn.contains(",")){
                            String nameC = nn.split(",")[1] + " " +  nn.split(",")[0];
                            process_general(nameC,"peopleSpeaker");
                        }else {

                            process_general(nn, "peopleSpeaker");
                        }
                    }


                    /** for Committee Section only */
                    else if(key==6 || (key==9&&!Htype.equals("type2")&&!Htype.equals("type3")) || (key==12 && (Htype.equals("type2")||Htype.equals("type3")))){
                        //print out the former cumulative result first
                        // if(helper_6==0){process_pre(sb.toString());helper_6++;}

                        if(Htype.equals("type2") || Htype.equals("type3")){
                            process_committeeMembers(str);

                        }else{

                            if(countRow==1){
                                process_board(str,"Chairman");

                            }else if(countRow==3){
                                String[] strings = str.split("\\s{2,}");

                                for(String s:strings) {
                                    process_board(s,"ViceChair");
                                }
                            }
                            else{

                                process_committeeMembers(str);

                            }


                        }



                        countRow++;
                        System.out.println("key is " +key);

                    }

                    else if(((key==13 || key==3 || key==4 || key==8 || key==41 || key==11) && !key2on)||((key==1||key==9) && repeatedTitle==4 && Htype.equals("type2"))){


                        if(((key==13 || key==3 || key==4 || key==8 || key==41 || key==11))) {
                            openCount++;

                            if (isTitleUpperCase(str) && openCount == 2) {
                                openString = openString + str.trim();
                                process_general(openString, "opening");
                            } else if (openCount == 2 && !isTitleUpperCase(str)) {
                                process_general(openString, "opening");
                            }
                        }

                        if(((key==1||key==9) && repeatedTitle==4 && Htype.equals("type2"))) {

                            if (countRow < 6) {
                                countRow++;
                                continue;
                            }
                        }


                        if(!switchSpeaker(str) && !isParagraphStart(str) && !isTitleUpperCase(str)){

                            statementSB.append(str).append("\n");



                        } else if(!switchSpeaker(str)&&isParagraphStart(str)){
                            if(!statementSB.toString().equals("")) {
                                if (statementSB.toString().trim().startsWith("The committee met") ||
                                        statementSB.toString().trim().startsWith("Members present") ||
                                        statementSB.toString().trim().startsWith("Present:")) {
                                    process_paragraph(statementSB.toString().trim(), "hearing-info", paraID);
                                } else {
                                    process_paragraph(statementSB.toString().trim(), "paragraph", paraID);
                                }
                            }

                            statementSB = new StringBuilder().append(str).append("\n");
                            paraID++;
                        }

                        else if(switchSpeaker(str)&&isParagraphStart(str)) {
                            if(!statementSB.toString().equals("")){
                                process_paragraph(statementSB.toString(),"paragraph",paraID);
                            }
                            int index = (str.trim().matches("^(Chairman).*")) ? StringUtils.ordinalIndexOf(str.trim(), ".", 1) :
                                    StringUtils.ordinalIndexOf(str.trim(), ".", 2);
                            if(index>-1){
                                String speakerName = str.trim().substring(0,index);
                                process_general(speakerName,"speaker");
                                speakerNameAttr = speakerName;
                                String details = str.trim().substring(index+1);
                                statementSB=new StringBuilder().append(details).append("\n");
                            }else{
                                process_paragraph(str,"paragraph",paraID);

                            }
                            paraID++;

                        }

                        countRow++;

                    }


//                    else if(key==1){
//                        titleCount++;
//                        if(titleCount==1&&isTitleUpperCase(str)){
//
//                            if(helperTitle==1){
//                                process_general(title,"hearing-title");}
//                            else{
//                                process_general(title,"overview-title");
//                            }
//
//                        }
//                        sb.append(str).append("\n");

                    //                   }

                    else if(key==0){
                        process_general(str,"info");
                    }

                    else {

                        sb.append(str).append("\n");
                    }


                    count++;
                }



                i++;
            }
            in.close();
            writeXML();
        }
        catch (Exception e) { e.printStackTrace(); }
        return outputname;
    }

    public boolean isPeople(String s){
        return (Pattern.compile("[a-zA-Z.\\s]{2,}.*").matcher(s.trim()).matches());
    }

    public boolean isTitleUpperCase(String s){
        return (Pattern.compile("[\\[\\]\\(\\)A-Z.\\s]+").matcher(s.trim()).matches());
    }

    public boolean isParagraphStart(String s){
        return (Pattern.compile("^(\\s){4}.*").matcher(s).matches()&&(!Pattern.compile("[A-Z\\s]+").matcher(s.trim()).matches()));
    }



    public boolean changeSection(String str){



        List<Pattern> patternList = new ArrayList<>();

        //patternList.add(Pattern.compile("^("+((title.length()>50)?title.substring(0,50):title)+").*"));
        String titleM = (title.length()>15)?title.substring(0,15):title;
        String titleS = (title.replaceAll("OVERSIGHT HEARING ON ","").length()>20)?title.replaceAll("OVERSIGHT HEARING ON ","").substring(0,20):title;;
        patternList.add(Pattern.compile("^("+titleM + ").*"));
        patternList.add(Pattern.compile("^("+titleS + ").*"));
        patternList.add(Pattern.compile("C O N T E N T S"));
        patternList.add(Pattern.compile("CONTENTS"));
        patternList.add(Pattern.compile("^(OPENING STATEMENT OF).*"));
        patternList.add(Pattern.compile("^(\\[The prepared statement).*"));
        patternList.add(Pattern.compile("^((HOUSE )*COMMITTEE ON).*"));
        patternList.add(Pattern.compile("^(CONGRESSIONAL-EXECUTIVE COMMISSION ON CHINA).*"));
        patternList.add(Pattern.compile("^(TESTIMONY).*"));
        patternList.add(Pattern.compile("^(Subcommittee on).*"));
        patternList.add(Pattern.compile("^(\\[*Prepared statement of)[\\sa-zA-Z]+.*"));
        patternList.add(Pattern.compile("^(\\[*The information referred to follows:).*"));
        // patternList.add(Pattern.compile("^(\\[*[The information follows:]).*"));
        patternList.add(Pattern.compile("^(SUBCOMMITTEE ON).*"));
        patternList.add(Pattern.compile("^(STATEMENT OF)[\\sA-Z]+.*"));
        patternList.add(Pattern.compile("^(Letter to)[\\sa-zA-Z\\.]+"));
        patternList.add(Pattern.compile("^(The statement of )[\\sa-zA-Z\\.:]+"));
        patternList.add(Pattern.compile("^(Witnesses)$"));
        patternList.add(Pattern.compile("^(Submitted Material)$"));
        patternList.add(Pattern.compile("^(APPENDIX)$"));
        patternList.add(Pattern.compile("^(Appendix I: Answers to Post-Hearing Questions)$"));
        patternList.add(Pattern.compile("^(Appendix II: Answers to Post-Hearing Questions)$"));
        patternList.add(Pattern.compile("^(STATEMENTS)$"));
        patternList.add(Pattern.compile("^(Panel I)$"));
        patternList.add(Pattern.compile("^(Panel II)$"));
        patternList.add(Pattern.compile("^(FOR THE RECORD)$"));
        patternList.add(Pattern.compile("^(Statement of Members:)$"));
        patternList.add(Pattern.compile("^(Statement of Witnesses:)$"));
        patternList.add(Pattern.compile("^(Additional Submissions:)$"));
        patternList.add(Pattern.compile("^(WITNESSES)$"));
        patternList.add(Pattern.compile("^(Witnesses:)$"));
        patternList.add(Pattern.compile("^(OPENING STATEMENTS)$"));
        patternList.add(Pattern.compile("^(Prepared Statements:)$"));
        patternList.add(Pattern.compile("^(Questions and Answers for the Record:)$"));
        patternList.add(Pattern.compile("^(Additional Material for the Record:)$"));
        patternList.add(Pattern.compile("^(Additional Materials Submitted for the Record:)$"));
        patternList.add(Pattern.compile("^(ADDITIONAL MATERIAL SUBMITTED FOR THE RECORD)$"));
        patternList.add(Pattern.compile("^\\s?(Additional Material Submitted for the Record)$"));
        patternList.add(Pattern.compile("^(Additional Material Submitted for the Record:)$"));
        patternList.add(Pattern.compile("^(Unprinted Material Submitted for the Hearing Record)$"));
        patternList.add(Pattern.compile("^(STATEMENTS PRESENTED BY MEMBERS OF CONGRESS)$"));
        patternList.add(Pattern.compile("^(LETTERS, STATEMENTS, ETC., SUBMITTED FOR THE HEARING)$"));
        patternList.add(Pattern.compile("^(TESTIMONY)$"));
        patternList.add(Pattern.compile("^(PREPARED STATEMENTS SUBMITTED BY WITNESSES)$"));
        patternList.add(Pattern.compile("^(Prepared statements:)$"));
        patternList.add(Pattern.compile("^(Statements)$"));
        patternList.add(Pattern.compile("^(SUBMISSIONS FOR THE RECORD)$"));










        if(Pattern.compile("^((HOUSE )*COMMITTEE ON).*").matcher(str.trim()).matches()||
                Pattern.compile("^(CONGRESSIONAL-EXECUTIVE COMMISSION ON CHINA).*").matcher(str.trim()).matches()){
            repeatedC++;
        }

        else if(Pattern.compile("^(Subcommittee on).*").matcher(str.trim()).matches()){
            repeatedS++;
        }

        else if(Pattern.compile("^("+titleM + ").*").matcher(str.trim()).matches()||
                Pattern.compile("^("+titleS + ").*").matcher(str.trim()).matches()){
            repeatedTitle++;
            System.out.println("repeatedTitle is "+repeatedTitle);
        }



        for(Pattern pattern : patternList){

            if(pattern.matcher(str.trim()).matches()){
                return true;
            }
        }

        return false;

    }

    public void tableParserHelper(String[] info){

        if(info.length>0){
            String nameInfo = info[0].replaceAll("Statement by","")
                    .replaceAll("^(the Honorable)","")
                    .replaceAll("^(The Honorable)","")
                    .replaceAll("^(Questions from The Honorable )","");


            if(key==2 ){

                if (Ttype.equals("444")) {
                    String name = (info.length>1) ? (info[1] + " "+nameInfo.trim() ) : nameInfo;
                    process_general(name.trim(),"peopleSpeaker");

                }else {

                    process_general(nameInfo, "peopleSpeaker");
                }
            }else if(key==14 && (!unwlist.contains(nameInfo.replaceAll("^\\s+",""))) ){

                if( (Ttype.equals("444") || Ttype.equals("111") || Ttype.equals("222"))){
                    String name = (info.length>1) ? ( info[1] + " "+nameInfo.replaceAll("^\\s+","") ) : nameInfo.replaceAll("^\\s+","");
                    process_general(name.trim(),"Witness");
                    System.out.println("info[1] is: " + info[1]);
                }else{
                    process_general(nameInfo.trim(),"Witness");}

            }else if(key==15 || key==19){
                process_general(nameInfo.trim(),"SubmittedMaterial");
            }else if(key==16 && (!unwlist.contains(nameInfo.replaceAll("^\\s+","")))) {

                if(Ttype.equals("444")|| Ttype.equals("111") || Ttype.equals("222")){
                    String name = (info.length>1) ? (info[1] + " "+nameInfo.trim() ) : nameInfo;
                    process_general(name.trim(),"peopleSpeaker");

                }else{
                    process_general(nameInfo.trim(),"peopleSpeaker");}
            }else if(key==18){
                String name = (info.length>1) ? (info[1] + " "+nameInfo.trim()) : nameInfo;
                process_general(name.trim(),"Witness");
            }else if(key==17){
                String name = (info.length>1) ? (info[1] + " "+nameInfo.trim()) : nameInfo;
                process_general(name.trim(),"Speaker");
            }else if(key==20){
                process_general(nameInfo.trim(),"peopleSpeaker");
            }


            if(Ttype.equals("111")){

                String infoPeople = info.length>=4 ? (info[2] + info[3]) : (info.length>=3) ? (info[2]) : info[1];
                process_general(infoPeople.trim(), "peopleinfo");



            } else if(Ttype.equals("444")){

                String infoPeople = info.length>=4 ? (info[2] + info[3]) : (info.length>=3) ? (info[1]) : info[1];
                process_general(infoPeople.trim(), "peopleinfo");

            }

            else{
                String infoPeople = info.length>=4 ? (info[1] + info[2] + info[3]) : (info.length>=3) ? (info[1] + info[2]) : info[1];
                process_general(infoPeople.trim(), "peopleinfo");
            }



        }




    }

    public String extractName(String s){

        StringHandler SH = new StringHandler();
        if(key==3){
            //String one = SH.removePrefix(s.trim(),"OPENING STATEMENT OF ");
            String one = s.trim().replaceAll("OPENING STATEMENT OF ","").replaceAll("Letter to ","");

            one=SH.removePostfix(one);
            return one;
        }
        if(key==8){
            String one = SH.removePrefix(s.trim(),"TESTIMONY OF ");
            one=SH.removePostfix(one);
            return one;
        }
        if(key==4){
            if(s.length()>0){
                s = (s.trim().startsWith("[")?s.trim().substring(1,s.trim().length()-1):s.trim());
                String one = SH.removePrefix(s,"The prepared statement of ");
                one=SH.removePreparedPostfix(one).replaceAll("The statement of","");
                return one;}
        }
        if(key==41){
            String one = SH.removePrefix(s.trim(),"Prepared statement of ");
            one=SH.removePostfix(one);
            return one;
        }
        if(key==13){
            String one = SH.removePrefix(s.trim(),"STATEMENT OF ");
            one = SH.removePostfix(one);
            return one;
        }
        return null;

    }


    public void getKey(String str){
        String titleM = (title.length()>15)?title.substring(0,15):title;
        String titleS = (title.replaceAll("OVERSIGHT HEARING ON ","").length()>20)?title.replaceAll("OVERSIGHT HEARING ON ","").substring(0,20):title;;
        if(Pattern.compile("^("+titleM + ").*").matcher(str.trim()).matches()||
                Pattern.compile("^("+titleS + ").*").matcher(str.trim()).matches()){
            key=1;
        }
        if(Pattern.compile("C O N T E N T S").matcher(str.trim()).matches()||
                Pattern.compile("CONTENTS").matcher(str.trim()).matches()){
            key=2;
        }
        if(Pattern.compile("^(OPENING STATEMENT OF).*").matcher(str.trim()).matches()||
                Pattern.compile("^(Letter to)[\\sa-zA-Z]+.*").matcher(str.trim()).matches()){
            key=3;
        }
        if(Pattern.compile("^\\[*(The prepared statement).*").matcher(str.trim()).matches()
                ||Pattern.compile("^(\\[*The statement of)[\\sa-zA-Z]+.*").matcher(str.trim()).matches()){
            key=4;
        }

        if(Pattern.compile("^(\\[*Prepared statement of)[\\sa-zA-Z]+.*").matcher(str.trim()).matches()){
            key=41;
        }

        if(Pattern.compile("^((HOUSE )*COMMITTEE ON).*").matcher(str.trim()).matches()
                ||Pattern.compile("^(CONGRESSIONAL-EXECUTIVE COMMISSION ON CHINA).*").matcher(str.trim()).matches() ){
            switch (repeatedC) {
                case 1:
                    key=5;
                    break;
                case 3:
                    key=6;
                    break;
            }
        }

        if(Pattern.compile("^(TESTIMONY).*").matcher(str.trim()).matches()){
            key=8;
        }

        if(Pattern.compile("^(Subcommittee on).*").matcher(str.trim()).matches()){
            switch (repeatedS){
                case 1:
                    key=9;
                    break;
                case 3:
                    key=10;
                    break;
            }
        }
        if(Pattern.compile("^(\\[*The information referred to follows:).*").matcher(str.trim()).matches()){
            key=11;
        }
        if(Pattern.compile("^(SUBCOMMITTEE ON).*").matcher(str.trim()).matches()){
            key =12;
        }

        if(Pattern.compile("^(STATEMENT OF)[\\sA-Z]+.*").matcher(str.trim()).matches()
                ||Pattern.compile("^(TESTIMONY)[\\sA-Z]+.*").matcher(str.trim()).matches()){
            key =13;
        }

        if(Pattern.compile("^(Witnesses)$").matcher(str.trim()).matches()
                || Pattern.compile("^(WITNESSES)$").matcher(str.trim()).matches()
                || Pattern.compile("^(Witnesses:)$").matcher(str.trim()).matches()
                ||Pattern.compile("^(Statement of Witnesses:)$").matcher(str.trim()).matches()){
            key =14;
        }

        if(Pattern.compile("^(Submitted Material)$").matcher(str.trim()).matches()
                ||  Pattern.compile("^(Additional Submissions:)$").matcher(str.trim()).matches()
                || Pattern.compile("^(Questions and Answers for the Record:)$").matcher(str.trim()).matches()
                ||  Pattern.compile("^(Additional Material for the Record:)$").matcher(str.trim()).matches()
                ||  Pattern.compile("^(Unprinted Material Submitted for the Hearing Record)$").matcher(str.trim()).matches()
                || Pattern.compile("^(LETTERS, STATEMENTS, ETC., SUBMITTED FOR THE HEARING)$").matcher(str.trim()).matches()
                || Pattern.compile("^(SUBMISSIONS FOR THE RECORD)$").matcher(str.trim()).matches()
                ||Pattern.compile("^(Additional Materials Submitted for the Record:)$").matcher(str.trim()).matches()
                || Pattern.compile("^(ADDITIONAL MATERIAL SUBMITTED FOR THE RECORD)$").matcher(str.trim()).matches()
                || Pattern.compile("^(Additional Material Submitted for the Record:)$").matcher(str.trim()).matches()
                || Pattern.compile("^\\s?(Additional Material Submitted for the Record)$").matcher(str.trim()).matches()){
            key =15;
        }

        if(Pattern.compile("^(STATEMENTS)$").matcher(str.trim()).matches()
                ||Pattern.compile("^(FOR THE RECORD)$").matcher(str.trim()).matches()
                ||Pattern.compile("^(Prepared Statements:)$").matcher(str.trim()).matches()
                ||Pattern.compile("^(STATEMENTS PRESENTED BY MEMBERS OF CONGRESS)$").matcher(str.trim()).matches()
                ||Pattern.compile("^(Statements)$").matcher(str.trim()).matches()){
            key =16;
        }

        if(Pattern.compile("^(Statement of Members:)$").matcher(str.trim()).matches()){
            key =17;
        }

        if(Pattern.compile("^(Panel I)$").matcher(str.trim()).matches()
                ||Pattern.compile("^(Panel II)$").matcher(str.trim()).matches()
        ){
            key =18;
        }
        if(Pattern.compile("^(APPENDIX)$").matcher(str.trim()).matches()
                ||Pattern.compile("^(Appendix I: Answers to Post-Hearing Questions)$").matcher(str.trim()).matches()
                || Pattern.compile("^(Appendix II: Answers to Post-Hearing Questions)$").matcher(str.trim()).matches()){
            key=19;
        }

        if(Pattern.compile("^(OPENING STATEMENTS)$").matcher(str.trim()).matches()){
            key=20;
        }

        if(Pattern.compile("^(PREPARED STATEMENTS SUBMITTED BY WITNESSES)$").matcher(str.trim()).matches()
                || Pattern.compile("^(Prepared statements:)$").matcher(str.trim()).matches()){
            key=21;
        }





    }

    public boolean switchSpeaker(String str){


        if(Pattern.compile("^(Mr. |Ms. |Chairman\\s+)[a-zA-Z0-9]+\\..*").matcher(str.trim()).matches()
                ||Pattern.compile("^(The Chairman\\. ).*").matcher(str.trim()).matches()){
            return true;
        }
        return false;
    }


    public void initXML() throws ParserConfigurationException{
        // JAXP + DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        xmldoc = impl.createDocument(null, "Hearing", null);

        root = xmldoc.getDocumentElement();
    }

    public void process_general(String s, String nodeName){
        Element e0 = xmldoc.createElement(nodeName);
        Node n0 = xmldoc.createTextNode(s);
        e0.appendChild(n0);
        root.appendChild(e0);
    }

    public void process_node(String s, String nodeName){
        Element e0 = xmldoc.createElement(nodeName);
        e0.setAttribute("speaker",speakerNameAttr);
        Node n0 = xmldoc.createTextNode(s);
        e0.appendChild(n0);
        root.appendChild(e0);
    }

    public void process_paragraph(String s, String nodeName, int id){
        Element e0 = xmldoc.createElement(nodeName);
        e0.setAttribute("speaker",speakerNameAttr);
        e0.setAttribute("paraId",String.valueOf(paraID));
        Node n0 = xmldoc.createTextNode(s);
        e0.appendChild(n0);
        root.appendChild(e0);
    }

    public void process_board(String s, String nodeName){

        if (isPeople(s)) {
            String[] people_info = s.split(",");

            String people_name = people_info[0];

            String people_location = people_info[people_info.length-1];
            Element e0 = xmldoc.createElement(nodeName);
            Element e1 = xmldoc.createElement("Cname");
            Element e2 = xmldoc.createElement("Clocation");

            Node n1 = xmldoc.createTextNode(people_name);
            Node n2 = xmldoc.createTextNode(people_location);

            e1.appendChild(n1);
            e2.appendChild(n2);
            e0.appendChild(e1);
            e0.appendChild(e2);
            root.appendChild(e0);



        }
    }

    public void process_committeeMembers(String str){


        String[] strings = str.split("\\s{2,}");

        for(String s:strings) {
            if (isPeople(s)) {
                String[] people_info = s.split(",");

                String people_name = people_info[0];

                String people_location = people_info[people_info.length-1];
                Element e0 = xmldoc.createElement("people");
                Element e1 = xmldoc.createElement("Pname");
                Element e2 = xmldoc.createElement("Plocation");

                Node n1 = xmldoc.createTextNode(people_name);
                Node n2 = xmldoc.createTextNode(people_location);

                e1.appendChild(n1);
                e2.appendChild(n2);
                e0.appendChild(e1);
                e0.appendChild(e2);
                root.appendChild(e0);



            }
        }

    }

    public static String remove_parenthesis(String input_string, String parenthesis_symbol){
        // removing parenthesis and everything inside them, works for (),[] and {}
        if(parenthesis_symbol.contains("[]")){
            return input_string.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ");
        }else if(parenthesis_symbol.contains("{}")){
            return input_string.replaceAll("\\s*\\{[^\\}]*\\}\\s*", " ");
        }else{
            return input_string.replaceAll("\\s*\\([^\\)]*\\)\\s*", " ");
        }
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