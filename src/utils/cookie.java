package utils;


import org.openqa.selenium.WebDriver;




public class cookie extends TestBase {
	public static String getCookie(WebDriver driver, String cookie) { //To obtain an specific cookie
				return driver.manage().getCookieNamed(cookie).getValue();
				}
	
}
