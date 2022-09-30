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

import utils.Data;
import utils.TestBase;

public class CheckOut extends TestBase{

	String tipoServicio="";

	@Test(description="Se encarga de gestionar la parte de checkout una vez los productos ya están añadidos al carrito", priority=1)
	@Parameters({"productos","totalEsperado","formaPago","nuevaTarjeta","testCardNumber","cad1","cad2","cvv","pedidoConfirmadoString" , "shop", "email", "miMonederoString",
		"formaPago2", "tipoServicio","unidades","mesa", "totalEsperadoMasCargos", "repartoPermitido"})
	public void finalizarPedido(String productos, String totalEsperado, String formaPago,
			@Optional ("true") String nuevaTarjeta, @Optional ("4548812049400004") String testCardNumber,
			@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv, String pedidoConfirmadoString, 
			String shop, String customerMail, @Optional ("")String miMonederoString, @Optional ("") String formaPago2, 
			String tipoServicio, @Optional ("") String unidades, @Optional ("") String mesa, @Optional ("") String totalEsperadoMasCargos,
			@Optional ("true") String repartoPermitido) {

		String[] arrayNombres;
		this.tipoServicio=tipoServicio;
		arrayNombres = productos.split(",");
		//SI ES NUEVO USUARIO EL CORREO QUE NOS VIENE DEL TEST NO ES VÁLIDO.
		Data.getInstance().setUltimoDocId(getLastDoc__Doc(shop));//RECUPERAMOS ULTIMO DOCDOCID DEL CLIENTE ANTES DE REALIZAR EL PEDIDO	
		WebElement checkoutButton = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'basket-button')]")));
		checkoutButton.click();
		espera(1000);
		
		w2 = new WebDriverWait(TestBase.driver,Duration.ofSeconds(60)); // LE DAMOS 60 SEGUNDOS PARA HACER EL CHECKOUT.
		if((customerMail.equalsIgnoreCase("") || customerMail.equalsIgnoreCase("invitado@portalrest.com")) && formaPago.equalsIgnoreCase("Redsys Test"))nuevaTarjeta="true"; //SI NO TENEMOS CLIENTE SIGNIFICA QUE ES NUEVO Y NECESITAREMOS TARJETA NUEVA
		if(!(validaPantalla(arrayNombres,totalEsperado,unidades,totalEsperadoMasCargos,repartoPermitido)))Assert.assertTrue(false);
		if(formaPago.equalsIgnoreCase("Redsys Test"))
			pagarPedidoConTarjeta(formaPago, nuevaTarjeta.equalsIgnoreCase("true"),testCardNumber,cad1,cad2,cvv,pedidoConfirmadoString);
		if (formaPago.equalsIgnoreCase("Pagar a la entrega"))
			pagarPedidoPagarEnCaja(formaPago,pedidoConfirmadoString);
		if (formaPago.equalsIgnoreCase("saldo"))
			pagarPedidoSaldo(formaPago,formaPago2,pedidoConfirmadoString,totalEsperado, miMonederoString);
		Assert.assertTrue(true);	  
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

		if(( round((saldoAnteriorDouble-importeVentaDouble),1))!=(round(saldoActualDouble,1))) {
			log("Saldo incorrecto despues de realizar venta - Saldo Actual: " + (round(saldoActualDouble,1)) + " - Saldo anterior: " +  saldoAnteriorDouble + " - Importe venta: " +importeVentaDouble);
			Assert.assertTrue(false);
		}else {
			log("Saldo correcto despues de realizar venta - Saldo Actual: " + (round(saldoActualDouble,1)) + " - Saldo anterior: " +  saldoAnteriorDouble + " - Importe venta: " +importeVentaDouble);		
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

	private void pagarPedidoConTarjeta(String formaPago, boolean nuevaTarjeta, String testCardNumber, String cad1, String cad2, String cvv, String pedidoConfirmado) {

		log("Se paga el pedido con tarjeta");
		clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO

		List<WebElement> checkBoxes = driver.findElements(By.xpath("//div[contains(@class,'mat-checkbox-inner-container')]"));
		for(int i=0;i<checkBoxes.size();i++) {
			clicJS(checkBoxes.get(i));// MARCO CHECKS DE ACEPTO CONDICIONES
		}
		
		//clicJS(driver.findElement(By.xpath("//div[contains(@class,'mat-checkbox-inner-container')]"))); // MARCO CHECK DE ACEPTO CONDICIONES
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
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
			w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
			validarReciboCheckout(pedidoConfirmado);
		}else {
			log("- No encuentro el resutlado ok en la pantalla de redsys");
			Assert.assertTrue(false);
		}
	}

	private void validarReciboCheckout(String pedidoConfirmadoString) {
		// ESTA PANTALLA ES UNA MIERDA, NO TIENE CLASES PARA IDENTIFICAR, HABLAR CON RAMON
		log("Esperando a la pantalla de Recibo electrónico");
		espera();
		if (!isElementPresent(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"))){
			log("No hemos encontrado pantalla del recibo electrónico");
			Assert.assertTrue(false);
		}

		driver.findElement(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"));
		log("Hemos llegado a la pantalla del recibo electrónico -> " + pedidoConfirmadoString);
		espera();
		
		
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
			
			driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][3]//div[contains(text(),'Total')]"));
			driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][3]//div[contains(text(),'"+totalEsperadoMasCargos+"')]"));
	
		}else {
			driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][2]//div[contains(text(),'Total')]"));
			driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][2]//div[contains(text(),'"+totalEsperado+"')]"));
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

}
