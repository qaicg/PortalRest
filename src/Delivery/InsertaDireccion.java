package Delivery;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.TestBase;

public class InsertaDireccion extends TestBase{
	@Test(description="Crea una dirección en una venta delivery")
    @Parameters({"direccion","importeMinimo","cargoReparto","cp","ciudad"})
  public void creaNuevaDireccion(String direccion, String importeMinimo, String cargoReparto, String cp, String ciudad) {
    	w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'user-address-add')]"))).click();
    	w.until (ExpectedConditions.presenceOfElementLocated(By.tagName("form")));	
    	log("Registramos nueva dirección");
    	String address=direccion.split(", ")[0];
    	driver.findElement(By.xpath("//textArea[@formControlName='roadName']")).sendKeys(address);
    	String number=direccion.split(", ")[1];
    	driver.findElement(By.xpath("//input[@formControlName='roadNumber']")).sendKeys(number);
    	driver.findElement(By.xpath("//input[@formControlName='city']")).sendKeys(ciudad);
    	driver.findElement(By.xpath("//input[@formControlName='postalCode']")).sendKeys(cp);
    	driver.findElement(By.xpath("//textArea[@formControlName='observations']")).sendKeys("Observaciones de prueba.");
    	driver.findElement(By.xpath("//app-square-progress-button[@class='square-progress-button']")).click();
    	w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='btn-confirm']"))).click();
    	w.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
    	
    }
}
