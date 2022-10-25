package Ventas;

import org.testng.annotations.AfterTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.model.Log;

import org.testng.AssertJUnit;
import org.testng.ITestContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Objects.ProductItem;
import Verificaciones.VerificarPedidos;
import graphql.Assert;
import utils.TestBase;

//ESTA CLASSE SE UTILIZA PARA : 
//-- VENTAS CONSECUTIVAS USANDO BACK AL FINAL DEL PEDIDO.
//-- REPETIR UN PEDIDO AL FINAL DE PROCESO DE UNA VENTA, USANDO BACK PARA AÑADIR MAS PRODUCTOS Y LUEGO TOTALIZAR.

public class RepeatAddCarrito extends TestBase {
	String repeatOrderWithBack = "";
	String repeatOrderProducts = "";
	String orderTotalPrice = "";
	
	
	@Test(description="Este test permite repetir el pedido manualmente con otros productos" , priority=1)
	@Parameters({"repeatProductos","repeatProductosTotalPrice","opcionesMenu","unidades", "goBack", "productos", "totalEsperado", "preciototalEsperado"})
	public void addCartPedido(String repeatProductos, String repeatProductosTotalPrice, @Optional ("") String opcionesMenu, @Optional ("") String unidades, 
			@Optional ("") String goBack, @Optional ("") String productos, @Optional ("") String totalEsperado, @Optional ("") String preciototalEsperado) {
		
		String firstOrderProducts = productos;
		
		if( goBack.equalsIgnoreCase("true") || goBack.equalsIgnoreCase("false")) {
						
			repeatOrderProducts = repeatProductos + "," + productos;
			
			repeatProductosTotalPrice  = preciototalEsperado;
		}
				
		AddCarrito	repeatPedido = new AddCarrito();
		repeatPedido.addCart(repeatProductos, repeatProductosTotalPrice, opcionesMenu, unidades, goBack, firstOrderProducts);
	}
	
	@Test(description="Este test permite el checkout del pedido repetido manualmente" , priority=1)
	@Parameters({"repeatProductos","repeatProductosTotalPrice","formaPago","nuevaTarjeta","testCardNumber","cad1","cad2","cvv","pedidoConfirmadoString" , "shop", "email", "miMonederoString",
		"formaPago2", "tipoServicio","unidades","mesa", "totalEsperadoMasCargos", "repartoPermitido", "goBack", "productos", "totalEsperado"})
	public void checkOutPedido(String repeatProductos, String repeatProductosTotalPrice, String formaPago,
			@Optional ("true") String nuevaTarjeta, @Optional ("4548812049400004") String testCardNumber,
			@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv, String pedidoConfirmadoString, 
			String shop, String customerMail, @Optional ("")String miMonederoString, @Optional ("") String formaPago2, 
			String tipoServicio, @Optional ("") String unidades, @Optional ("") String mesa, @Optional ("") String totalEsperadoMasCargos,
			@Optional ("true") String repartoPermitido,  @Optional ("") String goBack, @Optional ("") String productos, @Optional ("") String totalEsperado) {
		
		if( goBack.equalsIgnoreCase("true") ) {		
			goBack = "false";
			//Los productos del pedido
			repeatProductos = repeatOrderProducts;
			//el precio total esperado despues de repetir el pedido
			repeatProductosTotalPrice = totalEsperado;			
		}
		
		CheckOut	checkout = new CheckOut();
		checkout.finalizarPedido(repeatProductos, repeatProductosTotalPrice, formaPago, nuevaTarjeta, testCardNumber, cad1, cad2, cvv, pedidoConfirmadoString, 
				shop, customerMail, miMonederoString, formaPago2, tipoServicio, unidades, mesa, totalEsperadoMasCargos, repartoPermitido, goBack);
	}
	
	
	@Test(description="Este test permite validar en la BDD el pedido repetido manualmente" , priority=1)
	@Parameters({"shop","email","totalEsperado", "tipoServicio", "mesa", "totalEsperadoMasCargos", "repartoPermitido"})
	public void validarPedido(String shop, String customerMail, String netAmount, int tipoServicio, @Optional ("") String mesa,
			@Optional ("") String totalEsperadoMasCargos, @Optional ("") String repartoPermitido)  {
		
		ValidacionPedidos validatesOrder = new ValidacionPedidos();
		validatesOrder.validarPedidoSimpleBD(shop, customerMail, netAmount, tipoServicio, mesa, totalEsperadoMasCargos, repartoPermitido);
	}
}
