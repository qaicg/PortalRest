package PaymentMethod;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Clientes.AbrirPaginaRegistro;
import Clientes.CrearCliente;
import VentasConPropinas.Propina;
import pedido.Pedido;
import utils.Data;
import utils.TestBase;

public class Payment extends TestBase {
	String tipoServicio="";
	String validarImporteMinimoPedido = null;
	String importeMinimo = null;
	String totalEsperado = null;
	
	String importe;
	String shop;
	boolean cancelarPago;
	boolean pagoDenegadoPorBanco;	
	
	public String fakeIban = "4745852049400004"; // fake numero de tajeta bancaria que nos debe funcionar en la plataforma del banco

	@Test(description = "Pulsar el botón Ver Pedido / Pedir ahora", priority = 1, groups = { "runButtonOrder" })
	public void runButtonOrder() {
		//Estamos en la pantalla del carrito añadiendo productos
		validationLabelButtonOrder ();
		WebElement botonVerPedidoPantallaCarito =  getElementByFluentWait(By.xpath(PaymentResource.botonVerPedidoPantallaCarritoXpath), 20, 5);
		
		botonVerPedidoPantallaCarito.click();
		espera(1000);
	}	
	
	@Test(description = "Validar las propinas sugeridas en la pantalla", priority = 2, groups = { "validarPropina" })
	@Parameters({ "propinaList", "propinaSelected", "precioTotalConPropina", "totalEsperado"}) 
	public void validarPropina(@Optional("") String propinaList, @Optional("") String propinaSelected, @Optional("") String precioTotalConPropina, String totalEsperado) {
		
		Propina loyaltyCard = new Propina();
		loyaltyCard.checkPropina(propinaList, propinaSelected, precioTotalConPropina, totalEsperado);
		espera(3000);
	}
	
