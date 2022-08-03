package utils;


import org.json.JSONObject;


public class JSONValue {
  public static String value (String cookieValue, String shop, String remember) {
	  
	  JSONObject jsonObj=new JSONObject(cookieValue); //This converts in to JSON Object
	  Boolean TorF = jsonObj.getJSONObject(shop).getBoolean(remember); //getString("rememberMe");
	  
	  @SuppressWarnings("removal")
	  String cookieTorF = new Boolean(TorF).toString();
	  return cookieTorF;
  }
}
