package AbrePortalRestPackage;

import org.testng.annotations.Test;
import org.testng.annotations.Test;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Test;

import utils.TestBase;

public class VerificarCookies extends TestBase {
	
	 @Test (description="Verifica si sale o no la ventana de aceptación de cookies", priority=1)
	  public void verificarCookies() {
		  WebDriverWait w = new WebDriverWait(driver,Duration.ofSeconds(2));
		  w.until(ExpectedConditions.presenceOfElementLocated (By.className("cookies-info-content")));
		  Reporter.log("Hemos encontrado la classe cookies-info-content");
		  
	  }
	 
	 //Solo se ejecuta si el test de verificar cookies es correcto.
	 @Test (description="Acepta cookies",  dependsOnMethods="verificarCookies", priority=2)
	  public void aceptaCookies() {
		  WebElement aceptarCookies = driver.findElement(By.className("btn-confirm"));
		  aceptarCookies.click();
		  Reporter.log("Hemos aceptado las cookies");  
	  }
}
