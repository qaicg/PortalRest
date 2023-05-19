package Ventas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Reservas.MailReading;
import main.Correo;
import pedido.Pedido;
import pedido.Product;
import utils.Data;
import utils.PortalRestOrderElements;
import utils.TestBase;

public class ConfirmarPedidPorEmail extends TestBase {
	boolean isValidatedTicket = false;
	Map<String, String> informationEmail;
	List<String> infoPedido = new ArrayList<String>();
	List<Product> emailProducts = new ArrayList<Product>();
	Pedido emailPedido = new Pedido();
	
	@Test(priority = 1, groups = {"checkConfirmeOrderEmail"})
	@Parameters({"urlEmail", "emailCliente", "passwordCliente", "pedidoConfirmadoString", "isMailSac"})
	public void consultarPedido(@Optional("") String urlEmail, String emailCliente, String passwordCliente, String pedidoConfirmadoString, @Optional("false") boolean isMailSac) {
		
		if(isMailSac) {
			
			emailCliente = PortalRestOrderElements.MailSac.email;
			passwordCliente = PortalRestOrderElements.MailSac.passwordMailSac;
				  
			Correo correo = openLastMessageFromMailSac(emailCliente, passwordCliente);
			Data.getInstance().getPedido().setAvisoPorEmail(correo.getRemitente(), correo.getAsunto(), correo.getCuerpo());
			
			log("Aviso por mail en text del pedido para más tarde: " + correo.getCuerpo() );
			
			String[] infoPedidos = correo.getCuerpo().split("\n");
			String link = null;
			
			
			for(int i=0; i < infoPedidos.length; i++) {
				
				if(infoPedidos[i].contains("Recoger ahora [")) {
					link = infoPedidos[i];
				}
				
				if(infoPedidos[i].contains("Precio :")) {
					int pos1 = infoPedidos[i].indexOf("Precio :");
					emailPedido.setPrecioTotal((infoPedidos[i].substring(pos1)+1));
				}
				
				
			}
			
			
			if(!isNullOrEmpty(link)) {
				log("print position [ " +link.indexOf("["));
				log("print position ] " +link.indexOf("]"));
				
				link =  link.substring(link.indexOf("[")+1, link.indexOf("]"));
				
				log("infoPedidos link " + link);
			} else {
				log("Error: No se ha podido recuperar el link del pedido para la recogida");
				Assert.assertTrue(false);
			}
			
			//
			
			/* Buscar el product en el correo de notificacion
			 * Forma en la tabla infoPedidos:
			 * 1.0
			 * Fanta Taronja 30 ml
			 * €1.0
			 * Modificar el codigo cuando la incidencia sea arreglada
			 * #33470 > Cambios en el correo recogido más tarde (avisar por email), enviado al cliente.
			 */
			int productFinded = 0;
			for(int j = 0 ; j < Data.getInstance().getPedido().getProduct().size(); j++) {
				for(int i=0; i < infoPedidos.length; i++) {
					if(infoPedidos[i].contains(Data.getInstance().getPedido().getProduct().get(j).getNombre())) {
						Product emailProductEncontrado = new Product();
						emailProductEncontrado.setNombre(infoPedidos[i]); //Nombre
						emailProductEncontrado.setUnidad(infoPedidos[i - 2].split(".")[0]); //1.0 Unidad
						emailProductEncontrado.setPrecioPorUnidad(infoPedidos[i + 2]); //Precio
						
						//Añadir el producto
						emailProducts.add(emailProductEncontrado);
						//productFinded = true;
						productFinded ++;
						
						log(productFinded + " Producto en email encontrado y añadido: " + infoPedidos[i]);
					}
				}
			}
			
			//Test si encontramos todos los artículos en el correo de notificación
			if(Data.getInstance().getPedido().getProduct().size() != productFinded) {
				int falta = Data.getInstance().getPedido().getProduct().size() - productFinded;
				log("Falta producto que no se ha visto en el email de notificacíon");
				log("Hemos encontrado " + productFinded + " productos");
				log("Nos faltan " + falta + " productos");
				Assert.assertTrue(false);
			}
			else {
				log("Hemos encontrado todos los productos, son: " + productFinded + " productos, no falta producto");
			}
			
			
			
			emailPedido.setProduct(emailProducts);
			//
			
			openNewWindowTab(link);
			espera(3000);
			
			//String linkCogerPedido = infoPedidos[20].split("[")[1].split("]")[0];
			//log("infoPedidos linkCogerPedido" + linkCogerPedido);
			//driver.manage().window().
			//driver.get(linkCogerPedido);
			
			
			//Recogida del pedido pulsando el botón Recoger ahora
			/*
			String btnRegoger = "//div//a[contains(text(), 'Recoger ahora')]";
			waitUntilPresence(btnRegoger, true, false);
			clicJS(driver.findElement(By.xpath(btnRegoger)));
			ArrayList<String> switchTabs = new ArrayList<String> (driver.getWindowHandles());
			log("numero de pestañas abiertas después del clic para abrir el recibo del pedido a recoger --> " +switchTabs.size());
			
			driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL, Keys.TAB);
			
			espera(2000);
			validarReciboRecogerPedido(pedidoConfirmadoString);
			*/
		}
		else { //Estamos en yahoo
		
			openNewWindowTab(urlEmail);
			
			MailReading readCustomerEmail = new MailReading();
			
			try {
				
				readCustomerEmail.openWebMail(emailCliente, passwordCliente, false);
				espera(2000);
				
				//verificamos si el correo electrónico está abierto
				String lastEmail = "//ul[contains(@aria-label, 'Message list')]//child::a[contains(@role, 'article') and contains(@data-test-read, 'false')]";
		    	waitUntilPresence(lastEmail, true);
		    	if(isElementPresent(By.xpath(lastEmail)) && !readCustomerEmail.isSesionMailOpen()) {
		    		readCustomerEmail.setSesionMailOpen(true);
		    	}
		    	
				//
				
				if(!readCustomerEmail.isSesionMailOpen()) {
					log("No se ha podido abrir la sesión email del cliente para leer la notificación del pedido a recoger");
					Assert.assertTrue(false);
				}
				
				readCustomerEmail.extractContentMail("Email del cliente");
				espera(500);
				
				this.informationEmail = readCustomerEmail.getInfoEmailMap();
				
				//this.informationEmail.get("mensajeDelMail");
				
				espera(500);
				//Open the last email not reading yet
				readCustomerEmail.lastEmailNotReading();
				espera(1500);	
				
				//Recogida del pedido pulsando el botón Recoger ahora
				String btnRegoger = "//div//a[contains(text(), 'Recoger ahora')]";
				waitUntilPresence(btnRegoger, true, false);
				clicJS(driver.findElement(By.xpath(btnRegoger)));
				ArrayList<String> switchTabs = new ArrayList<String> (driver.getWindowHandles());
				log("numero de pestañas abiertas después del clic para abrir el recibo del pedido a recoger --> " +switchTabs.size());
				
				driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL, Keys.TAB);
				
				espera(2000);
				validarReciboRecogerPedido(pedidoConfirmadoString);
				
				
				readCustomerEmail.cerrarSesionMail(false);
				espera(1500);
				
				
				
				//close tab
				log("Current Url -> " + driver.switchTo().window(driver.getWindowHandle()).getCurrentUrl());
				closeWindowTab();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			 
		}
	}
	
