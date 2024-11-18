package Ventas;

import java.math.RoundingMode;
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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mysql.cj.util.StringUtils;

import Clientes.AbrirPaginaRegistro;
import Clientes.CrearCliente;
import utils.Data;
import utils.RetryTestsFailed;
import utils.TestBase;
import utils.getDummyData;

public class CheckOut extends TestBase{

	String tipoServicio="";
	String validarImporteMinimoPedido = null;
	String importeMinimo = null;
	String totalEsperado = null;
	String mesaEsperada="";
	

	@Test(description="Se encarga de gestionar la parte de checkout una vez los productos ya están añadidos al carrito", priority=1, groups = { "checkOut" })
	@Parameters({"productos","totalEsperado","formaPago","nuevaTarjeta","testCardNumber","cad1","cad2","cvv","pedidoConfirmadoString" , "shop", "email", "miMonederoString",
		"formaPago2", "tipoServicio","unidades","mesa", "totalEsperadoMasCargos", "repartoPermitido", "goBack", "goBackByAddOrderButton", "importeMinimo", "validarImporteMinimo"})
	public void finalizarPedido(String productos, String totalEsperado, String formaPago,
			@Optional ("true") String nuevaTarjeta, @Optional ("4548810000000003") String testCardNumber,
			@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv, String pedidoConfirmadoString, 
			String shop, String customerMail, @Optional ("")String miMonederoString, @Optional ("") String formaPago2, 
			String tipoServicio, @Optional ("") String unidades, @Optional ("") String mesa, @Optional ("") String totalEsperadoMasCargos,
			@Optional ("true") String repartoPermitido, @Optional ("") String goBack, @Optional ("") String goBackByAddOrderButton, @Optional ("") String importeMinimo, @Optional ("") String validarImporteMinimo) {

		String[] arrayNombres;
		mesaEsperada = mesa;
		this.tipoServicio=tipoServicio;
		arrayNombres = productos.split(",");
		this.importeMinimo = importeMinimo;
		this.totalEsperado = totalEsperado;
		//SI ES NUEVO USUARIO EL CORREO QUE NOS VIENE DEL TEST NO ES VÁLIDO.
		Data.getInstance().setUltimoDocId(getLastDoc__Doc(shop));//RECUPERAMOS ULTIMO DOCDOCID DEL CLIENTE ANTES DE REALIZAR EL PEDIDO	
		//w = new WebDriverWait(TestBase.driver, Duration.ofSeconds(120));
		WebElement checkoutButton = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'basket-button')]")));
		checkoutButton.click();
		espera(1000);
		
		//Recuperar el email del usuario si es un nuevo cliente creado:
		if(!isNullOrEmpty(Data.getInstance().getNewUserMail()) && isNullOrEmpty(customerMail)) {
			customerMail= Data.getInstance().getNewUserMail();
		}
		
		if(customerMail.equalsIgnoreCase("comoInvitado")) {
			//Ne debe aparecer una forma de pago pendiente
			if(isElementPresent(By.xpath("//div[contains(@class, 'payment-name line-clamp-2') and contains(text(), 'Pagar a la entrega')]"))) {
				log("No debe aparecer una forma de pago pendiente!!!");
				Assert.assertTrue(false);
			}
			
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'login-ask-wrapper')]/child::button[2]")));
			clicJS(driver.findElement(By.xpath("//div[contains(@class, 'login-ask-wrapper')]/child::button[2]")));
			espera(1000);
		}
		
		if(isNullOrEmpty(customerMail) && isNullOrEmpty(Data.getInstance().getNewUserMail())) { //Registrar el ususario y recuperar su email antes de continuar la venta
			
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'login-ask-wrapper')]/child::button[1]")));
			clicJS(driver.findElement(By.xpath("//div[contains(@class, 'login-ask-wrapper')]/child::button[1]")));
			espera(2000);
			
			AbrirPaginaRegistro openSignPage = new AbrirPaginaRegistro();
			
			openSignPage.abrePaginaRegistro("");
			
			CrearCliente newUser = new CrearCliente();
			
			newUser.crearCliente(true, true, false, false, "", "", "", shop, "", "");
			espera(1000);
			if(newUser.isCreatedUser) {
				customerMail = Data.getInstance().getNewUserMail();
			}
			//crearCliente(@Optional ("true") boolean resultadoEsperado, @Optional ("true") boolean aceptoTerminos, @Optional ("false") boolean IcgCloud, @Optional ("false") boolean validationCliente,
			//  @Optional("") String menu, @Optional("") String profile, @Optional("") String personal) 
		}
		
		
		
		w2 = new WebDriverWait(TestBase.driver,Duration.ofSeconds(60)); // LE DAMOS 60 SEGUNDOS PARA HACER EL CHECKOUT.
		if(customerMail.equalsIgnoreCase("comoInvitado") || (customerMail.equalsIgnoreCase("") || customerMail.equalsIgnoreCase("invitado@portalrest.com")) && formaPago.equalsIgnoreCase("Redsys Test"))nuevaTarjeta="true"; //SI NO TENEMOS CLIENTE SIGNIFICA QUE ES NUEVO Y NECESITAREMOS TARJETA NUEVA
		
		espera(1000);
		if(!(validaPantalla(arrayNombres,totalEsperado,unidades,totalEsperadoMasCargos,repartoPermitido)))Assert.assertTrue(false);
		
		// Usar back para añadi mas productos al final del proceso de la venta.
		if(goBack.equalsIgnoreCase("true")) {
			if(goBackByAddOrderButton.equalsIgnoreCase("true")) {
				w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'more-units-wrapper')]/child::mat-icon")));
				WebElement addOrderBackButton = driver.findElement(By.xpath("//div[contains(@class,'more-units-wrapper')]/child::mat-icon"));
				addOrderBackButton.click();
				espera(1000); // Wait for go back by using add order button
			} else {	
				driver.navigate().back();
			}
			Assert.assertTrue(true);
		} else {
			espera(1000);
			
			if(formaPago.equalsIgnoreCase("Redsys Test")) {
				pagarPedidoConTarjeta(formaPago, nuevaTarjeta.equalsIgnoreCase("true"),testCardNumber,cad1,cad2,cvv,pedidoConfirmadoString, validarImporteMinimo);
			}
			
			if (formaPago.equalsIgnoreCase("Pagar a la entrega")) {
				pagarPedidoPagarEnCaja(formaPago,pedidoConfirmadoString);
			}
			
			if (formaPago.equalsIgnoreCase("saldo") || formaPago.equalsIgnoreCase("combinado")) {
				pagarPedidoSaldo(formaPago, formaPago2, pedidoConfirmadoString, totalEsperado, miMonederoString, validarImporteMinimo, nuevaTarjeta, testCardNumber, cad1, cad2, cvv);
			}
			
			Assert.assertTrue(true);
			
		}
	}

	private void pagarPedidoSaldo(String formaPago, String formaPago2, String pedidoConfirmadoString, String totalEsperado , String miMonederoString, String validarImporteMinimo, 
			@Optional ("true") String nuevaTarjeta, @Optional ("4548810000000003") String testCardNumber,
			@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv) {
		log("Se paga el pedido con "+ formaPago);
		
		if(!isElementPresent(By.xpath("//div[contains(@class,'loyalty-card-info')]"))) {
			log("No se puede combinar dos formas de pago: La tarjeta de fidelizacion no tiene saldo");
			log("Primero, Cargar la tarjet de fidelizacion antes de combinar la forma de pago!!!");
			Assert.assertTrue(false);
		}
		
		String saldoActual = driver.findElements(By.xpath("//div[contains(@class,'loyalty-card-info')]")).get(1).getAttribute("innerText");
		log("- El saldo actual de la tarjeta es  " + saldoActual);
		String caducidad = driver.findElements(By.xpath("//div[contains(@class,'loyalty-card-info')]")).get(0).getAttribute("innerText");
		log("- La caducidad de la tarjeta es  " + caducidad);

		if(formaPago.equalsIgnoreCase("combinado")) {
			log("Forma de pago: Combinado");
			//TODO  Pendiente de implementar el pago en dos formas de pago. Saldo y Redsys. Crear cliente nuevo primero, hacer carga de poco saldo y usar en venta.
			
			clicJS(driver.findElement(By.xpath("//div[contains(@class,'gift-card-logo')]")));//SELECCIONO FORMA DE PAGO
			
			String saldo = saldoActual.replaceAll("€", "").replace(",",".").trim();
			Double saldoDisponible = Double.parseDouble(saldo);
			
			String totalAPagar = totalEsperado.replaceAll("€", "").replace(",",".").trim();
			Double precioTotal = Double.parseDouble(totalAPagar);
			
			Double faltaPagar = precioTotal - saldoDisponible;
			DecimalFormat formato1 = new DecimalFormat("#.00");
			
			saldo = formato1.format(saldoDisponible);
			saldo = String.valueOf(saldoDisponible).replace(".", ",") + "€";
			log("Saldo de la tarjeta de fidelización: "+ saldo);
			
			String precioFalta = formato1.format(faltaPagar);
			precioFalta = precioFalta.replace(".", ",") + "€";
			log("Lo que falta del saldo de la tarjeta de fidelizacion para pagar el pedido : "+ precioFalta);
			
			//Si estamos con pago combinado(Saldo tarjeta de fidelizacion inferior al precio del pedido a pagar) se mostra la pantalla de dialogo que nos pide confirmar
			if(faltaPagar == 0) {
				log("No se puede combinar dos formas de pago: La tarjeta de fidelizacion no tiene saldo.");
				log("Primero, Cargar la tarjet de fidelizacion antes de combinar la forma de pago!!!");
				Assert.assertTrue(false);
			} else if (saldoDisponible > precioTotal) {
				log("No se puede combinar dos formas de pago: El Saldo de la tarjeta de fidelizacion es suficiente.");
				log("El Saldo de la tarjeta de fidelizacion: " +saldo);
				Assert.assertTrue(false);
			} else if(isElementPresent(By.xpath("//div[contains(@class,'dialog-container')]")) && faltaPagar > 0) {	
				w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'"+saldoActual+"')][contains(text(),'"+precioFalta+"')]")));
				
				driver.findElement(By.xpath("//div[contains(text(),'"+saldoActual+"')][contains(text(),'"+precioFalta+"')]"));
				espera(500);
				w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'btn-confirm btn-centered')]")));
				clicJS(driver.findElement(By.xpath("//button[contains(@class,'btn-confirm btn-centered')]")));  //Aceptar
				
				log("Añadir la secunda forma de pago tarjeta Redsys a combinar");
				boolean isNewCard = false;
				if (nuevaTarjeta.equalsIgnoreCase("true"))
					isNewCard = true;
				
				pagarPedidoConTarjeta(formaPago2, isNewCard, testCardNumber, cad1, cad2, cvv, pedidoConfirmadoString, validarImporteMinimo);
				
				//Validar el saldo restante si es "0,00€", despues de realizar la venta.
				log("Validando saldo restante en la tarjeta...");
				abrirMiMonedero(miMonederoString);
				String saldoRestante = "0,00€";
				w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='Saldo disponible']/following::span[1]")));
				String saldoReal = driver.findElement(By.xpath("//span[text()='Saldo disponible']/following::span[1]")).getAttribute("innerText");
				if(!saldoReal.equalsIgnoreCase(saldoRestante)) {
					log("El Saldo("+ saldoReal +") encuentrado despues de realizar la venta no es correcto y debe ser " + saldoRestante);
					Assert.assertTrue(false);
				}
				
				log("El Saldo("+ saldoReal +") encuentrado despues de realizar la venta es correcto");
				Assert.assertTrue(true);
				
			} else {
				log("No se puede combinar dos formas de pago: Error. aalgo ha pasado");
				Assert.assertTrue(false);
			}

		} else if(formaPago.equalsIgnoreCase("saldo")){
			clicJS(driver.findElement(By.xpath("//div[contains(@class,'gift-card-logo')]")));//SELECCIONO FORMA DE PAGO - TARJETA DE FIDELIZACION
			clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
			w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
			validarReciboCheckout(pedidoConfirmadoString);
			validarSaldoRestante(saldoActual,totalEsperado, miMonederoString);
		}
		
	}
	
	//Saldo restante para tarjeta de fidelización 
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
		if(( round((saldoAnteriorDouble - importeVentaDouble), 2)) != (round(saldoActualDouble, 2))) {
			log("Saldo incorrecto despues de realizar venta - Saldo Actual: " + (round(saldoActualDouble, 2)) + " - Saldo anterior: " +  saldoAnteriorDouble + " - Importe venta: " +importeVentaDouble);
			Assert.assertTrue(false);
		}else {
			log("Saldo correcto despues de realizar venta - Saldo Actual: " + (round(saldoActualDouble, 2)) + " - Saldo anterior: " +  saldoAnteriorDouble + " - Importe venta: " +importeVentaDouble);		
		}


	}

