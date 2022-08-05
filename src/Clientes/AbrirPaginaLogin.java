package Clientes;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import graphql.Assert;
import utils.TestBase;

public class AbrirPaginaLogin extends TestBase {
	@Test (priority=1)
	@Parameters ("login")
	  public void abrePaginaLogin(String register) {
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
		  List<WebElement> menuIcons = driver.findElements(By.xpath("//*[@class='header-icon']"));
		  if (menuIcons.size()>=1) {
			  menuIcons.get(menuIcons.size()-1).click();			  
			  WebElement menuItems= driver.findElement(By.xpath("//*[contains(text(), '"+ register +"')]"));
			  espera(500);
			  menuItems.click();
		  }
		  
		  else {
			  log("Menu -> No encuentro la class header-icon");
			  Assert.assertTrue(false);
		  }  
		  
		  w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//*[@class='btn-hyperlink']")));		 
	  }
}