	//@Test(dependsOnMethods = "consultarPedido", priority = 2)
	//@Parameters({"pedidoConfirmadoString"})
	public void validarReciboRecogerPedido(String pedidoConfirmadoString) {
		ArrayList<String> switchTabs= new ArrayList<String> (driver.getWindowHandles());
		
		driver.switchTo().window(switchTabs.get(2));
		//switchTabs.get(0)
		log("numero de pestañas abiertas --> " +switchTabs.size());
		//ticket-background
		String pantallaRecogerPedido = "//div[contains(@class, 'ticket-background')]";
		
		String sTextPedido = "PEDIDO";
		waitUntilPresence(pantallaRecogerPedido, true, false);
		
		log("Validar que se muestre encabezado el titulo " +sTextPedido);
		
		if(isElementPresent(By.xpath("//div[contains(@class, 'generic-title')]")) 
				&& (!driver.findElement(By.xpath("//div[contains(@class, 'generic-title')]")).getText().toUpperCase().contentEquals(sTextPedido))) {
			String textEncontrado = driver.findElement(By.xpath("//div[contains(@class, 'generic-title')]")).getText().toUpperCase() ;
			log("No hemos encontrado encabezado el titulo --> " +sTextPedido);
			log("Hemos encontado encabezado el titulo --> " + textEncontrado);
			Assert.assertTrue(false);
		}
		
		log("Esperando a la pantalla de Recibo electrónico de la recogida del pedido por más tarde");
		String numPedido = Data.getInstance().getPedidoActual();
		
		log("el numero de pedido a recoger para comprobar:" + numPedido);
		
		//comprobar el numero del pedido
		String elmeNumePedido = "//app-pickup-order/div/div[2]/div/div/div[3]/div[2]";
		
		if(isElementPresent(By.xpath("//app-pickup-order/div/div[2]/div/div/div[4]/div[2]"))) {
			if(StringUtils.isNumericSpace(driver.findElement(By.xpath("//app-pickup-order/div/div[2]/div/div/div[4]/div[2]")).getText().trim())) {
				elmeNumePedido = "//app-pickup-order/div/div[2]/div/div/div[4]/div[2]";
			}
		}
		log("String id pedido " + elmeNumePedido);
		
		waitUntilPresence(elmeNumePedido, true);
		String sNumePedido = driver.findElement(By.xpath(elmeNumePedido)).getText();
		if(!numPedido.contentEquals(sNumePedido)) {
			log("No hemos encontrado el numero del pedido a recoger");
			log("el numero del pedido esperado es --> "+ numPedido);
			log("el numero del pedido encontrado es --> "+ sNumePedido);
			Assert.assertTrue(false);
		}
		
		
		//espera(1000);
		if (!isElementPresent(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"))){
			log("No hemos encontrado pantalla del recibo electrónico de la recogida del pedido por más tarde");
			Assert.assertTrue(false);
		}

		driver.findElement(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"));
		log("Hemos llegado a la pantalla del recibo electrónico  de la recogida del pedido por más tarde -> " + pedidoConfirmadoString);
		espera();
		
		if(isElementPresent(By.xpath("//ul/preceding::div[5]"))){
			numPedido = driver.findElement(By.xpath("//ul/preceding::div[5]")).getText();//driver.findElement(By.xpath("//ul/preceding::div[1]")).getText();
		} else if(isElementPresent(By.id("ticket2-orderNumber"))) {
			numPedido = driver.findElement(By.id("ticket2-orderNumber")).getText();
		}
		
		if(isNullOrEmpty(numPedido)) {
			log("No se ha encontrado el numero de pedido");
			Assert.assertTrue(false);
		}
		
		if(isElementPresent(By.xpath("//button[@class='main-btn basket-button']"))) {
			log("No debemos encontrar el botón Volver al inicio");
			Assert.assertTrue(false);
		}
		
		closeWindowTab(2, 1);
		//Assert.assertTrue(true);
		
	}
	
	/*
	 * Validación del correo elctrónico de recogida del pedio:
	 * Articulos
	 * Precio total a pagar
	 * 
	 * 
	 */
	public void checkOrderMail() {
		
	}

}
