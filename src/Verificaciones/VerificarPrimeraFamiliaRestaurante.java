package Verificaciones;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
//import Cadenas.Es;
import utils.TestBase;

public class VerificarPrimeraFamiliaRestaurante extends TestBase{
	
	@Test(description="Verifica que hemos abierto la priemra familia del restaurante y que tenemos el resto disponibles tambien a continuación", priority=1, groups = {"checkRestFirstFamily"})
	  @Parameters({"expectedTitle", "xpathExpected","idioma","nombrePrimeraFamilia" })
	 
	public void verificarPrimeraFamiliaRestaurante(String expectedTitle, String xpathExpected, @Optional ("es") String idioma, String nombrePrimeraFamilia) {		  
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
	      w.until(ExpectedConditions.presenceOfElementLocated (By.xpath(xpathExpected)));
	      List<WebElement> familiasRestaurante = driver.findElements(By.xpath(xpathExpected));
	      log("Hemos encontrado " +familiasRestaurante.size()+ " familias "); 
	      WebElement primeraFamilia = driver.findElement(By.xpath(xpathExpected));
	      log(primeraFamilia.getAttribute("innerText"));
	      	   
	      if(!nombrePrimeraFamilia.equalsIgnoreCase("")) {
	    	  Assert.assertEquals(primeraFamilia.getAttribute("innerText").compareTo(nombrePrimeraFamilia),0); 
	      }else {
	    	  log("El idioma actual es " + idioma + " y la primera familia esperada deberia ser " + nombrePrimeraFamilia + " y es " + primeraFamilia.getAttribute("innerText") );
	  		  Assert.assertTrue(false);
	      }
	     
	      
	  }
	
}
