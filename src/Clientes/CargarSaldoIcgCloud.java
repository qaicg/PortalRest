package Clientes;

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

import Ventas.CheckOut;
import utils.TestBase;

public class CargarSaldoIcgCloud extends TestBase {
	String balanceBeforeLoading = "";
	String balanceAfterLoading = "";
	String balanceToLoad = "";
	String validateChargedBalance = "";
	
	@Test(description="Prueba de login de un cliente en ICGCLoud y realizar carga de su tarjeta de fidelizacion", priority=1)
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
		
		//espera(500);
		w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'btn btn-confirm') and text()='"+cargarSaldoString+"']")));
		WebElement cargarSaldoButton = driver.findElement(By.xpath("//button[contains(@class,'btn btn-confirm') and text()='"+cargarSaldoString+"']"));
		cargarSaldoButton.click();
			
		//Verificar el precio que queremos cargar / poner el dinero a cargar el la tarjeta por ejemplo 5
		//espera(500);
		//if(isElementPresent(By.xpath(("//input[contains(@id, 'mat-input-2')]")))) {	
		
		if(isElementPresent(By.xpath("//div[contains(@class, 'mat-form-field-infix')]/child::input"))) {	
			
			//WebElement sCargar = driver.findElement(By.xpath("//input[contains(@id, 'mat-input-2')]"));
			WebElement sCargar = driver.findElement(By.xpath("//div[contains(@class, 'mat-form-field-infix')]/child::input"));
			String inputValue = sCargar.getAttribute("value");
							
			importe +=",00";
			
//			if(!isElementPresent(By.xpath("//input[contains(@id, 'mat-input-2') and text()='"+importe+"']"))) {
//				if(setInputValueJS(By.xpath("//input[contains(@id, 'mat-input-2')]"), importe)) {					
//					log("Se ha podio introducir el saldo " + importe +"€ a cargar en la tarjete de fidelización!!!");
//					//Assert.assertTrue(true);
//				} else {
//					log("No se ha podio introducir el saldo " + importe +"€ a cargar en la tarjete de fidelización!!!");
//					Assert.assertTrue(false);
//				}
//			}
			

			//if(!isElementPresent(By.xpath("//input[contains(@id, 'mat-input-2') and text()='"+importe+"']") )) {
			
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
			
			//clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO
		}
		
		espera(500);
		cargarSaldoCardBtn.click();
		
		w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//div[contains(@class,'dialog-content')]")));
		
		w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//input[@type='checkbox']//ancestor::div[@class='mat-checkbox-inner-container']")));
		WebElement accept = driver.findElement(By.xpath("//input[@type='checkbox']//ancestor::div[@class='mat-checkbox-inner-container']"));
		accept.click();//MARCAMOS CHECKBOX PARA ACEPTAR PAGO
	
		w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'main-btn basket-button')]")));
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'main-btn basket-button')]")));
		
		pagarPedidoConTarjeta(nuevaTarjeta.equalsIgnoreCase("true"), testCardNumber, cad1, cad2, cvv);
		
		if(validateChargedBalance.equalsIgnoreCase("true")) {
			driver.navigate().back();
		}
		
	}
		
	// pagar el saldo a carga en la tarjeta de fidelizacion al utilizar la tarjeta Redsys
	private void pagarPedidoConTarjeta(boolean nuevaTarjeta, String testCardNumber, String cad1, String cad2, String cvv) {
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
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@class,'btn btn-lg btn-continue')]")));
			driver.findElement(By.xpath("//input[contains(@class,'btn btn-lg btn-continue')]")).click();//CLIC EN CONTINUAR
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//app-card/child::div[contains(@class,'card-wrapper shadowed expanded')]")));//ESPERO HASTA QUE SALGA POR PANTALLA MONEDERO.
			
			clicJS(driver.findElement(By.xpath("//mat-icon[text()='info']")));
			espera(500);
			
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")));
			
			if(isElementPresent(By.xpath(("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")))) {	
				WebElement saldo = driver.findElement(By.xpath(("//button[contains(@class, 'btn btn-confirm')]//ancestor::span//preceding-sibling::span[2]")));
				String[] sArray = saldo.getText().split("€");
				balanceAfterLoading = sArray[0];
				log("balanceAfterLoading: " + balanceAfterLoading);
			}
			espera(1000);		
			validarSaldoCargado(); // valider el saldo cargado en la tarjeta.
			espera();
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
