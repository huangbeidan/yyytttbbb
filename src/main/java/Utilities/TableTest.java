package Utilities;



import java.util.Arrays;
import java.util.List;

import wagu.Board;
import wagu.Table;

public class TableTest {

    public static void main(String[]args){


        List<String> t2headers = Arrays.asList("CurrLoc", "TypeOfAttribute", "Expected", "Actual", "Reason");
        String string1 = "New Zealand is almost alone with the United States in not registering 96 percent of its firearms — and those are its most common firearms, the ones most used in crimes,” said Philip Alpers of GunPolicy.org, a clearinghouse for gun law data worldwide. “There are huge gaps in New Zealand law";
        List<List<String>> t2rowsList = Arrays.asList(
                Arrays.asList(string1.substring(0,50)+"...", "Male", "No", "23", "1200.27"),
                Arrays.asList("Libby", "Male", "No", "17", "800.50"),
                Arrays.asList("Rea", "Female", "No", "30", "10000.00"),
                Arrays.asList("Deandre", "Female", "No", "19", "18000.50"),
                Arrays.asList("Alice", "Male", "Yes", "29", "580.40"),
                Arrays.asList("Alyse", "Female", "No", "26", "7000.89"),
                Arrays.asList("Venessa", "Female", "No", "22", "100700.50")
        );
        List<Integer> t2ColWidths = Arrays.asList(60, 20, 60, 60,20);
        //bookmark 1
        Board board = new Board(250);

        String tableString = board.setInitialBlock(new Table(board, 250, t2headers, t2rowsList,t2ColWidths).tableToBlocks()).build().getPreview();




        System.out.println(tableString);

    }



}
