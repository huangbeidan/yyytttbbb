import java.net.HttpURLConnection;
import java.net.URL;

public class UploadToExistdb {

    public HttpURLConnection putHttpRequest(String url){
        URL uri = null;
        HttpURLConnection con = null;
        try{
            uri = new URL(url);
            con = (HttpURLConnection) uri.openConnection();
            con.setRequestMethod("PUT"); //type: POST, PUT, DELETE, GET
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setConnectTimeout(60000); //60 secs
            con.setReadTimeout(60000); //60 secs
            con.setRequestProperty("Accept-Encoding", "Your Encoding");
            con.setRequestProperty("Content-Type", "Your Encoding");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return con;
    }



}
