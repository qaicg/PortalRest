package Clientes;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import utils.TestBase;

public class AbrirPaginaLogin extends TestBase {
	@Test (priority=1, groups = { "paginaLogin" })
	@Parameters ("login")
	public void abrePaginaLogin(String register) {
		
		//La sesión ha caducado, la página se recargará
		if(isElementPresent(By.xpath("//div//child::button[contains(@class, 'btn-centered expired-button')"))) {
			clicJS(driver.findElement(By.xpath("//div//child::button[contains(@class, 'btn-centered expired-button')]")));
			espera(500);
		}
		
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(30));
		  List<WebElement> menuIcons = driver.findElements(By.xpath("//*[@class='header-icon']"));
		  
		  if (menuIcons.size()>=1) {
			  menuIcons.get(menuIcons.size()-1).click();	
			  
			  if(!isElementPresent(By.xpath("//*[contains(text(), '"+ register +"')]"))) {
				  List<WebElement> buttonSignOut = driver.findElements(By.xpath("//div[contains(@class, 'mat-menu-content')]//child::button"));
				  clicJS(buttonSignOut.get(buttonSignOut.size() -1)); //Cerrar la Sesión
				  
				  espera(500);
				  abrirMenu();
				  espera(500);
			  }
			  
			  String menuItemsXpath = "//*[contains(text(), '"+ register +"')]";
			  waitUntilPresence(menuItemsXpath, true, false);
			  
			  espera(500);
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
