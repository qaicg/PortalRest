package Clientes;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import graphql.Assert;
import utils.TestBase;

public class AbrirPaginaRegistro extends TestBase{
	//TO-DO -> Hay que mejorar los locators que se usan para localizar los elementos por otros m�s neutros y fijos.
	@Test (priority=1)
	@Parameters( "login" )
	  public void abrePaginaRegistro(String register) {
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));

		  List<WebElement> menuIcons = driver.findElements(By.xpath("//*[@class='header-icon']"));
		  if (menuIcons.size()>=1) {
			  menuIcons.get(menuIcons.size()-1).click();
			  WebElement menuItems= driver.findElement(By.xpath("//*[contains(text(), '"+ register +"')]"));
			  espera(500);
			  log("Hacemos clic en: "+ register);
			  menuItems.click();
			  espera(500);
			  WebElement create= driver.findElement(By.xpath("//label[@class='btn-hyperlink']"));
			  String text = create.getAttribute("innerText");
			  espera(500);
			  log("Hacemos clic en:"+ text);
		  }
		  
		  else {
			  log("No encuentro la class header-icon");
			  Assert.assertTrue(false);
		  }  
		  
		  w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//*[@class='btn-hyperlink']")));
		  WebElement vinculoRegistro = driver.findElement(By.xpath("//*[@class='btn-hyperlink']"));
		  vinculoRegistro.click();
		  w.until(ExpectedConditions.presenceOfElementLocated (By.className("generic-title")));
		  log("Pantalla "+ driver.findElement(By.className("generic-title")).getAttribute("innerHTML")+ " encontrada");
		  Assert.assertTrue(true);
	  }
}
