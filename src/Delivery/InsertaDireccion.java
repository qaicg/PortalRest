package Delivery;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.Data;
import utils.TestBase;

public class InsertaDireccion extends TestBase{
	@Test(description="Crea una dirección en una venta delivery")
    @Parameters({"direccion","importeMinimo","cargoReparto","cp","ciudad","shop"})
  public void creaNuevaDireccion(String direccion, String importeMinimo, String cargoReparto, String cp, String ciudad, String shop) {
		borraDireccionPruebas(shop);
		espera(500);
    	w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'user-address-add')]"))).click();
    	espera(500);
    	w.until (ExpectedConditions.presenceOfElementLocated(By.tagName("form")));	
    	espera(500);
    	log("Registramos nueva dirección");
    	String address=direccion.split(", ")[0];
    	driver.findElement(By.xpath("//textArea[@formControlName='roadName']")).sendKeys(address);
    	String number=direccion.split(", ")[1];
    	driver.findElement(By.xpath("//input[@formControlName='roadNumber']")).sendKeys(number);
    	driver.findElement(By.xpath("//input[@formControlName='city']")).sendKeys(ciudad);
    	driver.findElement(By.xpath("//input[@formControlName='postalCode']")).sendKeys(cp);
    	driver.findElement(By.xpath("//textArea[@formControlName='observations']")).sendKeys("Observaciones de prueba.");
    	espera(500);
    	driver.findElement(By.xpath("//app-square-progress-button[@class='square-progress-button']")).click();
    	espera(500);
    	w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='btn-confirm']"))).click();
    	espera(500);
    	
  
    	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
    		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
    	}
    	
    }

	private void borraDireccionPruebas(String shop) {
		// TODO Auto-generated method stub
		String SQL="update  Con__Address set isDiscontinued=1 where RoadName ='Avinguda Europa'";
		
		if (databaseConnection.ejecutaUpdate(SQL, "DB"+shop)==0) {
			log("Error borrando direccion de pruebas");
		}else
			log("Limpiamos posibles direcciones de prueba antes de empezar.");	
	}
}
