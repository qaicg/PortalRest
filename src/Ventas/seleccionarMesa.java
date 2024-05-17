package Ventas;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


import utils.TestBase;

public class seleccionarMesa extends TestBase{
	
	@Test(description="Este test selecciona la mesa para empezar el pedido" , priority=1)
	@Parameters({"mesa"})
	public void seleccionarMesaPorParametro(String mesa) {
		
		if (isElementPresent(By.xpath("//div[contains(text(),'¿En qué mesa desea recibir su pedido?')]"))) {
			if (isElementPresent(By.xpath("//button[contains(text(),'"+mesa+"')]"))) {
				driver.findElement(By.xpath("//button[contains(text(),'"+mesa+"')]")).click();
				espera(1000);
				driver.findElement(By.xpath("//button[contains(text(),'Aceptar')]")).click();
				espera(2000);
			}else {
				Assert.assertTrue(false,"No encuentro el botón de la mesa  " + mesa + " para ser pulsado");
			}
			
		}else {
			Assert.assertTrue(false,"No encuentro el titulo de la pantalla que pregunta por la mesa donde se desea recibir el pedido");
		}
		
	}
	

}
