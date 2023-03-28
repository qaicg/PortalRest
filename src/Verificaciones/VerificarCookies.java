package Verificaciones;

import org.testng.annotations.Test;

import graphql.Assert;

import org.testng.annotations.Test;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Test;

import utils.TestBase;

public class VerificarCookies extends TestBase {
	public boolean isVericarCookies = false;
	
	@Test(description = "Verifica si sale o no la ventana de aceptaci�n de cookies", priority = 1)
	public void verificarCookies() {
		WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(15));
		w.until(ExpectedConditions.presenceOfElementLocated(By.className("cookies-info-content")));
		if (isElementPresent(By.className("cookies-info-content"))) {
			log("Hemos encontrado la classe cookies-info-content");
		} else {
			log("No Hemos encontrado la classe cookies-info-content");
		}
		
		this.setVericarCookies(true);
	}

	// Solo se ejecuta si el test de verificar cookies es correcto.
	@Test(description = "Acepta cookies", dependsOnMethods = "verificarCookies", priority = 2)
	public void aceptaCookies() {
		String acpetarCookiesBooking = "//div[contains(@class, 'cookies-info-content-button')]//child::button[contains(@class, 'button primary-color')]";
		WebElement aceptarCookies = null;
		espera(1000);
		if(isElementPresent(By.xpath(acpetarCookiesBooking))) {
			aceptarCookies = driver.findElement(By.xpath(acpetarCookiesBooking));
		} 
		
		espera(1000);
		if(isElementPresent(By.className("btn-confirm"))) {
			aceptarCookies = driver.findElement(By.className("btn-confirm"));
		}
		
		if(aceptarCookies.getSize() != null) {
				
			if(driver.findElements(By.xpath("//button[contains(@class, 'btn-confirm')]")).size() > 1) { 
				//pantalla muestrando mensaje de error con botones vuelve a intertarlo y aceptar cookies.
				driver.findElements(By.xpath("//button[contains(@class, 'btn-confirm')]")).get(1).click();
			} else {
				//pantalla muestra solo el botón aceptar cookies
				aceptarCookies.click();
			}
			espera(1000);
			log("Hemos aceptado las cookies");
		} else {
			log("No se ha encuentrado el boton aceptar cookies");
			Assert.assertTrue(false);
		}
		
		//WebElement aceptarCookies = driver.findElement(By.className("btn-confirm"));
		//aceptarCookies.click();
		//log("Hemos aceptado las cookies");
	}
	
	public boolean isVericarCookies() {
		return isVericarCookies;
	}

	public void setVericarCookies(boolean isVericarCookies) {
		this.isVericarCookies = isVericarCookies;
	}
}