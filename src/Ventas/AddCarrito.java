package Ventas;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.model.Log;

import org.testng.AssertJUnit;

import static org.testng.Assert.ARRAY_MISMATCH_TEMPLATE;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Objects.ProductItem;
import dataProvider.StaticProvider;
import graphql.Assert;
import pedido.Formato;
import pedido.Product;
import utils.Data;
import utils.PortalRestOrderElements;
import utils.PortalRestOrderElements.Formatos;
import utils.TestBase;
import utils.Utils;

//ESTA FUNCION BUSCA UN PRODUCTO EN LA CARTA DE PORTALREST UTILIZANDO SU NOMBRE.
public class AddCarrito extends TestBase {
	String[] arrayNombres;
	String[] fOrderProducts;
	int productosEncontrados;
	
	ArrayList<Product> orderProductList = new ArrayList<Product>();
	Product order;
	
	List<Formato> articleformats = new ArrayList<Formato>();

	@Test (description="Este test busca un producto dado en el PortalRest actual" , priority=1, groups = { "carrito" })
	@Parameters({"productos","totalEsperado","opcionesMenu","unidades", "goBack", "firstOrderProducts", "goBackByAddOrderButton", "abrirFichaProducto", "formatos"})
	public void addCart(String productos, String totalEsperado, @Optional ("") String opcionesMenu, @Optional ("") String unidades,
						@Optional ("") String goBack, @Optional ("") String firstOrderProducts,  @Optional ("") String goBackByAddOrderButton, @Optional("false") boolean abrirFichaArticulo, @Optional("") String formatos) {
		arrayNombres = productos.split(",");
		
		ArrayList<ProductItem> productosAddeds = new ArrayList<ProductItem> ();
		
		//test 08/05/23
		 //this.getProductProvider(StaticProvider.ProductDataProvider.createProduct());
		 ArrayList<Product> productDataProvider = StaticProvider.ProductDataProvider.getProductProvider();
		
		//fin test 08/05/23
		
		//List<Formato> articleformats = new ArrayList<Formato>();
		
		if(goBack.equalsIgnoreCase("true")) {
			fOrderProducts = firstOrderProducts.split(",");			
		} else {			
			//w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(30));
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[contains(@class,'familyItem')]")));	
			List<WebElement> familias;
			productosEncontrados=0;
			//ArrayList<ProductItem> productosAddeds = new ArrayList<ProductItem> ();
			
			familias  = driver.findElements(By.xpath("//li[contains(@class,'familyItem')]"));
			//ENTRO EN LA PRIMERA FAMILIA
			if (familias.size()>0) {
				WebElement nombreFamilia = familias.get(0).findElement(By.xpath("//div[contains(@class,'familyName')]"));
				log("Entro en primera familia " + nombreFamilia.getAttribute("innerText"));
				espera(1000);
				clicJS(nombreFamilia);
				
			}
		}
		
		boolean doScroll=true;
		int vueltas=0;
		String familia = "";
		
		while (doScroll) {

			espera(1000);
			w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//div[@class='dishItem']")));

			List<WebElement> elements = driver.findElements(By.xpath("//span[contains(@class,'test-product-item')]"));
			if (isElementPresent(By.xpath("//div[contains(@class,'family-title')]"))) {
				familia = driver.findElement(By.xpath("//div[contains(@class,'family-title')]")).getAttribute("innerText");
			}		
			espera(1000);
			
			for(int i = 1; i <= elements.size(); i++) { 
				
				ProductItem currentItem = new ProductItem();
				String cantidadProducto = null;
				
				if(isElementPresent(By.xpath("//div[contains(@class,'dish-name')]//div"))) {
					currentItem.setNombre(driver.findElement(By.xpath("(//div[contains(@class,'dish-name')]//div)["+i+"]")).getAttribute("innerText"));	
				} else {
					currentItem.setNombre(driver.findElement(By.xpath("(//div[contains(@class,'dish-name')])["+i+"]")).getAttribute("innerText"));
				}
				
				//*Set precio del producto
				if(isElementPresent(By.xpath("//span[contains(concat(' ', normalize-space(@class), ' '), ' dish-price ')]"))) {
					String priceReal = driver.findElement(By.xpath("(//span[contains(concat(' ', normalize-space(@class), ' '), ' dish-price ')])["+i+"]")).getAttribute("innerText");
					currentItem.setPrecio(priceReal);	
					
					//log("El precio real del producto " + currentItem.getNombre() + " " + priceReal);
				}
				
				//***
				
				
				if (isElementPresent(By.xpath("(//div[contains(@class,'dish-image')])["+i+"]")))
					currentItem.setImagenElement(driver.findElement(By.xpath("(//div[contains(@class,'dish-image')])["+i+"]")));

				if(isElementPresent(By.xpath("(//button[contains(@class,'product-info')])["+i+"]"))) {
					currentItem.setBoton(driver.findElement(By.xpath("(//button[contains(@class,'product-info')])["+i+"]")));
				}
				else {
					currentItem.setBoton(driver.findElement(By.xpath("(//div[contains(@class,'product-item-add')])["+i+"]")));	
				}
				
				
				if(contieneNombre(arrayNombres,currentItem.getNombre()) && !productosAddeds.contains(currentItem) && (i<elements.size() || currentItem.getNombre().contains("MENÚ")))
				{
					if(abrirFichaArticulo) {
						addFromProductSheet(currentItem.getImagenElement(), currentItem.getNombre());
					} else {
						clicJS(currentItem.getBoton());
					}
					
					productosAddeds.add(currentItem);
					
					//Validamos si tiene formatos o modificadores
					espera(1000);
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
							
							//El menú tiene como cantidadProducto = "1"
							cantidadProducto = "1";

						}

						else if(isElementPresent(By.xpath("//div[contains(@class,'format-element-wrapper')]"))) { // SON FORMATOS
							List<WebElement> arrayFormatos = driver.findElements(By.xpath("//div[contains(@class,'format-element-wrapper')]"));
							if(arrayFormatos.size()>0) {
								//Añadir los formatos(Nombre y precio) en el producto
								log("Añadir los formatos(Nombre y precio) en el producto");
								
								espera(1500);
								Formatos formts = new PortalRestOrderElements.Formatos();
								
								articleformats = formts.getFormatList();
								
								articleformats.get(0).setSelected(true); // el formato seleccionado
								
								//Set precio por currentItem
									String precioDesde = driver.findElement(By.xpath("(//span[contains(concat(' ', normalize-space(@class), ' '), ' dish-price ')])["+i+"]")).getAttribute("innerText");
									log("el precio desde del producto con formatos: " + precioDesde);
									currentItem.setPrecio(precioDesde);
								//
								
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
					
					espera(2000);
					//String priceXpath ="//div[contains(@class,'dishItem')]//child::div[contains(@class, 'first-row')]//span[contains(@class, 'dish-price')]";
					// se puede usar //span[contains-token(@class, 'dish-price')] ???
					String priceXpath = "//span[contains(concat(' ', normalize-space(@class), ' '), ' dish-price ')]"; // seleccionamos la classe que corresponde con el nombre dado.
					
					//Set precio del producto
					if(isElementPresent(By.xpath(priceXpath)) && Utils.isNullOrEmpty(currentItem.getPrecio())) {
						//get(i-1) para posicionarse en el buen precio del producto si devuelve un precio falso de otro producto, por que for está iniciado a i = 1.
												
						String precio = driver.findElements(By.xpath(priceXpath)).get(i-1).getAttribute("innerText");
						
						currentItem.setPrecio(precio);
						log("precio del producto "+ currentItem.getNombre() + " --> " + precio);
					}
					
					//Set Cantidad
					String unidadXpath = "//div[contains(@class,'dishItem')]//div[contains(@class, 'product-item-add')]//child::app-input-number-spinner//div[contains(@class, 'number-spinner-value')]";
					if(isElementPresent(By.xpath(unidadXpath)) && Utils.isNullOrEmpty(cantidadProducto)) {
						log("Añadir la cantidad");
						List<WebElement> productUnit = driver.findElements(By.xpath(unidadXpath));
						if(productUnit.size() == 1) {
							cantidadProducto = productUnit.get(0).getAttribute("innerText");
						} 
						else if(productUnit.size() > 1) {
							cantidadProducto = productUnit.get(productUnit.size() -1).getAttribute("innerText");
						} else {
							log("Error cantidad 1 : No hemos podido añadir la cantidad de producto para:" + currentItem.getNombre());
							//Assert.assertTrue(false);
						}
					}
					else {
						if (Utils.isNullOrEmpty(cantidadProducto)) {
							log("Error cantidad 2: No hemos podido añadir la cantidad de producto para:" + currentItem.getNombre());
							extentTest.fail("Error cantidad 2: No hemos podido añadir la cantidad de producto para:" + currentItem.getNombre());
						}
					}
					
					//Save order product in list
					order = new Product();
					order.setNombre(currentItem.getNombre()) ;
					order.setPrecio(currentItem.getPrecio());
					if(StringUtils.isNumeric(cantidadProducto)) {
						order.setUnidad(cantidadProducto);
						log("unidad product del " + currentItem.getNombre() + "--> " + cantidadProducto);
					}
					//Calculo del precio total y guardarlo en order
					order.setPrecioPorUnidad();
					
					//Añadir y validar los formatos del producto
					if(!articleformats.isEmpty() && articleformats.size() > 0 ) {
						order.setFormatos(articleformats);
						//Actualizar  el precio del artículo por el precio del formato
						order.updatePriceProductByPriceFormat(articleformats);
						log("el formato seleccionado:" + order.getProductFormatSelected().getNombre() + " precio " + order.getProductFormatSelected().getPrecio());
						
						//Validar los formatos: Nombre y precio
						if(!Utils.isNullOrEmpty(formatos)) {
							String[] listFormatosAValidar = formatos.split(",")[1].split(";"); //Ejemplo de articulo con formatos: "Sake Kuramoto,JARRA 1/2 LITRO:2€;JARRA 1 LITRO:3€"
							for(int x = 0; x < listFormatosAValidar.length; x++) {
								if(!order.validateProductFormatName(listFormatosAValidar[x].split(":")[0])) {
									log("Bug: El nombre del formato " + listFormatosAValidar[x].split(":")[0] + " no es valido");
									extentTest.fail("Bug: El nombre del formato " + listFormatosAValidar[x].split(":")[0] + " no es valido");
									//getExtent().flush();									
								}
							}
						}
						
						articleformats.clear();
						
					} else {
						log("***El producto " + order.getNombre() +" no tiene formatos ");
					}
					
					orderProductList.add(order);
					
					//*****
					log("Producto " + currentItem.getNombre() + " encontrado y añadido al carrito con precio: " + currentItem.getPrecio());	
					
					log("*** Product order " + order.getNombre() + " encontrado y añadido al carrito con precio: " + order.getPrecio() + " unidad: " + order.getUnidad());
					
					if(order.getFormatos().size() > 0) {
						log("***El producto tiene formatos ");
						List<Formato> listFormatos = order.getFormatos();
						for(int l = 0; l < listFormatos.size(); l++) {
							log("*** Formato " + listFormatos.get(l).getNombre() + " Precio " + listFormatos.get(l).getPrecio());
						}
					}
					
					productosEncontrados++;	
					
					//**
					order = null;
					
					//Testear si tenemos todos los artículos añadidos en el carrito
					if(productosEncontrados==arrayNombres.length) {
						break;
					}
				}

			}

			if(vueltas>20 || productosEncontrados==arrayNombres.length) {
				doScroll=false;
				if(vueltas>20) {
					log("No he encontrado todos los productos " + Arrays.toString(arrayNombres) + " productos encontrados "+productosEncontrados  +" vueltas " + vueltas);
					Assert.assertTrue(false);
				}
				
				if(productosEncontrados==arrayNombres.length) {
					log("Todos los productos encontrados y añadidos al carrito " + Arrays.toString(arrayNombres));
					
					//Save order products in Data for using it  later to  validation order
					Data.getInstance().getPedido().setProduct(orderProductList);
					log("El precio total del pedido con todos los prodcutos en la cesta  --> " + Data.getInstance().getPedido().getPrecioTotal());

				}
			}
			
			espera(500);
			
			try {
				
				JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
				javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);",elements.get(elements.size()-1));
				
			} catch(Exception e) {
				
				log("Warning. La lista de los elementos ha sido invalidada y no podemos seguir haciendo scroll, se vuelve a capturar.");
				elements = driver.findElements(By.xpath("//span[contains(@class,'test-product-item')]"));
			}

			vueltas++;	 
		}  

		espera(1000);
		
		if(unidades.equalsIgnoreCase("")) { //SOLO VALIDO CARRITO FLOTANTE SI NO TRABAJAMOS CON UNIDADES MULTIPLES
			
			if(goBack.equalsIgnoreCase("true")) {
				productosEncontrados += fOrderProducts.length;
			}
			
			if(goBackByAddOrderButton.equalsIgnoreCase("true") && goBack.equalsIgnoreCase("false")) {
				fOrderProducts = firstOrderProducts.split(",");
				productosEncontrados += fOrderProducts.length;
			} 
			
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

		for(int i = 0; i < arrayNombres.length; i++) {
			//if (arrayNombres[i].equalsIgnoreCase(nombre))
			if (arrayNombres[i].equals(nombre)) {
				log("arrayNombres[i].equals(nombre) ---> " + nombre);
				return true;
			}
		}	
		return false;
	}
	
	//Update: 09/05/2023, para la factorizacion del codigo
	public boolean contieneNombre(ArrayList<Product> arrayNombres, String nombre ) {
		for(int i = 0; i < arrayNombres.size(); i++) {
			if(arrayNombres.get(i).getNombre().equals(nombre)) {
				return true;
			}
		}
		return false;
	} //Fin Update: 09/05/2023, para la factorizacion del codigo
	
	public void abrirFichaProducto(WebElement elementProducto) {
		clicJS(elementProducto);
		espera(500);
	}
	
	//Añadir artículo desde de la ficha del producto
	//** Abrir ficha producto
	public void addFromProductSheet(WebElement elementProducto, String productName) {
		
		abrirFichaProducto(elementProducto);
		
		String sProductInfoWrapper = "//div[contains(@class, 'product-info-wrapper-scrollable')]";
		
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(sProductInfoWrapper)));
		
