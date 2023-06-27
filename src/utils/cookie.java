package utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class cookie extends TestBase {
	//porta-rest-web-language
	//portal-rest-web-login
	//portal-rest-web-remember-map
	//portal-rest-web-cookie-accept
	
	public static String getCookie(WebDriver driver, String cookie) { //To obtain an specific cookie
		return driver.manage().getCookieNamed(cookie).getValue();
	}
	
	public static String getCookie(String cookie) { //To obtain an specific cookie
		return driver.manage().getCookieNamed(cookie).getValue();
	}
	
	public static String decodeCookieBase64(String cookie) {
		String decodeCookieBase64 = new java.lang.String(Base64.decodeBase64(cookie));
		//System.out.println("Tenemos el valor de la cookie: " + decodeCookieBase64);
		return decodeCookieBase64;
	}
	
	public static List<Map<String, String>> getCookiesPortalRest() {
		
		List<Map<String, String>> cookieList = new ArrayList<Map<String, String>>() ;
		
		for(CookiesPortalRest cookies: CookiesPortalRest.values()) {
			Map<String, String> cookieMap = new HashMap<String, String>();
			cookieMap.put(cookies.name(), cookies.getValue());
			//System.out.println("cookies.name()  cookies.getValue() " + cookies.name() + " --> " +cookies.getValue()); 
			cookieList.add(cookieMap);
		}
		
		return cookieList;
	}
	
	
	public static Map<String, String> getCookiePortalRest(CookiesPortalRest cookie) {
		
		for(Map<String, String> cookieMap: getCookiesPortalRest()) {
			if(cookieMap.containsKey(cookie.name())) {
				//System.out.println("getCookiePortalRest(CookiesPortalRest cookie) " +cookieMap.keySet() + " " + cookieMap.values());
				return cookieMap;
			}
		}
		System.out.println("Error no se encontrado la cookie ");
		Assert.assertTrue(false, "Error no se encontrado la cookie ");
		return null;
	}
}
