package Utilities;

public class StringHandler {

    public String removePrefix(String s, String prefix){
        if(s!=null && s.startsWith(prefix)){
            return s.split(prefix)[1];
        }
        return s;
    }

    public String removePostfix(String s){
        if(s!=null && s.contains(",")){
            return s.split(",")[0];
        }
        return s;
    }

    public String removePreparedPostfix(String s){
        if(s!=null && s.endsWith("follows:")){
            return s.split("follows:")[0];
        }
        return s;
    }



}
