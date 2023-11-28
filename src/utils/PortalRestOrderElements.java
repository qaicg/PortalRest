package utils;

import static org.testng.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;

import pedido.Formato;
import pedido.Product;

public class PortalRestOrderElements extends TestBase {
	
	//**Pantalla ¿CÓMO? en pedido PortalRest **/
	public static final String paginalPrincipalHow = "//app-how-when";
	public static final String listaBotonesComoXpath = "//div[contains(@class, 'how-when-button-list')]//button[contains(@class, 'how-when-button')]";
	public static final String literalListaBotonesComoXpath = "//div[contains(@class, 'how-when-button-list')]//button[contains(@class, 'how-when-button')]//div[contains(@class, 'how-when-button-wrapper')]";
	public static final String[] listaLiteralBotonesComoText = {"En el local", "Para llevar", "A domicilio", "Auto", "En la mesa"};
	
	/*
	 * Lista de restaurante del cliente 
	 * 
	 */
	public static class  ListaRestaurante {
		//página principal del listado restaurantes del cliente
		public static final String paginalPrincipalShopList =  "//app-shop-list";
		
		public static final String botonMenuXpath = "//div[contains(@class, 'header-icon')]//mat-icon";
		
		public static final String buttonCuisineTypesXpath = "//div[contains(@class, 'list')]//div[contains(@class, 'cuisine-types-wrapper')]";
		
		public static final String listaTipoCosinaCheckBoxXpath = "//app-cuisine-filters//div[contains(@class, 'filters-wrapper')]//mat-checkbox";
		
		public static final String buttonAccpetSelectCuisineTypesXpath = "//div[contains(@class, 'accept-button')]//button[contains(@class, 'basket-button')]";
		
		//mostra el nombre 
		public static final String literalTipoCosinaXpath = "//app-cuisine-filters//div[contains(@class, 'filters-wrapper')]//mat-checkbox//label//span[contains(@class, 'mat-checkbox-label')]";
		
		
		public static final String showMenuListXpath =  "//button[contains(@class, 'mat-menu-item')]";
		
		public static final String showListShopWrapperXpath = "//div[contains(@class, 'list-shop-wrapper')]"; //Página principal del listado de resuatante del cliente
		
		//Los botones de los restaurantes
		public static final String botonesRestauranteXpath = "//div[contains(@class, 'list-shop-element-wrapper')]";
		
		//El nombre del restaurante a seleccionar en el listado restaurantes:
		public static final String getNameShopSelected (String shopeNameSelected) {
			return  "//div[contains(@class, 'second-row')]//div[contains(@class, 'shop-data-name') and contains(text(), '"+ shopeNameSelected + "')]";
		}
		
		//Lista restaunrantes que tiene el cliente
		public static final String shopNameListXpath = "//div[contains(@class, 'second-row')]//div[contains(@class, 'shop-data-name')]";
		
		
	}
	
	public static class PedidoDiaHora {		
		
		public static final String horaDisponible = "//mat-icon[contains(text(), 'menu')]";
		public static final String diaSeleccionado = "//mat-calendar/div/mat-month-view/table/tbody/tr/td[contains(@class, 'mat-focus-indicator')]/div";
		
		//los dias del mes
		public static final String monthDays = "//mat-calendar/div/mat-month-view/table/tbody/tr/td[contains(@class, 'mat-focus-indicator')]";
		
		//devolve el dia de hoy
		public static final String toDayhoy = "//mat-calendar/div/mat-month-view/table/tbody/tr/td[contains(@class, 'mat-focus-indicator mat-calendar-body-active')]";
		
		public static final String diaNoDisponibleDelMes = "//mat-calendar/div/mat-month-view/table/tbody/tr/td[contains(@class, 'mat-focus-indicator mat-calendar-body-disabled')]";
		
		//Hora
		public static final String horaWrapper = "//div[contains(@class, 'shop-turn-button-wrapper')]";
		
		public static final String horasDisponible = "//div[contains(@class, 'shop-turn-button-wrapper')]/button[contains(@class, 'shop-turn-button')]";
		
		//Return days of month
		public static final List<WebElement> getMonthDays() {
			waitUntilPresence(monthDays);
			List<WebElement> listaDiaDisponibleDelMes= driver.findElements(By.xpath(monthDays));
			
			logStatic("Número de dias disponible en mes: " +  listaDiaDisponibleDelMes.size());
			
			return listaDiaDisponibleDelMes;
			
		}
		
