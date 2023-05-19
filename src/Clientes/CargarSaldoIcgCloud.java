package Clientes;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Ventas.AddCarrito;
import Ventas.CheckOut;
import Ventas.ValidacionPedidos;
import utils.TestBase;

public class CargarSaldoIcgCloud extends TestBase {
	String balanceBeforeLoading = "";
	String balanceAfterLoading = "";
	String balanceToLoad = "";
	String validateChargedBalance = "";
	
	@Test(description="Prueba de login de un cliente en ICGCLoud y realizar carga de su tarjeta de fidelizacion", priority=1, groups = {"cargaSaldoIcgCloud"})
	@Parameters({"saldoACargar","miMonederoString","cargarSaldoString", "nuevaTarjeta","testCardNumber","cad1","cad2","cvv"})
	public void cargarSaldo(String importe, String miMonederoString, String cargarSaldoString,
	    @Optional ("true") String nuevaTarjeta, @Optional ("") String testCardNumber,
		@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv) {
		
		//espera(500);
		abrirMiMonedero(miMonederoString);
		
		w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")));
		
		if(isElementPresent(By.xpath(("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")))) {	
			WebElement saldo = driver.findElement(By.xpath(("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")));
			String[] sArray = saldo.getText().split("€");
			balanceBeforeLoading = sArray[0];
			log("balanceBeforeLoading: " + balanceBeforeLoading);
		}
		
		By elmtBy = By.xpath(("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]"));
		
		WebElement newElement = getElementByFluentWait(elmtBy, 30, 5);
		
		if(newElement != null) {
			log(" text newElement > " + newElement.getText());
		}
		else {
			log("Error: text newElement ");
			Assert.assertTrue(false);
		}
		
		//espera(500);
		w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'btn btn-confirm') and text()='"+cargarSaldoString+"']")));
		WebElement cargarSaldoButton = driver.findElement(By.xpath("//button[contains(@class,'btn btn-confirm') and text()='"+cargarSaldoString+"']"));
		cargarSaldoButton.click();
		
		if(isElementPresent(By.xpath("//div[contains(@class, 'mat-form-field-infix')]/child::input"))) {	
			
			WebElement sCargar = driver.findElement(By.xpath("//div[contains(@class, 'mat-form-field-infix')]/child::input"));
			String inputValue = sCargar.getAttribute("value");
							
			importe +=",00";
			
			if(!inputValue.equalsIgnoreCase(importe)) {
				//if(setInputValueJS(By.xpath("//input[contains(@id, 'mat-input-2')]"), importe)) {	
				if(setInputValueJS(By.xpath("//div[contains(@class, 'mat-form-field-infix')]/child::input"), importe)) {					
					log("Se ha podio introducir el saldo " + importe +"€ a cargar en la tarjete de fidelización!!!");
					//Assert.assertTrue(true);
				} else {
					log("No se ha podio introducir el saldo " + importe +"€ a cargar en la tarjete de fidelización!!!");
					Assert.assertTrue(false);
				}
			}
			
		} else {
			log("No se ha podio introducir el sado " + importe +"€ a cargar en la tarjete de fidelización!!!");
			Assert.assertTrue(false);
		}
		
		// Validar para cargar saldo
		//espera(500);
		WebElement cargarSaldoCardBtn;
		if(isElementPresent(By.xpath("//div[contains(text(),'Redsys Test')]//preceding-sibling::div"))) {
			cargarSaldoCardBtn = driver.findElement(By.xpath("//div[contains(text(),'Redsys Test')]//preceding-sibling::div"));
			
		} else if(isElementPresent(By.xpath("//div[contains(@class, 'payment-means-wrapper')]/child::div[contains(@class, 'payment')]"))) {
			//espera(500);
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'payment-means-wrapper')]/child::div[contains(@class, 'payment')]")));
			cargarSaldoCardBtn = driver.findElement(By.xpath("//div[contains(@class, 'payment-means-wrapper')]/child::div[contains(@class, 'payment')]"));
			
		} else {
			log("Se paga el pedido con tarjeta");
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'Redsys Test')]")));
			cargarSaldoCardBtn = driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'Redsys Test')]"));			
		}
		
		//Verificar si tenemos que seleccionar nueva tarjeta
		espera(500);
		cargarSaldoCardBtn.click();
		
		espera(500);
		
		String matSelectPaymentCard = "//mat-select[contains(@class, 'mat-select payment-card-select')]";
		String selectPaymentCard = "//mat-select[contains(@class, 'mat-select payment-card-select')]//div[contains(@class, 'mat-select-value')]";
		
		String selectCard = "//div[contains(@class, 'mat-select-arrow-wrapper')]/div";
		String elementNuevaTarjeta ="//span[contains(@class, 'mat-option-text')]//label[contains(text(), 'Nueva tarjeta')]";
		
		
		if(nuevaTarjeta.equalsIgnoreCase("true") && 
				( isElementPresent(By.xpath(matSelectPaymentCard)) && isElementPresent(By.xpath(selectPaymentCard)) )
			) {
			
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
		///cargarSaldoCardBtn.click();
		//espera(500);
		
		w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//div[contains(@class,'dialog-content')]")));
		
		w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//input[@type='checkbox']//ancestor::div[@class='mat-checkbox-inner-container']")));
		WebElement accept = driver.findElement(By.xpath("//input[@type='checkbox']//ancestor::div[@class='mat-checkbox-inner-container']"));
		accept.click();//MARCAMOS CHECKBOX PARA ACEPTAR PAGO
		
		espera(1000);
		w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'main-btn basket-button')]")));
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'main-btn basket-button')]")));
		espera(1000);
		
		pagarPedidoConTarjeta(nuevaTarjeta.equalsIgnoreCase("true"), testCardNumber, cad1, cad2, cvv, miMonederoString);
		
		if(validateChargedBalance.equalsIgnoreCase("false")) {
			log("No hemos podido cargar la tarjeta de fidelización.");
			Assert.assertTrue(false);
		}
		
		//driver.navigate().back();
		espera(2000);
		
		//Pulsar el logo tipo del establecimiento para volver al inicio(la pantalla principal de la tienda)
		clicLogoEstablecimiento();
	}
		
	// pagar el saldo a carga en la tarjeta de fidelizacion al utilizar la tarjeta Redsys
	private void pagarPedidoConTarjeta(boolean nuevaTarjeta, String testCardNumber, String cad1, String cad2, String cvv, String miMonederoString) {
		espera(5000);
		
		// Test si no hobo fallo con: result-code error
		String codeError = "//div[contains(@class, 'result-header')]//div[contains(@class, 'result-code error')]";
		if(isElementPresent(By.xpath(codeError))) {
			Assert.assertTrue(false, "Error: No se puede realizar la operación\r\n"
					+ "Error en datos enviados. Contacte con su comercio. (SIS0321)");
		}
		//
		
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
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@class,'btn btn-lg btn-continue')]")));
			driver.findElement(By.xpath("//input[contains(@class,'btn btn-lg btn-continue')]")).click();//CLIC EN CONTINUAR
			
			//w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//app-card/child::div[contains(@class,'card-wrapper shadowed expanded')]")));//ESPERO HASTA QUE SALGA POR PANTALLA MONEDERO.
			
			//Update > desde la version 8.44.0.0 y 9.10.0.0
				waitUntilPresence("//div/button[contains(@class, 'main-btn basket-button')]", true, false); //ESPERO HASTA QUE SALGA POR PANTALLA Resultado de la carga Saldo.
				espera(1000);
				
				//TODO MAD: Que tenemos que validiar en esta pantalla tras terminar la carga saldo de la tarjeta de fidelización
				
				abrirMiMonedero(miMonederoString); //Abrir mi monedero para comprobar que ha ido bien la carda del saldo
				espera(2000);
				
				//waitUntilPresence("//app-card/child::div[contains(@class,'card-wrapper shadowed expanded')]", true, false);
				
				//waitUntilPresence("//mat-icon[text()='info']", true, false);
				
				//w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")));
				
			//Fin update 
			
			
			//clicJS(driver.findElement(By.xpath("//mat-icon[text()='info']")));
			//espera(500);
			
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")));
			
			if(isElementPresent(By.xpath(("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")))) {	
				WebElement saldo = driver.findElement(By.xpath(("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")));
				String[] sArray = saldo.getText().split("€");
				balanceAfterLoading = sArray[0];
				log("balanceAfterLoading: " + balanceAfterLoading);
			}
			espera(500);		
			validarSaldoCargado(); // valider el saldo cargado en la tarjeta.
			
		}else {
			log("- No encuentro el resutlado ok en la pantalla de redsys");
			Assert.assertTrue(false);
		}
	}
		
	//Validar que se ha hecho bien la carga de la tarjeta de fidelizacion
	private void validarSaldoCargado() {
		String vBalanceAfterLoading = balanceAfterLoading.replaceAll(",",".");
		Double bAfterLoad = Double.parseDouble(vBalanceAfterLoading);
		
		String vBalanceBeforeLoading = balanceBeforeLoading.replaceAll(",",".");
		Double bBeforeLoading = Double.parseDouble(vBalanceBeforeLoading);		
		
		if(bAfterLoad > bBeforeLoading) {
			validateChargedBalance = "true";
			log("la tarjeta de fidelización esta cargada.");
			Assert.assertTrue(true);
		} else {
			log("No hemos podido cargar la tarjeta de fidelización.");
			Assert.assertTrue(false);
		}
	}
}
