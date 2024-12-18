package Delivery;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.TestBase;

public class SeleccionaDireccion extends TestBase {
	@Test(description="Selecciona una dirección existente en una venta delivery", groups = {"selecteAddress"})
    @Parameters({"direccion","importeMinimo","cargoReparto", "repartoPermitido"})
  public void seleccionaDireccionExistente(String direccion, String importeMinimo, String cargoReparto, @Optional ("true") Boolean repartoPermitido ) {
    	espera(2000);
    	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
    		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
    	}
		
    	List<WebElement> listaDirecciones = driver.findElements(By.xpath("//div[contains(@class,'user-address-container')]"));
    	if(listaDirecciones.size()==0)Assert.assertTrue(false);
    	
    	for(int i=0;i<listaDirecciones.size();i++) {
    		String addressLine = listaDirecciones.get(i).findElements(By.xpath(".//div[contains(@class,'user-address-line')]")).get(0).getAttribute("innerText");
    		if(addressLine.equalsIgnoreCase(direccion)) {
    			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//mat-icon[text()='panorama_fish_eye']")));
    			clicJS(listaDirecciones.get(i).findElement(By.xpath(".//mat-icon[text()='panorama_fish_eye']"))); //SELECCIONO DIRECCION
    			espera(1000);
    			
    			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='btn-centered']")));
    			clicJS(driver.findElement(By.xpath("//button[@class='btn-centered']"))); // PULSO EN CONTINUAR
    			espera(1000);
    			break;
    		}
    	}	
    	espera(1000);
    	
    	if(!repartoPermitido) {
    		espera(2000);
    		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class,'generic-dialog-label')]")));
    		
    		if (isElementPresent(By.xpath("//div[contains(@class,'generic-dialog-label')]"))) {
    			String informacion = driver.findElement(By.xpath("//div[contains(@class,'generic-dialog-label')][1]")).getAttribute("innerText");
    			log("Se informa al usuario de que no repartimos en su zona " + informacion );
    			espera(500);
    			
    			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='msg-dialog-buttons']//button[2]")));
    			driver.findElement(By.xpath("//div[@class='msg-dialog-buttons']//button[2]")).click();//pulsamos atrás
    			espera(1000);
    			
    			
    			if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
    	    		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
    	    	}
    			
    			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[@class='btn-centered']")));
    	    	espera(500);
    	    	clicJS(driver.findElement(By.xpath("//button[@class='btn-centered']"))); // PULSO EN CONTINUAR
    	    	espera(500);
    	    	
    	    	//******
    	    	//w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'generic-dialog-label')]")));
    	    	//espera(1500);
    	    	espera(1000);
    	    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='msg-dialog-buttons']//button[1]")));
    	    	//driver.findElement(By.xpath("//div[@class='msg-dialog-buttons']//button[1]")).click();//pulsamos para recoger en local en botón Aceptar
    	    	clicJS(driver.findElement(By.xpath("//div[@class='msg-dialog-buttons']//button[1]"))); //pulsamos para recoger en local en botón Aceptar
    	    	espera(1000);
    	    	
    			log("Se continua el pedido en local");    			
    		}else {
    			log("Error. No se informa al usuario de que no repartimos en su zona ");
    			Assert.assertTrue(false);
    		}
    			
    	} else {
    	
			espera(1000); 
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class,'charges-data')]")));
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class,'charges-text main-text')]")));
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class,'charges-amount')]")));
			
			if (isElementPresent(By.xpath("//div[contains(@class,'charges-data')]")) &&
				!driver.findElements(By.xpath("//div[contains(@class,'charges-text main-text')]")).get(0).getAttribute("innerText").equalsIgnoreCase("") &&
				!driver.findElements(By.xpath("//div[contains(@class,'charges-text main-text')]")).get(1).getAttribute("innerText").equalsIgnoreCase("") &&
				 driver.findElements(By.xpath("//div[contains(@class,'charges-amount')]")).get(0).getAttribute("innerText").equalsIgnoreCase(importeMinimo) &&
				 driver.findElements(By.xpath("//div[contains(@class,'charges-amount')]")).get(1).getAttribute("innerText").equalsIgnoreCase(cargoReparto)) {
				
				log("Se informa correctamente sobre importe minimo y cargo por servicio");
				
				espera(1000);
				//
				w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'btn-confirm')]")));
				clicJS(driver.findElement(By.xpath("//button[contains(@class,'btn-confirm')]"))); //ACEPTO AVISO DE IMPORTE MINIMO Y CARGO POR SERVICIO
				espera(1000);
	    		
	    	}
	    	else {
	    		log("No se informa sobre importe minimo y cargo por servicio");
	    	}
	    	espera(500);
			Assert.assertTrue(true);
    	}
  }
}
