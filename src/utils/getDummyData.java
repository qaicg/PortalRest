package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.antlr.v4.runtime.Parser.TrimToSizeListener;
import org.testng.Reporter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.google.inject.spi.Element;
import com.mysql.cj.util.StringUtils;

import graphql.Assert;

public class getDummyData {
	
	static boolean limite = true;
	
	public static String getDummyUserName(){
		String json= (getJSON("https://api.generadordni.es/v2/person/username?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		
		if(isNullOrEmpty(element.getAsString())) {
			limite = true;
		}
	
		return element.getAsString();
	}
	
	public static String getDummyTelefono(){
		String json= (getJSON("https://api.generadordni.es/v2/misc/phonenumber?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		
		if(isNullOrEmpty(element.getAsString())) {
			limite = true;
		}
		
		return element.getAsString();
	}
	
	public static String getDummyPostalCode(){
		String json= (getJSON("https://api.generadordni.es/v2/misc/zipcode?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		
		if(isNullOrEmpty(element.getAsString())) {
			limite = true;
		}
		
		return element.getAsString();
	}
	
	public static String getDummyPassword(){
		String json= (getJSON("https://api.generadordni.es/v2/person/password?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		
		if(isNullOrEmpty(element.getAsString())) {
			limite = true;
		}

		return element.getAsString();
	}
	
	public static String getDummyCorreo(){
		String json= (getJSON("https://api.generadordni.es/v2/person/email?results=1"));		 
		JsonElement element = JsonParser.parseString(json); 
		
		if(isNullOrEmpty(element.getAsString())) {
			limite = true;
		}

		return element.getAsString();
	}
	
	public static List<String> getDummyJsonInformation(){
		List<String> informationUser = new ArrayList<String>();
		String persona = (getJSON("https://randomuser.me/api/"));
        JsonObject jsonObject = JsonParser.parseString(persona).getAsJsonObject();
        JsonArray resultsArray = jsonObject.getAsJsonArray("results");

        JsonObject firstResult = resultsArray.get(0).getAsJsonObject();
        
        String name = firstResult.getAsJsonObject("name").get("first").getAsString();
        name = name + " " +firstResult.getAsJsonObject("name").get("last").getAsString();
        
		 
		String allAddress = firstResult.getAsJsonObject("location").getAsJsonObject().get("street").getAsJsonObject().get("name").getAsString();
		
        String email = firstResult.get("email").getAsString();

		String password = firstResult.get("login").getAsJsonObject().get("password").getAsString()+"!aAb.";
		
		String phone = firstResult.get("phone").getAsString();
		
		
		
		
		
		phone= phone.replaceAll(" ", "").replaceAll("\\+", "").replaceAll("\\-", "").replaceAll("\\(", "").replaceAll("\\)", "");
		phone = phone.substring(0, 9);
		phone = limpiarYReemplazar(phone);
		
		String direccionSimple =  firstResult.getAsJsonObject("location").getAsJsonObject().get("street").getAsJsonObject().get("name").getAsString();
		
		String codigoPostal = firstResult.getAsJsonObject("location").get("postcode").getAsString();
				
		informationUser.add(name); //index: 0
		informationUser.add(allAddress); //index: 1
		informationUser.add(direccionSimple); //index: 2
		informationUser.add(codigoPostal); //index: 3
		informationUser.add(phone); //index: 4
		informationUser.add(email); //index: 5
		informationUser.add(password+"Ab1!"); //index: 6
		
		if(informationUser.size() == 0) {
			log("La generacion de datos ha fallado");
			limite = true;
			Assert.assertTrue(false);
		}

		return informationUser;

	}
	
	 public static String limpiarYReemplazar(String input) {
	        // Usamos un StringBuilder para construir la cadena resultante
	        StringBuilder resultado = new StringBuilder();

	        for (char c : input.toCharArray()) {
	            if (Character.isDigit(c)) {
	                // Si es un dígito, lo añadimos tal cual
	                resultado.append(c);
	            } else if (Character.isAlphabetic(c)) {
	                // Si es una letra, la reemplazamos por '0'
	                resultado.append('0');
	            } else {
	                // Si es otro carácter (como espacio o símbolo), lo ignoramos
	                // o puedes manejarlo según tu criterio
	            }
	        }
	        
	        return resultado.toString();
	 }
	
	public static List<String> getDummyInformation() {
		List<String> informationUser = new ArrayList<String>();
		
		//GENERATION DATOS :
		//url: https://mmoapi.com/cp/login
		//account: dptocalidadicg
		//password: .1234abcd
		//email: dptocalidadicg@gmail.com
		
		String persona = (getJSON("https://mmoapi.com/api/contact-generator?localization=es_ES&token=ba6fd25bcdf548c4e869e9241cba8e9c8307f3a72d23b439ade4c406c564"));
		JsonElement element = JsonParser.parseString(persona); 	
		
		String name = element.getAsJsonObject().get("fullname").getAsString();
		
		String allAddress = element.getAsJsonObject().get("address").getAsString();
		
		String email = element.getAsJsonObject().get("online").getAsJsonObject().get("email").getAsString().split("@")[0] +"@yopmail.com";
		
		String password = element.getAsJsonObject().get("online").getAsJsonObject().get("password").getAsString();
		
		String phone = element.getAsJsonObject().get("phone_number").getAsString().trim();
		if(phone.length()> 15) {
			phone = phone.replaceAll(" ", "");
			int endString = 0;
			if(phone.length() >= 15 )
			   endString = 14 ;
			else
				endString = phone.length();
			
			phone = phone.substring(0, endString);
		}
		
		String direccionSimple =  element.getAsJsonObject().get("address").getAsString().split(",")[0];
		
		String codigoPostal = element.getAsJsonObject().get("address").getAsString().split(",")[1];
				
		informationUser.add(name); //index: 0
		informationUser.add(allAddress); //index: 1
		informationUser.add(direccionSimple); //index: 2
		informationUser.add(codigoPostal); //index: 3
		informationUser.add(phone); //index: 4
		informationUser.add(email); //index: 5
		informationUser.add(password); //index: 6
		
		if(informationUser.size() == 0) {
			log("La generacion de datos ha fallado");
			limite = true;
			Assert.assertTrue(false);
		}

		return informationUser;

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
	
	public static boolean getLimite() {
		return limite;
	}
	
	public static boolean isNullOrEmpty(String variable) {
		try {
			if(StringUtils.isNullOrEmpty(variable))
				   return true;
			   else
				   return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	   
	}
	
	public static void log(String s) {
		System.out.println(s);
		Reporter.log(s + "<br>");
	}

}
