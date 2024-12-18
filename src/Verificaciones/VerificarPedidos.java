package Verificaciones;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.Data;
import utils.TestBase;

public class VerificarPedidos  extends TestBase {
	public String testResult = "false";
	
	@Test(description="Verificar el pedido en el apartado Mis pedidos", priority=1, groups = {"verificaPedidos"})
	@Parameters({"ultimoPedido", "menu", "profile", "pedidos", "productos", "totalEsperado"})
	public void VerificarPedidos (@Optional ("true") String ultimoPedido, String menu, String profile, String pedidos, String productos, String totalEsperado) {
		espera(500); // Wait for main page
		List<WebElement> productLineOrder = new ArrayList<WebElement>();
		String checkTicket2Activated = "false";
		int productLineOrderSize = 0;
		WebElement totalPriceTypeWebElm;
		String totalPrice; 
		
		if(isNullOrEmpty(menu)) {
			//espera(500);
			abrirMisPedidos(profile, pedidos, false);
			espera(2000);
		} else {
			abrirMisPedidos(profile, pedidos, true);
			espera(2000);
		}
		
	
		if (ultimoPedido.equalsIgnoreCase("true")) {
			String numeroPedidoActual = null;
			try {
				w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@class='scrollable-content']//child::app-order[1]")));
			}catch (Exception e) {
				e.printStackTrace();
				log(e.getMessage());
				
			}
			
			espera(2000); 
			WebElement pedidoInfo = driver.findElement(By.xpath("//*[@class='scrollable-content']//child::app-order[1]"));
			pedidoInfo.click();
			espera(2000); // Wait for order button
			
			String numPedidoValidar = Data.getInstance().getPedidoActual();
				
			
			//<Versio actual 8.23.>
				
				//w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul/preceding::div[5]")));
				//numeroPedidoActual = driver.findElement(By.xpath("//ul/preceding::div[5]")).getText(); 
				
				if(isElementPresent(By.xpath("//ul/preceding::div[5]"))) {
					w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul/preceding::div[5]")));
					numeroPedidoActual = driver.findElement(By.xpath("//ul/preceding::div[5]")).getText();
				} else if(isElementPresent(By.id("ticket2-orderNumber"))) { //(By.xpath("//*[@id='ticket2-orderNumber']")
					espera(500);
					w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("ticket2-orderNumber")));
					espera(500);
					w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("ticket2-orderNumber"))); //(By.xpath("//*[@id='ticket2-orderNumber']")));
					espera(500);
					WebElement numePedidoActualWebElm =  driver.findElement(By.id("ticket2-orderNumber")); //(By.xpath("//*[@id='ticket2-orderNumber']"));
					espera(); 
					numeroPedidoActual = numePedidoActualWebElm.getText();
					//numeroPedidoActual = driver.findElement(By.id("ticket2-orderNumber")).getText(); // ("//*[@id='ticket2-orderNumber']")
					//String[] totalEsperadoMount = totalEsperado.split("€");
					//totalEsperado = totalEsperadoMount[0] + " €";
 					log("Se ha encontrado el numero de pedido actual " + numeroPedidoActual);
				} else if(isElementPresent(By.xpath("//ul/preceding::div[1]"))) {
					w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul/preceding::div[1]")));
					numeroPedidoActual = driver.findElement(By.xpath("//ul/preceding::div[1]")).getText();
				}
				
				if(isNullOrEmpty(numeroPedidoActual)) {
					log("No se ha encontrado el numero de pedido actual");
					Assert.assertTrue(false);
				}
				
			//</Versio actual 8.23.>
			
			//<Versio estable 7.60.>
				//w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul/preceding::div[1]")));
				//String numeroPedidoActual = driver.findElement(By.xpath("//ul/preceding::div[1]")).getText();
			//</Versio estable 7.60.>
						
			//Validar el numero del pedido actual
			if(numeroPedidoActual.equalsIgnoreCase(numPedidoValidar)) {
				Assert.assertTrue(true);
			} else {
				log("El numero del pedido actual  del documento de la venta => " + numeroPedidoActual + " no es Valido. El numero esperado: " +numPedidoValidar) ;
				Assert.assertTrue(false);
			}
			
			
			// Validar la fecha de la venta 
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date();
			String fechaHoy = dateFormat.format(date);
			espera();
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[contains(text(), '"+ fechaHoy +"')]")));
			WebElement today = driver.findElement(By.xpath("//*[contains(text(), '"+ fechaHoy +"')]"));

			if(today.getText() != "") {
				Assert.assertTrue(true);
			} else {
				log("la fecha del documento de la venta no se encuentra");
				Assert.assertTrue(false);
			}
			
			
			// Validar los articulos de la venta (unidades, descripcion y el precio)
			String[] articles = productos.split(",");
			List<String> orderList = new ArrayList<String>();
			List<String> orderListNotFound = new ArrayList<String>();
			
			if(isElementPresent(By.xpath("//ul/descendant::li/child::div/child::div[3]"))) {
				productLineOrder = driver.findElements(By.xpath("//ul/descendant::li/child::div/child::div[3]"));
				productLineOrderSize = productLineOrder.size();
				
			} else if(isElementPresent(By.id("ticket2-linesBlock"))) {
				//productLineOrder = driver.findElements(By.id("ticket2-linesBlock")); //
				productLineOrder =  driver.findElements(By.xpath("//*[@id='ticket2-linesBlock']/child::div"));
				checkTicket2Activated = "true";
				productLineOrderSize = productLineOrder.size() - 1;
			}
			
			
			// validar el numero de linea de articulo en el documento
			if(productLineOrderSize == articles.length) {
				if(checkTicket2Activated.equalsIgnoreCase("true")) {
					
					for(int i= 0; i< productLineOrderSize; i++) {
						//articulo
						WebElement article = driver.findElement(By.xpath("//*[@id='ticket2-linesBlock']/child::div//*[contains(text(), '"+ articles[i] +"')]"));
						
						//precio del articulo
						WebElement articlePrice = driver.findElement(By.xpath("//*[@id='ticket2-linesBlock']/child::div//*[contains(text(), '"+ articles[i] +"')]//following-sibling::span"));
						
						//orderList.add(orderList.add(article.getText()) + orderList.add(articlePrice.getText()));
						orderList.add(article.getText() + articlePrice.getText());
						
						if(orderList.get(i).isEmpty()) {
							orderListNotFound.add( articles[i]);
						}
						
					}
					
				} else {
					
					for(int i= 0; i< articles.length; i++) {
						
						WebElement article = driver.findElement(By.xpath("//ul/descendant::li//*[contains(text(), '"+ articles[i] +"')]"));
											
						WebElement articlePrice = driver.findElement(By.xpath("//ul/descendant::li//*[contains(text(), '"+ articles[i] +"')]//following-sibling::div"));
						
						WebElement articleUnits = driver.findElement(By.xpath("//ul/descendant::li//*[contains(text(), '"+ articles[i] +"')]//preceding-sibling::div"));
						
						orderList.add(articleUnits.getText() + orderList.add(article.getText()) + orderList.add(articlePrice.getText()));
						
						if(orderList.get(i).isEmpty()) {
							orderListNotFound.add( articles[i]);
						}
					}
				}
				 
			} else {
				 log("el numero de articulos en el documento no es correcto ");
				 Assert.assertTrue(false);
			}
			
			if (orderListNotFound.isEmpty()) {
				Assert.assertTrue(true);
			} else {
				
				for(String orderNotFound : orderListNotFound){
				   log("No se encuentra el articulo : " + orderNotFound);
				}
				
				Assert.assertTrue(false);
			}
			
			//***Formatar el precio total esperaodo sin € 
			espera(1000);
			log("El precio Total esperado: " + totalEsperado);
			String[] totalEsperadoMount = totalEsperado.split("€");
			totalEsperado = totalEsperadoMount[0];
			log("El precio Total esperado sin €: " + totalEsperado);
			//*************
			
			if(checkTicket2Activated.equalsIgnoreCase("true")) {
				//espera(1000);
				//log("El precio Total esperado: " + totalEsperado);
				//String[] totalEsperadoMount = totalEsperado.split("€");
				//totalEsperado = totalEsperadoMount[0];
				//log("El precio Total esperado sin €: " + totalEsperado);
				w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id='ticket2-total-amount'][contains(text(),'"+ totalEsperado +"')]")));
				totalPriceTypeWebElm =  driver.findElement(By.xpath("//*[@id='ticket2-total-amount'][contains(text(), '"+ totalEsperado +"')]"));
				totalPrice = totalPriceTypeWebElm.getText();
			} else {
				w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul/following::*//*[contains(text(), '"+ totalEsperado +"')]")));
				totalPriceTypeWebElm = driver.findElement(By.xpath("//ul/following::*//*[contains(text(), '"+ totalEsperado +"')]"));
				totalPrice = totalPriceTypeWebElm.getText();
			}
			
			
			
			if(!isNullOrEmpty(totalPrice)) {
				Assert.assertTrue(true);
			} else {
				log("No se encuentra el precio total ('"+  totalPrice +"') esperado.");
				Assert.assertTrue(false);
			}
			
			testResult = "true";
			log(" Se ha validao el pedido: "+ numeroPedidoActual+ " correctamente.");
			
		}
		
	}
	
	@Test(description="Nos permite saber si el test de la fuccion ha sido bien o non")
	@Parameters()
	public boolean resulTest() {
		if (testResult.equalsIgnoreCase("true")) {
			return true;
		}
		
		return false;
	}
	 
}
