package AbrePortalRestPackage;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.TestBase;

public class VerificarCartaRestaurante extends TestBase {
	
	@Test(description="Este test abre la URL proporcionada para abrir PortalRest", priority=1)
	  @Parameters({"expectedTitle", "xpathExpected" })
	  public void verificarCartaRestaurante(String expectedTitle, String xpathExpected) {	 
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
	      w.until(ExpectedConditions.presenceOfElementLocated (By.xpath(xpathExpected)));
	      List<WebElement> familiasRestaurante = driver.findElements(By.xpath(xpathExpected));
	      Reporter.log("Hemos encontrado " +familiasRestaurante.size()+ " familias "); 
	  }
}
