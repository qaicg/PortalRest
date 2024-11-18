package Ventas;

import org.testng.annotations.AfterTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

//import com.aventstack.extentreports.model.Log;
import com.mysql.cj.util.StringUtils;

import org.testng.AssertJUnit;
import org.testng.ITestContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Objects.ProductItem;
import Verificaciones.VerificarPedidos;
import graphql.Assert;
import utils.RetryTestsFailed;
import utils.TestBase;

//ESTA CLASSE SE UTILIZA PARA : 
//-- VENTAS CONSECUTIVAS USANDO BACK AL FINAL DEL PEDIDO.
//-- REPETIR UN PEDIDO AL FINAL DE PROCESO DE UNA VENTA, USANDO BACK PARA AÑADIR MAS PRODUCTOS Y LUEGO TOTALIZAR.
//-- REALIZAR UNA VENTA Y AL FINALIZAR REPETIR PEDIDO. 

public class RepeatAddCarrito extends TestBase {
	String repeatOrderWithBack = "";
	String repeatOrderProducts = "";
	String orderTotalPrice = "";
	String isFindWhereRepeatOrder = "false";
	
	
	@Test(description="Este test permite repetir el pedido manualmente con otros productos" , priority=1, groups = {"addRepeatOrder"})
	@Parameters({"repeatProductos","repeatProductosTotalPrice","opcionesMenu","unidades", "goBack", "productos", "totalEsperado", "preciototalEsperado", "goBackByAddOrderButton", "abrirFichaProducto", "formatos"})
	public void addCartPedido(String repeatProductos, String repeatProductosTotalPrice, @Optional ("") String opcionesMenu, @Optional ("") String unidades, 
			@Optional ("") String goBack, @Optional ("") String productos, @Optional ("") String totalEsperado, @Optional ("") String preciototalEsperado,
			@Optional ("") String goBackByAddOrderButton, @Optional("false") boolean abrirFichaProducto, @Optional("") String formatos) {
		
		String firstOrderProducts = productos;
		
		if( goBack.equalsIgnoreCase("true") || goBack.equalsIgnoreCase("false")) {
						
			repeatOrderProducts = repeatProductos + "," + productos;
			
			repeatProductosTotalPrice  = preciototalEsperado;
		}
				
		AddCarrito	repeatPedido = new AddCarrito();
		repeatPedido.addCart(repeatProductos, repeatProductosTotalPrice, opcionesMenu, unidades, goBack, firstOrderProducts, goBackByAddOrderButton, abrirFichaProducto, formatos);
	}
	
	@Test(description="Este test permite el checkout del pedido repetido manualmente" , priority=2, groups = {"repeatOrderCheckOut"})
	@Parameters({"repeatProductos","repeatProductosTotalPrice","formaPago","nuevaTarjeta","testCardNumber",
		"cad1","cad2","cvv","pedidoConfirmadoString" , "shop", "email", "miMonederoString",
		"formaPago2", "tipoServicio","unidades","mesa", "totalEsperadoMasCargos", "repartoPermitido", "goBack", "productos", 
		"totalEsperado", "goBackByAddOrderButton", "importeMinimo","validarImporteMinimo"})	
	public void checkOutPedido(@Optional ("") String repeatProductos, @Optional ("") String repeatProductosTotalPrice, String formaPago,
			@Optional ("true") String nuevaTarjeta, @Optional ("4548810000000003") String testCardNumber,
			@Optional ("01") String cad1, @Optional ("28") String cad2, @Optional ("123") String cvv, String pedidoConfirmadoString, 
			String shop, String customerMail, @Optional ("")String miMonederoString, @Optional ("") String formaPago2, 
			String tipoServicio, @Optional ("") String unidades, @Optional ("") String mesa, @Optional ("") String totalEsperadoMasCargos,
			@Optional ("true") String repartoPermitido,  @Optional ("") String goBack, @Optional ("") String productos, @Optional ("") String totalEsperado,
			@Optional ("") String goBackByAddOrderButton, @Optional ("") String importeMinimo, @Optional ("") String validarImporteMinimo) {
		
		if( goBack.equalsIgnoreCase("true") || goBackByAddOrderButton.equalsIgnoreCase("true")) {		
			goBack = "false";
			goBackByAddOrderButton = "false";
			//Los productos del pedido
			repeatProductos = repeatOrderProducts;
			//el precio total esperado despues de repetir el pedido
			repeatProductosTotalPrice = totalEsperado;			
		}
		
		if((StringUtils.isNullOrEmpty(goBack) && StringUtils.isNullOrEmpty(goBackByAddOrderButton)) && (StringUtils.isNullOrEmpty(repeatProductos) && (StringUtils.isNullOrEmpty(repeatProductosTotalPrice)))) {
			repeatProductos = productos;
			repeatProductosTotalPrice = totalEsperado;
			goBack = "false";
			goBackByAddOrderButton = "false";
		}
		
		CheckOut	checkout = new CheckOut();
		checkout.finalizarPedido(repeatProductos, repeatProductosTotalPrice, formaPago, nuevaTarjeta, testCardNumber, cad1, cad2, cvv, pedidoConfirmadoString, 
				shop, customerMail, miMonederoString, formaPago2, tipoServicio, unidades, mesa, totalEsperadoMasCargos, repartoPermitido, goBack, goBackByAddOrderButton, importeMinimo, validarImporteMinimo);
	}
	
	
	@Test(description="Este test permite validar en la BDD el pedido repetido manualmente" , priority=3, groups = {"repeatVerifyOrder"})
	@Parameters({"shop","email","totalEsperado", "tipoServicio", "mesa", "totalEsperadoMasCargos", "repartoPermitido"})
	public void validarPedido(String shop, String customerMail, String netAmount, int tipoServicio, @Optional ("") String mesa,
			@Optional ("") String totalEsperadoMasCargos, @Optional ("") String repartoPermitido)  {
		
		ValidacionPedidos validatesOrder = new ValidacionPedidos();
		validatesOrder.validarPedidoSimpleBD(shop, customerMail, netAmount, tipoServicio, mesa, totalEsperadoMasCargos, repartoPermitido);
	}
	