		//Devolve el dia de hoy
		public static final WebElement getToDayHoy() {
			waitUntilPresence(toDayhoy);
			return driver.findElement(By.xpath(toDayhoy));
		}
		
		//Devuelve un dia seleccionado
		public static final WebElement selectDay(String selectDay, @Optional("true") boolean resultadoEsperado) {
			waitUntilPresence(monthDays);
			List<WebElement> listDays = getMonthDays();
			WebElement day = null;
			boolean elementDayfinded = false;
			
			if(Objects.isNull(selectDay)) {
				day = listDays.get(0);
				elementDayfinded = true;
			}
			
			else {
				for(int i=0; i<= listDays.size(); i++) {
					if(listDays.get(i).getAttribute("innerText").contentEquals(selectDay)) {
						day = listDays.get(i);
						elementDayfinded = true;
						break;
						
					}
					
				}
				
			}
			
			if(!elementDayfinded && resultadoEsperado) {
				logStatic("Error: No se ha podido encontrar la fecha a seleccionar para el pedido");
				Assert.assertTrue(false);
			}
			
			return day;
		}
		
		//Devolver los dias habilitados o deshabilitados del mes
		@SuppressWarnings("null")
		public static final List<WebElement> getMonthDaysEnabledDisabled(@Optional("true") boolean enabled){
			List<WebElement> listDaysEnabled = null;
			List<WebElement> listDays = getMonthDays();
			
			//Enabled
			if(enabled && listDays.size() > 0) {
				listDays.forEach(day -> {
					if(day.getAttribute("aria-disabled") == null) {
						listDaysEnabled.add(day);
					}
					
					
				});
				
				logStatic("Numero de dias habilitados al hacer pedido --> " + listDaysEnabled.size());
			}
			
			//Disabled
			if(!enabled && listDays.size() > 0) {
				listDays.forEach(day -> {
					if(day.getAttribute("aria-disabled") != null && day.getAttribute("aria-disabled").contains("true")) {
						listDaysEnabled.add(day);
					}
					
				});
				
				logStatic("Numero de dias deshabilitados al hacer pedido --> " + listDaysEnabled.size());
			}
						
			return listDaysEnabled;
			
		}
		
		//Devuelve horio disponible para pedidos
		public static final List<WebElement> getHoraDisponible(){
			List<WebElement> listaHoraDisponible = driver.findElements(By.xpath(horasDisponible));
			return listaHoraDisponible;
		}
		
		//devuelve hora seleccionada para pedido
		public static final WebElement selectHoraPedido(String horaParaPedido, @Optional("true") boolean resultadoEsperado) {
			waitUntilPresence(horaWrapper);
			waitUntilPresence(horasDisponible);
			List<WebElement> listHoras = getHoraDisponible();
			WebElement hora = null;
			boolean elementHorafinded = false;
			
			boolean elementFinded = false;
			
			if(Objects.nonNull(hora)) {
				
				if (Objects.isNull(horaParaPedido)) {
					hora = listHoras.get(0);
					elementFinded = true;
				}
				else {
				
					for(int i=0; i<= listHoras.size(); i++) {
						if((listHoras.get(i)).getAttribute("innerText").contentEquals(horaParaPedido)) {
							hora = listHoras.get(i);
							elementHorafinded = true;
							break;
							
						}
						
					}
				}
				
			}
			else {
				logStatic("Error: No hay aparece hora para el dia seleccionado!");
				Assert.assertTrue(false);
			}
			
			if(!elementHorafinded && resultadoEsperado) {
				logStatic("Error: No se ha podido encontrar la fecha a seleccionar para el pedido");
				Assert.assertTrue(false);
			}
			
			return hora;
		}
		
	}
	
	public class PedidoMasTarde {
		
	}
	
	public class MailSac {
		public static final String email = "portalrestcliente@mailsac.com";
		public static final String passwordMailSac = "k_kJafj5UAsHjv3pY68wNCdpvhXbw7RMvUtZolnMQ1dfa";
		public static final String passwordPortalRest = ".1234abcd";
	}
	
	public static class Formatos {
		
		public static final String arrayFormatos = "//div[contains(@class,'format-element-wrapper')]";
		
		public List<WebElement> webElmtArrayFormatos = driver.findElements(By.xpath(arrayFormatos)); //m
		
		public static final String arrayNombre = "//div[contains(@class,'format-element-wrapper')]//div[contains(@class, 'format-element-name')]";
		
		public List<WebElement> webElmtArrayNombre = driver.findElements(By.xpath(arrayNombre)); //m
		
