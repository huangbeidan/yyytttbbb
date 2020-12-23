package Utilities;

import java.io.*;
public class CommentLines {


    public String CommentDtDANDSyleshhet(String prefix, String fileName){


        // This will reference one line at a time
        String line = null;

        String OutputName = prefix + fileName.split("\\.")[0]+"-nodtd.xml";

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(prefix + fileName);

            FileWriter fileWriter =
                    new FileWriter(OutputName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            int i=0;

            while((line = bufferedReader.readLine()) != null) {

                if((i==1||i==2) && !line.replaceAll("\"","'").matches("^(<!--)")){

                    line = "<!--" + line + "-->";
                    System.out.println("This line has been transformed: "+line);

                }

                bufferedWriter.write(line);
                bufferedWriter.newLine();

                i++;
            }

            // Always close files.
            System.out.println("Successfully comment files: "+fileName+"; Save as: "+OutputName);
            bufferedReader.close();
            bufferedWriter.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        System.out.println("Outputname is " + OutputName);
        return OutputName;


    }

    public String CommentDtDANDSyleshhet(String fileName){


        // This will reference one line at a time
        String line = null;

        String OutputName = fileName.split("\\.")[0]+"-nodtd.xml";

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            FileWriter fileWriter =
                    new FileWriter(OutputName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            int i=0;

            while((line = bufferedReader.readLine()) != null) {

                if((i==1||i==2) && !line.replaceAll("\"","'").matches("^(<!--)")){

                    line = "<!--" + line + "-->";
                    System.out.println("This line has been transformed: "+line);

                }

                bufferedWriter.write(line);
                bufferedWriter.newLine();

                i++;
            }

            // Always close files.
            System.out.println("Successfully comment files: "+fileName+"; Save as: "+OutputName);
            bufferedReader.close();
            bufferedWriter.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return OutputName;


    }


    public static void main(String [] args) {

        // The name of the file to open.
        String prefix = "/Users/beidan/yyytttbbb/HR/";
        String fileName = "BILLS-115hr115rfs.xml";

        CommentLines commentLines = new CommentLines();
        commentLines.CommentDtDANDSyleshhet(prefix,fileName);




    }




}
