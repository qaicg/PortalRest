package ClientesPackage;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.TestBase;

public class LoginCliente extends TestBase {
  @Test
  @Parameters({"resultadoEsperado", "email","password" })
  public void loginCliente(boolean resultadoEsperado, String email, String password) {
	  WebElement inputEmail = driver.findElement(By.xpath("//input[@type='email']"));
	  WebElement inputPassword = driver.findElement(By.xpath("//input[@type='password']"));
	  WebElement buttonEntrar = driver.findElement(By.xpath("//button[@class='btn-centered']"));
	  inputEmail.sendKeys(email);
	  inputPassword.sendKeys(password);
	  buttonEntrar.click();
	  
	  if (resultadoEsperado) {
		  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10)); //Le damos 10 segundos para hacer login del usuario y salir de la pantalla de login.
		  w.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//input[@type='email']")));
		  Reporter.log("Haciendo login con usuario: " + email + " y password " + password);
	  }else {
		  //TO DO 
		  //Determinar expresiones a evaluar cuando queremos testear el login sin éxito.
	  }
	  
  }
  
  
}