		public static final String arrayPrecio = "//div[contains(@class,'format-element-wrapper')]//div[contains(@class, 'format-element-price')]";
		
		public List<WebElement> webElmtArrayPrice = driver.findElements(By.xpath(arrayPrecio));
		
		public static final String AddToOrderButton = "//button[contains(@class,'basket-button')]";
		
		
		public List<Formato> formatList = new ArrayList<Formato>();		
		
		public Formatos() {
			super();
			setFormatList();
		}

		public void setFormatList() {
			Formato formato;
			String nameFormat;
			String priceFormat;
			
			waitUntilPresence(arrayFormatos);

			waitUntilPresence(arrayNombre);
			
			waitUntilPresence(arrayPrecio);
			
			if(webElmtArrayFormatos.size() > 0) {
				
				for(int i = 0; i < webElmtArrayFormatos.size(); i++) {
					
					nameFormat = driver.findElements(By.xpath(arrayNombre)).get(i).getText();
					//Utils.logStatic("nombre del formato: " + nameFormat);
					
					priceFormat = driver.findElements(By.xpath(arrayPrecio)).get(i).getText();
					//Utils.logStatic("precio del formato: " + priceFormat);
					
					formato = new Formato(nameFormat, priceFormat);
					this.formatList.add(formato);
				}
			}
			
		}
		
		public List<Formato> getFormatList() {
			return this.formatList;
		}
		
		public String getXpathAddFormatoCombinado(String modificador) {
			return "//div[contains(@class, 'main-column fullWidth')]//child::div[contains(@class, 'dish-name line-clamp-2 modifier-menu-dish-name') and contains(text(), '"+ modificador +"')]//ancestor::div[contains(@class, 'main-column fullWidth')]//child::div[contains(@class, 'product-item-add')]";
		}
		
		public String getXpathDeleteFormatoCopmbinado( String modificador) {
			return "//div[contains(@class, 'main-column fullWidth')]//child::div[contains(@class, 'dish-name line-clamp-2 modifier-menu-dish-name') and contains(text(), '"+ modificador +"')]//ancestor::div[contains(@class, 'main-column fullWidth')]//child::div[contains(@class, 'number-spinner-spinner left-spinner')]";
		}
		
		
		//Validar los formatos desde un lista de formato
		public void validarFormatosEsperados( List<Formato> formatos) {
			List<Formato> formatoEncontradoList = getFormatList();
			
			//Ordonar los formatos
			List<Formato> sortFormatoEncontradoList = sortedFormatoList(formatoEncontradoList);//.stream()
			
			sortFormatoEncontradoList.forEach(sorteFormat1 -> {
				System.out.println("Lista de formatos encontrados y  ordonados " + sorteFormat1.getNombre());
			});
			
			List<Formato> sortFormatosList = sortedFormatoList(formatos);//.stream()
			
			sortFormatosList.forEach(sorteFormat2 -> {
				System.out.println("Lista de formatos esperados y ordonados " + sorteFormat2.getNombre());
			});
			
			Assert.assertTrue(sortFormatoEncontradoList.size() == sortFormatosList.size(), "Error: no hemos encontrado todos los formatos(sortFormatosList y sortFormatoEncontradoList ) esperados en PT");
			
			List<Formato> newformatoList = new ArrayList<Formato>();
		    for (Formato formatA: sortFormatoEncontradoList){
		      boolean equals = false;
		      for (Formato formatB: sortFormatosList) {
		        if (formatA.getNombre().equals(formatB.getNombre())) {
		          equals = true;
		        }
		      }
		      if (!equals) {
		    	  newformatoList.add(formatA);
		      }
		    }
						
			if(newformatoList.size() > 0) {
				newformatoList.stream().forEach(format -> {
					System.out.println("Error: No existen los siguiente formatos: " + format.getNombre());
					
				});
				Assert.assertTrue(false);
			}
			else {
				System.out.println("todos los formatos han sido validados ");
			}
			
		}
		
		//Validar los formatos desde el producto
		public void validarFormatosEsperados( Product product) {
			validarFormatosEsperados( product.getFormatos());
		}
		
		private static Comparator<Formato> formatoComparator() {

		  return new Comparator<Formato>() {
		      @Override
		      public int compare(Formato form1, Formato form2) {
		    	  return form1.getNombre().compareTo(form2.getNombre());
		      }
		  };

		}
		
		public static List<Formato> sortedFormatoList(List<Formato> sortedFormatoList){
			return sortedFormatoList.stream()
					.sorted(formatoComparator())
					.collect(Collectors.toList());
		}
		
