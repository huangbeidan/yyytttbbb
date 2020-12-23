package Utilities;


import java.io.*;
import java.util.regex.Pattern;

public class TextToXML_JavaMapping  {

    public static void main(String[] args){

        String test = "<title> - MEDICAID OVERSIGHT: EXISTING PROBLEMS AND WAYS TO STRENGTHEN THE PROGRAM</title>";
        String title2 = test.split("-")[1].split("</title>")[0].trim();

        String title = "H.R. 806, OZONE STANDARDS IMPLEMENTATION ACT OF 2017";


        Pattern pattern = Pattern.compile("^("+((title.length()>50)?title.substring(0,50):title)+").*");
        System.out.println(pattern.toString());
        String test2 = "MEDICAID OVERSIGHT: EXISTING PROBLEMS AND WAYS TO STRENGTHEN THE PROGRAM";
        System.out.println(pattern.matcher(test2).matches());

        Pattern pattern2 = Pattern.compile("\\s*(C O N T E N T S)\\s*");
        String test3 = "                          C O N T E N T S";
        System.out.println(pattern2.matcher(test3).matches());


    }
}
