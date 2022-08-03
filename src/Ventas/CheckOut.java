package Ventas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.Data;
import utils.DatabaseConnection;
import utils.TestBase;

public class CheckOut extends TestBase{

  @Test(description="Se encarga de gestionar la parte de checkout una vez los productos ya están añadidos al carrito", priority=1)
  @Parameters({"productos","totalEsperado","formaPago","nuevaTarjeta","testCardNumber","cad1","cad2","cvv","pedidoConfirmadoString" , "shop", "email", "miMonederoString"})
public void finalizarPedido(String productos, String totalEsperado, String formaPago,
		  @Optional ("true") String nuevaTarjeta, @Optional ("4548812049400004") String testCardNumber,
		  @Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv, String pedidoConfirmadoString, 
		  String shop, String customerMail, @Optional ("")String miMonederoString) {
	  String[] arrayNombres;
	  arrayNombres = productos.split(",");
	 //SI ES NUEVO USUARIO EL CORREO QUE NOS VIENE DEL TEST NO ES VÁLIDO.
	  Data.getInstance().setUltimoDocId(getLastDoc__Doc(shop));//RECUPERAMOS ULTIMO DOCDOCID DEL CLIENTE ANTES DE REALIZAR EL PEDIDO
	  WebElement checkoutButton = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'basket-button')]")));
	  checkoutButton.click();
	  espera(1000);
	  if(customerMail.equalsIgnoreCase("") && formaPago.equalsIgnoreCase("Redsys Test"))nuevaTarjeta="true"; //SI NO TENEMOS CLIENTE SIGNIFICA QUE ES NUEVO Y NECESITAREMOS TARJETA NUEVA
	  if(!(validaPantalla(arrayNombres,totalEsperado)))Assert.assertTrue(false);
	  if(formaPago.equalsIgnoreCase("Redsys Test"))
		  pagarPedidoConTarjeta(formaPago, nuevaTarjeta.equalsIgnoreCase("true"),testCardNumber,cad1,cad2,cvv,pedidoConfirmadoString);
	  if (formaPago.equalsIgnoreCase("Pagar a la entrega"))
		  pagarPedidoPagarEnCaja(formaPago,pedidoConfirmadoString);
	  if (formaPago.equalsIgnoreCase("saldo"))
		  pagarPedidoSaldo(formaPago,pedidoConfirmadoString,totalEsperado, miMonederoString);
	  Assert.assertTrue(true);
	  
  }

private void pagarPedidoSaldo(String formaPago, String pedidoConfirmadoString, String totalEsperado , String miMonederoString) {
	log("Se paga el pedido con "+ formaPago);
	String saldoActual = driver.findElements(By.xpath("//div[contains(@class,'loyalty-card-info')]")).get(1).getAttribute("innerText");
	log("- El saldo actual de la tarjeta es  " + saldoActual);
    String caducidad = driver.findElements(By.xpath("//div[contains(@class,'loyalty-card-info')]")).get(0).getAttribute("innerText");
    log("- La caducidad de la tarjeta es  " + caducidad);
	clicJS(driver.findElement(By.xpath("//div[contains(@class,'gift-card-logo')]")));//SELECCIONO FORMA DE PAGO
	clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
	w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(30)); // LE DAMOS 30 SEGUNDOS PARA HACER EL CHECKOUT.
	w.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
	validarReciboCheckout(pedidoConfirmadoString);
	validarSaldoRestante(saldoActual,totalEsperado, miMonederoString);
}

private void validarSaldoRestante(String saldoAnterior, String importeVenta, String miMonedero) { //VALIDA EL SALDO ANTERIOR CON EL NUEVO RESTADO DEL IMPORTE DE LA VENTA.
	log("Validando saldo restante en la tarjeta...");
	  clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
	  clicJS(driver.findElement(By.xpath("//button[text()='"+miMonedero+"']")));
	  clicJS(driver.findElement(By.xpath("//mat-icon[text()='info']")));
	  espera(500);
	  String saldoActual = driver.findElement(By.xpath("//span[text()='Saldo disponible']/following::span[1]")).getAttribute("innerText");
	  saldoActual = saldoActual.replaceAll("€", "").trim();
	  NumberFormat format = NumberFormat.getInstance(Locale.US);
	  double value = Double.parseDouble(saldoActual); //validar esto
}

private void pagarPedidoPagarEnCaja(String formaPago, String pedidoConfirmadoString) {
	log("Se paga el pedido con "+ formaPago);
	clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO
	clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
    w.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
	validarReciboCheckout(pedidoConfirmadoString);
	
}

