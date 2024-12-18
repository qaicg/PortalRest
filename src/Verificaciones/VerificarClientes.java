package Verificaciones;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import utils.TestBase;

public class VerificarClientes extends TestBase {
	boolean isValidatedInformationPersonalUser = true;
	@Test(priority = 1, groups = {"validateUser"})
	@Parameters({ "email", "menu", "profile", "personal" })
	public void validateUser(String email, String menu, String profile, String personal) {
		espera(500); // Wait for main page

		// Buttons are identified by text in page language
		WebElement menuButton = driver.findElement(By.xpath("//mat-icon[normalize-space()='" + menu + "']"));
		menuButton.click();
		espera(2000); // Wait for menu button
		WebElement profileButton = driver.findElement(By.xpath("//*[contains(text(), '" + profile + "')]"));
		profileButton.click();
		espera(2000); // Wait for profile button
		WebElement personalInfo = driver.findElement(By.xpath("//*[contains(text(), '" + personal + "')]"));
		personalInfo.click();
		espera(2000); // Wait for personaldata button
		w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='mat-input-5']")));
		WebElement userEmail = driver.findElement(By.xpath("//input[@id='mat-input-5']")); // obtaining the user email
		String user = userEmail.getAttribute("value");

		if (user.equalsIgnoreCase(email)) { // user email validation against email parameter in xml test
			log("Cliente validado correctamente");

		} else {
			log("Cliente erróneo: Correo entrada " + email + " obtenido " + user);
			Assert.assertTrue(false);
		}

	}
	
	@Test(description="Este test permite validar la información personal del usuario logeado." , priority=1, groups = {"validateUserInformation"})
	@Parameters({"icgCloud", "profile", "personal" , "name", "email", "telefone", "codigoPostal"})
	public void validatedPersonalInformationUser (@Optional("false") boolean icgCloud, String profile, String personal, @Optional("") String name, 
			@Optional("") String email, @Optional("") String telefone, @Optional("") String codigoPostal) {
		//
		espera(1000); //Wait for information personal page 
		abrirInformacionPersonal(profile, personal);
	  
		//w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//mat-form-field")));
		espera(1000);
	  
		//Validar la información personal del cliente(usuario connectado en PR.
		
		//************* Validar datos del cliente
		if(!isNullOrEmpty(name)) {
			espera(500);
			w2.until(ExpectedConditions.presenceOfElementLocated( By.xpath("//input[contains(@id, 'mat-input-2')]") ));
			if( driver.findElement(By.xpath("//input[contains(@id, 'mat-input-2')]")).getAttribute("value").equalsIgnoreCase(name)) {
				log("El nombre es correcto");
			} else {
				log("Cliente erróneo: Nombre entrada " + name + " obtenido " + driver.findElement(By.xpath("//input[contains(@id, 'mat-input-2')]")).getAttribute("value"));
				isValidatedInformationPersonalUser = false;
			}
		}
		
		if(!isNullOrEmpty(telefone)) {
			w2.until(ExpectedConditions.presenceOfElementLocated( By.xpath("//input[contains(@id, 'mat-input-3')]") ));
			if( driver.findElement(By.xpath("//input[contains(@id, 'mat-input-3')]")).getAttribute("value").equalsIgnoreCase(telefone)) {
				log("El Teléono es correcto");
			} else {
				log("Cliente erróneo: Teléfono entrada " + telefone + " obtenido " + driver.findElement(By.xpath("//input[contains(@id, 'mat-input-3')]")).getAttribute("value"));
				isValidatedInformationPersonalUser = false;
			}
		}

		if(!isNullOrEmpty(codigoPostal)) {
			w2.until(ExpectedConditions.presenceOfElementLocated( By.xpath("//input[contains(@id, 'mat-input-4')]") ));
			if(driver.findElement(By.xpath("//input[contains(@id, 'mat-input-4')]")).getAttribute("value").equalsIgnoreCase(codigoPostal)) {
				log("El código postal es correcto");
			} else {
				log("Cliente erróneo: Codigo postal entrada " + codigoPostal + " obtenido " + driver.findElement(By.xpath("//input[contains(@id, 'mat-input-4')]")).getAttribute("value"));
				isValidatedInformationPersonalUser = false;
			}
		}

		if(!isNullOrEmpty(email)) {
			w2.until(ExpectedConditions.presenceOfElementLocated( By.xpath("//input[contains(@id, 'mat-input-5')]") ));
			if(driver.findElement(By.xpath("//input[contains(@id, 'mat-input-5')]")).getAttribute("value").equalsIgnoreCase(email)) {
				log("El correo electrónico es correcto");
			} else {
				log("Cliente erróneo: Email entrada " + email + " obtenido " + driver.findElement(By.xpath("//input[contains(@id, 'mat-input-5')]")).getAttribute("value"));
				isValidatedInformationPersonalUser = false;
			}
		}
		
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
		//************ Validar los literales
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
			
			if(!isElementPresent(By.xpath ("//mat-label[contains(text(), 'Name')]"))  && !isElementPresent(By.xpath ("//mat-label[contains(text(), 'Nombre')]"))) {
				log("El literal del nombre no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
		  
			if(!isElementPresent(By.xpath ("//mat-label[contains(text(), 'Phone')]")) && !isElementPresent(By.xpath ("//mat-label[contains(text(), 'Teléfono')]"))) {
				log("El literal del Teléono no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
			
			
			if(!isElementPresent(By.xpath ("//mat-label[contains(text(), 'ZIP')]")) && !isElementPresent(By.xpath ("//mat-label[contains(text(), 'Código postal')]"))) {
				log("El literal del código postal no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
		  
			if(!isElementPresent(By.xpath ("//mat-label[contains(text(), 'Email')]")) && !isElementPresent(By.xpath ("//mat-label[contains(text(), 'Correo electrónico')]"))) {
				log("El literal del correo electrónico no es correcto"); 
				isValidatedInformationPersonalUser = false;
			}
		}
	}

}
