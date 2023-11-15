package Verificaciones;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

//import Cadenas.Es;
import Objects.ProductItem;
//import graphql.Assert;
import utils.TestBase;

public class VerificarFamiliasRestaurante extends TestBase {

	@Test(description="Este test abre la URL proporcionada para abrir PortalRest", priority=1, groups = { "familiasRestaurante" })
	  @Parameters({"expectedTitle", "xpathExpected" , "soloConsulta", "familiasEsperadas","cartaContratada", "labelTitleCartaContratada", "labelErrorCartaContratada"})
	
	public void verificarFamiliasRestaurante(String expectedTitle, @Optional ("") String xpathExpected, @Optional ("false") String soloConsulta,
			@Optional ("99") String familiasEsperadas, @Optional ("") String cartaContratada, @Optional("") String labelTitleCartaContratada, @Optional("") String labelErrorCartaContratada) {	
		  
		 if(!cartaContratada.equalsIgnoreCase("") && (labelTitleCartaContratada.equalsIgnoreCase("") && !labelErrorCartaContratada.equalsIgnoreCase(""))) {
			 w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'"+cartaContratada+"')]")));
			 log("Se informa al usuario que la carta no está contratada");
			 Assert.assertTrue(true);
		 }
		 else if(!labelTitleCartaContratada.equalsIgnoreCase("") && !labelErrorCartaContratada.equalsIgnoreCase("")) {
			 w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'"+labelTitleCartaContratada+"')]")));
			 w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'"+labelErrorCartaContratada+"')]")));
			 
			 log("Se informa al usuario que la carta no está contratada");
			 Assert.assertTrue(true);
		 } else {
			 waitUntilPresence(xpathExpected, true, false);
			 
			  int familiasEsperadasInt= new Integer(familiasEsperadas);
		      w.until(ExpectedConditions.presenceOfElementLocated (By.xpath(xpathExpected)));
		      List<WebElement> familiasRestaurante = driver.findElements(By.xpath(xpathExpected));
		     
		      if (familiasEsperadasInt!=99 && (familiasEsperadasInt!=familiasRestaurante.size())) {
		    	  log("Hemos encontrado " +familiasRestaurante.size()+ " familias y esperabamos " + familiasEsperadas); 
		    	  Assert.assertTrue(false);
		      }
	 
		 }
		
	      	     
	  }	

}
