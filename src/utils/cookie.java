package utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class cookie extends TestBase {
	//porta-rest-web-language
	//portal-rest-web-System.out.printlnin
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
	
	public static void deleteAllCookiesPortalRest (boolean refreshAppPortalRest) {
    	// """Clear the cookies and cache for the ChromeDriver instance."""
    	//Cookies de PortalRest: https://cloudquality04.hiopos.com
	    	//portal-rest-web-cookie-geolocation
	    	//cookie portal-rest-web-cookie-accept
	    	//portal-rest-web-System.out.printlnin
	    	//porta-rest-web-language
	    	//portal-rest-web-remember-map
		
		try {
			
			Set <Cookie> cookies = driver.manage().getCookies();
			
			System.out.println("Suprimir las cookies de portRest");
			cookies.forEach(cookie -> {
				//System.out.println("Eliminamos la  cookie " + cookie.getName());
				//System.out.println("getValue de la  cookie " + cookie.getValue());
				driver.manage().deleteCookie(cookie);			
			});
			
			//Verificar se ha eliminado las cookies
			if(driver.manage().getCookies().size() == 0 ) {
				System.out.println("Se ha eliminado todas las cookies de PortalRest desde https://cloudquality04.hiopos.com ");
				
				if(refreshAppPortalRest) driver.navigate().refresh();
			}
			else {
				System.out.println("No se ha podiddo eliminar las cookies de portalRest ");
				driver.manage().getCookies().iterator().forEachRemaining(k -> {
					System.out.println("Nos queda la cookie " + k.getName());
				});
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("No hemos encontrado las cookies de portalRest desde https://cloudquality04.hiopos.com ");
			
			driver.manage().getCookies().iterator().forEachRemaining(k -> {
				System.out.println("Nos queda la cookie " + k.getName());
			});
		}

	}
	
	public static void deleteCookiePortalRest(String cookieName, boolean refreshAppPortalRest) {
		
		Set<Cookie> cookies = driver.manage().getCookies();
		
		java.util.Optional<Cookie> cookieFind = cookies.stream().
				filter(c -> c.getName().contentEquals(cookieName)).findFirst();
		
		if(cookieFind.isPresent()) {
			driver.manage().deleteCookieNamed(cookieFind.get().getName());
			
			Cookie cookieAccept = driver.manage().getCookieNamed(cookieFind.get().getName());
			if(Objects.isNull(cookieAccept)) {
				System.out.println("Se ha eliminado la cookie " + cookieName);
				
				if(refreshAppPortalRest) driver.navigate().refresh();
			}
			else {
				System.out.println("Error: No se ha podido suprimir la cookie " + cookieName);
				
				Assert.assertTrue(false, "Error: No se ha podido suprimir la cookie " + cookieName);
			}
		} 
		else {
			System.out.println("Warning: No hemos encontrado la cookie en el navegador " + cookieName);
		}
		
	}
	
	@Test(description = "Comprobar que se muestra la página del motero al suprimir las cookies de PortalRest")
	public void deleteCookiesShowErroPage() {
		deleteAllCookiesPortalRest(true);
		espera(1500);
		
		if(!isElementPresent(By.xpath(PortalRestOrderElements.ErrorPage.errorImageXpath))) {
			Assert.assertTrue(false, "Error: No se ha cargado la página Error del Motero");
		}
		
		log("Se ha cargado la página Error del Motero");
	}
}