		if(!driver.findElement(By.xpath(sProductInfoWrapper)).getAttribute("textContent").contains(productName)) {
			String errorMensaje = "Errror: no se ha encontrado el producto " + productName + " en la ficha abierta";
			log(errorMensaje);
			Assert.assertTrue(false);
		}
		
		String sButtonAdd = "//app-basket-button//button[contains(@class, 'main-btn basket-button')]";
		String sButtonAddLabel = "+ Añadir al pedido";
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(sButtonAdd)));
		
		if(!isElementPresent(By.xpath(sButtonAdd))) {
			String errorMensaje = "Errror: no se ha encontrado el button  " + sButtonAddLabel + " en la ficha abierta para añadir el artículo a la cesta";
			log(errorMensaje);
			Assert.assertTrue(false);
		}
		
		if(driver.findElements(By.xpath(sButtonAdd)).size() > 0) {
			List<WebElement> elmeBtn = driver.findElements(By.xpath(sButtonAdd));
			elmeBtn.forEach(btn -> {
				if(btn.getText().contains(sButtonAddLabel)) {
					clicJS(btn);  //CLIC EN AÑADIR A CARRITO
				}
			});
		} else {
			clicJS(driver.findElement(By.xpath(sButtonAdd))); //CLIC EN AÑADIR A CARRITO
		}
		
		log("Producto" + productName + " añadido desde la ficha");
	}
	
}
