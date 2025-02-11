package AbrePortalRest;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Clientes.AbrirPaginaLogin;
import Clientes.LoginCliente;
import configuration.EnumServidor;
import utils.Data;
import utils.PortalRestOrderElements;
import utils.TestBase;

public class ListaRestaurante extends TestBase{
	
	@Test(priority = 1)
	@Parameters({"listaRestaurante"})
	public void showRestaurantList () {
		
	}
	
	/*
	 * Validar el menu al abrir la página principal Lista restaurante
	 * Actualmente tiene solo dos elementos: Idioma y Condiciones de uso
	 */
	public void validarMenuListaRestaurante () {
		
	}
	
	@Test(priority = 1)
	@Parameters({"selectRestaurante", "numeroRestaurante", "shopNameSelected"})
	public void selectRestaurant(@Optional("1") int restaurante, int numeroRestaurante, String shopNameSelected) {
		w2.until(ExpectedConditions.visibilityOfAllElements(driver.findElements(By.xpath(PortalRestOrderElements.ListaRestaurante.paginalPrincipalShopList))));
		
		String btnRestauranteXpath = PortalRestOrderElements.ListaRestaurante.botonesRestauranteXpath;
		List<WebElement> botonesRestaurante = driver.findElements(By.xpath(btnRestauranteXpath));
		
		//Abrir el restaurante seleccionado para el pedido
		if(!botonesRestaurante.isEmpty()) {
			//Validar el numero de restaurante el página principal
			int restauranteNumber = botonesRestaurante.size();
			if(restauranteNumber != numeroRestaurante) {
				log(" El numero de restaurante " + restauranteNumber + " encontrado no cuadra al numero " + numeroRestaurante + " esperado");
				org.testng.Assert.assertTrue(restauranteNumber != numeroRestaurante, " El numero de restaurante " + restauranteNumber + " encontrado no cuadra al numero " + numeroRestaurante + " esperado");
			}
			
			//Open restaurant
			//Validamos que existe el restaurante a seleccionar en el listado. Luego pulsar encima del restaurante para realizar el pedido
			String restaurantNameSelectedXpath = PortalRestOrderElements.ListaRestaurante.getNameShopSelected(shopNameSelected);
			
			String restaurantList = driver.findElements(By.xpath(PortalRestOrderElements.ListaRestaurante.shopNameListXpath)).stream()
										.filter(r -> !r.getText().isEmpty())
										.map(r -> r.getText())
										.toList().toString();
			
			log("Lista restaurantes que tiene el cliente:  " + restaurantList);
			
			org.testng.Assert.assertTrue(isElementPresent(By.xpath(btnRestauranteXpath)), "Error: No hemos encontrado el restaurante " + shopNameSelected + " para realizar nuestro pedido ."
					+ "\n Los Restaurantes encontrados: " + restaurantList);
		

			
			botonesRestaurante.forEach( r ->{r.getText();} );
			
			WebElement restaurantNameSelected = driver.findElement(By.xpath(restaurantNameSelectedXpath));
			clicJS(restaurantNameSelected);
			//botonesRestaurante.get(restaurante).click();
			espera(3000);
		}
		else {
			//
			log(" Erro: no hay restaurante listado en la página principal ");
			org.testng.Assert.assertTrue(false, " Erro: no hay restaurante listado en la página principal ");
		}
		
	}
	
	@Test (priority=2, groups = { "paginaLogin" })
	@Parameters ("login")
	public void abrePaginaLogin(String register) {
		AbrirPaginaLogin openLoginPage = new AbrirPaginaLogin();
		openLoginPage.abrePaginaLogin(register);
		espera(1100);
	}
	
	@Test(priority = 3, groups = { "loginCliente" })
	@Parameters({"resultadoEsperado", "email","password","rememberMe","shop","loginCheckout","login", "realizarPedido", "realizarPedidoString", "isMailSac"})
	public void makeOrder(@Optional ("true") String resultadoEsperado, String email, String password, @Optional ("false") String rememberMe, 
			String shop, @Optional ("false") boolean loginCheckout, String loginString, @Optional("false") boolean realizarPedido, 
			@Optional("") String realizarPedidoString, @Optional("false") boolean isMailSac) {
		
		LoginCliente dologin = new LoginCliente();
		dologin.loginCliente(resultadoEsperado, email, password, rememberMe, shop, loginCheckout, loginString, realizarPedido, realizarPedidoString, isMailSac);
		espera(1100);
		
		//Página principal How(Cómo)
		w2.until(ExpectedConditions.visibilityOfAllElements(driver.findElements(By.xpath(PortalRestOrderElements.paginalPrincipalHow))));
		
		//Seleccionar el Cómo: en el local
		String howButtonSelectXpath = PortalRestOrderElements.listaBotonesComoXpath;
		String labelHowButtonSelectXpath = PortalRestOrderElements.literalListaBotonesComoXpath;
		
		
		org.testng.Assert.assertTrue(isElementPresent(By.xpath(howButtonSelectXpath)), " Error: No hemos encontrado los botones de Cómo");
		
		org.testng.Assert.assertTrue(isElementPresent(By.xpath(labelHowButtonSelectXpath)), " Error: No hemos encontrado literales de los botones de Cómo");
		
		List<WebElement> howButtonSelect = driver.findElements(By.xpath(labelHowButtonSelectXpath));
	
		String literalButtonSelect = PortalRestOrderElements.listaLiteralBotonesComoText[0];
		org.testng.Assert.assertTrue(howButtonSelect.get(0).getText().contentEquals(literalButtonSelect), "Error: No hemos encontrado El literal " + literalButtonSelect + " esperado en el botón uno de ¿Cómo? ");
		
		//clicar en el local
		if(Data.getInstance().getConfigServer().getName() == EnumServidor.QUALITY04.getServerName() || Data.getInstance().getConfigServer().getName() == EnumServidor.QUALITY03.getServerName()) {
			String buttonEnLocalXpath = "//button[@class='how-when-button ng-star-inserted']//div/div[contains(text(), 'En el local')]";
			WebElement buttonEnLocal = getElementByFluentWait(By.xpath(buttonEnLocalXpath), 30, 5);
			clicJS(buttonEnLocal);
		}
		else 
			howButtonSelect.get(0).click();
		
		espera(1100);

	}
	
	public void checkMenuPaginaListaRestaurante () {
		
	}

}
