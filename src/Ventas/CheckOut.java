package Ventas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mysql.cj.util.StringUtils;

import utils.Data;
import utils.TestBase;

public class CheckOut extends TestBase{

	String tipoServicio="";
	String validarImporteMinimoPedido = null;
	String importeMinimo = null;
	String totalEsperado = null;
	

	@Test(description="Se encarga de gestionar la parte de checkout una vez los productos ya están añadidos al carrito", priority=1)
	@Parameters({"productos","totalEsperado","formaPago","nuevaTarjeta","testCardNumber","cad1","cad2","cvv","pedidoConfirmadoString" , "shop", "email", "miMonederoString",
		"formaPago2", "tipoServicio","unidades","mesa", "totalEsperadoMasCargos", "repartoPermitido", "goBack", "goBackByAddOrderButton", "importeMinimo", "validarImporteMinimo"})
	public void finalizarPedido(String productos, String totalEsperado, String formaPago,
			@Optional ("true") String nuevaTarjeta, @Optional ("4548812049400004") String testCardNumber,
			@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv, String pedidoConfirmadoString, 
			String shop, String customerMail, @Optional ("")String miMonederoString, @Optional ("") String formaPago2, 
			String tipoServicio, @Optional ("") String unidades, @Optional ("") String mesa, @Optional ("") String totalEsperadoMasCargos,
			@Optional ("true") String repartoPermitido, @Optional ("") String goBack, @Optional ("") String goBackByAddOrderButton, @Optional ("") String importeMinimo, @Optional ("") String validarImporteMinimo) {

		String[] arrayNombres;
		this.tipoServicio=tipoServicio;
		arrayNombres = productos.split(",");
		this.importeMinimo = importeMinimo;
		this.totalEsperado = totalEsperado;
		//SI ES NUEVO USUARIO EL CORREO QUE NOS VIENE DEL TEST NO ES VÁLIDO.
		Data.getInstance().setUltimoDocId(getLastDoc__Doc(shop));//RECUPERAMOS ULTIMO DOCDOCID DEL CLIENTE ANTES DE REALIZAR EL PEDIDO	
		WebElement checkoutButton = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'basket-button')]")));
		checkoutButton.click();
		espera(1000);
		
		
		
		w2 = new WebDriverWait(TestBase.driver,Duration.ofSeconds(60)); // LE DAMOS 60 SEGUNDOS PARA HACER EL CHECKOUT.
		if((customerMail.equalsIgnoreCase("") || customerMail.equalsIgnoreCase("invitado@portalrest.com")) && formaPago.equalsIgnoreCase("Redsys Test"))nuevaTarjeta="true"; //SI NO TENEMOS CLIENTE SIGNIFICA QUE ES NUEVO Y NECESITAREMOS TARJETA NUEVA
		if(!(validaPantalla(arrayNombres,totalEsperado,unidades,totalEsperadoMasCargos,repartoPermitido)))Assert.assertTrue(false);
		
		// Usar back para añadi mas productos al final del proceso de la venta.
		if(goBack.equalsIgnoreCase("true")) {
			if(goBackByAddOrderButton.equalsIgnoreCase("true")) {
				w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'more-units-wrapper')]/child::mat-icon")));
				WebElement addOrderBackButton = driver.findElement(By.xpath("//div[contains(@class,'more-units-wrapper')]/child::mat-icon"));
				addOrderBackButton.click();
				espera(1000); // Wait for go back by using add order button
			} else {	
				driver.navigate().back();
			}
			Assert.assertTrue(true);
		} else {
			
			
			if(formaPago.equalsIgnoreCase("Redsys Test"))
				pagarPedidoConTarjeta(formaPago, nuevaTarjeta.equalsIgnoreCase("true"),testCardNumber,cad1,cad2,cvv,pedidoConfirmadoString, validarImporteMinimo);
			if (formaPago.equalsIgnoreCase("Pagar a la entrega"))
				pagarPedidoPagarEnCaja(formaPago,pedidoConfirmadoString);
			if (formaPago.equalsIgnoreCase("saldo"))
				pagarPedidoSaldo(formaPago,formaPago2,pedidoConfirmadoString,totalEsperado, miMonederoString);
			
			Assert.assertTrue(true);	
		}
	}

	private void pagarPedidoSaldo(String formaPago, String formaPago2, String pedidoConfirmadoString, String totalEsperado , String miMonederoString) {
		log("Se paga el pedido con "+ formaPago);
		String saldoActual = driver.findElements(By.xpath("//div[contains(@class,'loyalty-card-info')]")).get(1).getAttribute("innerText");
		log("- El saldo actual de la tarjeta es  " + saldoActual);
		String caducidad = driver.findElements(By.xpath("//div[contains(@class,'loyalty-card-info')]")).get(0).getAttribute("innerText");
		log("- La caducidad de la tarjeta es  " + caducidad);

		if(!formaPago2.equalsIgnoreCase("")) {
			//TODO  Pendiente de implementar el pago en dos formas de pago. Saldo y Redsys. Crear cliente nuevo primero, hacer carga de poco saldo y usar en venta.
		}

		clicJS(driver.findElement(By.xpath("//div[contains(@class,'gift-card-logo')]")));//SELECCIONO FORMA DE PAGO
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
		w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
		validarReciboCheckout(pedidoConfirmadoString);
		validarSaldoRestante(saldoActual,totalEsperado, miMonederoString);
	}

	private void validarSaldoRestante(String saldoAnterior, String importeVenta, String miMonedero) { //VALIDA EL SALDO ANTERIOR CON EL NUEVO RESTADO DEL IMPORTE DE LA VENTA.
		log("Validando saldo restante en la tarjeta...");
		abrirMiMonedero(miMonedero);
		w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='Saldo disponible']/following::span[1]")));
		String saldoActual = driver.findElement(By.xpath("//span[text()='Saldo disponible']/following::span[1]")).getAttribute("innerText");
		saldoActual = saldoActual.replaceAll("€", "").replace(".","").trim();
		saldoAnterior = saldoAnterior.replaceAll("€", "").replace(".","").trim();
		importeVenta = importeVenta.replaceAll("€", "").replace(".","").trim();
		
		double saldoActualDouble = 0,saldoAnteriorDouble = 0,importeVentaDouble = 0;
		saldoActualDouble = Double.parseDouble(saldoActual.replace(',', '.'));
		saldoAnteriorDouble = Double.parseDouble(saldoAnterior.replace(',', '.'));
		importeVentaDouble = Double.parseDouble(importeVenta.replace(',', '.'));
	
		back();

		if(( round((saldoAnteriorDouble - importeVentaDouble), 1)) != (round(saldoActualDouble, 1))) {
			log("Saldo incorrecto despues de realizar venta - Saldo Actual: " + (round(saldoActualDouble, 1)) + " - Saldo anterior: " +  saldoAnteriorDouble + " - Importe venta: " +importeVentaDouble);
			Assert.assertTrue(false);
		}else {
			log("Saldo correcto despues de realizar venta - Saldo Actual: " + (round(saldoActualDouble, 1)) + " - Saldo anterior: " +  saldoAnteriorDouble + " - Importe venta: " +importeVentaDouble);		
		}


	}

	private static double round (double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}

	private void pagarPedidoPagarEnCaja(String formaPago, String pedidoConfirmadoString) {
		log("Se paga el pedido con "+ formaPago);
		clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
		w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
		validarReciboCheckout(pedidoConfirmadoString);

	}

	private void pagarPedidoConTarjeta(String formaPago, boolean nuevaTarjeta, String testCardNumber, String cad1, String cad2, String cvv, String pedidoConfirmado, String validarImporteMinimo) {

		log("Se paga el pedido con tarjeta");
		clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO

		List<WebElement> checkBoxes = driver.findElements(By.xpath("//div[contains(@class,'mat-checkbox-inner-container')]"));
		for(int i=0;i<checkBoxes.size();i++) {
			clicJS(checkBoxes.get(i));// MARCO CHECKS DE ACEPTO CONDICIONES
		}
		
		//clicJS(driver.findElement(By.xpath("//div[contains(@class,'mat-checkbox-inner-container')]"))); // MARCO CHECK DE ACEPTO CONDICIONES
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
		
		// Validar el importe minimo de delivery
		if((!StringUtils.isNullOrEmpty(this.tipoServicio) ) && !StringUtils.isNullOrEmpty(this.importeMinimo) && (!StringUtils.isNullOrEmpty(validarImporteMinimo) && validarImporteMinimo.equalsIgnoreCase("true"))) {
			espera(1000);
			validarImporteMinimoPedido(this.importeMinimo, this.totalEsperado);
			
			if(validarImporteMinimoPedido.equalsIgnoreCase("false")) {
				return;
			}
		}
		
		
		espera(5000);

		
		//TENEMOS PANTALLA DE REDSYS ABIERTA.
		if (nuevaTarjeta) {
			w2.until(ExpectedConditions.presenceOfElementLocated(By.id("inputCard")));
			log("- La tarjeta es nueva " + testCardNumber);
			driver.findElement(By.id("inputCard")).sendKeys(testCardNumber);
			driver.findElement(By.id("cad1")).sendKeys(cad1);
			driver.findElement(By.id("cad2")).sendKeys(cad2);
			driver.findElement(By.id("codseg")).sendKeys(cvv);
			driver.findElement(By.id("divImgAceptar")).click();
			w.until(ExpectedConditions.presenceOfElementLocated(By.id("boton"))).click(); //ACEPTAMOS SIMULADOR FINANET
		}
		
		

		w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'result-header')]")));
		
		

		if(isElementPresent(By.xpath("//div[contains(@class,'result-code ok')]"))) {
			log("- Respuesta de cobro correcta");
			
			driver.findElement(By.xpath("//input[contains(@class,'btn-continue')]")).click();//CLIC EN CONTINUAR
			
			//.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
			
			if(isElementPresent(By.id("orderReceiptHeader"))) {
				w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
			} else {
				w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'ticket-background')]")));				
			}
						
			validarReciboCheckout(pedidoConfirmado);
		}else {
			log("- No encuentro el resutlado ok en la pantalla de redsys");
			Assert.assertTrue(false);
		}
	}

	private void validarReciboCheckout(String pedidoConfirmadoString) {
		// ESTA PANTALLA ES UNA MIERDA, NO TIENE CLASES PARA IDENTIFICAR, HABLAR CON RAMON
		log("Esperando a la pantalla de Recibo electrónico");
		String numPedido = null;
		espera();
		if (!isElementPresent(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"))){
			log("No hemos encontrado pantalla del recibo electrónico");
			Assert.assertTrue(false);
		}

		driver.findElement(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"));
		log("Hemos llegado a la pantalla del recibo electrónico -> " + pedidoConfirmadoString);
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
		
		Data.getInstance().setPedidoActual(numPedido);
		log("el numero de pedido actual a comprobar:" + numPedido);
		if(isElementPresent(By.xpath("//button[@class='main-btn basket-button']"))) {
			driver.findElement(By.xpath("//button[@class='main-btn basket-button']")).click();//Pulsamos en volver al inicio
		}else {
			back(); //Desde la versión del 16/09/2022 el el botón de back tiene otro comportamiento y regresa a la ultima pantalla abierta. 
		}
			
		Assert.assertTrue(true);
	}

	private void back() {
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='keyboard_arrow_left']")));	
	}

	private boolean validaPantalla(String[] arrayNombres,String totalEsperado, String unidades, String totalEsperadoMasCargos, String repartoPermitido) {

		String basketTitle = w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'basket-ticket-title')]"))).getAttribute("innerText");
		String basketDate = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'basket-ticket-row')]"))).getAttribute("innerText"); 
		int unidadesInteger=1;
		List<WebElement> basketLines = driver.findElements(By.tagName("app-basket-line"));

		log("Validación de pantalla de resumen de pedido");
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

					log("- " + arrayNombres[i]);
				}	
			}
		}
		
		
		if(basketLines.size()!=arrayNombres.length) {
			log("- Error. Me faltan artículos en el resumen del basket, deberían haber " + arrayNombres.length);
			return false;
		}

		espera(1000);

		//VALIDAMOS PIE DE SUBTOTAL Y TOTAL
		driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][1]//div[contains(text(),'Subtotal')]"));
		driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][1]//div[contains(text(),'"+totalEsperado+"')]"));

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
			log("- Error. No tenemos titulo en sección de Formas de Pago.");
			return false;
		}

		espera(1000);
		List<WebElement> payMenMeans = driver.findElements(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(@class,'payment-name')]"));
		if (payMenMeans.size()==0) {
			log("- Error. No tenemos formas de pago visibles.");
			return false;
		}
		
		if (tipoServicio.equalsIgnoreCase("3") && repartoPermitido.equalsIgnoreCase("true")) { //TIPO DELIVERY
			
			if (!validaCarritoFlotante(String.valueOf((arrayNombres.length)*unidadesInteger),totalEsperadoMasCargos)) {
				log("- Error. El total del carrito flotante no tiene el importe esperado");
				return false;
			}
			
		}else {
			
			if (!validaCarritoFlotante(String.valueOf((arrayNombres.length)*unidadesInteger),totalEsperado)) {
				log("- Error. El total del carrito flotante no tiene el importe esperado");
				return false;
			}
		}

		return true;
	}

	private boolean validaCarritoFlotante(String productosEncontrados, String totalEsperado) {

		log("Validaciones de botón flotante de carrito");

		//VALIDAMOS UNIDADES TOTALES AÑADIDAS AL CARRITO Y VISIBLES EN EL BOTÓN FLOTANTE..
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-units')]")).getAttribute("innerText").equalsIgnoreCase(String.valueOf(productosEncontrados))) {
			log("- "+productosEncontrados + " productos añadidos");
		}else {
			log("- Error en validación de productos añadidos al carrito" );
			return false;
		}

		//VALIDAMOS IMPORTE TOTAL VISIBLE DESDE EL BOTÓN DEL CARRITO FLOTANTE
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-amount')]")).getAttribute("innerText").equalsIgnoreCase(String.valueOf(totalEsperado))) {
			log("- "+totalEsperado + " total validado");
		}else {
			log("- Error en validación de productos añadidos al carrito" );
			return false;
		}

		if(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")).getAttribute("innerText").equalsIgnoreCase("")) {
			log("- Error en validación del string del botón flotante del carrito, está vacio (debería aparecer algo como Ver pedido)" );
			return false;
		}

		log("Carrito flotante validado correctamente");

		return true;
	}

	public boolean contieneNombre(String[] arrayNombres, String nombre ) {

		for(int i = 0;i<arrayNombres.length;i++) {
			if (arrayNombres[i].equalsIgnoreCase(nombre))
				return true;
		}	
		return false;
	}

	private int getLastDoc__Doc(String db) {

		String queryMaxPedido = "select MAX(DocId) as DocId from Doc__Doc dd";
		ResultSet rs =  databaseConnection.ejecutarSQL(queryMaxPedido,"DB"+db); 

		if (rs!=null) {		
			try {
				rs.first();
				return rs.getInt("DocId");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				databaseConnection.desconectar();
			}
		}

		return 0;
	}

	public static double sGetDecimalStringAnyLocaleAsDouble (String value) {

		if (value == null) {
			return 0.0;
		}

		Locale theLocale = Locale.getDefault();
		NumberFormat numberFormat = DecimalFormat.getInstance(theLocale);
		Number theNumber;
		try {
			theNumber = numberFormat.parse(value);
			return theNumber.doubleValue();
		} catch (ParseException e) {
			// The string value might be either 99.99 or 99,99, depending on Locale.
			// We can deal with this safely, by forcing to be a point for the decimal separator, and then using Double.valueOf ...
			//http://stackoverflow.com/questions/4323599/best-way-to-parsedouble-with-comma-as-decimal-separator
			String valueWithDot = value.replaceAll(",",".");

			try {
				return Double.valueOf(valueWithDot);
			} catch (NumberFormatException e2)  {
				// This happens if we're trying (say) to parse a string that isn't a number, as though it were a number!
				// If this happens, it should only be due to application logic problems.
				// In this case, the safest thing to do is return 0, having first fired-off a log warning.
				return 0.0;
			}
		}
	}
	
	private int getUnidadesToAdd(List<String> products, String productName, List<Integer> unidades) {
		
		return unidades.get(products.indexOf(productName));
		
	
	}
	
	public void validarImporteMinimoPedido(String importeMinimo, String totalEsperado) {
		validarImporteMinimoPedido = "true";
		Double iMinimo = null;
		Double sTotal = null;
		Double saldoACompletar = null;
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'price-breakdown-line')]/child::div[2]")));
		List<WebElement> allPrice = driver.findElements(By.xpath("//div[contains(@class, 'price-breakdown-line')]/child::div[2]"));
		
		if(allPrice.size() != 0 && allPrice.size() >= 0){
			
			WebElement subTotalWeb = allPrice.get(0);
			String subTotal = subTotalWeb.getAttribute("innerText");
			subTotal = subTotal.replaceAll("€", "").replace(",",".").trim();
			sTotal = Double.parseDouble(subTotal);
			iMinimo = Double.parseDouble(importeMinimo.replaceAll("€", "").replace(",",".").trim());
			
			WebElement deliveryChargeWeb = allPrice.get(1);
			String deliveryCharge = deliveryChargeWeb.getAttribute("innerText");
			deliveryCharge = deliveryCharge.replaceAll("€", "").replace(",",".").trim();
			
			
			WebElement TotalPriceWeb = allPrice.get(2);
			String TotalPrice = TotalPriceWeb.getAttribute("innerText");
			TotalPrice = TotalPrice.replaceAll("€", "").replace(",",".").trim();
			
			if(sTotal < iMinimo) {
				saldoACompletar = iMinimo - sTotal;
				validarImporteMinimoPedido = "false";
				log("la venta no cumple el requesito del importe minimo ("+ importeMinimo +") esperado  para entregrar el pedido");
				log("la venta falta la cantidad de ( "+ String.valueOf(saldoACompletar) +"€ ) esperado  para entregrar el pedido");
			}
			
		} else {
			validarImporteMinimoPedido = "false";
			log("no tenemos todos los precios para verificar el importe minimo");
			Assert.assertTrue(false);
		}
		
		if(validarImporteMinimoPedido.equalsIgnoreCase("false") && (sTotal < iMinimo)) {
			String saldoCompletar = String.valueOf(saldoACompletar);
			saldoCompletar = saldoCompletar + "€";
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//app-showmessage//div[contains(@class, 'msg-dialog-text main-text')]"))); // message
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//app-showmessage//div[contains(@class, 'msg-dialog-buttons')]/child::button[contains(@class, 'btn-confirm')]"))); //button aceptar
			
			WebElement messageWeb = driver.findElement(By.xpath("//app-showmessage//div[contains(@class, 'msg-dialog-text main-text')]"));
			String message = messageWeb.getAttribute("innerText");
			
			WebElement buttonAccept = driver.findElement(By.xpath("//app-showmessage//div[contains(@class, 'msg-dialog-buttons')]/child::button[contains(@class, 'btn-confirm')]"));

			if(message.split(saldoCompletar).length > 0 ) {
				buttonAccept.click();
				log("la cantidad ("+ saldoCompletar +") que falta existe en el mensaje");
			} else {
				log("la cantidad ("+ saldoCompletar +") que falta no existe en el mensaje");
			}
			
		} 
		
	}
}
