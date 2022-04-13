package ClientesPackage;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import graphql.Assert;
import utils.TestBase;
import utils.getDummyData;

public class CrearCliente extends TestBase {
	
	 String dummyUserName;
	 String dummyTelefono;
	 String dummyPostalCode;
	 String dummyEmail;
	 String dummyPassword;
	
  @Test  (priority=1)
  @Parameters({"resultadoEsperado", "aceptoTerminos" })
  public void crearCliente(boolean resultadoEsperado, boolean aceptoTerminos) {
	  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
	  w.until(ExpectedConditions.presenceOfElementLocated (By.className("generic-title")));
	  dummyUserName = getDummyData.getDummyUserName();
	  dummyTelefono = getDummyData.getDummyTelefono();
	  dummyPostalCode = getDummyData.getDummyPostalCode();
	  dummyEmail = dummyUserName+"@yopmail.com";
	  dummyPassword = getDummyData.getDummyPassword();
	  
	  //Se deberian usar otros localziadores mejores pero no los hay...
	  WebElement inputName = driver.findElement(By.cssSelector("#mat-input-2"));
	  WebElement inputPhone = driver.findElement(By.cssSelector("#mat-input-3"));
	  WebElement inputPostalCode = driver.findElement(By.cssSelector("#mat-input-4"));
	  WebElement inputEmail = driver.findElement(By.cssSelector("#mat-input-5"));
	  WebElement inputPassword = driver.findElement(By.cssSelector("#mat-input-6"));
	  WebElement inputRepeatPassword = driver.findElement(By.cssSelector("#mat-input-7"));
	  WebElement checkboxCondiciones = driver.findElement(By.xpath("//label[@for='mat-checkbox-2-input']//div[@class='mat-checkbox-inner-container']"));
	  WebElement buttonCrear= driver.findElement(By.xpath("//button[@class='btn-centered']//div[contains(text(),'Crear')]"));
	  
	  enviarTexto(inputName,dummyUserName);
	  enviarTexto(inputPhone,dummyTelefono);
	  enviarTexto(inputPostalCode,dummyPostalCode);
	  enviarTexto(inputEmail,dummyEmail);
	  enviarTexto(inputPassword,dummyPassword);
	  enviarTexto(inputRepeatPassword,dummyPassword);
	  if(aceptoTerminos)checkboxCondiciones.click(); 
	  buttonCrear.click();
	  
	  if (resultadoEsperado) {
		  w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10)); //Le damos 10 segundos para registrar el usuario y salir de la pantalla de registro.
		  w.until(ExpectedConditions.invisibilityOfElementLocated(By.className("generic-title")));
		  Reporter.log("Cliente a crear -> Nombre: "+ dummyUserName + " email: "+ dummyEmail+ " password: " + dummyPassword);
	  }else {
		  //TO DO 
		  //Determinar expresiones a evaluar cuando queremos testear el registro sin éxito.
	  }
	 
	  
  }
  

  //Se hace un metodo de envio de txto para que haga pausas necesarias.
  public void enviarTexto(WebElement elemento, String texto) {
	  elemento.sendKeys(texto);
	  espera(150);
  }
  
	
}
