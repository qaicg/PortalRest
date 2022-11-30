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

import Verificaciones.VerificarClientes;
import graphql.Assert;
import utils.Data;
import utils.TestBase;
import utils.getDummyData;

public class CrearCliente extends TestBase {
	
	 String dummyUserName;
	 String dummyTelefono;
	 String dummyPostalCode;
	 String dummyEmail;
	 String dummyPassword;
	 boolean isValidatedInformationPersonalUser = true;
	 public boolean isCreatedUser = false;
	
  @Test  (priority=1)
  @Parameters({"resultadoEsperado", "aceptoTerminos", "ICGCloud", "validationCliente", "menu", "profile", "personal" })
  public void crearCliente(@Optional ("true") boolean resultadoEsperado, @Optional ("true") boolean aceptoTerminos, @Optional ("false") boolean IcgCloud, @Optional ("false") boolean validationCliente,
		  @Optional("") String menu, @Optional("") String profile, @Optional("") String personal) {
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
		  
		  if(isElementPresent(By.className("generic-title"))) {
			  isCreatedUser = true;
		  }
		  
		  log("Cliente a crear -> Nombre: "+ dummyUserName + " email: "+ dummyEmail+ " password: " + dummyPassword);
	  }else {
		  //TO DO 
		  //Determinar expresiones a evaluar cuando queremos testear el registro sin �xito.
		 
	  }
	  
	  if(validationCliente) {
		  //validateNewUser(menu, profile, personal);
		  //Validation of the saved data in new user
		  espera(1000);
		  validatedPersonalInformationUser(IcgCloud, profile, personal, dummyUserName, dummyEmail, dummyTelefono, dummyPostalCode);
		  espera(1000);
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
  	
	public void validatedPersonalInformationUser (@Optional("false") boolean icgCloud, String profile, String personal, @Optional("") String name, 
			@Optional("") String email, @Optional("") String telefone, @Optional("") String codigoPostal) {
		//
		espera(500); //Wait for information personal page 
		abrirInformacionPersonal(profile, personal);
	  
		//espera(1000);
	  
		//Validar la información personal del cliente(usuario connectado en PR.
		//*************
		if(!isNullOrEmpty(name)) {
			w2.until(ExpectedConditions.presenceOfElementLocated( By.xpath("//input[contains(@id, 'mat-input-8')]") ));
			if( driver.findElement(By.xpath("//input[contains(@id, 'mat-input-8')]")).getAttribute("value").equalsIgnoreCase(name)) {
				log("El nombre es correcto");
			} else {
				log("Cliente erróneo: Nombre entrada " + name + " obtenido " + driver.findElement(By.xpath("//input[contains(@id, 'mat-input-8')]")).getAttribute("value"));
				isValidatedInformationPersonalUser = false;
			}
		}
		
		if(!isNullOrEmpty(telefone)) {
			w2.until(ExpectedConditions.presenceOfElementLocated( By.xpath("//input[contains(@id, 'mat-input-9')]") ));
			if( driver.findElement(By.xpath("//input[contains(@id, 'mat-input-9')]")).getAttribute("value").equalsIgnoreCase(telefone)) {
				log("El Teléono es correcto");
			} else {
				log("Cliente erróneo: Teléfono entrada " + telefone + " obtenido " + driver.findElement(By.xpath("//input[contains(@id, 'mat-input-9')]")).getAttribute("value"));
				isValidatedInformationPersonalUser = false;
			}
		}

		if(!isNullOrEmpty(codigoPostal)) {
			w2.until(ExpectedConditions.presenceOfElementLocated( By.xpath("//input[contains(@id, 'mat-input-10')]") ));
			if(driver.findElement(By.xpath("//input[contains(@id, 'mat-input-10')]")).getAttribute("value").equalsIgnoreCase(codigoPostal)) {
				log("El código postal es correcto");
			} else {
				log("Cliente erróneo: Codigo postal entrada " + codigoPostal + " obtenido " + driver.findElement(By.xpath("//input[contains(@id, 'mat-input-10')]")).getAttribute("value"));
				isValidatedInformationPersonalUser = false;
			}
		}

		if(!isNullOrEmpty(email)) {
			w2.until(ExpectedConditions.presenceOfElementLocated( By.xpath("//input[contains(@id, 'mat-input-11')]") ));
			if(driver.findElement(By.xpath("//input[contains(@id, 'mat-input-11')]")).getAttribute("value").equalsIgnoreCase(email)) {
				log("El correo electrónico es correcto");
			} else {
				log("Cliente erróneo: Email entrada " + email + " obtenido " + driver.findElement(By.xpath("//input[contains(@id, 'mat-input-11')]")).getAttribute("value"));
				isValidatedInformationPersonalUser = false;
			}
		}
		//************
		
		//TODO: A decidir si validamos los literales
	    //validatedLabelsInformationUser (icgCloud) 
		
		if(isValidatedInformationPersonalUser) {
			log("Cliente validado correctamente");
		} else {
			log("Hay error en la validacion del cliente ");
			Assert.assertTrue(false);
		}
	}
	
	public void validatedLabelsInformationUser (@Optional("") boolean icgCloud) {
		if(!icgCloud) {
			if(!isElementPresent(By.xpath("//mat-label[contains(@class, 'app-input-label') and text()='Nombre*']"))) {
				log("El literal del Nombre no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
			
			if(!isElementPresent(By.xpath("//mat-label[contains(@class, 'app-input-label') and text()='Teléfono*']"))) {
				log("El literal del Teléfono no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
			
			if(!isElementPresent(By.xpath("//mat-label[contains(@class, 'app-input-label') and text()='Código postal']"))) {
				log("El literal del código postal no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
			
			if(!isElementPresent(By.xpath("//mat-label[contains(text(), 'Email')]"))) {
				log("El literal del correo electrónico no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
		  
		} else {
		  
			if(!isElementPresent(By.xpath ("//mat-label[contains(text(), 'Name')]"))) {
				log("El literal del nombre no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
		  
			if(!isElementPresent(By.xpath ("//mat-label[contains(text(), 'Phone')]"))) {
				log("El literal del Teléono no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
			
			if(!isElementPresent(By.xpath ("//mat-label[contains(text(), 'ZIP')]"))) {
				log("El literal del código postal no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
		  
			if(!isElementPresent(By.xpath ("//mat-label[contains(text(), 'Email')]"))) {
				log("El literal del correo electrónico no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
		}
		
	}
}

	

