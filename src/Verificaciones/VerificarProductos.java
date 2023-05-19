package Verificaciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Objects.ProductItem;
import graphql.Assert;
import utils.TestBase;

public class VerificarProductos extends TestBase {
	
	ArrayList<ProductItem> productItems;
	private static String ULTIMOPRODUCTO; //SE GAURDA EL NOMBRE DEL ÚLTIMO PRODUCTO DE LA CARTA PARA PODER HACER SCROLL HASTA LLEGAR ALLÍ.
	private static String articuloConFormatos,articuloConModificadores,articuloMenu;
	WebDriverWait w2;
	boolean soloConsultaBool;
	
  @Test(description="Este test valida productos de PortalRest teniendo en cuenta sus escenarios", priority=1, groups = {"checkProducts"})
  @Parameters({"soloConsulta", "familiasEsperadas","idioma","ultimoProducto","articuloConFormatos","articuloConModificadores","articuloMenu", "pedidosContratados"})
  public void verificaProductos(@Optional ("false") String soloConsulta,
			@Optional ("99") String familiasEsperadas, @Optional ("es") String idioma, String ultimoProducto, @Optional ("") String articuloConFormatos, @Optional ("") 
  			String articuloConModificadores, String articuloMenu, @Optional ("") String pedidosContratados) {

	  this.articuloConFormatos=articuloConFormatos;
	  this.articuloConModificadores = articuloConModificadores;
	  this.articuloMenu = articuloMenu;
	  ULTIMOPRODUCTO = ultimoProducto;
	  
	  w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[contains(@class,'familyItem')]")));	
	  List<WebElement> familias = driver.findElements(By.xpath("//li[contains(@class,'familyItem')]"));
	  
	  //ENTRO EN LA PRIMERA FAMILIA
      if (familias.size()>0) {
    	  WebElement nombreFamilia = familias.get(0).findElement(By.xpath("//div[contains(@class,'familyName')]"));
    	  log("Entro en primera familia" + nombreFamilia.getAttribute("innerText"));
    	  nombreFamilia.click();	  
      }
	  

	  log("Iteramos todos los productos de la carta en modo consulta= " + soloConsulta + " hasta que lleguemos al último producto " + ultimoProducto);
	 
	  if(!pedidosContratados.equalsIgnoreCase("")) {
		  w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'"+pedidosContratados+"')]")));
		  log("Se informa al usuario de que el restaurante no acepta pedidios");
		  clicJS(driver.findElement(By.xpath("//button[contains(@class,'btn-confirm')]")));
	  }
	  
	  espera();
      iterarProductos(ultimoProducto,soloConsulta);
      
  
  }
  
  private void iterarProductos(String ultimoProducto, String soloConsulta) {
	  
	  boolean doScroll=true;
   	  int vueltas=0;
      String familia = "";
      productItems = new ArrayList<ProductItem>();
     
	  
   	  while (doScroll) {
   		  
   		  espera(2000);
   		  w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//div[@class='dishItem']")));
	   	  List<WebElement> elements = driver.findElements(By.xpath("//span[contains(@class,'test-product-item')]"));

	   	  if (isElementPresent(By.xpath("//div[contains(@class,'family-title')]"))) {
	   	    familia = driver.findElement(By.xpath("//div[contains(@class,'family-title')]")).getAttribute("innerText");
	   	  }		

   		 for(int i=1;i<= elements.size();i++) { 
	   			      
   			 ProductItem currentItem = new ProductItem();	   		 
	   		 currentItem.setNombre(driver.findElement(By.xpath("(//div[contains(@class,'dish-name')])["+i+"]")).getAttribute("innerText"));
	   		 currentItem.setPrecio(driver.findElement(By.xpath("(//div[contains(@class,'dish-price')])["+i+"]")).getAttribute("innerText"));
	   		 currentItem.setFoto(driver.findElement(By.xpath("(//img[contains(@class,'dish-image')])["+i+"]")).getAttribute("src"));
	   		 currentItem.setDescripcion(driver.findElement(By.xpath("(//div[contains(@class,'dishInfo')])["+i+"]")).getAttribute("innerText"));
	   		 currentItem.setImagenElement(driver.findElement(By.xpath("(//div[contains(@class,'dish-image')])["+i+"]")));
	   		 if (soloConsulta.equalsIgnoreCase("true"))currentItem.setBoton(driver.findElement(By.xpath("(//button[contains(@class,'product-info')])["+i+"]")));
	   		 else currentItem.setBoton(driver.findElement(By.xpath("(//div[contains(@class,'product-item-add')])["+i+"]")));
	   		 currentItem.setFamilia(familia);
	   		
	   		 if (soloConsulta.equalsIgnoreCase("true")) {
	   			 //SE VERIFICA BOTÓN DE INFO
	   			 Assert.assertTrue(currentItem.getBoton().getAttribute("innerText").equalsIgnoreCase("+ info"));
	   		 }
	   		 
	   		 if (!productItems.contains(currentItem)) {
	   			 productItems.add(currentItem);
		   		 Assert.assertTrue(abreDetalleProducto(currentItem,soloConsulta.equalsIgnoreCase("true")));
	   		 }	   		 
	   	  }
   		 
   		 if (productItems.contains(new ProductItem(ULTIMOPRODUCTO))) {
   			 //YA TENGO TODAS LAS FAMILIAS RECORRIDAS. HE ENCONTRADO EL ÚLTIMO PRODUCTO
   			 doScroll=false;
   		 }
   		 else if(vueltas>10) {
   			 //NO DEBERÍA LLEVAR TANTAS VUELTAS ENCONTRAR EL ULTIMO ARTÍCULO, HA OCURRIDO UN ERROR
   			 doScroll=false;
   			 log("Error encontrando último artículo de la carta -> " + ULTIMOPRODUCTO);
   			 Assert.assertTrue(false);
   		 }else {	
   			 
   			try {
				JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
				javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);",elements.get(elements.size()-2));
			}catch(Exception e) {
				log("Warning. La lista de elementos ha sido invalidada y no podemos seguir haciendo scroll, se vuelve a capturar.");
				elements = driver.findElements(By.xpath("//span[contains(@class,'test-product-item')]"));
			}  			 
   		
   		 }
   		vueltas++;	 
   	  }
   	  
   	  System.out.println("Productos recuperados -> " + productItems.size());  	 
   	  log("Productos encontrados y guardados -> "+productItems.size());
  }
  
	//VALIDA QUE DENTRO DEL DETALLE DEL PRODUCTO SE VE TODA LA INFORMACIÓN CORRECTAMENTE.
	private boolean abreDetalleProducto(ProductItem currentItem, boolean soloConsulta) {

		//TODO DO - VERIFICAR DETALLE DE ARTÍCULOS MENU Y CON MODIFICADORES, LOS ELEMENTOS SON DISTINTOS.	
		WebElement imageElement,titleElement;
		WebElement descriptionElement,priceElement,ingredientesElement,caloriasElement,alergenosElement,complementosElement = null,productFormatsElement = null;
		List<WebElement> componentesMenuElement;
		espera(500);
		
		if(soloConsulta) { //EN MODO CONSULTA SE PULSA EN EL BOTON DE MAS INFO
			 try {
				    w.until(ExpectedConditions.elementToBeClickable(currentItem.getBoton())).click();
			     } catch (Exception e) {
			        JavascriptExecutor executor = (JavascriptExecutor) driver;
			        executor.executeScript("arguments[0].click();", currentItem.getBoton());
			     }
		}else { //EN MODO NORMAL SE PULSA SOBRE LA IMAGEN PARA ENTRAR EN DETALLE
				try {
				    w.until(ExpectedConditions.elementToBeClickable(currentItem.getImagenElement())).click();
			     } catch (Exception e) {
			        JavascriptExecutor executor = (JavascriptExecutor) driver;
			        executor.executeScript("arguments[0].click();", currentItem.getImagenElement());
			     }
		}	
	    
		log("Abrimos detalle de producto " + currentItem.getNombre() + " en modo consulta= " + soloConsulta );
		
	    if (soloConsulta && isElementPresent(By.xpath("//div[contains(@class,'product-info-basket-line')]"))) {
	    	log("El elemento del carrito no deberia estar visible");
	    	return false;
	    }
	    
	    if (isElementPresent(By.tagName("app-menu-dialog"))) { // ES UN MENÚ
	    	componentesMenuElement = driver.findElements(By.className("accordion-wrapper"));
	    	titleElement = driver.findElement(By.xpath("//div[contains(@class,'dialog-content')]//app-product-item-list//div[contains(@class,'dishWrapper')]//div[@class='dishItem']//div[contains(@class,'dish-name')]"));
	    	priceElement = driver.findElement(By.xpath("//div[contains(@class,'dialog-content')]//app-product-item-list//div[contains(@class,'dishWrapper')]//div[@class='dishItem']//div[contains(@class,'dish-price')]"));
	    	imageElement = driver.findElement(By.xpath("//div[contains(@class,'dialog-content')]//app-product-item-list//div[contains(@class,'dishWrapper')]//div[@class='dishItem']//div[contains(@class,'dish-image')]"));
	    	log("El artículo " + currentItem.getNombre() + " es un menú");
	    	//TODO VENDIENTE VALIDAR MÁS CAMPOS.
	    	
	    }else { //ES UN PRODUCTO NORMAL
	    	
	    	w.until(ExpectedConditions.presenceOfElementLocated (By.tagName("app-product-card-dialog")));	 
	 		w.until(ExpectedConditions.presenceOfElementLocated (By.className("product-image")));
	 		espera(500);
	 		
	 		titleElement = driver.findElement(By.className("product-info-name"));
	 		imageElement = driver.findElement(By.className("product-image"));
	 	    descriptionElement = driver.findElement(By.className("product-info-description"));
	 	    priceElement = driver.findElement(By.xpath("//div[contains(@class,'product-info-price')]"));
	 	    ingredientesElement = driver.findElements(By.xpath("//div[contains(@class,'product-info-ingredients')]")).get(0);//HAY DOS, UNO PARA INGREDIENTES Y OTRO PARA CALORIAS
	 	    caloriasElement = driver.findElements(By.xpath("//div[contains(@class,'product-info-ingredients')]")).get(1);//HAY DOS, UNO PARA INGREDIENTES Y OTRO PARA CALORIAS
	 	   
	 	    if(isElementPresent(By.xpath("//div[contains(@class,'product-info-allergens')]"))){
	 	    	alergenosElement = driver.findElements(By.xpath("//div[contains(@class,'product-info-allergens')]")).get(0);
	 	    }
	   
	 	    if (isElementPresent(By.xpath("//div[contains(@class,'product-info-allergens')]")) && driver.findElements(By.xpath("//div[contains(@class,'product-info-allergens')]")).size()==3) {
	 	    	complementosElement = driver.findElement(By.xpath("//div[contains(@class,'product-info-allergens')][2]//button"));
	 	    }
	 	    
	 	   if (isElementPresent(By.xpath("//div[contains(@class,'product-formats')]"))) {
	 	    	productFormatsElement = driver.findElement(By.xpath("//div[contains(@class,'product-formats')]"));
	 	    }
	    }
	    
	    
	    if(currentItem.getNombre().equalsIgnoreCase(VerificarProductos.articuloConFormatos))validarArticuloConFormatos(currentItem,VerificarProductos.articuloConFormatos,productFormatsElement);
	    else if(currentItem.getNombre().equalsIgnoreCase(VerificarProductos.articuloConModificadores))validarArticuloConModificadores(currentItem,VerificarProductos.articuloConModificadores,complementosElement);
	    else if(currentItem.getNombre().equalsIgnoreCase(VerificarProductos.articuloMenu))validarArticuloMenu(currentItem,VerificarProductos.articuloMenu);
	  
	    espera(500);
		WebElement back = driver.findElements(By.className("header-icon")).get(0);
		Actions actions = new Actions(driver);
		espera(500);
		actions.moveToElement(back).click().build().perform();
		espera(500);
		return true;
	}
	

	private void  validarArticuloConFormatos(ProductItem currentItem, String articuloConFormatos , WebElement element) {
		
		log("- Validando artículo con formatos " + articuloConFormatos);
		List<WebElement> formatLine = element.findElements(By.xpath("//div[contains(@class,'product-format-name')]"));
		
		if (formatLine.size()>0) {
			log("-- Tengo " + formatLine.size() + " formatos");
			for (int i=0;i<formatLine.size();i++) {
				log("--- "+formatLine.get(i).getAttribute("innerText"));
			}
		}else {
			log("No tengo formatos pintados en la ficha del producto");
			Assert.assertTrue(false);
		}
		

	}
	
	private void validarArticuloConModificadores(ProductItem currentItem, String articuloConModificadores, WebElement element) {
		log("- Validando artículo con modificadores " + articuloConModificadores);
	
		element.click();
		log("- Abriendo modal de modificadores");
		w.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-emerge-dialog")));
		log("- Modal de modificadores abierto, inicio de validación de cabecera");
		
		Assert.assertTrue(isElementPresent(By.xpath("//app-emerge-dialog//div[contains(@class,'product-item-wrapper')]//div[contains(@class,'dish-name')]"))&&
				isElementPresent(By.xpath("//app-emerge-dialog//div[contains(@class,'product-item-wrapper')]//div[contains(@class,'dish-price')]"))&&
				isElementPresent(By.xpath("//app-emerge-dialog//div[contains(@class,'product-item-wrapper')]//div[contains(@class,'dishInfo')]"))&&
				isElementPresent(By.xpath("//app-emerge-dialog//div[contains(@class,'product-item-wrapper')]//div[contains(@class,'dish-image')]")));
		
		log("- Validación de cabecera del modificador correcta ");
		log("- Inicio de validación de detalle de modificador");
		
		Assert.assertTrue(isElementPresent(By.xpath("//app-emerge-dialog//div[contains(@class,'formats-modifiers-wrapper')]")) &&
				isElementPresent(By.xpath("//app-emerge-dialog//div[contains(@class,'formats-modifiers-wrapper')]//div[@class='order-name']"))&&
				isElementPresent(By.xpath("//app-emerge-dialog//div[contains(@class,'formats-modifiers-wrapper')]//app-product-item-list")));
		
		List<WebElement> platos = driver.findElements(By.xpath("//app-emerge-dialog//div[contains(@class,'modifier-menu-dish-name')]"));
		
		for(int i=0;i<platos.size();i++) {
			log("-- "+platos.get(i).getAttribute("innerText"));
		}
		
		log("- Validación del detalle del modificador correcta");
		
		driver.findElement(By.xpath("//mat-icon[text() = 'close']")).click(); //CERRAMOS MODAL Y SEGUIMOS.
			
	}
	
	private void validarArticuloMenu(ProductItem currentItem, String articuloMenu ) {
		log("Validando artículo menú " + articuloMenu);
	}
}
