package ClientesPackage;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Test;
import graphql.Assert;
import utils.TestBase;

public class AbrirPaginaRegistro extends TestBase{
	//TO-DO -> Hay que mejorar los locators que se usan para localizar los elementos por otros más neutros y fijos.
	@Test (priority=1)
	  public void abrePaginaRegistro() {
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));

		  List<WebElement> menuIcons = driver.findElements(By.xpath("//*[@class='header-icon']"));
		  if (menuIcons.size()>=1) {
			  menuIcons.get(menuIcons.size()-1).click();
			  List<WebElement> menuItems= driver.findElements(By.xpath("//button[@role = 'menuitem']"));
			  Reporter.log("Hacemos clic en: "+ menuItems.get(3).getAttribute("innerHTML") );
			  menuItems.get(3).click();
		  }
		  
		  else {
			  Reporter.log("No encuentro la class header-icon");
			  Assert.assertTrue(false);
		  }  
		  
		  w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//*[@class='btn-hyperlink']")));
		  WebElement vinculoRegistro = driver.findElement(By.xpath("//*[@class='btn-hyperlink']"));
		  vinculoRegistro.click();
		  w.until(ExpectedConditions.presenceOfElementLocated (By.className("generic-title")));
		  Reporter.log("Pantalla "+ driver.findElement(By.className("generic-title")).getAttribute("innerHTML")+ " encontrada");
		  Assert.assertTrue(true);
	  }
}
