package AbrePortalRest;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Verificaciones.VerificarCookies;
import graphql.Assert;
import utils.TestBase;

public class AbrirUrl extends TestBase {
	boolean  isFindedBtnMasTarde = false;
	
	@Test()
	 @Parameters({ "Url", "pedidoMasTarde", "pedidoDiaHora"})
	  public void abrirURL(String Url, @Optional("false") boolean pedidoMasTarde, @Optional("false") boolean pedidoDiaHora) {
		boolean paginaCargada = false;  
		espera(500);
		  driver.get(Url);
		  espera(500);
		// JavaScript Executor to check ready state
	      JavascriptExecutor j = (JavascriptExecutor)driver;
	      if (j.executeScript("return document.readyState").toString().equals("complete")){    
	      }
		
	      //iterate 10 times after every one second to verify if in ready state
	      for (int i=0; i<10; i++){
	         try {
	            Thread.sleep(1000);
	         }catch (InterruptedException ex) {
	            System.out.println("Page has not loaded yet ");
	         }
	         //again check page state
	         if (j.executeScript("return document.readyState").toString().equals("complete")){
	        	log("Página Cargada - Title: " + driver.getTitle());
	        	paginaCargada = true;
	            break;
	         }else {
	        	 System.out.println("Página no cargada todavia");
	         }
	      }
	      
	      if(paginaCargada && pedidoMasTarde && !pedidoDiaHora) {
	    	  String elemtBtnMasTarde = "//app-when-deliver/div/div[2]/div/div/button[1]/div[1]";
	    	  abrirPedidoMasTarde (elemtBtnMasTarde);
	      }
	      else if(paginaCargada && !pedidoMasTarde && pedidoDiaHora) {
	    	  String elemtBtnDiaHora = "//app-when-deliver/div/div[2]/div/div/button[2]/div[1]";
	    	  abrirPedidoMasTarde(elemtBtnDiaHora);
	      }
	  }
	
	public void abrirPedidoMasTarde (String xpathBtn) {
		String paraCuandoXpath = "//div[contains(@class, 'how-when-button-list')]";
		
		waitUntilPresence(paraCuandoXpath, true);
		
		String btnHowWhenList =  "//div[contains(@class, 'how-when-button-list')]//button[contains(@class, 'how-when-button')]";
		
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(btnHowWhenList)));
		List<WebElement> btnHowWhenListWbmt = driver.findElements(By.xpath(btnHowWhenList));
		
		//Verificar cookies
		verificarCookies();
		
		espera(1500);
		btnHowWhenListWbmt.forEach( how -> {
			
			if(isElementPresent(By.xpath(xpathBtn))) {
				clicJS(how.findElement(By.xpath(xpathBtn)));
				espera(2000);
				isFindedBtnMasTarde = true;
			}
		});
		
		if(!isFindedBtnMasTarde) {
			log("No hemos encontrado el botón para pedido Más tarde");
			Assert.assertTrue(false);
		}
	}

	public void verificarCookies() {
		//Verificar cookies
		VerificarCookies validarCookies = new VerificarCookies();
		validarCookies.verificarCookies();
		espera(500);
		 if(validarCookies.isVericarCookies()) {
		 	validarCookies.aceptaCookies();
		 }
//		 else {
//			 log("Error al verifica cookies: no sale la ventana de aceptaci�n de cookies");
//			 Process process = null;
//			 process.
//		 }
	}

}
