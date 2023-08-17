package PaymentMethod;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import utils.TestBase;

public class PaymentResource {
	
	public static String botonVerPedidoPantallaCarritoXpath = "//button[contains(@class,'basket-button')]";
	
	public static String[] labelBotonVerPedidoPantallaCarrito = {"Ver pedido", "Pedir ahora"}; //0 > pedido, 1 > hioPay  
	
	public static String pantallaResumenPedido = "//div[contains(@class,'basket-lines-container')]"; //Pantalla Resumen de pedido: elegir el medio de pago
	
	public static String checkBoxeXpath = "//div[contains(@class,'mat-checkbox-inner-container')]"; // check botón de aceptar condiciones y terminos de pago
	
	public static String botonPagarXpath = "//button[contains(@class,'basket-button')]"; // se muestran en botón el importe total del pedido
	
	public static String precioBotonPagarXpath = "//div[contains(@class, 'basket-button-amount')]"; // el precio pintado en el botón Pagar
		
	
	public static String getButtonFormaPagoXpath(String formaPago) { // Escoger el medio de pago para finalizar el pedido
		
		return  "//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"; // para seleccionar el medio de pago 
	}
	
	/*
	 * Pantalla RESUMEN PEDIDO
	 * eliminar un producto del pedido
	 */
	public static String getBotonEliminarArticuloDelPedido(String articulo) {//Recuperar el botón del producto para eliminarlo del pedido desde la pantalla RESUMEN PEDIDO
		String botonSuprimirArticulo = "//label[contains(text(), '1 x "+articulo +"')]//parent::div//following-sibling::div//app-input-number-spinner//*[contains(@class, 'number-spinner-spinner left-spinner')]";
		return botonSuprimirArticulo;
	}
	
	/*
	 * Pantalla RESUMENT PEDIDO
	 * Aumentar la cantidad del product
	 */
	public static String getBotonMasCantidadArticuloDelPedido(String articulo) {//Recuperar el botón + del producto para aumentar la cantidad del artículo desde la pantalla RESUMEN PEDIDO
		String botonSuprimirArticulo = "//label[contains(text(), '1 x "+articulo +"')]//parent::div//following-sibling::div//app-input-number-spinner//*[contains(@class, 'number-spinner-spinner right-spinner')]";
		return botonSuprimirArticulo;
	}
	
	
	/*
	 * Pantalla RESUMEN PEDIDO
	 * Obtener la cantidad asignada al producto
	 */
	public static String getCantidadArticuloDelPedido(String articulo) {//Recuperar la cantidad del artículo desde la pantalla RESUMEN PEDIDO
		String botonSuprimirArticulo = "//label[contains(text(), '1 x "+articulo +"')]//parent::div//following-sibling::div//app-input-number-spinner//*[contains(@class, 'number-spinner-spinner right-spinner')]";
		return botonSuprimirArticulo;
	}
	
	/*
	 * Pantalla de pago del pedido
	 * Medio de pago Bizum
	 * 
	 */
	public static class Bizum {
		
		public static String waitFormularioBizumXpath = "//div[contains(@class, 'wrapper position-relative')]"; // El formulario de la plataforma de pago del banco
		
		public static String botonCancelarXpath = "//div/a[contains(@class, 'volver-tienda-btn')]"; // el boton para cancelar el pago del pedido
		
		public static String inputTelefoneXpath = "//div/input[contains(@id, 'iPhBizInit')]"; // introducir el telefone para pagar con bizum
		
		public static String importApagarXpath = "//p[contains(@class, 'datos')]/span/span[contains(@id, 'import')]"; // Importe del pago
		
		public static String resultErorWarning = "//div[contains(@class, 'warn-wrapper')]"; //Warning en el caso que el pago sea rejectada
		
		public static String resultBodyPagoOK = "//div[contains(@class, 'result-header')]//div[contains(@class, 'result-code ok')]"; // el pago está aceptada
		
		public static String resultPagoCodeOK = "//div[contains(@class, 'result-header')]//div[contains(@class, 'result-code ok')]"; //Inofrmación que el pago ha sido aceptada 
		
		public static String resultPagoCodeError =  "//div[contains(@class, 'result-header')]//div[contains(@class, 'result-code error')]"; //Inofrmación que el pago ha sido denegado 
		
		public static String textPagoDenegadoMessageError = "No se puede realizar la operación\r\n" + "Transacción denegada"; //el mesage de la entidad bancaria para informar al cliente que su pago ha sido denegado
		