//	private static double round (double value, int precision) {
//		int scale = (int) Math.pow(10, precision);
//		return (double) Math.round(value * scale) / scale;
//	}

	private void pagarPedidoPagarEnCaja(String formaPago, String pedidoConfirmadoString) {
		log("Se paga el pedido con "+ formaPago);
		clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
		w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
		validarReciboCheckout(pedidoConfirmadoString);

	}

	private void pagarPedidoConTarjeta(String formaPago, boolean nuevaTarjeta, String testCardNumber, String cad1, String cad2, String cvv, String pedidoConfirmado, String validarImporteMinimo) {

		log("Se paga el pedido con tarjeta formaPago " + formaPago);
		
		String formaPagoXpath = "//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]";
		
		waitUntilPresence(formaPagoXpath, true, false);
		
		w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
		espera(2000);
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
			espera(2000);
			w2.until(ExpectedConditions.presenceOfElementLocated(By.id("card-number")));
			//w2.until(ExpectedConditions.presenceOfElementLocated(By.id("inputCard")));
			log("- La tarjeta es nueva " + testCardNumber);
			driver.findElement(By.id("card-number")).sendKeys(testCardNumber);
			driver.findElement(By.id("card-expiration")).sendKeys(cad1);
			driver.findElement(By.id("card-expiration")).sendKeys(cad2);
			driver.findElement(By.id("card-cvv")).sendKeys(cvv);
			//driver.findElement(By.id("cad1")).sendKeys(cad1);
			//driver.findElement(By.id("cad2")).sendKeys(cad2);
			//driver.findElement(By.id("codseg")).sendKeys(cvv);
			driver.findElement(By.id("divImgAceptar")).click();
			espera(2000);
			w.until(ExpectedConditions.presenceOfElementLocated(By.id("boton"))).click(); //ACEPTAMOS SIMULADOR FINANET
			espera(500);
		}
		
		

		w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'result-header')]")));
		espera(2000);
		

		if(isElementPresent(By.xpath("//div[contains(@class,'result-code ok')]"))) {
			log("- Respuesta de cobro correcta");
			
			driver.findElement(By.xpath("//input[contains(@class,'btn-continue')]")).click();//CLIC EN CONTINUAR
			espera(2000);
			
			espera(2000);
			if(validarPantallaRecibo()) {
				log("Encuentro la pantalla del Recibo electrónico");
			}
			espera(2000);
			//espera(1000);			
			validarReciboCheckout(pedidoConfirmado);
		} else {
			log("- No encuentro el resutlado ok en la pantalla de redsys");
			if(isElementPresent(By.xpath("//div//child::text[contains(@lngid, 'noSePuedeRealizarOperacion')]"))) {
				
				log("No se puede realizar la operación\r\n"
						+ "El sistema está ocupado, inténtelo más tarde (SIS0038)");
			}
			Assert.assertTrue(false);
		}
	}

	private void validarReciboCheckout(String pedidoConfirmadoString) {
		// ESTA PANTALLA ES UNA MIERDA, NO TIENE CLASES PARA IDENTIFICAR, HABLAR CON RAMON
		log("Esperando a la pantalla de Recibo electrónico");
		String numPedido = null;
		//espera(1000);
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
		
		if (isElementPresent(By.xpath("//span[contains(text(),'En la mesa 0')]"))) {
			log("Se encontro el literal En la mesa 0");
			Assert.assertTrue(false,"No se muestra el número de mesa correcto en el recibo de pedido confirmado.");
		}
		
		if(!mesaEsperada.equalsIgnoreCase("")) { // Espero una mesa porque es un pedido con mesa
			if (isElementPresent(By.xpath("//span[contains(text(),'En la mesa "+mesaEsperada+"')]"))) {
				log("Mesa esperada presente en el tiquet " + mesaEsperada);
			}
			else {
				log("Mesa esperada "+mesaEsperada+ " no está presente en el tiquet ");
			}
		}
		
		if(isNullOrEmpty(numPedido)) {
			log("No se ha encontrado el numero de pedido");
			Assert.assertTrue(false);
		}
		
		Data.getInstance().setPedidoActual(numPedido);
		log("el numero de pedido actual a comprobar:" + numPedido);
		if(isElementPresent(By.xpath("//button[@class='main-btn basket-button']"))) {
			driver.findElement(By.xpath("//button[@class='main-btn basket-button']")).click();//Pulsamos en volver al inicio
			//Verificamos si siguemos siendo en la misma página o no
			// #oscar Este metodo para verificar si tengo el botón de basket visible hace fallat el test aunque lo unico que quremos es verificar que hemos salido de la pantalla donde estavamos.
			// lo sustituimos por otro.
			//WebElement botonVolverAlInicio = getElementByFluentWait(By.xpath("//button[@class='main-btn basket-button']"), 40, 5); 
			espera(5000); //#oscar
			if (!isElementPresent(By.xpath("//button[@class='main-btn basket-button']"))){ //#oscar
				System.out.println("Ya no me encuentro en la pantalla de finalización del pedido, sigo adelante"); //#oscar
			}else { //#oscar
				WebElement botonVolverAlInicio = getElementByFluentWait(By.xpath("//button[@class='main-btn basket-button']"), 40, 5);  //#oscar
				Assert.assertFalse(isElementPresent(botonVolverAlInicio), "Error: No se ha podido volver a la página inicial debido al fallo del botón"); //#oscar

			}
			
			
			log("Ahora estamos a la página inicial de la tienda.");
		}else {
			back(); //Desde la versión del 16/09/2022 el el botón de back tiene otro comportamiento y regresa a la ultima pantalla abierta. 
		}
		
		//Save el numero de pedido en Object pedido/Sirve para futuro validación por ejemplo en Pedio más tarde y Día y hora
		Data.getInstance().getPedido().setNumeroPedido(numPedido);
		
		Assert.assertTrue(true);
	}

