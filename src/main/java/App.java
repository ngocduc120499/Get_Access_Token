import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.*;
import Config.UnitConfig;
public class App {
    public static void main(String[] args) throws Exception {
       String token = getAccessToken(UnitConfig.tenant_id,UnitConfig.client_id,UnitConfig.client_secret);
       System.out.println("Access Token: " + token);
    }
    public static String getAccessToken(String tenant_id,String client_id, String client_secret) throws Exception{
        HttpURLConnection conn = null;
        InputStream input = null;
        try {
            URL url = new URL("https://login.microsoftonline.com/" + tenant_id + "/oauth2/v2.0/token");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);//Time to connect millisecond
            conn.setReadTimeout(5000);//Time to read data milliseconds
            conn.setRequestMethod("POST");//HTTP method
            conn.setUseCaches(false);//Use cache
            conn.setDoOutput(true);//Allow sending of request body(False for GET,Set to true for POST)
            conn.setDoInput(true);//Allow receiving body of response
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            String data = //Data to post
                    "client_id=" + client_id
                            + "&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default"  //java.net.May be escaped with URLEncoder
                            + "&client_secret=" + client_secret
                            + "&grant_type=client_credentials";
            conn.getOutputStream().write(data.getBytes("utf-8"));
            conn.getOutputStream().close(); // send

            //Receive the result and convert it to a Json object
            //If you don't use the JSON library, just parse the returned text on your own
            //I'm getting something similar to the string I was getting with Curl
            int code = conn.getResponseCode();
            input = (code == 200 ? conn.getInputStream() : conn.getErrorStream());
            JsonReader jsonReader = Json.createReader(new BufferedReader(new InputStreamReader(input, "utf-8")));
            JsonObject json = jsonReader.readObject();
            jsonReader.close();
            conn.disconnect();

            //I got the access token!
            String access_token = json.getString("access_token");

            return access_token;
        }catch (Error e){
            e.printStackTrace();
        }finally {
            if(input != null) try { input.close(); } catch(Exception e){}
            if(conn != null) try { conn.disconnect(); } catch(Exception e){}
        }
        return null;

    }
}
