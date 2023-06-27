package PaymentMethod;

public class PaymentResource {
	
	public static String botonVerPedidoPantallaCarritoXpath = "//button[contains(@class,'basket-button')]";
	
	public static String pantallaResumenPedido = "//div[contains(@class,'basket-lines-container')]"; //Pantalla Resumen de pedido: elegir el medio de pago
	
	public static String checkBoxeXpath = "//div[contains(@class,'mat-checkbox-inner-container')]"; // check botón de aceptar condiciones y terminos de pago
	
	public static String botonPagarXpath = "//button[contains(@class,'basket-button')]"; // se muestran en botón el importe total del pedido
	
	public static String getButtonFormaPagoXpath(String formaPago) { // Escoger el medio de pago para finalizar el pedido
		
		return  "//div[contains(@class,'payment-means-wrapper')]//div[contains(text(),'"+formaPago+"')]"; // para seleccionar el medio de pago 
	}
	
	/*
	 * eliminar un producto del pedido
	 */
	public static String getBotonEliminarArticuloDelPedido(String articulo) {//Recuperar el botón del producto para eliminarlo del pedido
		String botonSuprimirArticulo = "//label[contains(text(), '1 x "+articulo +"')]//parent::div//following-sibling::div//app-input-number-spinner//*[contains(@class, 'number-spinner-spinner left-spinner')]";
		return botonSuprimirArticulo;
	}
	
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

}
