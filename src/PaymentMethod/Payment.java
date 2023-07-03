package PaymentMethod;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import pedido.Pedido;
import utils.Data;
import utils.TestBase;

public class Payment extends TestBase {
	public enum paymentType{
		
	}
	
	public void makePayment() {
		
	}
	
	@Test(description= "Pulsar el botón Ver Pedido / Pedir ahora", priority = 1)
	public void runButtonOrder() {
		//Estamos en la pantalla del carrito añadiendo productos
		validationLabelButtonOrder ();
		WebElement botonVerPedidoPantallaCarito =  getElementByFluentWait(By.xpath(PaymentResource.botonVerPedidoPantallaCarritoXpath), 20, 5);
		
		botonVerPedidoPantallaCarito.click();
		espera(1000);
	}
	
	public void validationLabelButtonOrder () {
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

}
