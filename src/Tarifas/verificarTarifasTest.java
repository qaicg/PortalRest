package Tarifas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.Data;
import utils.TestBase;

public class verificarTarifasTest extends TestBase {
	
	@Test(description="Valida los artículos visibles por pantalla, deben ser los esperados según la tarifa proporcionada en el test.", priority=1)
	@Parameters({"tarifaSelected","articulosEsperados","articulosEsperadosPrecios"})
	public void test(String tarifaSelected, String articulosEsperados, String articulosEsperadosPrecios) {
		
		String[] articulosEsperadosArray = articulosEsperados.split(",");
		String[] articulosEsperadosPreciosArray = articulosEsperadosPrecios.split(";");
		Data.getInstance().getExtentTest().info("Los artículos y precios esperados son: " + articulosEsperados + " " + articulosEsperadosPrecios);
		
		int numArticulosEsperados = articulosEsperadosArray.length;
		
		if (!isElementPresent(By.xpath("//app-product-item-list"))) {
			Data.getInstance().getExtentTest().fail("Error localizando items del listado de productos");
			Assert.assertTrue(false, "Error localizando items del listado de productos");
		}
		
		//Encuentro el listado de productos
		espera(2000);
		
		List<WebElement> elements = driver.findElements(By.xpath("//app-product-item-list"));
		
		if (elements.size()!=numArticulosEsperados) {
			Data.getInstance().getExtentTest().fail("Error localizando items del listado de productos, esperaba "+numArticulosEsperados+" y tengo " + elements.size());
			Assert.assertTrue(false, "Error localizando items del listado de productos, esperaba "+numArticulosEsperados+" productos y tengo " + elements.size());
		}
		
		//El numero de resultados es el esperado.
		
		//Analizando los resultados encontrados.
		
		for (int i=0; i< elements.size(); i++) {
			WebElement element = elements.get(i);
		
			if (!element.findElement(By.xpath(".//div[contains(@class,'dish-name line-clamp-2')]")).getText().equalsIgnoreCase(articulosEsperadosArray[i])) {
				
				Data.getInstance().getExtentTest().fail("El nombre del producto que encuentro "+element.findElement(By.xpath(".//div[contains(@class,'dish-name line-clamp-2')]")).getText()+
						" no coincide con el que espero. " + articulosEsperadosArray[i]);
				Assert.assertTrue(false, "El nombre del producto que encuentro "+element.findElement(By.xpath(".//div[contains(@class,'dish-name line-clamp-2')]")).getText()+
						" no coincide con el que espero. " + articulosEsperadosArray[i]);
			}
			
			if (!element.findElement(By.xpath(".//span[contains(@class,'dish-price')]")).getText().equalsIgnoreCase(articulosEsperadosPreciosArray[i])) {
				
				Data.getInstance().getExtentTest().fail("El precio del producto que encuentro "+element.findElement(By.xpath(".//span[contains(@class,'dish-price')]')]")).getText()+
						" no coincide con el que espero. " + articulosEsperadosPreciosArray[i]);
				Assert.assertTrue(false, "El precio del producto que encuentro "+element.findElement(By.xpath(".//span[contains(@class,'dish-price')]')]")).getText()+
						" no coincide con el que espero. " + articulosEsperadosPreciosArray[i]);
			}
			
		}
		
		//Los nombres y precios encontrados coinciden
		
		Data.getInstance().getExtentTest().pass("Los artículos y precios mostrados en carta coinciden con los esperados");
		
		Assert.assertTrue(true);
	}
}
