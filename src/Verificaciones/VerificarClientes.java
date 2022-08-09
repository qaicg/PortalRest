package Verificaciones;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import graphql.Assert;
import utils.TestBase;

public class VerificarClientes extends TestBase {
  @Test
  @Parameters({ "email" , "menu", "profile", "personal"})
  public void validateUser(String email, String menu, String profile, String personal) {
	  espera(500); //Wait for main page 
	  
	  //Buttons are identified by text in the language of the page
	  WebElement menuButton = driver.findElement(By.xpath("//mat-icon[normalize-space()='"+ menu + "']"));
	  menuButton.click();
	  espera(500); //Wait for main page 
	  WebElement profileButton = driver.findElement(By.xpath("//*[contains(text(), '"+ profile +"')]"));
	  profileButton.click();
	  espera(500); //Wait for main page 
	  WebElement personalInfo = driver.findElement(By.xpath("//*[contains(text(), '"+ personal +"')]"));
	  personalInfo.click();
	  espera(1000);
	  w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='mat-input-5']")));
	  WebElement userEmail = driver.findElement(By.xpath("//input[@id='mat-input-5']")); //obtaining the user email
	  String user = userEmail.getAttribute("value");
	  
	  if (user.equalsIgnoreCase(email)) { //user email validation against email parameter in xml test
		  log("Cliente validado correctamente");
		  
	  }else { 
		   log("Cliente erróneo: Correo entrada "+ email +" obtenido "+ user);
		   Assert.assertTrue(false);
	  } 
	  
	  
  }
}