private void pagarPedidoConTarjeta(String formaPago, boolean nuevaTarjeta, String testCardNumber, String cad1, String cad2, String cvv, String pedidoConfirmado) {
	
	log("Se paga el pedido con tarjeta");
	clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO
	
	clicJS(driver.findElement(By.xpath("//div[contains(@class,'mat-checkbox-inner-container')]"))); // MARCO CHECK DE ACEPTO CONDICIONES
	clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
	espera(5000);
	
	String actualTitle = driver.getTitle();
	if(!actualTitle.equalsIgnoreCase("RedSys")) {
		log("No he encontrado la pantalla de Redsys");
		Assert.assertTrue(false);
	}
	
	//TENEMOS PANTALLA DE REDSYS ABIERTA.
	if (nuevaTarjeta) {
		log("- La tarjeta es nueva " + testCardNumber);
		driver.findElement(By.id("inputCard")).sendKeys(testCardNumber);
		driver.findElement(By.id("cad1")).sendKeys(cad1);
		driver.findElement(By.id("cad2")).sendKeys(cad2);
		driver.findElement(By.id("codseg")).sendKeys(cvv);
		driver.findElement(By.id("divImgAceptar")).click();
		w.until(ExpectedConditions.presenceOfElementLocated(By.id("boton"))).click(); //ACEPTAMOS SIMULADOR FINANET
	}
	
	w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'result-header')]")));
	
	if(isElementPresent(By.xpath("//div[contains(@class,'result-code ok')]"))) {
		log("- Respuesta de cobro correcta");
		driver.findElement(By.xpath("//input[contains(@class,'btn-continue')]")).click();//CLIC EN CONTINUAR
		w.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
		validarReciboCheckout(pedidoConfirmado);
	}else {
		log("- No encuentro el resutlado ok en la pantalla de redsys");
		Assert.assertTrue(false);
	}
}

private void validarReciboCheckout(String pedidoConfirmadoString) {
	// ESTA PANTALLA ES UNA MIERDA, NO TIENE CLASES PARA IDENTIFICAR, HABLAR CON RAMON
	driver.findElement(By.xpath("//div[contains(text(),'"+pedidoConfirmadoString+"')]"));
	log("Hemos llegado a la pantalla del recibo electrónico -> " + pedidoConfirmadoString);
	atras();

	Assert.assertTrue(true);
}



private void atras() {
	clicJS(driver.findElement(By.xpath("//mat-icon[text()='keyboard_arrow_left']")));	
}

private boolean validaPantalla(String[] arrayNombres,String totalEsperado) {
	
	String basketTitle = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'basket-ticket-title')]"))).getAttribute("innerText");
	String basketDate = w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'basket-ticket-row')]"))).getAttribute("innerText"); 
	
	List<WebElement> basketLines = driver.findElements(By.tagName("app-basket-line"));
	int elementosEncontrados=0;
	log("Lineas en resumen del pedido");
	for(int i =0; i< basketLines.size();i++) {
		
		if(basketLines.get(i).findElement(By.xpath("//label[contains(@class,'dishName')]")).getAttribute("innerText")!="") { //POR AHORA SOLO MIRO QUE LA LINEA NO QUEDE VACIA
			
			log("- " + arrayNombres[i]);
		}	
	}
	
	if(basketLines.size()!=arrayNombres.length) {
		log("Me faltan artículos en el resumen del basket, deberían haber " + arrayNombres.length);
		return false;
	}
	
	espera(500);
	
	//VALIDAMOS PIE DE SUBTOTAL Y TOTAL
	driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][1]//div[contains(text(),'Subtotal')]"));
	driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][1]//div[contains(text(),'"+totalEsperado+"')]"));
	
	driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][2]//div[contains(text(),'Total')]"));
	driver.findElement(By.xpath("//div[contains(@class,'price-breakdown-line')][2]//div[contains(text(),'"+totalEsperado+"')]"));
	
	//VALIDAMOS QUE TENEMOS TITULO DE SELECCIÓN DE FORMAS DE PAGO
	if (driver.findElement(By.xpath("//div[contains(@class,'generic-subtitle')]")).getAttribute("innerText").equalsIgnoreCase(""))return false;
	
	List<WebElement> payMenMeans = driver.findElements(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(@class,'payment-name')]"));
	if (payMenMeans.size()==0)return false;
	
	if (!validaCarritoFlotante(String.valueOf(arrayNombres.length),totalEsperado)) return false;

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


}
