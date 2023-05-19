package Clientes;

import java.sql.ResultSet;
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
	 WebElement inputName, inputPhone, inputPostalCode, inputEmail, inputPassword, inputRepeatPassword,  checkboxCondiciones, buttonCrear;
	
  @Test  (priority=1, groups = {"createUser"})
  @Parameters({"resultadoEsperado", "aceptoTerminos", "ICGCloud", "validationCliente", "menu", "profile", "personal" , "shop", "email", "telefone"})
  public void crearCliente(@Optional ("true") boolean resultadoEsperado, @Optional ("true") boolean aceptoTerminos, @Optional ("false") boolean IcgCloud, @Optional ("false") boolean validationCliente,
		  @Optional("") String menu, @Optional("") String profile, @Optional("") String personal, @Optional("") String shop, @Optional("") String email, @Optional("") String telefone) {
	  WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
	  w.until(ExpectedConditions.presenceOfElementLocated (By.className("generic-title")));
	  espera(1000);
	  if(getDummyData.getLimite()) {
		  List<String> dummyInformation = getDummyData.getDummyInformation();
		  
		  dummyUserName = dummyInformation.get(0);
		  dummyTelefono = dummyInformation.get(4);
		  dummyPostalCode = dummyInformation.get(3);
		  
		  dummyEmail = stripAccents(dummyInformation.get(5));
		  
		  Data.getInstance().setNewUserMail(dummyEmail);//LO GUARDAMOS PARA PODER VALIDAR POSIBLES PEDIDOS
		  dummyPassword = dummyInformation.get(6);

	  
	  } else {
		  dummyUserName = getDummyData.getDummyUserName();
		  dummyTelefono = getDummyData.getDummyTelefono();
		  dummyPostalCode = getDummyData.getDummyPostalCode();
		  dummyEmail =stripAccents(dummyUserName) + "@yopmail.com";
		  Data.getInstance().setNewUserMail(dummyEmail);//LO GUARDAMOS PARA PODER VALIDAR POSIBLES PEDIDOS
		  dummyPassword = getDummyData.getDummyPassword();
	  }
	  
	  //WebElement inputName, inputPhone, inputPostalCode, inputEmail, inputPassword, inputRepeatPassword, checkboxCondiciones, buttonCrear;
	  
	 /* if(IcgCloud) { //Input for ICGCloud login
		  inputName = driver.findElement(By.id("mat-input-4"));
		  inputPhone = driver.findElement(By.id("mat-input-5"));
		  inputPostalCode = driver.findElement(By.id("mat-input-6"));
		  inputEmail = driver.findElement(By.id("mat-input-7"));
		  inputPassword = driver.findElement(By.id("mat-input-2"));
		  inputRepeatPassword = driver.findElement(By.id("mat-input-3"));
		  
		  //checkboxCondiciones = driver.findElement(By.xpath("//label[@for='mat-checkbox-2-input']//div[@class='mat-checkbox-inner-container']"));
		  //buttonCrear= driver.findElement(By.xpath("//button[@class='btn-centered']//div[contains(text(),'Crear')]"));
		  
		 
	  } else { //Input for PRT login
		  //Se deberian usar otros localziadores mejores pero no los hay...
		  inputName = driver.findElement(By.cssSelector("#mat-input-2"));
		  inputPhone = driver.findElement(By.cssSelector("#mat-input-3"));
		  inputPostalCode = driver.findElement(By.cssSelector("#mat-input-4"));
		  inputEmail = driver.findElement(By.cssSelector("#mat-input-5"));
		  inputPassword = driver.findElement(By.cssSelector("#mat-input-6"));
		  inputRepeatPassword = driver.findElement(By.cssSelector("#mat-input-7"));
		  
		  //checkboxCondiciones = driver.findElement(By.xpath("//label[@for='mat-checkbox-2-input']//div[@class='mat-checkbox-inner-container']"));
		  //buttonCrear= driver.findElement(By.xpath("//button[@class='btn-centered']//div[contains(text(),'Crear')]"));
	  }*/
	  
	  
	  //
	  espera(500);
	  createInputElement();
	  espera(1000);
	  checkboxCondiciones = driver.findElement(By.xpath("//label[@for='mat-checkbox-2-input']//div[@class='mat-checkbox-inner-container']"));
	  buttonCrear= driver.findElement(By.xpath("//button[@class='btn-centered']//div[contains(text(),'Crear')]"));
	  
	  //
	  enviarTexto(inputName, dummyUserName);
	  if(!isNullOrEmpty(telefone))
		  enviarTexto(inputPhone, telefone);
	  else
		  enviarTexto(inputPhone, dummyTelefono);
	  
	  enviarTexto(inputPostalCode,dummyPostalCode);
	  
	  if(!isNullOrEmpty(email))
		  enviarTexto(inputEmail, email);
	  else
		  enviarTexto(inputEmail, dummyEmail);
	  
	  enviarTexto(inputPassword,dummyPassword);
	  enviarTexto(inputRepeatPassword,dummyPassword);  
	   
	  if(aceptoTerminos)checkboxCondiciones.click(); 
	  buttonCrear.click();
	  espera(1000);
	  
	  if (resultadoEsperado) {
		  w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10)); //Le damos 10 segundos para registrar el usuario y salir de la pantalla de registro.
		  espera(500);
		  w.until(ExpectedConditions.invisibilityOfElementLocated(By.className("generic-title")));
		  
		  if(isElementPresent(By.className("generic-title"))) {
			  isCreatedUser = true;
		  }
		  
		  log("Cliente a crear -> Nombre: "+ dummyUserName + " email: "+ dummyEmail+ " password: " + dummyPassword);
	  }else {
		  //TO DO 
		  //Determinar expresiones a evaluar cuando queremos testear el registro sin exito debido al email ya existente en la BD.
		  if(!isNullOrEmpty(email) && !isNullOrEmpty(shop)) {
			  if(isPersonalInformationValidatedDB("", email, "", "", shop, true)) {
				  log("No se puede registrar debido a la existencia del correo electronico");
				  log("Resulta esperado es correcto");
				  
				  //validar la notificacion de PortalRest al usuario que su email ya existe 
				  // WebDriverWait error = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
				  // error.until(ExpectedConditions.visibilityOfElementLocated(By.id("mat-error-53")));
				  //
				  String typeEmail = "email";
				  String ariaInvalid = "true";
				  String ariaRequired = "false";
				  String typeId = "10";
				  String typeIdMatError = "mat-error-10";
				  
				  if(IcgCloud) {
					  typeEmail = "text";
					  ariaInvalid = "true";
					  ariaRequired = "true";
					  typeId = "7";
					  typeIdMatError = "mat-error-7";
				  }
				  
				  w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[contains(@type, '" +typeEmail + "') and contains(@aria-invalid, '" + ariaInvalid + "') and contains(@aria-required, '" + ariaRequired + "')]")));
				  if(!isElementPresent((By.xpath("//input[contains(@type, '" +typeEmail + "') and contains(@aria-invalid, '" + ariaInvalid + "') and contains(@aria-required, '" + ariaRequired + "')]")))) {
					  //Debemos mostrar al usuario que su email ya esta en uso 
					  log("Debemos mostrar al usuario que su email ya está en uso en la tienda ");
					  Assert.assertTrue(false);
				  } else {
					  w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-error[contains(@role, 'alert') and contains(@id, '" + typeIdMatError + "') and contains(@class, 'mat-error') and contains(text(), 'email')]")));
					  
					  if(!isElementPresent(By.xpath("//mat-error[contains(@role, 'alert') and contains(@id, '" + typeIdMatError + "') and contains(@class, 'mat-error') and contains(text(), 'email')]"))) {
						  log("Debemos mostrar al usuario que su email ya está en uso en la tienda ");
						  Assert.assertTrue(false);
					  }
				  }
				  
				  log("Registrar failed as spected");
				  Assert.assertTrue(true);
			  } else {
				  log("Error: Debe devuelver el registro del usuario ya creado con el correo electronico o/y del telefone");
				  Assert.assertTrue(false);
			  }
		  }
		  
		  //Verificar que el nuevo usuario no ha aceptado terminos y condiciones de uso de la tienda
		  if(!aceptoTerminos) {
			  String typeElement = "checkbox" ;
			  String classElement = "mat-checkbox-input";
			  String typeId = "mat-checkbox-2-input";
			  String ariaChecked = "false";
			  
			  w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-checkbox[contains(@id, 'mat-checkbox-2') and contains(@class, 'ng-invalid') and contains(@formcontrolname, 'conditionsAccepted')]")));
			  
			  if(!isElementPresent(By.xpath("//mat-checkbox[contains(@id, 'mat-checkbox-2') and contains(@class, 'ng-invalid') and contains(@formcontrolname, 'conditionsAccepted')]"))) {
				  log("Error: Debe avisar que el nuevo cliente na acepte terminos y condiciones de uso de la tienda");
				  Assert.assertTrue(false);
			  }
			  
			  log("Avisar al nuevo cliente debe aceptar terminos y condiciones de uso de la tienda");
			  log("Registrar failed as spected");
			  
		  }
	  }
	  
	  if(validationCliente) {
		  //validateNewUser(menu, profile, personal);
		  //Validation of the saved data in new user
		  espera(1000);
		  validatedPersonalInformationUser(IcgCloud, profile, personal, dummyUserName, dummyEmail, dummyTelefono, dummyPostalCode);
		  espera(1000);
		  
		  //Validar la informacion personal del cliente en BBDD
		  if(!IcgCloud) {
			  if(isPersonalInformationValidatedDB(dummyUserName, dummyEmail, dummyTelefono, dummyPostalCode, shop, false)) {
				  log("Se ha validado correctamiente la información personal del cliente en la BBDD");
			  } else {
				  log("Error: Hubo fallo en la validación de la información personal del cliente en la BBDD");
				  Assert.assertTrue(false);
			  }
		  }
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
	
	private boolean isPersonalInformationValidatedDB(@Optional("") String nombre, @Optional("") String email, @Optional("") String telefono, @Optional("") String codigoPostal, String shop, @Optional("false") boolean verifyEmailTelefone) {
		boolean valueReturn = false;
		String SQL = null;
		
		if(!verifyEmailTelefone)
	    	SQL = "SELECT cc.ContactTypeId , cc.Name, cc.Email, cc.Phone, cc.PostalCode " 
	    			+ "FROM Con__Contact cc "
	    			+ "WHERE cc.Name = '"+nombre+"'  AND cc.Email = '"+email+"' AND cc.Phone = '"+telefono+"' AND cc.PostalCode = '"+codigoPostal+"'";
		else if(verifyEmailTelefone && !isNullOrEmpty(email) && !isNullOrEmpty(telefono))
			SQL = "SELECT cc.ContactTypeId , cc.Name, cc.Email, cc.Phone, cc.PostalCode " 
	    			+ "FROM Con__Contact cc "
	    			+ "WHERE cc.Email = '"+email+"' AND cc.Phone = '"+telefono+"'";
		else if(verifyEmailTelefone && !isNullOrEmpty(email))
			SQL = "SELECT cc.ContactTypeId , cc.Name, cc.Email, cc.Phone, cc.PostalCode " 
	    			+ "FROM Con__Contact cc "
	    			+ "WHERE cc.Email = '"+email+"'";
		else if(verifyEmailTelefone && !isNullOrEmpty(telefono))
			SQL = "SELECT cc.ContactTypeId , cc.Name, cc.Email, cc.Phone, cc.PostalCode " 
	    			+ "FROM Con__Contact cc "
	    			+ "WHERE cc.Email = '"+telefono+"'";
		
    	ResultSet rs =  databaseConnection.ejecutarSQL(SQL,"DB" + shop); 
    	
    	if (rs!=null) {
     		 try {		
     			if (rs.first()) {
     				log("La información personal del cliente se encuentra en la BBDD");
     				valueReturn = true;
     			}else {
     				log("La información personal del cliente ya no se encuentra en la BBDD");	
     				log(SQL);
     				Assert.assertTrue(false);
     			}
     			
     		} catch (Exception e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}finally {
     			databaseConnection.desconectar();
     		}
     		 //Assert.assertTrue(false);
	    }
    	
    	return valueReturn;
	}
	
	public void createInputElement() {
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'user-register-form')]//child::input")));
		List<WebElement> inputElements = driver.findElements(By.xpath("//div[contains(@class, 'user-register-form')]//child::input"));
		inputName = inputElements.get(0);
		inputPhone = inputElements.get(1);
		inputPostalCode = inputElements.get(2);
		inputEmail = inputElements.get(3);
		inputPassword = inputElements.get(4);
		inputRepeatPassword = inputElements.get(5);
	}
	
	public void validatedEmailTelefoneUserDB(@Optional("") String email, @Optional("") String telefone, @Optional("") String shop) {
		//Verificar que el correo electronico no existe en la BBDD antes de crear el usuario
		if(isPersonalInformationValidatedDB("", email, "", "", shop, true)) {
			log("No se puede registrar debido a la existencia del correo electronico");
			log("Resulta esperado es correcto");
			Assert.assertTrue(true);
		} else {
			log("Error: Debe devuelver el registro del usuario ya creado con el correo electronico o/y del telefone");
			Assert.assertTrue(false);
		}
	}

}

