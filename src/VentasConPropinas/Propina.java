package VentasConPropinas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import PaymentMethod.PaymentResource;
//import PaymentMethod.PaymentResource.Propina;
import utils.TestBase;

public class Propina extends TestBase {
	String botonPagarXpath = PaymentResource.botonPagarXpath;
	String precioBotonPagarXpath = PaymentResource.precioBotonPagarXpath;
	 
	String propinaWrapperXpath = PaymentResource.Propina.waitFormularioPropinaXpath;
	
	String subTotalSinPropinaXpath = PaymentResource.Propina.subTotalSinPropinaXpath;
	String precioPorpinaSeleccionadaXpath = PaymentResource.Propina.precioPorpinaSeleccionadaXpath;
	String precioTotalPagarXpath = PaymentResource.Propina.precioTotalPagarXpath;
	String propinaSelectedXpath = PaymentResource.Propina.propinaSelectedXpath;
	
	@Test(description = "Prueba de ventas con propinas ", priority = 1)
	@Parameters({ "propinaList", "propinaSelected", "precioTotalConPropina", "totalEsperado"}) 
	public void checkPropina (@Optional("") String propinaList, @Optional("") String propinaSelected, @Optional("") String precioTotalConPropina, String totalEsperado) {
		//Validar que estamos en la pantalla de pago y que las propinas son visibles.
		String[] propinasListEsperadas = propinaList.split(";");
		
		
		WebElement propinaWrapper = getElementByFluentWait(By.xpath(propinaWrapperXpath), 30, 5);
		
		Assert.assertTrue(propinaWrapper.isDisplayed(), "No se muestran las propinas en la pantalla de pago!!!");
		
		log("Las propinas sugeridas --> " + propinaWrapper.getText());
		
		String[] propinaListEncontrada = {propinaWrapper.getText().split("\n")[0] + "=" + propinaWrapper.getText().split("\n")[1], 
				propinaWrapper.getText().split("\n")[2] + "=" +propinaWrapper.getText().split("\n")[3],
				propinaWrapper.getText().split("\n")[4] + "=" +propinaWrapper.getText().split("\n")[5]};
		
		//Validar el numero de propina muestrado a la pantalla
		Assert.assertTrue(propinasListEsperadas.length == propinaListEncontrada.length, "El numero de propinpa esperado " + propinasListEsperadas.length +" no cuadra al numero de propina encontrado" + propinaListEncontrada.length );
		
		//Listado de las propinas 
		String propinaElementListXpath = PaymentResource.Propina.listadoPropinasXpath;
		List<WebElement> propinaElementList = getElementsByFluentWait(By.xpath(propinaElementListXpath), 30, 5);
		

		
		//Validar las propinas sugeridas en la pantalla con las esperadas
		int numPropina = 0;
		for(String propina: propinaListEncontrada) {
			Assert.assertTrue(propina.equalsIgnoreCase(propinasListEsperadas[numPropina]), "La propina " + propina + " no cuadra con la esperada " + propinasListEsperadas[numPropina]);
			
			//Validar que no hay propina preseleccionada por defecto
			Assert.assertFalse(isElementPresent(By.xpath(propinaSelectedXpath)), "La aplicación no debería preseleccionar propina");
			
			clicJS(propinaElementList.get(numPropina)); //Seleccionamos la propina
			espera(1000);
			
			//Validar importes Subtotal, Propina, Total, cantidad en el botón pagar
			String subTotalEsperado = totalEsperado;
			String pricioPropinaEsperado = propinasListEsperadas[numPropina].split("=")[1];
			String totalPagarEsperado = getTotalPagarEsperadoByCalcul(subTotalEsperado, pricioPropinaEsperado);
			
			Assert.assertTrue(getSubTotal().equalsIgnoreCase(subTotalEsperado), "No es valido el subTotal del pedido");
			
			Assert.assertTrue(getPrecioPropina().equalsIgnoreCase(pricioPropinaEsperado), "No es valid el precio de la propina seleccionada");
			
			Assert.assertTrue(getPrecioTotalPagar().equalsIgnoreCase(totalPagarEsperado), "No es valido el Total del pedido a pagar");
			
			Assert.assertTrue(getPrecioTotalBotonPagar().equalsIgnoreCase(totalPagarEsperado), "El importe a pagar del boton pagar no es correcto");
			
			//deseleccionar la propina
			clicJS(propinaElementList.get(numPropina));
			
			numPropina++;
		}
		
		// Todo ha pasado bien, seleccionar la primera propina y continuar con el proceso de finalización del pedido
		if(!isEmptyOrWhitespaceOnly(propinaSelected)) {
			clicJS(propinaElementList.get(Integer.parseInt(propinaSelected)-1)); //Seleccionamos la propina
			espera(1000);
		} else {
			log("No queremos seleccionar propinas sugeridas en la pantalla de pago!!!");
		}
	}
		
	private String getSubTotal() {
		return PaymentResource.Propina.getSubTotal();
	}
	
	private String getPrecioPropina() {
		return PaymentResource.Propina.getPrecioPropina();
	}
	
	private String getPrecioTotalPagar() {
		return PaymentResource.Propina.getPrecioTotalPagar();
	}
	
	private String getPrecioTotalBotonPagar() {
		return PaymentResource.Propina.getPrecioTotalBotonPagar();
	}
	
	private String getTotalPagarEsperadoByCalcul(String subTotalEsperado, String pricioPropinaEsperado) {
		return String.valueOf(  Double.parseDouble(subTotalEsperado.replace("€", "").replace(",", "."))  +   Double.parseDouble(pricioPropinaEsperado.replace("€", "").replace(",", "."))  ).replace(".", ",") + "€";
	}

}
