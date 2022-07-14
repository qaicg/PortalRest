package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class AbrirUrl extends TestBase {
	
	 @Test
	 @Parameters({ "Url"})
	  public void abrirURL(String Url) {
		
		  driver.get(Url);
		// JavaScript Executor to check ready state
	      JavascriptExecutor j = (JavascriptExecutor)driver;
	      if (j.executeScript("return document.readyState").toString().equals("complete")){
	         System.out.println("Page has loaded");
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
	        	 Reporter.log("Página Cargada - Title: " + driver.getTitle());
	            break;
	         }
	      }
	  }
}
