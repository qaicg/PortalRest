package Clientes;

import static org.testng.Assert.assertTrue;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Objects.LoyaltyCard;
import Ventas.AddCarrito;
import Ventas.CheckOut;
import Ventas.ValidacionPedidos;
import io.opentelemetry.api.internal.Utils;
import utils.Data;
import utils.TestBase;

public class CargarSaldoIcgCloud extends TestBase {
	String balanceBeforeLoading = "";
	String balanceAfterLoading = "";
	String balanceFromDB = ""; //En HPC no FRS con ICGCLOUD
	String validateChargedBalance = "";
	boolean checkLoadLoyaltyCardAlways;
	
	String ICGCloud;
	String importe; //indicar el importe a cargar en la tarjeta de saldo 
	
	LoyaltyCard tarjetaSaldo;
	
	@BeforeMethod
	@Parameters({"cargarSaldoSiempre", "saldoACargar",  "formaPago", "totalEsperado", "ICGCloud"})
	public void checkLoardLoyaltyCardAlways(boolean cargarSaldoSiempre, String saldoACargar, String formaPago, String totalEsperado, String ICGCloud) {
		this.ICGCloud = ICGCloud;
		this.importe = saldoACargar + ",00";
		/*cargarSaldoSiempre: 
		 * 	true -> Siempre cargar la tarjeta de fidelizacion independamente del saldo y del total a pagar del pedido
		 * 	False -> Siempre verificamos si el saldo es suficiente antes de cargar la tarjeta cuando el forma de pago no es combinado
		 */
		if(!utils.Utils.isNullOrEmpty(this.ICGCloud) && !this.ICGCloud.equalsIgnoreCase("true") || utils.Utils.isNullOrEmpty(this.ICGCloud)) { //Estamos en HPC no con FRS ICGCLOUD
			//LoyaltyCard tarjetaSaldo = getInformationLoayaltyCardFromDB();
			tarjetaSaldo = getInformationLoayaltyCardFromDB();
			
			tarjetaSaldo.setCargarSaldoSiempre(cargarSaldoSiempre);
			
			Data.getInstance().getUser().setTarjetaSaldo(tarjetaSaldo);
			
			Data.getInstance().getUser().getTarjetaSaldo().setCargarSaldoSiempre(cargarSaldoSiempre);
			
			this.checkLoadLoyaltyCardAlways = checkLoadLoyaltyCardAlways(saldoACargar, formaPago, totalEsperado);
		}
		
	}
	
