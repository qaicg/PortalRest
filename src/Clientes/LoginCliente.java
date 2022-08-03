package Clientes;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import graphql.Assert;
import utils.Decrypt;
import utils.JSONValue;
import utils.TestBase;
import utils.cookie;

public class LoginCliente extends TestBase {
  @Test
  @Parameters({"resultadoEsperado", "email","password","rememberMe","shop" })
  public void loginCliente(@Optional ("true") String resultadoEsperado, String email, String password, @Optional ("false") String rememberMe, String shop) {
	  WebElement inputEmail = driver.findElement(By.xpath("//input[@type='email']"));
	  WebElement inputPassword = driver.findElement(By.xpath("//input[@type='password']"));
	  WebElement buttonEntrar = driver.findElement(By.xpath("//button[@class='btn-centered']"));
	  inputEmail.sendKeys(email);
	  inputPassword.sendKeys(password);
	  
	  if(rememberMe.equalsIgnoreCase("true")) {  //Check if remember me box is true or false
		WebElement remember = driver.findElement(By.xpath("//div[@class='mat-checkbox-inner-container']"));
		remember.click();
		}else {		}  
	  
	  buttonEntrar.click();
	  
	  if (resultadoEsperado.equalsIgnoreCase("true")) {   //Check remember me value in the stored cookie
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10)); //Le damos 10 segundos para hacer login del usuario y salir de la pantalla de login.
		  if (!w.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//input[@type='email']")))) {
			  log("Login erroneo");
			  Assert.assertTrue(false);
		  }
		  String cookieEncriptada = cookie.getCookie(driver, "portal-rest-web-remember-map");
		  String cleanCookie = cookieEncriptada.replace("%","");
     	  String cookieDecriptada = Decrypt.decodeBase64(cleanCookie);
		  String cookieResult = JSONValue.value(cookieDecriptada, shop, "rememberMe");
		  if (rememberMe.equalsIgnoreCase("true")) {
			  Assert.assertTrue(cookieResult.equalsIgnoreCase("True"));
			  log("Se ha guardado el usuario en la cookie correctamente");
			  
		  }else { 
			  Assert.assertTrue(cookieResult.equalsIgnoreCase("False"));
			  log("No se ha guardado el usuario en la cookie");
		  }
		   
	  }else {
		  WebDriverWait error = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
		  error.until(ExpectedConditions.visibilityOfElementLocated(By.id("mat-error-1")));
		  log("Login failed as spected");
		  //TODO VERIFICAR QUE PARAMETROS SE DEBEN MIRAR CUANDO NO QUEREMOS HACER LOGIN CORRECTO. 
		  
	  }
	  
  }
  
  
}
