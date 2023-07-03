package AbrePortalRest;

import java.io.IOException;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.MediaEntityBuilder;

import Verificaciones.VerificarCookies;
import graphql.Assert;
import interfaces.ITipoPedido;
import interfaces.ITipoPedidoAhora;
import interfaces.ITipoPedidoDiaHora;
import interfaces.ITipoPedidoMasTarde;
import pedido.TipoPedido;
import pedido.TipoPedido.*;
import pedido.Pedido;
import utils.Data;
import utils.TestBase;

public class AbrirUrl extends TestBase {
	private static ITipoPedido tipoPedidoAhora = null;
	boolean  isFindedBtnMasTarde = false;
	public static ITipoPedido newTipoOrder;
	public Pedido pedido = new Pedido();
	public TipoPedido orderType;
	
	@Test(groups = { "openbrowser" })
	 @Parameters({ "Url", "pedidoMasTarde", "pedidoDiaHora", "tipoPedido"})
	  public void abrirURL(String Url, @Optional("false") boolean pedidoMasTarde, @Optional("false") boolean pedidoDiaHora, @Optional("1") String tipoPedido) {
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
	      
	      
	  	/*
	  	 * Tipo de pedido
	  	 * 
	  	 */
	      
	  	/**
	  	 * tipoPedido
	  	 *  0 -> Hiopay
	  	 *  1 -> Pedido por Ahora : Por defecto
	  	 *  2 -> Pedido por Más tarde con aviso por email
	  	 *  3 -> Pedido por día y hora
	  	 */
	     espera(1500);
	     
	  	 int iTipoPedido = Integer.parseInt(tipoPedido);
	     switch(iTipoPedido) {
	      	case 0:
	      		log("Estamos en HioPay");
	      		orderType =  new TipoPedido(iTipoPedido); 
	      		break;
	      	case 1:
	      		log("PortalRest pedido para Ahora");
	      		orderType =  new TipoPedido(iTipoPedido); 
	      		break;
	      	case 2:
	      		log("PortalRest pedido para más tarde");
	      		orderType =  new TipoPedido(iTipoPedido);    
	      		orderType.abrirPedido();
	    	  break;
	      	case 3:
	      		log("PortalRest pedido por día y hora");
		    	orderType =  new TipoPedido(iTipoPedido); 
		    	orderType.abrirPedido();
	      }
	      
//	      if(paginaCargada && pedidoMasTarde && !pedidoDiaHora) {
//	    	 //
//	    	  log("Estamos con pedido para más tarde");
//	    	  orderType =  new TipoPedido(2);    
//	    	  orderType.abrirPedido();
//
//	      }
//	      else if(paginaCargada && !pedidoMasTarde && pedidoDiaHora) {
//	    	  log("Estamos con pedido por día y hora");
//	    	  orderType =  new TipoPedido(3); 
//	    	  orderType.abrirPedido();
//
//	      }
	      
	      if((iTipoPedido == 2 || iTipoPedido == 3) && !orderType.isFindedBtn()) {
	    	  //Cerrar la sesión del usuario si ya está abierta
	    	  cerrarSesion();
	      }
	      
	      this.pedido.setTipoPedido(orderType);
    	  //empezamos a guardar el pedido para completarlo. Vamos utilizar el objecto pedido para guardar pedido y hacer validacion del pedido
    	  Data.getInstance().setPedido(pedido);
    	  
	  }
	
}