	@Test(description="Prueba de login de un cliente en ICGCLoud y realizar carga de su tarjeta de fidelizacion", priority=1, groups = {"cargaSaldoIcgCloud"})
	@Parameters({"saldoACargar","miMonederoString","cargarSaldoString", 
		"nuevaTarjeta","testCardNumber","cad1","cad2","cvv", 
		"formaPago", "totalEsperado", "cargarSaldoSiempre"})
	public void cargarSaldo(String importe, String miMonederoString, String cargarSaldoString,
	    @Optional ("true") String nuevaTarjeta, @Optional ("") String testCardNumber,
		@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv,
		String  formaPago, String totalEsperado, String cargarSaldoSiempre) {
		
		//Verificar si vamos cargar  la tarjeta o no
		if( ( (!utils.Utils.isNullOrEmpty(this.ICGCloud) && !this.ICGCloud.equalsIgnoreCase("true")) || utils.Utils.isNullOrEmpty(this.ICGCloud) ) 
				&& !checkLoadLoyaltyCardAlways) {
			return; //no cargar	
		}
		
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
							
			//importe +=",00";
			importe = this.importe;//getImporteACargar(balanceSaldo, totalEsperado, importe, formaPago)
			
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
		//clicLogoEstablecimiento();
		log("pulsar al logo y verificar se muestre la pantalla inicial del restaurante");
		By familiaEnPantallaPrincila = By.xpath("//li[contains(@class,'familyItem')]");
		clicLogoEstablecimientoDespuesDeCarga(familiaEnPantallaPrincila);
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
	
	//Verificar si cargamos la tarjeta o non
	private boolean checkLoadLoyaltyCardAlways(String saldoACargar, String formaPago, String totalEsperado) {
		LoyaltyCard tarjetaSaldo = Data.getInstance().getUser().getTarjetaSaldo();
				
		String balance = tarjetaSaldo.getBalanceDB();
		
		boolean isBalanceSuperiorTotalEsperado = isBalanceSuperiorTotalEsperado(balance, totalEsperado);
		
		if(tarjetaSaldo.isCargarSaldoSiempre()) {
			return true; // Siempre cargar la tarjeta 
		}
		
		//No cargar siempre la tarjeta
		if(!tarjetaSaldo.isCargarSaldoSiempre()) {
			//Forma de pago combinado
			//Tenemos un poco de saldo, en el caso que hay saldo cargar la tarjeta
			if(formaPago.equalsIgnoreCase("combinado")) {
				if((!isBalanceSuperiorTotalEsperado && Double.parseDouble(balance) < 1 )) {  //No hay saldo suficiente
					log("Cargar la tarjeta de fidelizacion por que no tenemos saldo suficiente " + balance);
					
					//en caso que la balance sea 0.00 y  el importe a cargar superior or igual al precio del pedido --> cambiar el importe a cargar en la tarjeta por una cuantidad inferior al total del pedido. 
					//Así se podra aplicar el modo combinado 									
					this.importe = getImporteACargar(this.balanceFromDB, totalEsperado, importe, formaPago);
										
					isBalanceSuperiorTotalEsperado = true;
				}
				else if(!isBalanceSuperiorTotalEsperado && Double.parseDouble(balance) > 0 ) { //Hay suficiente saldo en la tarjeta
					log("methodo checkLoadLoyaltyCardAlways: No Cargar la t fidelizacion por que hay saldo suficiente " + balance);
					isBalanceSuperiorTotalEsperado = false;
				}
				else if (isBalanceSuperiorTotalEsperado) {
					//La balance de la carte en la BBDD es superior al precio total esperado del pedido
					//Regularisamos el saldo de la carta para permitir pagar con el modo combinado saldo
					//modificar el saldo insertando otra linea hasta que el saldo en BBDD sea inferior al precio total del pedido
					log("modificar el saldo de la tarjeta en la BBDD hasta sea inferior al precio total del pedido");
					this.insertIntoLoyCardMovement(Double.parseDouble(balance), Double.parseDouble(totalEsperado.replace("€", "").replace(",", ".")));
					driver.navigate().refresh();
					//isBalanceSuperiorTotalEsperado = false;
				}
				
			}
			
			//Forma de pago Saldo
			if(formaPago.equalsIgnoreCase("saldo")) {
				if(isBalanceSuperiorTotalEsperado || (!isBalanceSuperiorTotalEsperado && isBalanceEqualsTotalEsperado(balance, totalEsperado))) { //Hay suficiente saldo en la tarjeta
					isBalanceSuperiorTotalEsperado = false;
				}
				else if(!isBalanceSuperiorTotalEsperado) { //No hay saldo suficiente
					
					isBalanceSuperiorTotalEsperado = true;
				}
			}
		}
		
		return isBalanceSuperiorTotalEsperado;
	}
	
	
	//Comparar totalEsperado a la balance 
	public boolean isBalanceSuperiorTotalEsperado(String balance, String totalesprado) {
		
		Double  mountExpect =  Double.parseDouble(totalesprado.replace("€", "").replace(",", "."));
		
		Double  mountBalance =   Double.parseDouble(balance); 
		
		log("Balance de la t.Saldo " + balance + " es superior al total esperado " + totalesprado +  " -->  rsTest: " + (mountBalance > mountExpect ? true : false));
		
		return mountBalance > mountExpect;
	}
	
	//Comparar totalEsperado a la balance 
	public boolean isBalanceEqualsTotalEsperado(String balance, String totalesprado) {
		
		Double  mountExpect =  Double.parseDouble(totalesprado.replace("€", "").replace(",", "."));
		
		log("Balance de la t.Saldo " + balance + " es igual al total esperado " + totalesprado);
		
		Double  mountBalance =   Double.parseDouble(balance); 
				
		return mountBalance.compareTo(mountExpect) == 0 ? true : false;
	}
	
	
	// Recuperar información de la tarjeta de solde desde la BBDD
	private LoyaltyCard getInformationLoayaltyCardFromDB() {
		
		LoyaltyCard tarjetaSaldo = new LoyaltyCard();
		
		String email = Data.getInstance().getUser().getUserEmail();
		log("El email del user -> " + email);
		String scriptSQL = LoyaltyCard.getScriptSqlLoyaltyCard(email);
		String shopOrDB = Data.getInstance().getBD();
		
		log("La BBDD del cliente --> " +  shopOrDB);
		ResultSet rs =  databaseConnection.ejecutarSQL(scriptSQL, shopOrDB); 

	  	
	  	 if (rs!=null) {
	  		 try {	
	  			Assert.assertFalse(rs.getFetchSize() > 1, "Error: Hemos encontrado más de 1 tarjeta que admite saldo");
	  			
	  			if (rs.first()) {
	  				log("Tarjeta de fidelización del usuario encontrada en BBDD " + rs.getFloat("cardBalance"));
	  				
		  				DecimalFormat formato1 = new DecimalFormat("#.00");
		  				String saldo = rs.getFloat("cardBalance") == 0.0 ? "0.00" : formato1.format(rs.getFloat("cardBalance")); 
	  				
	  				log("String.valueOf(rs.getString(\"cardBalance\")) " + saldo);
	  				
	  				tarjetaSaldo.setAliasOrName(rs.getString("loyaltyCardName"));
	  				tarjetaSaldo.setBalanceDB(saldo);
	  				tarjetaSaldo.setCardId(String.valueOf(rs.getInt("cardId")));
	  				this.balanceFromDB = saldo;
	  			} else {
	  				log("Error. No tenemos resultados para la tarjeta saldo del usuario en BBDD");
	  				log(scriptSQL);
	  				Assert.assertTrue(false);
	  			}
	  			
	  		} catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  			log(scriptSQL);
	  			Assert.assertTrue(false);
	  		}finally {
	  			databaseConnection.desconectar();
	  		}
	  	 } else {
	  		log(scriptSQL);
	  		Assert.assertTrue(false);
	  	 }
		
		return tarjetaSaldo;
	}
	
