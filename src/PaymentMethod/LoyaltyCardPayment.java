package PaymentMethod;

import java.text.DecimalFormat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;

import utils.TestBase;

public class LoyaltyCardPayment extends TestBase {
	
	//**********************   Pagar con tarjeta de fidelización  ***********************//
	
	public void pagarPedidoSaldo(String formaPago, String formaPago2, String pedidoConfirmadoString, String totalEsperado , String miMonederoString, String validarImporteMinimo, 
			@Optional ("true") String nuevaTarjeta, @Optional ("4548812049400004") String testCardNumber,
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
				log("El Saldo de la tarjeta de fidelizacion: " + saldo);
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
				
				if(formaPago2.equalsIgnoreCase("Pagar a la entrega")) {
					Payment pago = new Payment();
					pago.pagarPedidoPagarEnCaja(formaPago2, pedidoConfirmadoString);
				}
				else {
					RedsysPayment tarjetRedsys = new RedsysPayment();
					tarjetRedsys.pagarPedidoConTarjeta(formaPago2, isNewCard, testCardNumber, cad1, cad2, cvv, pedidoConfirmadoString, validarImporteMinimo);
				}
				
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
			WebElement orderReceip = getElementByFluentWait(By.id("orderReceiptHeader"), 45, 10);
		    //Assert.assertTrue(orderReceip != null, "Error: no se ha encontrado el recibo electrónico");
			w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
			ValidarPayment.validarReciboCheckout(pedidoConfirmadoString);
			validarSaldoRestante(saldoActual,totalEsperado, miMonederoString);
		}
		
	}
	
	//Saldo restante de la tarjeta de fidelización 
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
}