	@Test(description="Este test permite repetir el pedido via el boton repetir pedido al finalizar una venta", priority=1, groups = {"runRepeatOrder"})
	@Parameters({"ultimoPedido", "menu", "profile", "pedidos", "productos", "totalEsperado", "whereRepeateOrder" })
	public void repeatOrder(@Optional ("true") String ultimoPedido, String menu, String profile, String pedidos, String productos, String totalEsperado, @Optional ("") String whereRepeateOrder) {
		VerificarPedidos order = new VerificarPedidos();
		order.VerificarPedidos(ultimoPedido, menu, profile, pedidos, productos, totalEsperado);
		WebElement repeatOrderButton = null;
		
		if(order.resulTest()) {
			// recuperamos el numedo del pedido que sera repetido y el precio total
			
			//versio actual
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul/preceding::div[5]")));
			
			//versio estable
			//w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul/preceding::div[1]")));
			
			//versio actual
			String numeroPedidoActual = driver.findElement(By.xpath("//ul/preceding::div[5]")).getText();
			
			//versio estable
			//String numeroPedidoActual = driver.findElement(By.xpath("//ul/preceding::div[1]")).getText();
			
			// test de verificar pedido ha ido bien
			log("la verificacion del pedido con numero " + numeroPedidoActual + " ha ido bien");
			
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'btn-centered')]/child::div")));
			repeatOrderButton = driver.findElement(By.xpath("//div[contains(text(),'Repetir pedido')]"));
			repeatOrderButton.click();
			
			log("Vuelver a repetir el pedido  " + numeroPedidoActual + " con los productos " + productos + " y precio " + totalEsperado);
			espera(1000); // Wait for go by using repeat order button
			
			//Verificar donde estamos 
			if(!StringUtils.isNullOrEmpty(whereRepeateOrder)) {
				w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//app-how-when//div[contains(@class, 'how-when-button-list')]")));
				List<WebElement> pageHowDeliver = driver.findElements(By.xpath("//app-how-when//div[contains(@class, 'how-when-button-list')]"));	
				
				if(pageHowDeliver.size() != 0) {
					findWhereRepeatOrder(whereRepeateOrder);
				}
			}
			
		} else {
			log("No se ha podido vericar el pedido antes de repetirlo  con los productos " + productos + " y precio " + totalEsperado);
			Assert.assertTrue(false);
		}
	}
	
	public void findWhereRepeatOrder(@Optional String howDeliver) {
		WebElement whereRepeatOrder = null;
		
		if(StringUtils.isNullOrEmpty(howDeliver)){
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//app-how-when//div[contains(@class, 'how-when-button-list')]/child::button[1]")));
			whereRepeatOrder = driver.findElement(By.xpath("//app-how-when//div[contains(@class, 'how-when-button-list')]/child::button[1]"));	
			isFindWhereRepeatOrder = "true";
		} else {
			// elegir donde vamos repetir el pedido
			w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//app-how-when//div[contains(@class, 'how-when-button-list')]/child::button['"+ howDeliver+"']")));
			whereRepeatOrder = driver.findElement(By.xpath("//app-how-when//div[contains(@class, 'how-when-button-list')]/child::button['"+ howDeliver + "']"));
			isFindWhereRepeatOrder = "true";
		}
		
		if(isFindWhereRepeatOrder.equalsIgnoreCase("true")) {
			whereRepeatOrder.click();
			espera(1000); // Wait for go by using how-delivery button
		}
		
	}
}
