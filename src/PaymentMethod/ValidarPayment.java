package PaymentMethod;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import utils.Data;
import utils.TestBase;

public class ValidarPayment extends TestBase {

	public static boolean validaPantalla(String[] arrayNombres,String totalEsperado, String unidades, String totalEsperadoMasCargos, String repartoPermitido, String tipoServicio) {

		String basketTitle = w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'basket-ticket-title')]"))).getAttribute("innerText");
		String basketDate = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'basket-ticket-row')]"))).getAttribute("innerText"); 
		int unidadesInteger=1;
		List<WebElement> basketLines = driver.findElements(By.tagName("app-basket-line"));

		logStatic("Validación de pantalla de resumen de pedido");
		if(!unidades.equalsIgnoreCase("")) {
			
			 unidadesInteger = Integer.parseInt(unidades);
			
			for(int i =0; i< basketLines.size();i++) {	
				if(basketLines.get(i).findElement(By.xpath("//label[contains(@class,'dishName')]")).getAttribute("innerText")!="") { //POR AHORA SOLO MIRO QUE LA LINEA NO QUEDE VACIA

					for(int z=1;z<unidadesInteger;z++) {
						espera(500);
						clicJS(driver.findElements(By.xpath("//div[contains(@class,'right-spinner')]")).get(i));
					}
				}	
			}
				
		}else {
			
			for(int i =0; i< basketLines.size();i++) {	
				if(basketLines.get(i).findElement(By.xpath("//label[contains(@class,'dishName')]")).getAttribute("innerText")!="") { //POR AHORA SOLO MIRO QUE LA LINEA NO QUEDE VACIA

					logStatic("- " + arrayNombres[i]);
				}	
			}
		}
		
		
		if(basketLines.size()!=arrayNombres.length) {
			logStatic("- Error. Me faltan artículos en el resumen del basket, deberían haber " + arrayNombres.length);
			return false;
		}

		espera(1000);

		//VALIDAMOS PIE DE SUBTOTAL Y TOTAL
		driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][1]//div[contains(text(),'Subtotal')]"));
		
		//Verificar si hay propina
		if(PaymentResource.Propina.isPropinaSelected()) {
			driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][1]//div[contains(text(),'"+ PaymentResource.Propina.getSubTotal() +"')]"));
		}
		else {
			driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][1]//div[contains(text(),'"+totalEsperado+"')]"));
		}
		

		if (tipoServicio.equalsIgnoreCase("3") && repartoPermitido.equalsIgnoreCase("true")) { //TIPO DELIVERY
			
			//driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][3]//div[contains(text(),'Total')]"));
			
			if(isElementPresent(By.xpath("//div[contains(@class,'price-breakdown-line')][3]//div[contains(text(),'Total')]"))) {
				driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][3]//div[contains(text(),'Total')]"));
			} else {
				driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line price-breakdown-total')]"));
			}
			
			
			//driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][3]//div[contains(text(),'"+totalEsperadoMasCargos+"')]"));
			
			if(isElementPresent(By.xpath("//div[contains(@class,'price-breakdown-line')][3]//div[contains(text(),'"+totalEsperadoMasCargos+"')]"))) {
				driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][3]//div[contains(text(),'"+totalEsperadoMasCargos+"')]"));
			} else {
				driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line price-breakdown-total')]/child::div[2]"));
				totalEsperadoMasCargos = totalEsperado;
			}
			
	
		}else {
			
			if(isElementPresent(By.xpath("//div[contains(@class,'price-breakdown-line')][2]//div[contains(text(),'Total')]"))) {
				driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][2]//div[contains(text(),'Total')]"));
			} else { 
			 driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line price-breakdown-total')][1]//div[contains(text(),'Total')]"));
			}
			
			if(isElementPresent(By.xpath("//div[contains(@class,'price-breakdown-line')][2]//div[contains(text(),'"+totalEsperado+"')]"))) {
				driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][2]//div[contains(text(),'"+totalEsperado+"')]"));
			} else {
				driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line price-breakdown-total')][1]//div[contains(text(),'"+totalEsperado+"')]"));
			}
		}
		

		 w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'generic-subtitle')]")));
		//VALIDAMOS QUE TENEMOS TITULO DE SELECCIÓN DE FORMAS DE PAGO
		if (driver.findElement(By.xpath("//div[contains(@class,'generic-subtitle')]")).getAttribute("innerText").equalsIgnoreCase("")) {
			logStatic("- Error. No tenemos titulo en sección de Formas de Pago.");
			return false;
		}

		espera(1000);
		List<WebElement> payMenMeans = driver.findElements(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(@class,'payment-name')]"));
		if (payMenMeans.size()==0) {
			logStatic("- Error. No tenemos formas de pago visibles.");
			return false;
		}
		
		if (tipoServicio.equalsIgnoreCase("3") && repartoPermitido.equalsIgnoreCase("true")) { //TIPO DELIVERY
			
			if (!validaCarritoFlotante(String.valueOf((arrayNombres.length)*unidadesInteger),totalEsperadoMasCargos)) {
				logStatic("- Error. El total del carrito flotante no tiene el importe esperado");
				return false;
			}
			
		}else {
			
			if (!validaCarritoFlotante(String.valueOf((arrayNombres.length)*unidadesInteger),totalEsperado)) {
				logStatic("- Error. El total del carrito flotante no tiene el importe esperado");
				return false;
			}
		}
		
		//Save precio total a pagar del pedido
		//Save el precio total a pagar
		Data.getInstance().getPedido().setPrecioTotal(totalEsperado);
		
		return true;
	}
	
	
	
	public static boolean validarPantallaRecibo() {
		espera(2000);
		
		String matSpinner = "//mat-spinner[contains(@class, 'mat-spinner mat-progress-spinner mat-primary mat-progress-spinner-indeterminate-animation')]";
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(matSpinner)));
		
		espera(2000);
		if(isElementPresent(By.id("orderReceiptHeader"))) {
			logStatic("ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO POR ID orderReceiptHeader.");
			w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
			return true;
		} 
		
		espera(2000);
		if(isElementPresent(By.xpath("//div[contains(@class, 'ticket-background')]"))) {
			logStatic("ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO POR class ticket-background.");
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'ticket-background')]")));
			return true;
		} 
		
		espera(2000);	
		if(isElementPresent(By.xpath("//img[contains(@alt, 'Redsys')]")) || isElementPresent(By.xpath("//img[contains(@alt, 'Mantemimiento del servicio')]")) || 
				isElementPresent(By.xpath("//h1[contains(text(), 'Estamos realizando tareas de mantenimiento, vuelva a intentarlo más tarde')]")) || 
				isElementPresent(By.xpath("//span[contains(text(), 'Disculpen las molestias')]"))) {
			logStatic("Redsys ha fallado.");
			logStatic("Pantalla Redsys error | 500 ");
			espera(1000);
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[contains(@alt, 'Redsys')]")));
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[contains(@alt, 'Mantemimiento del servicio')]")));
		
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h1[contains(text(), 'Estamos realizando tareas de mantenimiento, vuelva a intentarlo más tarde')]")));
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h1[contains(text(), 'We are working on maintenance tasks, please try again later')]")));
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[contains(text(), 'Disculpen las molestias')]")));
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[contains(text(), 'Apologize for the inconvenience')]")));
			logStatic("Redsys ha fallado.");
			logStatic("Pantalla Redsys error | 500 ");
		}
		logStatic("No se ha podido validar la pantalla del recibo !!!");
		Assert.assertTrue(false);
		return false;		
	}	
	
	public static void validarReciboCheckout(String pedidoConfirmadoString) {
		// ESTA PANTALLA ES UNA MIERDA, NO TIENE CLASES PARA IDENTIFICAR, HABLAR CON RAMON
		logStatic("Esperando a la pantalla de Recibo electrónico");
		String numPedido = null;
		//espera(1000);
		if (!isElementPresent(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"))){
			logStatic("No hemos encontrado pantalla del recibo electrónico");
			Assert.assertTrue(false);
		}

		driver.findElement(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"));
		logStatic("Hemos llegado a la pantalla del recibo electrónico -> " + pedidoConfirmadoString);
		espera();
		
		if(isElementPresent(By.xpath("//ul/preceding::div[5]"))){
			numPedido = driver.findElement(By.xpath("//ul/preceding::div[5]")).getText();//driver.findElement(By.xpath("//ul/preceding::div[1]")).getText();
		} else if(isElementPresent(By.id("ticket2-orderNumber"))) {
			numPedido = driver.findElement(By.id("ticket2-orderNumber")).getText();
		}
		
		if(isNullOrEmpty(numPedido)) {
			logStatic("No se ha encontrado el numero de pedido");
			Assert.assertTrue(false);
		}
		
		Data.getInstance().setPedidoActual(numPedido);
		logStatic("el numero de pedido actual a comprobar:" + numPedido);
		if(isElementPresent(By.xpath("//button[@class='main-btn basket-button']"))) {
			driver.findElement(By.xpath("//button[@class='main-btn basket-button']")).click();//Pulsamos en volver al inicio
		}else {
			back(); //Desde la versión del 16/09/2022 el el botón de back tiene otro comportamiento y regresa a la ultima pantalla abierta. 
		}
		
		//Save el numero de pedido en Object pedido/Sirve para futuro validación por ejemplo en Pedio más tarde y Día y hora
		Data.getInstance().getPedido().setNumeroPedido(numPedido);
		
		Assert.assertTrue(true);
	}
	
	public static boolean validaCarritoFlotante(String productosEncontrados, String totalEsperado) {

		logStatic("Validaciones de botón flotante de carrito");

		//VALIDAMOS UNIDADES TOTALES AÑADIDAS AL CARRITO Y VISIBLES EN EL BOTÓN FLOTANTE..
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-units')]")).getAttribute("innerText").equalsIgnoreCase(String.valueOf(productosEncontrados))) {
			logStatic("- "+productosEncontrados + " productos añadidos");
		} else {
			logStatic("- Error en validación de productos añadidos al carrito" );
			return false;
		}

		//VALIDAMOS IMPORTE TOTAL VISIBLE DESDE EL BOTÓN DEL CARRITO FLOTANTE
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-amount')]")).getAttribute("innerText").equalsIgnoreCase(String.valueOf(totalEsperado))) {
			logStatic("- "+totalEsperado + " total validado");
		}else {
			logStatic("- Error en validación de productos añadidos al carrito" );
			return false;
		}

		if(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")).getAttribute("innerText").equalsIgnoreCase("")) {
			logStatic("- Error en validación del string del botón flotante del carrito, está vacio (debería aparecer algo como Ver pedido)" );
			return false;
		}

		logStatic("Carrito flotante validado correctamente");

		return true;
	}
	
	
}
