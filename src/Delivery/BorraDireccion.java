package Delivery;

import java.sql.ResultSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import graphql.Assert;
import utils.TestBase;

public class BorraDireccion  extends TestBase{
	  @Test(description="Borra una dirección delivery", groups = {"borraDireccion"})
	    @Parameters({"direccion","miPerfilString","misDireccionesString","email","shop"})
	  public void borrarDireccion(String direccion, String miPefil, String misDireccoines, String email, String shop) {
	    	espera(1500);
	    	if(isElementPresent(By.xpath("//div[contains(@class,'user-address-add')]"))) {
	    		atras();
	    		abrirMisDirecciones(miPefil,misDireccoines);	    		
	    		espera(2000);
	        	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
	        		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
	        	}
	        	
	        	
	    		List<WebElement> listaDirecciones = driver.findElements(By.xpath("//div[contains(@class,'user-address-container')]"));
	    		
	    		for(int i=0;i<listaDirecciones.size();i++) {
	        		String addressLine = listaDirecciones.get(i).findElements(By.xpath(".//div[contains(@class,'user-address-line')]")).get(0).getAttribute("innerText");
	        		if(addressLine.equalsIgnoreCase(direccion)) {
	        			clicJS(listaDirecciones.get(i).findElement(By.xpath(".//img[contains(@class,'user-address-delete-icon')]"))); //SELECCIONO BORRAR DIRECCION
	        			espera(500);
	        			clicJS(driver.findElement(By.xpath("//div[@class='msg-dialog-buttons']//button[2]"))); //CONFIRMO QUE QUIERO BORRAR
	        			log("Dirección "+addressLine+" borrada");
	        			espera(500);
	        			break;
	        		}
	        	}		
	    	}
	    	atras();
	    	espera(500);
	    	atras();
	    	espera(500);
	    	abrirMisDirecciones(miPefil,misDireccoines);
	    	espera(500);

	    	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
	    		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
	    	}
	    	
	    	List<WebElement> listaDirecciones = driver.findElements(By.xpath("//div[contains(@class,'user-address-container')]"));
	    	
	    	for(int i=0;i<listaDirecciones.size();i++) {
	    		String addressLine = listaDirecciones.get(i).findElements(By.xpath(".//div[contains(@class,'user-address-line')]")).get(0).getAttribute("innerText");
	    		if(addressLine.equalsIgnoreCase(direccion)) {
	    			log("Error. Sigo viendo la dirección despues de borrarla");
	    			Assert.assertTrue(false);
	    		}
	    	}
	    	
	    	log("Se confirma visualmente que la dirección "+direccion+" ya no aparece en el listado");
	    	
	    	String address=direccion.split(", ")[0];
	    	String number=direccion.split(", ")[1];

	    	String SQL = "select ca.RoadName,ca.RoadNumber  from Con__Address ca, Con__ContactAddress cca, Con__Contact cc "
	    			+ "where ca.AddressId = cca.AddressId and cca.ContactId = cc.ContactId and cc.Email ='"+email+"'"
	    			+ "and ca.isDiscontinued = 0 and ca.RoadName ='"+address+"' and ca.RoadNumber ='"+number+"'";
	    	
	    	ResultSet rs =  databaseConnection.ejecutarSQL(SQL,"DB"+shop); 
	    	
	     	 if (rs!=null) {
	     		 try {		
	     			if (rs.first()) {
	     				log("Error. La dirección se encuentra activa en BBDD");
	     				log(SQL);
	     				Assert.assertTrue(false);
	     			} else {
	     				log("Direccion ya no se encuentra activa en BBDD");	
	     			}
	     			
	     		} catch (Exception e) {
	     			// TODO Auto-generated catch block
	     			e.printStackTrace();
	     		}finally {
	     			databaseConnection.desconectar();
	     		}
	    	    	
	    	Assert.assertTrue(true);
	    	
	    }
	}
}
