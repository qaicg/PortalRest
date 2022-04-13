package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class getDummyData {

	public static String getDummyUserName(){
		String json= (getJSON("https://api.generadordni.es/v2/person/username?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		return element.getAsString();
	}
	
	public static String getDummyTelefono(){
		String json= (getJSON("https://api.generadordni.es/v2/misc/phonenumber?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		return element.getAsString();
	}
	
	public static String getDummyPostalCode(){
		String json= (getJSON("https://api.generadordni.es/v2/misc/zipcode?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		return element.getAsString();
	}
	
	public static String getDummyPassword(){
		String json= (getJSON("https://api.generadordni.es/v2/person/password?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		return element.getAsString();
	}
	
	public static String getDummyCorreo(){
		String json= (getJSON("https://api.generadordni.es/v2/person/email?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		return element.getAsString();
	}
	
	private static String getJSON(String url) {
        HttpsURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpsURLConnection) u.openConnection();
            con.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();


        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

}
