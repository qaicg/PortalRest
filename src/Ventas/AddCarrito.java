package Ventas;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Assert;

import org.testng.AssertJUnit;

import static org.testng.Assert.ARRAY_MISMATCH_TEMPLATE;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.LogManager;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
import pedido.Formato;
import pedido.Modificador;
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
	
	//Listado de formatos que son combinados con modificadore(s) definido desde el parametro del test
	List<Formato> formatoCombinadoList = new ArrayList<Formato>(); //Los formatos combinados esperados en el test
	
	List<Formato> formatoEsperadoList = new ArrayList<Formato>(); //Los formatos esperados en el test 
	
	List<Product> articuloConFormatoEsperadoList = new ArrayList<Product>(); //Productos esperados en el test
	
	boolean isFormatoSelectdDefined = false; // permite saber si hay un formato a seleccionar en la lista de formatos

	@Test (description="Este test busca un producto dado en el PortalRest actual" , priority=1, groups = { "carrito" })
	@Parameters({"productos","totalEsperado","opcionesMenu","unidades", "goBack", "firstOrderProducts", "goBackByAddOrderButton", "abrirFichaProducto", "formatos"})
	public void addCart(String productos, String totalEsperado, @Optional ("") String opcionesMenu, @Optional ("") String unidades,
						@Optional ("") String goBack, @Optional ("") String firstOrderProducts,  @Optional ("") String goBackByAddOrderButton,
						@Optional("false") boolean abrirFichaArticulo, @Optional("") String formatos) {
		arrayNombres = productos.split(",");
		
		ArrayList<ProductItem> productosAddeds = new ArrayList<ProductItem> ();
		
		ArrayList<Product> productDataProvider = StaticProvider.ProductDataProvider.getProductProvider();
				 
		List<Integer> platosToSelect = new ArrayList<Integer>();
		List<WebElement> arrayPlatosMenu = new ArrayList<WebElement>();
		
		if(goBack.equalsIgnoreCase("true")) {
			fOrderProducts = firstOrderProducts.split(",");			
		} else {			
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[contains(@class,'familyItem')]")));	
			List<WebElement> familias;
			productosEncontrados=0;
			
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
						espera(1000);
						clicJS(currentItem.getBoton());
						espera(1000);
						
						//Test si se habre la fiche del artículo: 16/10/2023
						String articleInfosXpath = "//div[contains(@class, 'product-info-wrapper-scrollable')]";
						
						if(isElementPresent(By.className("product-info-wrapper-scrollable"))) {
							addFromProductSheet(currentItem.getNombre());
						}
						else {
							log("Se ha añadido el producto " +  currentItem.getNombre() + " con exíto.");
						}
						//							
						
					}
					
					productosAddeds.add(currentItem);
					
					//Validamos si tiene formatos o modificadores
					espera(1000);
					if (isElementPresent(By.tagName("mat-dialog-container"))) {
						espera(1000);
						if (isElementPresent(By.tagName("app-menu-dialog"))) { // ES UN MENÚ
							//EL OBJETIVO ES CLICAR LAS PRIMERAS OPCIONES DE TODOS LOS ORDENES.
							arrayPlatosMenu = driver.findElements(By.xpath("//app-menu-dialog//div[contains(@class,'dish-menu-item')]"));
							platosToSelect = stringArrayToInteger(opcionesMenu);
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
						else if(isElementPresent(By.xpath("//div[contains(@class,'format-element-wrapper')]"))) { // SON FORMATOS(Simples o combinados con modificadores)
							List<WebElement> arrayFormatos = driver.findElements(By.xpath("//div[contains(@class,'format-element-wrapper')]"));
							if(arrayFormatos.size()>0) {
								//Añadir los formatos(Nombre y precio) en el producto
								log("Añadir los formatos(Nombre y precio) en el producto");
								
								espera(1500);
								Formatos formts = new PortalRestOrderElements.Formatos();
																
								articleformats = formts.getFormatList();
								
								//Validar la lista de formatos encontrados si son los esperados.
								//Validar los formatos
								//Validar los formatos esperados del artículo
								log("Validar los formatos esperados del artículo " + currentItem.getNombre());
								WebElement formatoSelectedByWebElement;
								articuloConFormatoEsperadoList.stream().forEach(p -> {
									if(p.getNombre().equals(currentItem.getNombre())) {
										formts.validarFormatosEsperados(p);
									}
								});
																
								/*
								 * Seleccionar el formato esperado en la lista si ya está definido sino elegir el primer de la lista
								 */
								WebElement formatoElementSelected = null;
								Formato formatoSelected = new Formato();
								espera(1000);
								for(Product product: articuloConFormatoEsperadoList) {
									if(!isFormatoSelectdDefined)
										break;
									
									if(product.getNombre().equals(currentItem.getNombre())) {
										
										Formato formatoSelectdDelProducto = product.getFormatos().stream() //Obterner el formato definido a seleccionar
										.filter(formato -> formato.isSelected())
										.findFirst()
										.get();
										
										//Buscar el formato a seleccionar 
										formatoElementSelected = formts.getFormatoElementByNameAndPrice(formatoSelectdDelProducto.getNombre(), formatoSelectdDelProducto.getPrecio());
										formatoSelected = formts.getFormatoByNameAndPrice(formatoSelectdDelProducto.getNombre(), formatoSelectdDelProducto.getPrecio());
										driver.findElement(By.xpath("//div[contains(@class,'format-element-name') and text()='"+formatoSelected.getNombre()+"']")).click();//#Oscar
										//Añadimos un clic al formato porque a veces no queda seleccionado por defecto.
										break;
									}
									
								}
								
								if(Objects.isNull(formatoElementSelected) && isFormatoSelectdDefined) {
									log("Error: No se ha encontro el formato a seleccionar");									
									Assert.assertTrue(false);
								}
								else {
									int z = 0;
									boolean formatoSelectedFind = false;
									
									if(!isFormatoSelectdDefined && articuloConFormatoEsperadoList.size() >= 1) {
										for(Product currentProduct: articuloConFormatoEsperadoList) {
											if(currentProduct.getNombre().equals(currentItem.getNombre())) {
												currentProduct.setformatoSelectPorDefecto();//Seleccionar el formato 0 de la lista de formatos
												
												formatoSelected = currentProduct.getProductFormatSelected();
												formatoElementSelected = formts.getFormatoElementByNameAndPrice(formatoSelected.getNombre(), formatoSelected.getPrecio());
												formatoSelectedFind = true;
												break;
											}
										}
									}									
									else if(!isFormatoSelectdDefined && articuloConFormatoEsperadoList.size() == 0) {
										articleformats.get(0).setSelected(true);
										formatoSelected = articleformats.get(0);
										formatoElementSelected = formts.getFormatoElementByNameAndPrice(formatoSelected.getNombre(), formatoSelected.getPrecio());
										formatoSelectedFind = true;
									}
									
									if(Objects.isNull(formatoSelected) && Objects.isNull(formatoElementSelected)) {
										log("Error: No hemos podido encontrar el formato a seleccionar en la busqueda");
										Assert.assertTrue(false);
									}

								}
								/*
								 * Fin Seleccionar el formato esperado en la lista si ya está definido sino elegir el primer de la lista
								 */
								
																								
								//Formato combinado con modificador
								//Compruebe que el formato consta de modificador.
								Formato formatoCombinado = new Formato();
								Modificador modificadorSelected = new Modificador();
								
								if(formatoCombinadoList.size() > 0) {//Formatos combinados
									log("Tenemos formatos combinados: " + formatoCombinadoList.size());
									//Formato formatoCombinado;
									
									for(Formato frm: formatoCombinadoList) {
										log("Nombre del formato 1 -> " + frm.getNombre());
										
										log("Nombre del formato 2 -> " + articleformats.get(0).getNombre());
										
										if(frm.getNombre().contains(articleformats.get(0).getNombre())) {
											log("Nombre del formato -> Compruebe que el formato consta de modificador " + articleformats.get(0).getNombre());
											formatoCombinado = frm;
											formatoCombinado.setSelected(true);
											break;
											
										}
										else {
											log("Error: No se ha encontrada el formato combinado " + articleformats.get(0).getNombre());
											Assert.assertTrue(false);
										}
										//Seleccionar el formato combinado
										espera(500);
										clicJS(formatoElementSelected);//CLIC EN PRIMER FORMATO / MODIFICADOR
										
										espera(500);
									}
									
									if(Objects.nonNull(formatoCombinado)) {
										//Modificador modificadorSelected
										for(Modificador mdf: formatoCombinado.getModificadorList()) {
											if(mdf.isSelected()) {
												modificadorSelected = mdf;
												log("Tenemos el modificador a seleccionar");
												break;
											}
										}
										
										Assert.assertTrue(Objects.nonNull(modificadorSelected), "Error: no hemos podido encontrar el modificador combinado al formato del artículo");
										
										//Validar el modificar a seleccionar en el formato combinado
										testAddDeleteModificadorCombinadoAlFormato(formts,  formatoCombinado, modificadorSelected);
										
										//Añadir el modificador del formato
										addtModificadorCombinadoAlFormato(formts,  formatoCombinado, modificadorSelected);
										

									}
									else {
										log("Error: No Se ha encontrado modificador combinado al formato del artículo para seleccionarlo");
										Assert.assertTrue(false);
									}
								}
								else {
									log("No hay formatos combinados");
									//log("Testear que se puede seleccionar los formatos")
									
									clicJS(formatoElementSelected);//CLIC EN PRIMER FORMATO / MODIFICADOR
								}
								//Fin formato con modificador
																
								//Set precio por currentItem
									String precioDesde = driver.findElement(By.xpath("(//span[contains(concat(' ', normalize-space(@class), ' '), ' dish-price ')])["+i+"]")).getAttribute("innerText");
									log("el precio desde del producto con formatos: " + precioDesde);
									currentItem.setPrecio(precioDesde);
									espera(1000);
								 
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
										
					if(Utils.isNullOrEmpty(cantidadProducto)) {
						getElementByFluentWait(By.xpath(unidadXpath), 50, 5);
						//waitUntilPresence(unidadXpath, true);
					}
					
					if(isElementPresent(By.xpath(unidadXpath)) && Utils.isNullOrEmpty(cantidadProducto)) {
						log("Añadir la cantidad");
						List<WebElement> productUnit = getElementsByFluentWait(By.xpath(unidadXpath), 30, 5);//driver.findElements(By.xpath(unidadXpath));
						if(productUnit.size() == 1) {
							cantidadProducto = productUnit.get(0).getAttribute("innerText");
						} 
						else if(productUnit.size() > 1) {
							cantidadProducto = productUnit.get(productUnit.size() -1).getAttribute("innerText");
						} else {
							log("Error cantidad 1 : No hemos podido añadir la cantidad de producto para:" + currentItem.getNombre());
							Data.getInstance().getExtentTest().warning("Error cantidad 1 : No hemos podido añadir la cantidad de producto para:" + currentItem.getNombre());
							//Assert.assertTrue(false);
						}
					}
					else {
						if (Utils.isNullOrEmpty(cantidadProducto)) {
							log("Error cantidad 2: No hemos podido añadir la cantidad de producto para:" + currentItem.getNombre());
							Data.getInstance().getExtentTest().fail("Error cantidad 2: No hemos podido añadir la cantidad de producto para:" + currentItem.getNombre());
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
						order.setProductFormatSelected();
						//Actualizar  el precio del artículo por el precio del formato
						order.updatePriceProductByPriceFormat(articleformats);
						log("el formato seleccionado: " + order.getProductFormatSelected().getNombre() + " precio " + order.getProductFormatSelected().getPrecio());
						
						//Validar los formatos: Nombre y precio
						if(!Utils.isNullOrEmpty(formatos)) {
							String[] listFormatosAValidar = formatos.split("\\*")[1].split(";");
							
							for(int x = 0; x < listFormatosAValidar.length; x++) {
								if(!order.validateProductFormatName(listFormatosAValidar[x].split(":")[0])) {
									log("Bug: El nombre del formato " + listFormatosAValidar[x].split(":")[0] + " no es valido");
									Data.getInstance().getExtentTest().fail("Bug: El nombre del formato " + listFormatosAValidar[x].split(":")[0] + " no es valido");
									//getExtent().flush();									
								}
							}
														
						}
						
						articleformats.clear();//limpiar los formatos del artículo
						
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
					log("Total unidades muestrado en el boton ver pedido / pedir ahora y añadido en la cesta  --> " + Data.getInstance().getPedido().getTotalUnidades());

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
			
			Assert.assertTrue(validaCarritoFlotante((Integer.toString(productosEncontrados)),totalEsperado),"Error validando total del carrito flotante."); 
		}
		
	}

	private boolean validaCarritoFlotante(String productosEncontrados, String totalEsperado) {

		log("Validaciones de botón flotante de carrito");

		//VALIDAMOS UNIDADES TOTALES AÑADIDAS AL CARRITO Y VISIBLES EN EL BOTÓN FLOTANTE..
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-units')]")).getText().equalsIgnoreCase(productosEncontrados)) {
			log("- "+productosEncontrados + " productos añadidos");
		}else {
			log("- Error en validación de unidades añadidas al carrito -> Esperadas:" + productosEncontrados + " Visibles: "+ driver.findElement(By.xpath("//div[contains(@class,'basket-button-units')]")).getAttribute("innerText") );
			return(false);
		}

		//VALIDAMOS IMPORTE TOTAL VISIBLE DESDE EL BOTÓN DEL CARRITO FLOTANTE
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-amount')]")).getText().equalsIgnoreCase(totalEsperado)) {
			log("- "+totalEsperado + " total validado");
		}else {
			log("- Error en validación de total visible en carrito" );
			return(false);
		}

		if(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")).getText().equalsIgnoreCase("")) {
			log("- Error en validación del string del botón flotante del carrito, está vacio (debería aparecer algo como Ver pedido)" );
			return(false);
		}

		return true;
	}

	public boolean contieneNombre(String[] arrayNombres, String nombre ) {

		for(int i = 0; i < arrayNombres.length; i++) {
			if (arrayNombres[i].equals(nombre)) {
				//log("arrayNombres[i].equals(nombre) ---> " + nombre);
				return true;
			}
		}	
		return false;
	}
	
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
	
	//Añadir artículo desde de la ficha del producto
	//** Abrir ficha producto
	public void addFromProductSheet(String productName) {
				
		String sProductInfoWrapper = "//div[contains(@class, 'product-info-wrapper-scrollable')]";
		
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(sProductInfoWrapper)));
		
		if(!driver.findElement(By.xpath(sProductInfoWrapper)).getAttribute("textContent").contains(productName)) {
			String errorMensaje = "Errror: no se ha encontrado el producto " + productName + " en la ficha abierta";
			log(errorMensaje);
			Assert.assertTrue(false);
		}
		
		String sButtonAdd = "//app-basket-button//button[contains(@class, 'main-btn basket-button')]";
		String sButtonAddLabel1 = "+ Añadir al pedido";
		String sButtonAddLabel2 = "Aceptar"; //cuando ya se abre la ficha con el producto añadido en la cesta. Falta solo aceptar.
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(sButtonAdd)));
		
		if(!isElementPresent(By.xpath(sButtonAdd))) {
			String errorMensaje = "Errror: no se ha encontrado el button  " + sButtonAddLabel1 + "/" + sButtonAddLabel2 + " en la ficha abierta para añadir el artículo a la cesta";
			log(errorMensaje);
			Assert.assertTrue(false);
		}
		
		if(driver.findElements(By.xpath(sButtonAdd)).size() > 0) {
			List<WebElement> elmeBtn = driver.findElements(By.xpath(sButtonAdd));
			elmeBtn.forEach(btn -> {
				if(btn.getText().contains(sButtonAddLabel1) || btn.getText().contains(sButtonAddLabel2)) {
					clicJS(btn);  //CLIC EN AÑADIR A CARRITO
				}
			});
		} else {
			clicJS(driver.findElement(By.xpath(sButtonAdd))); //CLIC EN AÑADIR A CARRITO
		}
		
	}

	
	public void addFormato () {
		
	}
	
	public void deleteFormato () {
		
	}
	
	public void selectFormatoCombinadoAlProducto () {
		
	}
	
	//Testear que se puede añadir y eliminar los modificadores de formatos combinados
	public void testAddDeleteModificadorCombinadoAlFormato (Formatos formato, Formato formatoCombinado, Modificador modificadorSelected) {
		addtModificadorCombinadoAlFormato(formato, formatoCombinado, modificadorSelected);
		deleteModificadorCombinadoAlFormato(formato, formatoCombinado, modificadorSelected);
	}
	
	//Añadir formato combinado
	public void addtModificadorCombinadoAlFormato (Formatos formato, Formato formatoCombinado, Modificador modificadorSelected) {
		Assert.assertTrue(Objects.nonNull(modificadorSelected), "Error: no hemos podido encontrar el modificador combinado al formato del artículo");
		
		//Validar el modificar a seleccionar en el formato combinado
		String xpathElementAddModificadorSelected = formato.getXpathAddFormatoCombinado(modificadorSelected.getNombre());
		Assert.assertTrue(isElementPresent(By.xpath(xpathElementAddModificadorSelected)), "Error: El modificador "+ formatoCombinado.getNombre() + " del formato combinado no existe(No aparece) en el listado mofificadores");
		
		WebElement modificadorSelectedElement = getElementByFluentWait(By.xpath(xpathElementAddModificadorSelected), 30, 5);	
		
		clicJS(modificadorSelectedElement);
		
		espera(500);
		
		//Verrificar que el botón añadir no está 
		Assert.assertTrue(!modificadorSelectedElement.getAttribute("innerText").equalsIgnoreCase("+\n1"), "Error: El botón añadir(+) del formato combinado "+ formatoCombinado.getNombre() + " no deberia aparecer!!!");

	}
	
	//Suprimir formato combinado
	public void deleteModificadorCombinadoAlFormato (Formatos formato, Formato formatoCombinado, Modificador modificadorSelected ) {
		Assert.assertTrue(Objects.nonNull(modificadorSelected), "Error: no hemos podido encontrar el modificador combinado al formato del artículo");
		
		//Validar el modificar a seleccionar en el formato combinado
		String xpathElementDeleteModificadorSelected = formato.getXpathDeleteFormatoCopmbinado(modificadorSelected.getNombre());
		
		Assert.assertTrue(isElementPresent(By.xpath(xpathElementDeleteModificadorSelected)), "Error: El modificador "+ formatoCombinado.getNombre() + " del formato combinado no existe(No aparece) en el listado mofificadores");
		
		WebElement deleteModificadorSelectedElement = getElementByFluentWait(By.xpath(xpathElementDeleteModificadorSelected), 30, 5);										

		clicJS(deleteModificadorSelectedElement);
		espera(500);	
		
		//Verificar que el botón eliminar ha desaparecido dejando el botón añadir
		String xpathElementAddModificadorSelected = formato.getXpathAddFormatoCombinado(modificadorSelected.getNombre());
		Assert.assertTrue(isElementPresent(By.xpath(xpathElementAddModificadorSelected)), "Error: El modificador "+ formatoCombinado.getNombre() + " del formato combinado no existe(No aparece) en el listado mofificadores");
		
	}
	
	//Agregación de artículo con formato simple o combinado con modificadores
	@BeforeTest
	@Parameters({"formatoCombinado", "modificadorSelected", "formatoSelected", "formatos"})
	public void definirFormato(@Optional("") String formatoCombinado, @Optional("") String modificadorSelected, @Optional("") String formatoSelected, String formatos) {
		
		this.isFormatoSelectdDefined = StringUtils.isAllEmpty(formatoSelected) ? false : true;
		
		List<Product> articuloConFormatoList = new ArrayList<Product>();
		List<Formato> formatoList = new ArrayList<Formato>();
		Product producto;
		Formato formato;
				
		if(!Utils.isNullOrEmpty(formatos)) {
			
			log("Tenemos " + StringUtils.split(formatos, "\\").length + "  producto(s)");
			
			log("Tenemos " + StringUtils.split(formatos, "*").length + " formato(s)");

			if(StringUtils.split(formatos, "\\").length >= 1) {
				String[] productoListString = StringUtils.split(formatos, "\\");
				
				for(int i= 0; i < StringUtils.split(formatos, "\\").length; i++) {
					log("El producto " + StringUtils.split(productoListString[i], "*")[0] + " tiene formatos");
					producto = new Product( StringUtils.split(productoListString[i], "*")[0]);
					
					String[] formatoListString = StringUtils.split(StringUtils.split(productoListString[i], "*")[1], ";");
					for(int x=0; x < formatoListString.length; x++) {
						String formatoName = formatoListString[x].split(":")[0];
						String formatoPrice = formatoListString[x].split(":")[1];
						
						log("Formato + nombre y precio " + formatoName + formatoPrice);
						
						formato = new Formato(formatoName, formatoPrice);
						
						if(formato.getNombre().equals(formatoSelected)) {
							formato.setSelected(true);
							Assert.assertTrue(formato.isSelected(), "Error: El formato no se ha podido elegir como formato a seleccionar!!!");
						}
						else {
							formato.setSelected(false);
						}
						
						formatoList.add(formato);
					}
					
					if(formatoList.size() > 0) { //Añadir los formatos al producto
						//Definir el primero formato como formato a seleccionar si el parametro formatoSelected es null
						if(StringUtils.isAllEmpty(productoListString)) {
							formatoList.get(0).setSelected(false);
							Assert.assertTrue(formatoList.get(0).isSelected(), "Error: El formato no se ha podido elegir como formato a seleccionar!!!");
						}
						
						if(!isFormatoSelectdDefined && !Utils.isNullOrEmpty(formatos)) {
							formatoList.get(0).setSelected(true);
						}
						
						producto.setFormatos(formatoList);

					}
					
					producto.setProductFormatSelected();
					if(producto.getFormatos().size() > 0) {
						log("Formatos " + producto.getFormatos().size());
						log(" formato Selected");
					}
					
					articuloConFormatoList.add(producto); //Lista de artículo con formatos
					
					articuloConFormatoEsperadoList = articuloConFormatoList;
				}
			}
			else {
				log("No hay formato definido en parametro del test");
			}
			
			if(!Utils.isNullOrEmpty(formatoCombinado))//Artículo en Formato combinado con modificadore
				addProductEnFormatoCombinado(formatoCombinado, modificadorSelected);
			else {
				log("No hay artículo con formato combinado en los parametros del Test");
			}
						
		}

	}
	
	//Verificar si  en el test hay producto con formato que tiene modificadores combinados
	public void addProductEnFormatoCombinado(@Optional("") String formatoCombinado, @Optional("") String modificadorSelected) {

		if(Utils.isNullOrEmpty(formatoCombinado)) { //Veriricar que hay formato combinado en el artículo
			return;
		}
		
		Assert.assertTrue(!Utils.isNullOrEmpty(modificadorSelected), "Se solicita definir el paramaetro del modificador del formato de artículo a seleccionar ");
		
		if(formatoCombinado.split(";").length < 2) { //Verificar que hay solo un formato combinado definido en el test
			definirFormatoCombinado(formatoCombinado, modificadorSelected);
		}
		else if(formatoCombinado.split(";").length >= 2) {//Hay varios formatos combinados con modificadores en el test
			definirmultipleFormatoCombinado(formatoCombinado, modificadorSelected);
		}
		
	}
	
	//Definir un formato combinado con modificador
	public void definirFormatoCombinado(@Optional("") String formatoCombinado, @Optional("") String modificadorSelected) {
		Formato objectFormato;
		Modificador modificador;
		
		Assert.assertTrue(formatoCombinado.split("\\{").length > 1,"No hay modificadores cambinados al formato definido en parametro! Se solicita definir bien el formato combinado en el parametro del test ");
		
		String nuevoFormato = formatoCombinado.split("\\{")[0];
		objectFormato = new Formato(nuevoFormato);
		
		String[] modificadorList =  formatoCombinado.split("\\{")[1].split("\\}")[0].split(",");
		
		for(int i=0; i < modificadorList.length; i++) {
			
			modificador = new Modificador(modificadorList[i]);
			
			if(modificador.getNombre().equals(modificadorSelected)) {
				modificador.setSelected(true);
			}
			else {
				modificador.setSelected(false);
			}
			
			objectFormato.setModificadorList(modificador);
		}
		
		formatoCombinadoList.add(objectFormato);//Listado de formatos que son combinados con modificadore(s) definido desde el parametro del test

	}
	
	//Tenemos article con varios formatos combidados con modificadores
	public void definirmultipleFormatoCombinado(@Optional("") String formatoCombinado, @Optional("") String modificadorSelected) {
		//TODO: implementar cuando tenemso que seleccionar multiple formatos combinados para un artículo 
		Assert.assertTrue(false, "Error: no se puede tratar la selección de multiple formatos combinados");
	}
		
}
