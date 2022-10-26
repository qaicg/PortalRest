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
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import graphql.Assert;
import utils.Data;
import utils.TestBase;

public class VerificarPedidos  extends TestBase {
	public String testResult = "false";
	
	@Test(description="Verificar el pedido en el apartado Mis pedidos", priority=1)
	@Parameters({"ultimoPedido", "menu", "profile", "pedidos", "productos", "totalEsperado"})
	public void VerificarPedidos (@Optional ("true") String ultimoPedido, String menu, String profile, String pedidos, String productos, String totalEsperado) {
		espera(500); // Wait for main page
		
		// Buttons are identified by text in page language
		WebElement menuButton = driver.findElement(By.xpath("//mat-icon[normalize-space()='" + menu + "']"));
		menuButton.click();
		espera(2000); // Wait for menu button
		WebElement profileButton = driver.findElement(By.xpath("//*[contains(text(), '" + profile + "')]"));
		profileButton.click();
		espera(2000); // Wait for profile button
		WebElement pedidosInfo = driver.findElement(By.xpath("//*[contains(text(), '" + pedidos + "')]"));
		pedidosInfo.click();
		espera(2000); // Wait for orders button
		
	
		if (ultimoPedido.equalsIgnoreCase("true")) {
			WebElement pedidoInfo = driver.findElement(By.xpath("//*[@class='scrollable-content']//child::app-order[1]"));
			pedidoInfo.click();
			
			String numPedidoValidar = Data.getInstance().getPedidoActual();
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul/preceding::div[1]")));
			
			String numeroPedidoActual = driver.findElement(By.xpath("//ul/preceding::div[1]")).getText();
						
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
			
			w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[contains(text(), '"+ fechaHoy +"')]")));
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
			
			List<WebElement> productLineOrder = driver.findElements(By.xpath("//ul/descendant::li/child::div/child::div[3]"));
			
			// validar el numero de linea de articulo en el documento
			if(productLineOrder.size() == articles.length) {
				for(int i= 0; i< articles.length; i++) {
					
					WebElement article = driver.findElement(By.xpath("//ul/descendant::li//*[contains(text(), '"+ articles[i] +"')]"));
										
					WebElement articlePrice = driver.findElement(By.xpath("//ul/descendant::li//*[contains(text(), '"+ articles[i] +"')]//following-sibling::div"));
					
					WebElement articleUnits = driver.findElement(By.xpath("//ul/descendant::li//*[contains(text(), '"+ articles[i] +"')]//preceding-sibling::div"));
					
					orderList.add(articleUnits.getText() + orderList.add(article.getText()) + orderList.add(articlePrice.getText()));
					
					if(orderList.get(i).isEmpty()) {
						orderListNotFound.add( articles[i]);
					}
				}
				 
			} else {
				 log("el numero de articulos en el documento no es correcto ");
			}
			
			if (orderListNotFound.isEmpty()) {
				Assert.assertTrue(true);
			} else {
				
				for(String orderNotFound : orderListNotFound){
				   log("No se encuentra el articulo : " + orderNotFound);
				}
				
				Assert.assertTrue(false);
			}
			
			WebElement totalPrice = driver.findElement(By.xpath("//ul/following::*//*[contains(text(), '"+ totalEsperado +"')]"));
			
			
			if(totalPrice.getText() != "") {
				Assert.assertTrue(true);
			} else {
				log("No se encuentra el precio total ('"+  totalPrice.getText() +"') esperado.");
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