	//Importe al cargar saldo con medio de pago combinado
	/* en caso que la balance sea 0.00 y  el importe a cargar superior or igual al precio del pedido:
	 *  cambiar el importe a cargar en la tarjeta por una cuantidad inferior al total del pedido. 
	 *  Así se podra aplicar el modo combinado 
	 */
	private String getImporteACargar(String balanceSaldo, String totalEsperado, String saldoACargar, String formaPago) {
		//en caso que la balance sea 0.00 y  el importe a cargar superior or igual al precio del pedido --> cambiar el importe a cargar en la tarjeta por una cuantidad inferior al total del pedido. 
		//Así se podra aplicar el modo combinado 
		Double  totalPedidoExpext =  Double.parseDouble(totalEsperado.replace("€", "").replace(",", "."));
		Double  cargar =  Double.parseDouble(saldoACargar.replace("€", "").replace(",", "."));
		
		if(Double.parseDouble(balanceSaldo) == 0.00 && cargar >= totalPedidoExpext && formaPago.equalsIgnoreCase("combinado")) {
			cargar = cargar >= totalPedidoExpext ? cargar - totalPedidoExpext : cargar - 0.10; //quitamos 0,10 del importe a cargar definido en el fichero xml de test suite.
			
			while(cargar >= totalPedidoExpext) {
				cargar -=  totalPedidoExpext;
			}
			
		}
		
		if(Double.parseDouble(balanceSaldo) > 0.00 && formaPago.equalsIgnoreCase("combinado")) {
			cargar = cargar >= totalPedidoExpext ? cargar - totalPedidoExpext : cargar - 0.10; //quitamos 0,10 del importe a cargar definido en el fichero xml de test suite.
			
			while(cargar >= totalPedidoExpext || ( Double.parseDouble(balanceSaldo) + cargar ) >= totalPedidoExpext) {
				cargar -= totalPedidoExpext;
			}			
		}
				
		DecimalFormat formato = new DecimalFormat("#.00");
		
		this.importe = formato.format(cargar).replace(".", ",");
		
		log("Cambiar el importe a cargar en la tageta de saldo usando el forma de pago combinado");
		log("Se ha cambiado el importe a ingresar en la tarjeta de saldo " + saldoACargar + " por la cuantidad: " + this.importe);
				
		return this.importe;
	}
	
	
	private int getLastCardMovementId() {
		int cardMovementId = 1 ;
		String sql = LoyaltyCard.getScriptLastCardMovementId();
		String db = Data.getInstance().getBD();
		try {
			ResultSet rs =  databaseConnection.ejecutarSQL(sql, db);
			log("Res rs " + rs.toString());
			if(rs!= null && rs.first()) {
				cardMovementId = rs.getInt("CardMovementId");
				log("cardMovementId " + cardMovementId);
			}
		} catch (Exception e) {
			// TODO: handle exception
						
		}	
		return cardMovementId;
	}
	
	private void insertIntoLoyCardMovement(double balance, double totalEsperado) {
		String db = Data.getInstance().getBD();
		int cardMovementID = getLastCardMovementId() + 1;
		double amount = getAmountInsert(balance, totalEsperado);
		int cardId = Integer.parseInt(Data.getInstance().getUser().getTarjetaSaldo().getCardId());
		
		String sql = LoyaltyCard.getScriptInsertIntoLoyCardMovement(balance, totalEsperado, amount, cardMovementID, cardId);
		
		log("Insercion Datos " + sql);
		
		boolean rs =  databaseConnection.execute(sql, db);
		
		Assert.assertTrue(rs, "No se ha podido insertar la linea: " + sql);
		
	} 

	private double getAmountInsert(double balance, double totalEsperado) {
		double dAmount = -(balance - totalEsperado + 1);
		
		DecimalFormat formato1 = new DecimalFormat("#.00");
		
		String saldo = formato1.format(dAmount);
		
		dAmount = Double.parseDouble(saldo);
		
		return dAmount;
	}
	
	private float getBalance() {
		return 11;
	}
 
}
