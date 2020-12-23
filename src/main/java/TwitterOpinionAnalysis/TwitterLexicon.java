package TwitterOpinionAnalysis;

import java.io.*;

public class TwitterLexicon {

    public static void main(String[] args){

        TwitterLexicon twitterLexicon = new TwitterLexicon();
        twitterLexicon.generateLexicon();


    }


    public void generateLexicon(){


//        String directory = System.getProperty("user.home");
        String InputfileName = "subjclueslen1.txt";
        String OutputfileName = "twitterLexicon.txt";

//        String absolutePath = directory + File.separator + fileName;





// read the content from file
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(InputfileName));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OutputfileName));

        ) {
            String line = bufferedReader.readLine();

            while(line != null) {
                //System.out.println(line);
                line = bufferedReader.readLine();

                if(line!=null){

                    String[] comps = line.split("\\s");


                    if (comps.length == 6) {

                       // System.out.println(comps[0] + comps[1] + comps[2] + comps[3] + comps[4] + comps[5]);

                        String term = comps[2].replaceAll("word1=","");

                        double sentiment = comps[5].replaceAll("priorpolarity=", "").equals("positive") ?
                                1.0 : -1.0;

                        if (comps[0].replaceAll("type=", "").startsWith("strong")) {

                            sentiment = sentiment *  1.5;
                        }



                        // write the content in file
                        bufferedWriter.write(term + " " + sentiment);
                        System.out.println(term + " " + sentiment);
                        bufferedWriter.newLine();


                    }

                }


            }

        } catch (FileNotFoundException e) {
            // exception handling
        } catch (IOException e) {
            // exception handling
        }



    }


}