		public static void addFormato () {  //Añadir el formato en la cesta desde de la pantalla usando el botón +
			
		}
		
		public static void deleteFormato () { //Elimninar el formato de la cesta desde de la pantalla usando el botón -
			
		}
		
		//Obtener el webelement de formato con nombre y precio
		public WebElement getFormatoElementByNameAndPrice(String formatoName, String formatoPrice) {
			WebElement formatoElementByNameAndPrice = null;
			
			String nameFormat;
			String priceFormat;
									
			waitUntilPresence(arrayFormatos);

			waitUntilPresence(arrayNombre);
			
			waitUntilPresence(arrayPrecio);
			
			if(webElmtArrayFormatos.size() > 0) {
				
				for(int i = 0; i < webElmtArrayFormatos.size(); i++) {
					
					nameFormat = driver.findElements(By.xpath(arrayNombre)).get(i).getText().replaceAll("\\s","");
					
					priceFormat = driver.findElements(By.xpath(arrayPrecio)).get(i).getText().replaceAll("\\s","");
					
					if(nameFormat.contentEquals(formatoName.replaceAll("\\s","")) && priceFormat.contentEquals(formatoPrice.replaceAll("\\s",""))) {
						formatoElementByNameAndPrice = webElmtArrayFormatos.get(i);
						break;
					}
					
					System.out.println("Error: No se ha encontrado el webElement del formato con el nombre y precio -> " + formatoName + " / " +formatoPrice);
					System.out.println("Estamos con el webelment del formato con nombre y precio " + nameFormat + " / " +priceFormat);
					
					
				}
			}
			
			Assert.assertTrue(isElementPresent(formatoElementByNameAndPrice), "Error: No se ha encontrado el webElement del formato con el nombre y precio -> " + formatoName + " / " +formatoPrice);

			return formatoElementByNameAndPrice;

		}
		
		//Obtener el formato con nombre y precio
		public Formato getFormatoByNameAndPrice(String formatoName, String formatoPrice) {
			Formato formatoEncontrado = null;

			formatoEncontrado = (Formato) getFormatList().stream()
					.filter(formato -> formato.getNombre().contentEquals(formatoName))
					.findFirst().get();
			
			Assert.assertTrue(!Objects.isNull(formatoEncontrado), "Error: No se ha encontrado el formato con el nombre y precio -> " + formatoName + " " +formatoPrice);
			
			return formatoEncontrado;

		}

		
	}

	/**
	 * Pagina de la carta del restaurante : restaurant Menu page
	 * 	Product List
	 *  find product
	 *  scrollpage
	 *  addProduct
	 */
	public static class RestaurantMenuPage extends TestBase {
		public static String sProductListElement = "//span[contains(@class,'test-product-item')]";
		public static String sCartPageFooter = "//div[contains(@class,'footer-item')]";
		
		public static String sProductInfoWrapper = "//div[contains(@class, 'product-info-wrapper-scrollable')]";
		
		public static String sButtonAdd = "//app-basket-button//button[contains(@class, 'main-btn basket-button')]";
		
		public List<WebElement> productList = new ArrayList<WebElement>();
		
		public List<WebElement> getProductList() {
			return productList;
		}
		public void setProductList(List<WebElement> productList) {
			this.productList = productList;
		}
		
		public void setProductList() {
			this.productList = driver.findElements(By.xpath(sProductListElement));
		}
		
		//Añadir artículo desde de la ficha del producto
		//** Abrir ficha producto
		public void addFromProductSheet(WebElement elementProducto, String productName) {
			
			abrirFichaProducto(elementProducto);
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(sProductInfoWrapper)));
			
			if(!driver.findElement(By.xpath(sProductInfoWrapper)).getAttribute("textContent").contains(productName)) {
				String errorMensaje = "Errror: no se ha encontrado el producto " + productName + " en la ficha abierta";
				log(errorMensaje);
				Assert.assertTrue(false);
			}
			
			
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
		
		public void abrirFichaProducto(WebElement elementProducto) {
			clicJS(elementProducto);
			espera(500);
		}
		
	}
	
	public static class ErrorPage {
		public static String labelTitleText = "Oops...";
		public static String labelTitleXpath = "//div[contains(@class, 'labelTitle')]";
		
		public static String labelErrorText = "Parámetros incorrectos";
		public static String labelErrorXpath = "//div[contains(@class, 'labelError')]";
		
		public static String errorImageXpath= "//img[contains(@class, 'error-img')]";
		
	}
	
}
