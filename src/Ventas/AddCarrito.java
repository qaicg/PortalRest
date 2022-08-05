package Ventas;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.model.Log;

import org.testng.AssertJUnit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Objects.ProductItem;
import graphql.Assert;
import utils.TestBase;

//ESTA FUNCION BUSCA UN PRODUCTO EN LA CARTA DE PORTALREST UTILIZANDO SU NOMBRE.
public class AddCarrito extends TestBase{
	String[] arrayNombres;
	int productosEncontrados;

	@Test (description="Este test busca un producto dado en el PortalRest actual" , priority=1)
	@Parameters({"productos","totalEsperado","opcionesMenu","unidades"})
	public void addCart(String productos, String totalEsperado, @Optional ("") String opcionesMenu, @Optional ("") String unidades) {
		arrayNombres = productos.split(",");
		w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(30));
		w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[contains(@class,'familyItem')]")));	
		List<WebElement> familias;
		productosEncontrados=0;
		ArrayList<ProductItem> productosAddeds = new ArrayList<ProductItem> ();

		familias  = driver.findElements(By.xpath("//li[contains(@class,'familyItem')]"));
		//ENTRO EN LA PRIMERA FAMILIA
		if (familias.size()>0) {
			WebElement nombreFamilia = familias.get(0).findElement(By.xpath("//div[contains(@class,'familyName')]"));
			log("Entro en primera familia " + nombreFamilia.getAttribute("innerText"));
			espera(1000);
			clicJS(nombreFamilia);

		}

		boolean doScroll=true;
		int vueltas=0;
		String familia = "";

		while (doScroll) {

			espera(2500);
			w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//div[@class='dishItem']")));
			List<WebElement> elements = driver.findElements(By.xpath("//span[contains(@class,'test-product-item')]"));

			if (isElementPresent(By.xpath("//div[contains(@class,'family-title')]"))) {
				familia = driver.findElement(By.xpath("//div[contains(@class,'family-title')]")).getAttribute("innerText");
			}		

			for(int i=1;i<= elements.size();i++) { 

				ProductItem currentItem = new ProductItem();	   		 
				currentItem.setNombre(driver.findElement(By.xpath("(//div[contains(@class,'dish-name')])["+i+"]")).getAttribute("innerText"));		   
				if (isElementPresent(By.xpath("(//div[contains(@class,'dish-image')])["+i+"]")))
					currentItem.setImagenElement(driver.findElement(By.xpath("(//div[contains(@class,'dish-image')])["+i+"]")));

				if(isElementPresent(By.xpath("(//button[contains(@class,'product-info')])["+i+"]"))) currentItem.setBoton(driver.findElement(By.xpath("(//button[contains(@class,'product-info')])["+i+"]")));
				else currentItem.setBoton(driver.findElement(By.xpath("(//div[contains(@class,'product-item-add')])["+i+"]")));	

				if(contieneNombre(arrayNombres,currentItem.getNombre()) && !productosAddeds.contains(currentItem) && (i<elements.size() || currentItem.getNombre().contains("MENÚ")))
				{
					clicJS(currentItem.getBoton());
					productosAddeds.add(currentItem);
					
					//Validamos si tiene formatos o modificadores
					espera(500);
					if (isElementPresent(By.tagName("mat-dialog-container"))) {
						espera(1000);
						if (isElementPresent(By.tagName("app-menu-dialog"))) { // ES UN MENÚ
							//EL OBJETIVO ES CLICAR LAS PRIMERAS OPCIONES DE TODOS LOS ORDENES.
							List<WebElement> arrayPlatosMenu = driver.findElements(By.xpath("//app-menu-dialog//div[contains(@class,'dish-menu-item')]"));
							List<Integer> platosToSelect = stringArrayToInteger(opcionesMenu);
							espera(1000);
							log("Añado menú");
							for (int x=0;x<platosToSelect.size();x++) {
								log("- "+arrayPlatosMenu.get(platosToSelect.get(x)-1).findElement(By.xpath(".//div[contains(@class,'dish-name')]")).getAttribute("innerText"));
								clicJS(arrayPlatosMenu.get(platosToSelect.get(x)-1).findElement(By.xpath(".//app-input-number-spinner"))); 
								espera(1000);
							}

							clicJS(driver.findElements(By.xpath("//button[contains(@class,'basket-button')]")).get(1)); //CLIC EN AÑADIR A CARRITO

						}

						else if(isElementPresent(By.xpath("//div[contains(@class,'format-element-wrapper')]"))) { // SON FORMATOS
							List<WebElement> arrayFormatos = driver.findElements(By.xpath("//div[contains(@class,'format-element-wrapper')]"));
							if(arrayFormatos.size()>0) {
								clicJS(arrayFormatos.get(0));//CLIC EN PRIMER FORMATO / MODIFICADOR
								clicJS(driver.findElements(By.xpath("//button[contains(@class,'basket-button')]")).get(1)); //CLIC EN AÑADIR A CARRITO

							}
						}	

						else if (isElementPresent(By.xpath("//div[contains(@class,'dish-menu-item')]"))){ //SON MODIFICADORES
							List<WebElement> arrayModificadores = driver.findElements(By.xpath("//div[contains(@class,'dish-menu-item')]"));
							if(arrayModificadores.size()>0) {
								clicJS(arrayModificadores.get(0).findElement(By.tagName("app-input-number-spinner")));//CLIC EL PLUS DEL MODIFICADOR
								clicJS(driver.findElements(By.xpath("//button[contains(@class,'basket-button')]")).get(1)); //CLIC EN AÑADIR A CARRITO

							}
						}

						else {
							log("Menú de opciones abierto, falta detectar si son formatos, menus o modificadores. NO ENCAJA CON NADA CONOCIDO");
							Assert.assertTrue(false);
						}
					}
					
					espera(500);
					log("Producto " + currentItem.getNombre() + " encontrado y añadido a carrito");	
					productosEncontrados++;
				}

			}

			if(vueltas>20 || productosEncontrados==arrayNombres.length) {
				doScroll=false;
				if(vueltas>20) {
					log("No he encontrado todos los productos " + Arrays.toString(arrayNombres) + " productos encontrados "+productosEncontrados  +" vueltas " + vueltas);
					Assert.assertTrue(false);
				}
				if(productosEncontrados==arrayNombres.length)log("Todos los productos encontrados y añadidos al carrito " + Arrays.toString(arrayNombres));
			}

			JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
			javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);",elements.get(elements.size()-1));

