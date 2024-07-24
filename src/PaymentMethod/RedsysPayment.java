package PaymentMethod;

import static org.testng.Assert.assertTrue;

import java.util.List;

//package com.mysql.cj.util;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import enums.PaymentGateways;
import utils.Data;

public class RedsysPayment extends Payment {
	
	String tipoServicio="";
	String validarImporteMinimoPedido = null;
	String importeMinimo = null;
	String totalEsperado = null;
	
	PaymentGateways redsyPayment = PaymentGateways.REDSYS;

	boolean fakePhone;
	
	@Test(description = "Prueba de venta simple EN EL LOCAL con 4 artículos normales y pago con Bizum", priority = 1)
	@Parameters({"totalEsperado",  "shop", "cancelarPago", "pagoDenegadoPorBanco", "fakePhone"})
	public void makePayment(String importe, String shop, @Optional("false") boolean cancelarPago, @Optional("false") boolean pagoDenegadoPorBanco, @Optional("false")  boolean fakeIban, @Optional("false") boolean nuevaTarjeta) {
		this.importe = importe;
		this.shop = shop;
		this.cancelarPago = cancelarPago;
		this.pagoDenegadoPorBanco = pagoDenegadoPorBanco;
		this.fakePhone = fakePhone;
		
		Data.getInstance().setUltimoDocId(getLastDoc__Doc(shop));//RECUPERAMOS ULTIMO DOCDOCID DEL CLIENTE ANTES DE REALIZAR EL PEDIDO	
				
		//Validar que estamos en la pantalla Resumen pedido donde elegimos el medio de pago y continuar con el pago
		WebElement pantallaPago =  getElementByFluentWait(By.xpath(PaymentResource.pantallaResumenPedido), 40, 10);
		
		Assert.assertFalse(StringUtils.isAllBlank(pantallaPago.getAttribute("innerText"), "Error: No hemos encontrado la pantalla de pago RESUMEN PEDIDO"));
		
		espera(500);
		//Seleccionar el medio de pago Bizum
		Assert.assertTrue(isElementPresent(By.xpath(PaymentResource.getButtonFormaPagoXpath("Redsys"))), "El medio de pago Bizum no exite en el listado de pasarelas de pago");
		
		clicJS(driver.findElement(By.xpath(PaymentResource.getButtonFormaPagoXpath("Redsys"))));
		espera(500);
		
		//TENEMOS PANTALLA DE REDSYS ABIERTA.
		if (nuevaTarjeta) {
			espera(2000);
			//w2.until(ExpectedConditions.presenceOfElementLocated(By.id("inputCard")));
			w2.until(ExpectedConditions.presenceOfElementLocated(By.id("card-number")));
			
			espera(2000);
			w.until(ExpectedConditions.presenceOfElementLocated(By.id("boton"))).click(); //ACEPTAMOS SIMULADOR FINANET
			espera(500);
		}
		
		//CheckConditions 
		List<WebElement> checkBoxes = driver.findElements(By.xpath(PaymentResource.checkBoxeXpath));
		
		for(int i=0;i<checkBoxes.size();i++) {
			clicJS(checkBoxes.get(i));// MARCO CHECKS DE ACEPTO CONDICIONES
		}
				
		//Clicar el botón pagar
		clicJS(driver.findElement(By.xpath(PaymentResource.botonPagarXpath)));
		espera(2000);
		
		if(!this.cancelarPago && !this.fakePhone) {
			//Validacion del pago en la plataforma del banco
			isPaymentDone (importe);
			pagoAceptado();
			espera(1500);
		}
		else if(this.cancelarPago ) {
			isPaymentDone (importe) ;
			
			if(!pagoDenegadoPorBanco) {
				log("Pago cancelado por parte del cliente");
				clicJS(driver.findElement(By.xpath(PaymentResource.Bizum.botonCancelarXpath)));
				espera(1500);
			} else {
				clicJS(driver.findElement(By.xpath(PaymentResource.Bizum.getBotonContunarXpath(false))));
			}
			
		}
		else if(this.fakePhone) {
			//validar que no se ha podido pagar por que el usuario no está activo para compra Bizum
			if(!isPaymentDone (importe)) {
				log("Fallo al pagar el pedido con usurio no activo para compra Bizum");
				Assert.assertFalse(false, "Hubo error al pagar el pedio con Bizum");
			}
			espera(800);
						
			//vuelver a intentarlo con numero activo para compra Bizum
			Data.getInstance().getPedido().setPrecioTotal(importe);
			pagoAceptado();
			espera(1500);
		}
		else {
			Assert.assertTrue(false, "Error: Se ha encontrado fallo en le proceso de pago Bizum");
		}
	}
	