		public static String botonContinuarPagoXpath = "//div/input[contains(@lngid, 'continuar')]"; // el botón continuar para finalizar el pago
		
		public static String resultAlCancelarPagoXpath = "//div[contains(@class, 'result-header')]//*[contains(@id, 'result-error')]"; // pago cancelado
		
		public static String textMessageErrorAlCancelarPago = "Operación cancelada. El usuario no desea seguir."; //Mesage al confirmar la cancelación de la transacción parte del cliente
		
		public static String resultPagoErrorBizumXpath = "//div[contains(@class, 'result-header')]//div[contains(@class, 'result-code error')]//text[contains(@lngid, 'errorBizum9673')]"; //Información que el pago del pedido ha sido cancelado por parte del cliente.
		
		//** Numero no activado para compra Bizum
		public static String numeroNoActivadoEnBizumXpath = "//span[contains(@id, 'mensaje-error-no-disp')]";
		public static String messageErrorParaFakeNumero = "Usuario no activo para compra Bizum\n\nConsulta nuestra web" ;
				
		//** Fin Usuario no activo para pagar con Bizum
		
		public static String getBotonContunarXpath(boolean disabled) {
			String diasable = String.valueOf(disabled);
			return disabled ?  "//div/button[contains(@class, 'bBizInit') and contains(@disabled, '"+diasable+"')]" :  "//div/button[contains(@id, 'bBizInit')]";
		}
		
	}
	
	/*
	 * Pantalla de pago
	 * Medio de pago Redsys
	 */
	public static class Redsys {
		public static String matSelectPaymentCard = "//mat-select[contains(@class, 'mat-select payment-card-select')]";
		public static String selectPaymentCard = "//mat-select[contains(@class, 'mat-select payment-card-select')]//div[contains(@class, 'mat-select-value')]";
		
		public static String selectCard = "//div[contains(@class, 'mat-select-arrow-wrapper')]/div";
		public static String elementNuevaTarjeta ="//span[contains(@class, 'mat-option-text')]//label[contains(text(), 'Nueva tarjeta')]";
		
	}
	
	/*
	 * Propinas 
	 * 	
	 */
	public static class Propina {
		public static String waitFormularioPropinaXpath = "//app-tip[@class='basket-tip-wrapper']"; // el apartado de las propinas
		
		public static String listadoPropinasXpath = "//div[contains(@class, 'tip-percentage')]/ancestor::div[contains(@class, 'tip')]"; //listado de propina
		
		public static String propinaSelectedXpath = "//div[@class='tip selected']" ;// la propina seleccionada
		
		public static String precioPorpinaSeleccionadaXpath = "//div[contains(@class, 'price-breakdown-line')]//child::div[contains(text(), 'Propina')]/following-sibling::div"; // Precio de la propina elegina
		
		public static String subTotalSinPropinaXpath = "//div[contains(@class, 'price-breakdown-line')]//child::div[contains(text(), 'Subtotal')]/following-sibling::div"; // subTotal
		
		public static String precioTotalPagarXpath = "//div[contains(@class, 'price-breakdown-line')]//child::div[contains(text(), 'Total')]/following-sibling::div"; //Total a pagar co
		
		
		public static String getSubTotal() {
			WebElement subTotal = TestBase.getElementByFluentWait(By.xpath(subTotalSinPropinaXpath), 30, 5);
			return subTotal.getText();
		}
		
		public static String getPrecioPropina() {
			WebElement precioPropina = TestBase.getElementByFluentWait(By.xpath(precioPorpinaSeleccionadaXpath), 30, 5);
			return precioPropina.getText();
		}
		
		public static String getPrecioTotalPagar() {
			WebElement precioTotalPaga = TestBase.getElementByFluentWait(By.xpath(precioTotalPagarXpath), 30, 5);
			return precioTotalPaga.getText();
		}
		
		public static String getPrecioTotalBotonPagar() {
			WebElement precioTotalPagaBotonPagar = TestBase.getElementByFluentWait(By.xpath(precioBotonPagarXpath), 30, 5);
			return precioTotalPagaBotonPagar.getText();
		}
		
		public static boolean isPropinaSelected () {
			
			return TestBase.isElementPresent(By.xpath(propinaSelectedXpath)) && TestBase.isElementPresent(By.xpath(precioPorpinaSeleccionadaXpath));
	
		}
		
	}

}