			vueltas++;	 
		}  

		espera(1000);
		
		if(unidades.equalsIgnoreCase("")) { //SOLO VALIDO CARRITO FLOTANTE SI NO TRABAJAMOS CON UNIDADES MULTIPLES
			Assert.assertTrue(validaCarritoFlotante((Integer.toString(productosEncontrados)),totalEsperado));  	  
		}
	}

	private boolean validaCarritoFlotante(String productosEncontrados, String totalEsperado) {

		log("Validaciones de botón flotante de carrito");

		//VALIDAMOS UNIDADES TOTALES AÑADIDAS AL CARRITO Y VISIBLES EN EL BOTÓN FLOTANTE..
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-units')]")).getAttribute("innerText").equalsIgnoreCase(String.valueOf(productosEncontrados))) {
			log("- "+productosEncontrados + " productos añadidos");
		}else {
			log("- Error en validación de unidades añadidas al carrito -> Esperadas:" + productosEncontrados + " Visibles: "+ driver.findElement(By.xpath("//div[contains(@class,'basket-button-units')]")).getAttribute("innerText") );
			return(false);
		}

		//VALIDAMOS IMPORTE TOTAL VISIBLE DESDE EL BOTÓN DEL CARRITO FLOTANTE
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-amount')]")).getAttribute("innerText").equalsIgnoreCase(String.valueOf(totalEsperado))) {
			log("- "+totalEsperado + " total validado");
		}else {
			log("- Error en validación de total visible en carrito" );
			return(false);
		}

		if(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")).getAttribute("innerText").equalsIgnoreCase("")) {
			log("- Error en validación del string del botón flotante del carrito, está vacio (debería aparecer algo como Ver pedido)" );
			return(false);
		}

		return true;
	}


	public boolean contieneNombre(String[] arrayNombres, String nombre ) {

		for(int i = 0;i<arrayNombres.length;i++) {
			if (arrayNombres[i].equalsIgnoreCase(nombre))
				return true;
		}	
		return false;
	}

}