//	private void back() {
//		clicJS(driver.findElement(By.xpath("//mat-icon[text()='keyboard_arrow_left']")));	
//	}

	private boolean validaPantalla(String[] arrayNombres,String totalEsperado, String unidades, String totalEsperadoMasCargos, String repartoPermitido) {
		String basketDateElementXpath = null;
		String basketTitle = w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'basket-ticket-title')]"))).getAttribute("innerText");

		basketDateElementXpath = "//div[contains(@class,'basket-ticket-header-wrapper')]";
			
		//Testear que tenemos la información de la fecha pedido
		String basketDate = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath(basketDateElementXpath))).getAttribute("innerText"); 
		
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
		
		//Save precio total a pagar del pedido
		//Save el precio total a pagar
		Data.getInstance().getPedido().setPrecioTotal(totalEsperado);
		
		return true;
	}

	private boolean validaCarritoFlotante(String productosEncontrados, String totalEsperado) {

		log("Validaciones de botón flotante de carrito");

		//VALIDAMOS UNIDADES TOTALES AÑADIDAS AL CARRITO Y VISIBLES EN EL BOTÓN FLOTANTE..
		if(driver.findElement(By.xpath("//div[contains(@class,'basket-button-units')]")).getAttribute("innerText").equalsIgnoreCase(String.valueOf(productosEncontrados))) {
			log("- "+productosEncontrados + " productos añadidos");
		} else {
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
	
	public boolean validarPantallaRecibo() {
		espera(2000);
		
		String matSpinner = "//mat-spinner[contains(@class, 'mat-spinner mat-progress-spinner mat-primary mat-progress-spinner-indeterminate-animation')]";
		w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(matSpinner)));
		
		espera(2000);
		if(isElementPresent(By.id("orderReceiptHeader"))) {
			log("ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO POR ID orderReceiptHeader.");
			w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
			return true;
		} 
		
		espera(2000);
		if(isElementPresent(By.xpath("//div[contains(@class, 'ticket-background')]"))) {
			log("ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO POR class ticket-background.");
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'ticket-background')]")));
			return true;
		} 
		
		espera(2000);	
		if(isElementPresent(By.xpath("//img[contains(@alt, 'Redsys')]")) || isElementPresent(By.xpath("//img[contains(@alt, 'Mantemimiento del servicio')]")) || 
				isElementPresent(By.xpath("//h1[contains(text(), 'Estamos realizando tareas de mantenimiento, vuelva a intentarlo más tarde')]")) || 
				isElementPresent(By.xpath("//span[contains(text(), 'Disculpen las molestias')]"))) {
			log("Redsys ha fallado.");
			log("Pantalla Redsys error | 500 ");
			espera(1000);
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[contains(@alt, 'Redsys')]")));
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//img[contains(@alt, 'Mantemimiento del servicio')]")));
		
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h1[contains(text(), 'Estamos realizando tareas de mantenimiento, vuelva a intentarlo más tarde')]")));
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h1[contains(text(), 'We are working on maintenance tasks, please try again later')]")));
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[contains(text(), 'Disculpen las molestias')]")));
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[contains(text(), 'Apologize for the inconvenience')]")));
			log("Redsys ha fallado.");
			log("Pantalla Redsys error | 500 ");
		}
		log("No se ha podido validar la pantalla del recibo !!!");
		Assert.assertTrue(false);
		return false;		
	}
}