	private boolean isPaymentDone (String importe) {
		
		try {
			//validar pasos de pago con Bizum
			WebElement pantallaPagoDelBanco =  getElementByFluentWait(By.xpath(PaymentResource.Bizum.waitFormularioBizumXpath), 40, 10);
			Assert.assertFalse(StringUtils.isAllBlank(pantallaPagoDelBanco.getAttribute("InnerText"), "Error: No hemos encontrado la pantalla de pago RESUMEN PEDIDO"));
			
			//validar que el boton continuar está deshabilitado
			Assert.assertTrue(isElementPresent(By.xpath(PaymentResource.Bizum.getBotonContunarXpath(true))), "Error: El botón Continuar debería estar deshabilitado !!!");
			
			//Validar el importe a pagar
			
			Assert.assertTrue(isElementPresent(By.xpath(PaymentResource.Bizum.importApagarXpath)), "Error: No hemos encontrado el input del importe");
			String importeEncontradoApagar = driver.findElement(By.xpath(PaymentResource.Bizum.importApagarXpath)).getText();
			
			Assert.assertTrue(importeEncontradoApagar.equalsIgnoreCase(importe), "Error: Importe encontrado " + importeEncontradoApagar +" no es el importe esperado " + importe);
			
			//introducir el numero de telefono
			Assert.assertTrue(isElementPresent(By.xpath(PaymentResource.Bizum.inputTelefoneXpath)), "Error: No hemos encontrado el input del telefono");
			
			WebElement inputPhone = driver.findElement(By.xpath(PaymentResource.Bizum.inputTelefoneXpath));
			
			if(this.fakePhone)  inputPhone.sendKeys(redsyPayment.getPayment().getFakePhone()); else  inputPhone.sendKeys(redsyPayment.getPayment().getTelephone());
			
			//validar que el boton continuar está habilitado
			Assert.assertTrue(isElementPresent(By.xpath(PaymentResource.Bizum.getBotonContunarXpath(false))), "Error: El botón Continuar debería estar habilitado al insertar el telefono !!!");
			
			//pulsar continuar
			if (!this.cancelarPago) {
				clicJS(driver.findElement(By.xpath(PaymentResource.Bizum.getBotonContunarXpath(false))));
				
				//Al tener un usuario no activo para compra Bizum, comprobar que el banco no lo permite pagar el pedido
				if(this.fakePhone) {
					if(isPaymentDoneByFakePhone()) {
						this.fakePhone = false;
						WebElement inputFakePhone = driver.findElement(By.xpath(PaymentResource.Bizum.inputTelefoneXpath));
						inputFakePhone.sendKeys(this.redsyPayment.getPayment().getTelephone());
						clicJS(driver.findElement(By.xpath(PaymentResource.Bizum.getBotonContunarXpath(false))));
						espera(100);
						log("this.fakePhone " +this.fakePhone +" inputPhone.sendKeys(this.redsyPayment.getPayment().getTelephone()) " + this.redsyPayment.getPayment().getTelephone());
						return true;
					}
					else {
						log("Hubo error al paga el pedido con numero no activo para compra Bizum: " + redsyPayment.getPayment().getFakeIban());
						Assert.assertFalse(false, " Hubo error al paga el pedido con numero no activo para compra Bizum: " + redsyPayment.getPayment().getFakePhone());
						return false;
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
		return true;
	}
	
	public boolean isPaymentDoneByFakePhone () {
	
		espera(1100);
		if(!isElementPresent(By.xpath(PaymentResource.Bizum.numeroNoActivadoEnBizumXpath))) {
			return false;
		}
		
		WebElement  fakePhoneElement = driver.findElement(By.xpath(PaymentResource.Bizum.numeroNoActivadoEnBizumXpath));
		String errorFakePhone = PaymentResource.Bizum.messageErrorParaFakeNumero;
		
		if(!fakePhoneElement.getAttribute("innerText").equalsIgnoreCase(errorFakePhone)) {
			log("No tenemos el error esperado " + errorFakePhone + ",  hemos encontrado el error:  " + fakePhoneElement.getAttribute("innerText"));
			
			return false;
		}
		
		return true;
	}

	public void pagoAceptado () { //Verirficar que el pago ha sido aceptado
		Assert.assertFalse(isElementPresent(By.xpath(PaymentResource.Bizum.resultPagoCodeError)),
				"Error en el proceso de pago con Bizum. Desde la plantaforma del banco no se acepta el pago del importe " + this.importe);
		
		Assert.assertTrue(isElementPresent(By.xpath(PaymentResource.Bizum.resultBodyPagoOK)),
				"Error en el proceso de pago con Bizum. Desde la plantaforma del banco no se ha aceptado el pago ");		
		
		//Verificar la disponibilida del boton Continuar el pago en la plataforma del banco
		Assert.assertTrue(driver.findElement(By.xpath(PaymentResource.Bizum.botonContinuarPagoXpath)).isDisplayed(),
				"Error no tenemos el botón continuar !!!");
		
		clicJS(driver.findElement(By.xpath(PaymentResource.Bizum.botonContinuarPagoXpath)));
		
	}
	
	@Test(description = "Prueba de denegacion del pago por el banco.", priority = 2)
	@Parameters({"repetirElPago", "deleteProductList", "totalEsperado"})
	public void pagoDenegadoPorBanco (boolean repetirElPago, String deleteProductListFromOrder, String totalEsperado) { //Verirficar que el pago ha sido aceptado
		String[] deleteProductList = deleteProductListFromOrder.split(",");
		Assert.assertFalse(!isElementPresent(By.xpath(PaymentResource.Bizum.resultPagoCodeError)),				
				"Error en el proceso de pago con Bizum. Debe mostrar error de no aceptar el pago ");
		
		Assert.assertFalse(isElementPresent(By.xpath(PaymentResource.Bizum.resultBodyPagoOK)),
				"Error: El pago deberia fallar ");
		String textPagoDenegadoMessageError = PaymentResource.Bizum.textPagoDenegadoMessageError;
		
		Assert.assertFalse(!isElementPresent(By.xpath(PaymentResource.Bizum.resultPagoCodeError)),
				"Error: El pago deberia fallar ");
		
		WebElement errorDiv = driver.findElement(By.xpath(PaymentResource.Bizum.resultPagoCodeError));
		
		log("Error al denegar el pago --> " + errorDiv.getAttribute("innerText"));
		
		log("Error al denegar el pago --> textPagoDenegadoMessageError " + textPagoDenegadoMessageError);
		
		Assert.assertFalse(driver.findElement(By.xpath(PaymentResource.Bizum.resultPagoCodeError)).getAttribute("innerText").equalsIgnoreCase(textPagoDenegadoMessageError), 
				"Error: no se encontra la palabra esperaba " + textPagoDenegadoMessageError);
		
		//Verificar la disponibilida del boton Continuar el pago en la plataforma del banco
		Assert.assertTrue(driver.findElement(By.xpath(PaymentResource.Bizum.botonContinuarPagoXpath)).isDisplayed(),
				"Error no tenemos el botón continuar !!!");
		
		clicJS(driver.findElement(By.xpath(PaymentResource.Bizum.botonContinuarPagoXpath)));
		espera(1500);
		
		//eliminar productos para tener importer no superior a 15 euros, asi se prodría pagar el pedido con bizum
		for(String product: deleteProductList) {
			String elementProductXpath = PaymentResource.getBotonEliminarArticuloDelPedido(product);
			
			log("Element xpath del producto a eliminar --> " +elementProductXpath);
			
			WebElement botonEliminarProducto =getElementByFluentWait(By.xpath(elementProductXpath), 40, 5); //driver.findElement(By.xpath(elementProductXpath));
			clicJS(botonEliminarProducto);
			espera(400);

			//Validar al eliminación del producto del pedido
			Assert.assertFalse(isElementPresent(By.xpath(elementProductXpath)), "Error encontrado al eliminar el producto --> " + product);
			log("Se ha eliminado el producto --> " + product + " del carrito ");
		}

		
		if(repetirElPago) {
			log("Repetir el pago cancelado por parte del cliente");
			this.importe = totalEsperado;
			Data.getInstance().getPedido().setPrecioTotal(importe);
			makePayment(this.importe, this.shop, false, false, false, false);
		}
	}
	
	@Test(description = "Prueba de cancelación del pago por parte del cliente ", priority = 2)
	@Parameters({"repetirElPago"})
	public void pagoCanceladoPorCliente(boolean repetirElPago) { //Verificar que la cancelacion del pago ha sido aceptada
		Assert.assertTrue(isElementPresent(By.xpath(PaymentResource.Bizum.resultPagoCodeError)),
				"Error en el proceso de pago con Bizum. Debe mostrar error por pago Cancelado ");
		
		Assert.assertFalse(isElementPresent(By.xpath(PaymentResource.Bizum.resultBodyPagoOK)),
				"Error: El pago deberia fallar por que ha sido cancelado por el cliente ");
		String textMessageErrorAlCancelarPago = PaymentResource.Bizum.textMessageErrorAlCancelarPago;
		
		Assert.assertTrue(driver.findElement(By.xpath(PaymentResource.Bizum.resultPagoErrorBizumXpath)).getText().equalsIgnoreCase(textMessageErrorAlCancelarPago), 
				"Error: no se encontra la palabra esperaba de la pago cancelado por parte del cliente " + textMessageErrorAlCancelarPago);
		
		//Verificar la disponibilida del boton Continuar el pago en la plataforma del banco
		Assert.assertTrue(driver.findElement(By.xpath(PaymentResource.Bizum.botonContinuarPagoXpath)).isDisplayed(),
				"Error no tenemos el botón continuar !!!");
		
		clicJS(driver.findElement(By.xpath(PaymentResource.Bizum.botonContinuarPagoXpath)));
		
		if(repetirElPago) {
			log("Repetir el pago cancelado por parte del cliente");
			makePayment(this.importe, this.shop, false, false, false, false);
		}
		
	}
	
	
	//Para Tarjeta Redsys
	public void pagarPedidoConTarjeta(String formaPago, boolean nuevaTarjeta, String testCardNumber, String cad1, String cad2, String cvv, String pedidoConfirmado, String validarImporteMinimo) {

		log("Se paga el pedido con tarjeta formaPago " + formaPago);
		
		String formaPagoXpath = "//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]";
		
		waitUntilPresence(formaPagoXpath, true, false);
		
		w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
		clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO
		
		if(nuevaTarjeta) {
			this.selectNewCardPayment(nuevaTarjeta);
		}

		List<WebElement> checkBoxes = driver.findElements(By.xpath("//div[contains(@class,'mat-checkbox-inner-container')]"));
		for(int i=0;i<checkBoxes.size();i++) {
			clicJS(checkBoxes.get(i));// MARCO CHECKS DE ACEPTO CONDICIONES
		}
		
		//clicJS(driver.findElement(By.xpath("//div[contains(@class,'mat-checkbox-inner-container')]"))); // MARCO CHECK DE ACEPTO CONDICIONES
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
			
		// Validar el importe minimo de delivery
		if((!com.mysql.cj.util.StringUtils.isNullOrEmpty(this.tipoServicio) ) && !com.mysql.cj.util.StringUtils.isNullOrEmpty(this.importeMinimo) && (!com.mysql.cj.util.StringUtils.isNullOrEmpty(validarImporteMinimo) && validarImporteMinimo.equalsIgnoreCase("true"))) {
			espera(1000);
			validarImporteMinimoPedido(this.importeMinimo, this.totalEsperado);
			
			if(validarImporteMinimoPedido.equalsIgnoreCase("false")) {
				return;
			}
		}
				
		espera(5000);

		
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
			//.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
			
			//** Test
			
			if(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")) != null) {
				log("Element is finded");
			} else {
				log("Element not finded");
			}
			//**
						
			espera(2000);
			if(ValidarPayment.validarPantallaRecibo()) {
				log("Encuentro la pantalla del Recibo electrónico");
			}
			espera(2000);
			//espera(1000);			
			ValidarPayment.validarReciboCheckout(pedidoConfirmado);
		} else {
			log("- No encuentro el resutlado ok en la pantalla de redsys");
			if(isElementPresent(By.xpath("//div//child::text[contains(@lngid, 'noSePuedeRealizarOperacion')]"))) {
				
				log("No se puede realizar la operación\r\n"
						+ "El sistema está ocupado, inténtelo más tarde (SIS0038)");
			}
			Assert.assertTrue(false);
		}
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
	
	public void selectNewCardPayment(boolean nuevaTarjeta) {
		if(nuevaTarjeta) {
			
			String matSelectPaymentCard = PaymentResource.Redsys.matSelectPaymentCard;
			String selectPaymentCard = PaymentResource.Redsys.selectPaymentCard;;
			
			String selectCard = PaymentResource.Redsys.selectCard;
			String elementNuevaTarjeta = PaymentResource.Redsys.elementNuevaTarjeta;
			
			if( nuevaTarjeta && ( isElementPresent(By.xpath(matSelectPaymentCard)) && isElementPresent(By.xpath(selectPaymentCard)) ) ) {
				
				w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(selectCard)));
				clicJS(driver.findElement(By.xpath(selectCard)));
				
				if(isElementPresent(By.xpath(elementNuevaTarjeta))) {
					log("Selecciona la opción Nueva tarjeta");
					clicJS(driver.findElement(By.xpath(elementNuevaTarjeta)));
					
				} else {
					assertTrue(false, "No se ha podido seleccionar la opción Nueva tarjeta");
				}
			}

			espera(500);
		}
	}

}