	@Test(description="Se encarga de gestionar la parte de checkout una vez los productos ya están añadidos al carrito", priority=3, groups = { "finalizarPedido" })
	@Parameters({"productos","totalEsperado","formaPago","nuevaTarjeta","testCardNumber","cad1","cad2","cvv","pedidoConfirmadoString" , "shop", "email", "miMonederoString",
		"formaPago2", "tipoServicio","unidades","mesa", "totalEsperadoMasCargos", "repartoPermitido", "goBack", "goBackByAddOrderButton", "importeMinimo", "validarImporteMinimo"})
	public void finalizarPedido(String productos, String totalEsperado, @Optional("Pagar a la entrega") String formaPago,
			@Optional ("true") String nuevaTarjeta, @Optional ("4548810000000003") String testCardNumber,
			@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv, String pedidoConfirmadoString, 
			String shop, String customerMail, @Optional ("")String miMonederoString, @Optional ("") String formaPago2, 
			String tipoServicio, @Optional ("") String unidades, @Optional ("") String mesa, @Optional ("") String totalEsperadoMasCargos,
			@Optional ("true") String repartoPermitido, @Optional ("") String goBack, @Optional ("") String goBackByAddOrderButton,
			@Optional ("") String importeMinimo, @Optional ("") String validarImporteMinimo) {

		String[] arrayNombres;
		this.tipoServicio=tipoServicio;
		arrayNombres = productos.split(",");
		this.importeMinimo = importeMinimo;
		this.totalEsperado = totalEsperado;
		//SI ES NUEVO USUARIO EL CORREO QUE NOS VIENE DEL TEST NO ES VÁLIDO.
		Data.getInstance().setUltimoDocId(getLastDoc__Doc(shop));//RECUPERAMOS ULTIMO DOCDOCID DEL CLIENTE ANTES DE REALIZAR EL PEDIDO	
		WebElement checkoutButton = w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'basket-button')]")));
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
			
		}
				
		w2 = new WebDriverWait(TestBase.driver,Duration.ofSeconds(60)); // LE DAMOS 60 SEGUNDOS PARA HACER EL CHECKOUT.
		if(customerMail.equalsIgnoreCase("comoInvitado") || (customerMail.equalsIgnoreCase("") || customerMail.equalsIgnoreCase("invitado@portalrest.com")) && formaPago.equalsIgnoreCase("Redsys Test"))nuevaTarjeta="true"; //SI NO TENEMOS CLIENTE SIGNIFICA QUE ES NUEVO Y NECESITAREMOS TARJETA NUEVA
		
		espera(1000);
		if(!(ValidarPayment.validaPantalla(arrayNombres,totalEsperado,unidades,totalEsperadoMasCargos,repartoPermitido, tipoServicio))) Assert.assertTrue(false);
		
		// Usar back para añadir mas productos al final del proceso de la venta.
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
		} 
		else {
			espera(1000);
			
			if(formaPago.equalsIgnoreCase("Redsys Test")) {
				RedsysPayment redsysCard = new RedsysPayment();
				redsysCard.pagarPedidoConTarjeta(formaPago, nuevaTarjeta.equalsIgnoreCase("true"),testCardNumber,cad1,cad2,cvv,pedidoConfirmadoString, validarImporteMinimo);
			}
			
			if (formaPago.equalsIgnoreCase("Pagar a la entrega")) {
				pagarPedidoPagarEnCaja(formaPago,pedidoConfirmadoString);
			}
			
			if (formaPago.equalsIgnoreCase("saldo") || formaPago.equalsIgnoreCase("combinado")) {
				LoyaltyCardPayment loyaltyCard = new LoyaltyCardPayment();
				loyaltyCard.pagarPedidoSaldo(formaPago, formaPago2, pedidoConfirmadoString, totalEsperado, miMonederoString, validarImporteMinimo, nuevaTarjeta, testCardNumber, cad1, cad2, cvv);
			}
			
			Assert.assertTrue(true);
			
		}
	}	
	
	public enum paymentType {
		
	}
	
	public void makePayment() {
		
	}
	
	
	private void validationLabelButtonOrder () {
		//Estamos en la pantalla del carrito añadiendo productos
		//Validaciones de botón flotante de carrito
		Pedido pedido = Data.getInstance().getPedido();
		String cantidad = pedido.getTotalUnidades();
		String precioTotal = pedido.getPrecioTotal();

		WebElement botonVerPedidoPantallaCarito =  getElementByFluentWait(By.xpath(PaymentResource.botonVerPedidoPantallaCarritoXpath), 20, 5);
		Assert.assertTrue(botonVerPedidoPantallaCarito.isDisplayed(), "Error: No se ha encontrado el botón Ver Pedido de la pantalla Add carrito");
		
		String textBoton = (Data.getInstance().getPedido().getTipoPedido().getId() != 0)  ? PaymentResource.labelBotonVerPedidoPantallaCarrito[0] : PaymentResource.labelBotonVerPedidoPantallaCarrito[1];

		Assert.assertTrue(botonVerPedidoPantallaCarito.getAttribute("innerText")
				.split("\n")[0].contains(textBoton), "Error: No se ha encontrado el literal " + textBoton + " del boton Ver pedido / Pedir ahora");
		
		Assert.assertTrue(botonVerPedidoPantallaCarito.getAttribute("innerText")
				.split("\n")[1].contains(cantidad), "Error: no hemos encontrado la cantidad " + cantidad + " en el boton Ver pedido / Pedir ahora");
		
		String precioEncontrado = botonVerPedidoPantallaCarito.getAttribute("innerText")
				.split("\n")[2].split("€")[0].trim();
		
		
		String precioEsperado =  precioTotal.split("€")[0].trim();
				
		Assert.assertTrue(precioEncontrado.equalsIgnoreCase(precioEsperado),  "Error: no hemos encontrado el precio total esperado " + precioEsperado + " del pedido en el boton Ver pedido / Pedir ahora. Precio encontrado: " + precioEncontrado);
	}
	
	public void pagarPedidoPagarEnCaja(String formaPago, String pedidoConfirmadoString) {
		log("Se paga el pedido con "+ formaPago);
		clicJS(driver.findElement(By.xpath("//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"))); //SELECCIONO FORMA DE PAGO
		clicJS(driver.findElement(By.xpath("//button[contains(@class,'basket-button')]")));
		w2.until(ExpectedConditions.presenceOfElementLocated(By.id("orderReceiptHeader")));//ESPERO HASTA QUE SALGA POR PANTALLA EL RECIBO.
		ValidarPayment.validarReciboCheckout(pedidoConfirmadoString);

	}
	
}
