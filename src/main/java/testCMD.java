import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class testCMD {
    List<List<Integer>> ans;
    public static void main(String[] args) {

        String str = "Chairman Royce. As am I, Mr. Engel. Thank you.";
        if(Pattern.compile("^(Mr. |Ms. |Chairman\\s+)[a-zA-Z0-9]+\\..*").matcher(str.trim()).matches()){
            System.out.println("true");
        }else{
            System.out.println("false");
        }
    }

    public List<Integer> grayCode(int n) {
        List<Integer> ans = new ArrayList<>();
        for (int i=0; i< 1<<n; i++){
            ans.add(i ^ i>>1);
        }
        return ans;
    }




}