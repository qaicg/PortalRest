package AbrePortalRest;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.TestBase;

public class AbrirUrl extends TestBase {
	
	@Test
	 @Parameters({ "Url"})
	  public void abrirURL(String Url) {
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
	            break;
	         }else {
	        	 System.out.println("Página no cargada todavia");
	         }
	      }
	  }
}
