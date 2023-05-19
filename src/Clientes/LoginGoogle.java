package Clientes;




import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import net.bytebuddy.build.Plugin.Factory.UsingReflection.Priority;
import utils.TestBase;

public class LoginGoogle extends TestBase {
	
	@Test(priority = 1, groups = {"loginGmail"})
	@Parameters({"resultadoEsperado", "email","password" })
	public void loginGoogle() {
		WebDriverWait w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
		w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//button[@class='social-login-button login-google-button ng-star-inserted']")));
		WebElement GoogleButton = driver.findElement(By.xpath("//button[@class='social-login-button login-google-button ng-star-inserted']"));
		GoogleButton.click();	
		
		//Veririfcar si estamos bien logeado en gmail.		
		if(!checkSesionGmail()) {
			Assert.assertTrue(false, "Error: ha fallado el login con gmail en PortalRest");
		}
		
	}
	
	public boolean checkSesionGmail() {
		boolean result = true;
		abrirMenu();
		espera();
		String sesionGamil = "//div/button[contains(text(), 'Cerrar sesión Departamento Calidad ICG')]"; //"Cerrar sesión Departamento Calidad ICG"
		
		if(!isElementPresent(By.xpath(sesionGamil))) {
			log("No se ha podido connectarse con gmail");
			result = false;
		}
		
		//Cerrar el menú
		abrirMenu();
		
		return result;
	}
   
}



	  
