package pedido;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.aventstack.extentreports.MediaEntityBuilder;

import Verificaciones.VerificarCookies;
import graphql.Assert;
import interfaces.ITipoPedido;
import interfaces.ITipoPedidoAhora;
import interfaces.ITipoPedidoDiaHora;
import interfaces.ITipoPedidoMasTarde;
import utils.TestBase;

public class TipoPedido extends TestBase{
	private boolean  isFindedBtn = false;

	public boolean isFindedBtn() {
		return isFindedBtn;
	}


	public void setFindedBtn(boolean isFindedBtn) {
		this.isFindedBtn = isFindedBtn;
	}


	int id;
	String label;
	String elementXpath;
	

	String dia;
	String hora;
		
	public ITipoPedido orderType;
	
	public TipoPedido(int orderTypeId) {
		log("orderTypeId -> " + orderTypeId);
		if(StringUtils.isNumeric(String.valueOf(orderTypeId))) {
			if(ITipoPedidoAhora.id == orderTypeId)
			{
				id = ITipoPedidoAhora.id;
				label = ITipoPedidoAhora.label;
				elementXpath = ITipoPedidoAhora.label;
				
			} else if(ITipoPedidoDiaHora.id == orderTypeId) {
				id = ITipoPedidoDiaHora.id;
				label = ITipoPedidoDiaHora.label;
				elementXpath = ITipoPedidoDiaHora.elementXpath;
				
			} else if(ITipoPedidoMasTarde.id == orderTypeId) {
				id = ITipoPedidoMasTarde.id;
				label = ITipoPedidoMasTarde.label;
				elementXpath = ITipoPedidoMasTarde.elementXpath;
			}
		} else if(StringUtils.isNumeric(String.valueOf(orderTypeId)) && ( orderTypeId == ITipoPedido.id)) {
			//estamos en HioPay
			id = orderTypeId;
			label = "HioPay";
			isFindedBtn = true;
			
		} else {
			log("Error: No hemos encontrado el tipo pedido: " + orderTypeId);
			log("Se espera encontrar 0, 1, 2 o 3");
			Assert.assertTrue(false);
		}
		
	}
		

	/*
	 * Permite seleccionar cuando para el pedido: Más tarde(Avisar por email) o día y hora
	 */
	public void abrirPedido() {
		
		String paraCuandoXpath = "//div[contains(@class, 'how-when-button-list')]";
		
		waitUntilPresence(paraCuandoXpath, true);
		
		String btnHowWhenList =  "//div[contains(@class, 'how-when-button-list')]//button[contains(@class, 'how-when-button')]";
		
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(btnHowWhenList)));
		List<WebElement> btnHowWhenListWbmt = driver.findElements(By.xpath(btnHowWhenList));
		
		//Verificar cookies
		try {
			verificarCookies();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		espera(1500);
		btnHowWhenListWbmt.forEach( how -> {
			
			if(isElementPresent(By.xpath(this.elementXpath))) {
				clicJS(how.findElement(By.xpath(this.elementXpath)));
				espera(2000);
				isFindedBtn = true;
			}
		});
		
		if(!isFindedBtn) {
			log("No hemos encontrado el botón para el pedido -> " + this.label);
			Assert.assertTrue(false);
		}
	}
	
	
	public void verificarCookies() throws IOException {
		//Verificar cookies
		VerificarCookies validarCookies = new VerificarCookies();
		try {
			validarCookies.verificarCookies();
			espera(1500);
			
		}
		catch (Exception e) {
			// TODO: handle exception
			validarCookies.setVericarCookies(false);
			//super.getExtentTest().error(e, MediaEntityBuilder.createScreenCaptureFromPath("errorAcceptCookies.png").build());
			//super.getExtentTest().info("Error al verificar cookies: no sale la ventana de aceptación de cookies");
			//super.getExtentTest().fail("Error al verificar cookies: no sale la ventana de aceptación de cookies");
			//super.getExtentTest().skip("No ha podido validar las cookies por que el test anterio ha fallado y sus cookies no se han eliminadas !!!");
			log("No Hemos encontrado la classe cookies-info-content");
			
		}
		
		espera(500);
		 if(validarCookies.isVericarCookies()) {
		 	validarCookies.aceptaCookies();
		 }
		 
	}

	//*********** Getters and Setters **************
	public String getDia() {
		return dia;
	}


	public void setDia(String dia) {
		this.dia = dia;
	}


	public String getHora() {
		return hora;
	}


	public void setHora(String hora) {
		this.hora = hora;
	}


}
