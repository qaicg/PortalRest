package Clientes;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.Data;
import utils.TestBase;
import utils.getDummyData;

public class CrearCliente extends TestBase {
	
	 String dummyUserName;
	 String dummyTelefono;
	 String dummyPostalCode;
	 String dummyEmail;
	 String dummyPassword;
	
  @Test  (priority=1)
  @Parameters({"resultadoEsperado", "aceptoTerminos", "ICGCloud", "validationCliente", "menu", "profile", "personal" })
  public void crearCliente(@Optional ("true") boolean resultadoEsperado, @Optional ("true") boolean aceptoTerminos,@Optional ("false") boolean IcgCloud,@Optional ("false") boolean validationCliente, @Optional String menu, @Optional String profile, @Optional String personal) {
	  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
	  w.until(ExpectedConditions.presenceOfElementLocated (By.className("generic-title")));
	  dummyUserName = getDummyData.getDummyUserName();
	  dummyTelefono = getDummyData.getDummyTelefono();
	  dummyPostalCode = getDummyData.getDummyPostalCode();
	  dummyEmail = dummyUserName+"@yopmail.com";
	  Data.getInstance().setNewUserMail(dummyEmail);//LO GUARDAMOS PARA PODER VALIDAR POSIBLES PEDIDOS
	  
	  dummyPassword = getDummyData.getDummyPassword();
	  
	  WebElement inputName, inputPhone, inputPostalCode, inputEmail, inputPassword, inputRepeatPassword, checkboxCondiciones, buttonCrear;
	  
	  
	  if(IcgCloud) { //Input for ICGCloud login
		  inputName = driver.findElement(By.id("mat-input-4"));
		  inputPhone = driver.findElement(By.id("mat-input-5"));
		  inputPostalCode = driver.findElement(By.id("mat-input-6"));
		  inputEmail = driver.findElement(By.id("mat-input-7"));
		  inputPassword = driver.findElement(By.id("mat-input-2"));
		  inputRepeatPassword = driver.findElement(By.id("mat-input-3"));
		  checkboxCondiciones = driver.findElement(By.xpath("//label[@for='mat-checkbox-2-input']//div[@class='mat-checkbox-inner-container']"));
		  buttonCrear= driver.findElement(By.xpath("//button[@class='btn-centered']//div[contains(text(),'Crear')]"));
		  
		 
	  } else { //Input for PRT login
		  //Se deberian usar otros localziadores mejores pero no los hay...
		  inputName = driver.findElement(By.cssSelector("#mat-input-2"));
		  inputPhone = driver.findElement(By.cssSelector("#mat-input-3"));
		  inputPostalCode = driver.findElement(By.cssSelector("#mat-input-4"));
		  inputEmail = driver.findElement(By.cssSelector("#mat-input-5"));
		  inputPassword = driver.findElement(By.cssSelector("#mat-input-6"));
		  inputRepeatPassword = driver.findElement(By.cssSelector("#mat-input-7"));
		  checkboxCondiciones = driver.findElement(By.xpath("//label[@for='mat-checkbox-2-input']//div[@class='mat-checkbox-inner-container']"));
		  buttonCrear= driver.findElement(By.xpath("//button[@class='btn-centered']//div[contains(text(),'Crear')]"));
	  }
	  
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
		  log("Cliente a crear -> Nombre: "+ dummyUserName + " email: "+ dummyEmail+ " password: " + dummyPassword);
	  }else {
		  //TO DO 
		  //Determinar expresiones a evaluar cuando queremos testear el registro sin �xito.
		 
	  }
	  
	  if(validationCliente) {
		  validateNewUser(menu, profile, personal);
	  }
  }
  

  //Se hace un metodo de envio de txto para que haga pausas necesarias.
  public void enviarTexto(WebElement elemento, String texto) {
	  elemento.sendKeys(texto);
	  espera(150);
	  
  }
 
  //Validation of the saved data in new user
  
  public void validateNewUser (String menu, String profile, String personal) {
	  espera(500); //Wait for main page 
	  
	  //Buttons are identified by text in the language of the page
	  w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//mat-icon[normalize-space()='"+ menu + "']")));
	  espera();
	  WebElement menuButton = driver.findElement(By.xpath("//mat-icon[normalize-space()='"+ menu + "']"));
	  menuButton.click();
	  espera();
	  WebElement profileButton = driver.findElement(By.xpath("//*[contains(text(), '"+ profile +"')]"));
	  profileButton.click();
	  espera();
	  WebElement personalInfo = driver.findElement(By.xpath("//*[contains(text(), '"+ personal +"')]"));
	  personalInfo.click();
	  espera();
	  
	  //obtaining user data from profile info
	  List<WebElement> userInfoList = driver.findElements(By.xpath("//mat-form-field"));
	  
	  
	  
	  for(int i=1;i<= userInfoList.size();i++) {	     		     
		 
		    /* if ((userInfoList.get(i).getAttribute("textContent").equalsIgnoreCase("Nombre*")||userInfoList.get(i).getAttribute("textContent").equalsIgnoreCase("Nombre*"))&&userInfoList.get(i).getAttribute("value").equalsIgnoreCase(dummyUserName)) {
		    	 log("El nombre es correcto");
		     }
		     else if (userInfoList.get(i).getAttribute("textContent").equalsIgnoreCase("Teléfono")&&userInfoList.get(i).getAttribute("value").equalsIgnoreCase(dummyTelefono)) {
		    	 log("El telefono es correcto");
		     }
		     else if (userInfoList.get(i).getAttribute("textContent").equalsIgnoreCase("Código postal")&&userInfoList.get(i).getAttribute("value").equalsIgnoreCase(dummyPostalCode)) {
		    	 log("El código postal es correcto");
		     }
		     else if (userInfoList.get(i).getAttribute("textContent").equalsIgnoreCase("Email")&&userInfoList.get(i).getAttribute("value").equalsIgnoreCase(dummyEmail)) {
		    	 log("El email es correcto");
		     }else {
		    	 //log(
		    	 
		     }*/
	  }
			 
}
  
  
  	public void javaScriptExecutor () {
	JavascriptExecutor js = (JavascriptExecutor) driver;  
	js.executeScript("var results = []; for(var x=0;x<allInputs.length;x++)if(allInputs[x].value == value)results.push(allInputs[x]);return results;");
  	}
}

	

