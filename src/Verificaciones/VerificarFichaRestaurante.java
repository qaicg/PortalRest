package Verificaciones;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.TestBase;

public class VerificarFichaRestaurante extends TestBase {
  
	@Test(description="Este test abre la URL proporcionada para abrir PortalRest", priority=1)
	  @Parameters({"expectedTitle", "classExpected" })
	  public void verificaFichaRestaurante(String expectedTitle, String classExpected) {
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
	      w.until(ExpectedConditions.presenceOfElementLocated (By.className(classExpected)));
	      WebElement tituloRestaurante = driver.findElement(By.className(classExpected));
	      log("Hemos encontrado la classe " +classExpected+" con valor: " + tituloRestaurante.getAttribute("innerHTML")); 
	      //TODO VERIFICAR ELEMENTOS DE LA FICHA
	  }

}
