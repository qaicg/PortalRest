package Clientes;




import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import utils.TestBase;

public class LoginGoogle extends TestBase
{
  @Test 
  @Parameters({"resultadoEsperado", "email","password" })
  public void loginGoogle() {
	  
	  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
	  w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//button[@class='social-login-button login-google-button ng-star-inserted']")));
	  WebElement GoogleButton = driver.findElement(By.xpath("//button[@class='social-login-button login-google-button ng-star-inserted']"));
	  GoogleButton.click();
	  
	
	  }

	  
	   
  }



	  
