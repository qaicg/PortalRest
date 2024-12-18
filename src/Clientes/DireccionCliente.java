package Clientes;

import java.sql.ResultSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.TestBase;

public class DireccionCliente extends TestBase {
	String address = null;
	String number = null;
	String oldDirection = null;
	
	@Test(description="Crea una dirección en una venta delivery", groups = {"createDireccion"}, priority=3)
    @Parameters({"direccion", "numero", "bloque", "escalera", "planta", "puerta","ciudad","cp", "pais", "observaciones", "shop", "miPerfilString", "misDireccionesString"})
	public void createAddress(@Optional("Avinguda Europa")String direccion, @Optional("45")String numero, @Optional("2")String bloque, @Optional("3")String escalera, @Optional("5")String planta, 
			@Optional("7")String puerta, @Optional("Lleida")String ciudad, @Optional("25004")String cp, @Optional("")String pais, 
			@Optional("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...")String observaciones, 
			@Optional("")String shop, @Optional("") String perfile, @Optional("") String misDirecciones) {

		log("Registramos nueva dirección");
		oldDirection = direccion;
		setAddressAndNumber(direccion, numero);

		//Antes de crear cualquer direccion
    	//Verificar que la direccion no existe en la BD
    	//Eliminar la si existe en la BD.
    	deleteAddressBD(address, number, shop);
		
		espera(500);
		
    	w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'user-address-add')]"))).click();
    	espera(500);
    	w.until (ExpectedConditions.presenceOfElementLocated(By.tagName("form")));	
    	espera(500);
    	
    	//Ingresar la nueva direccion
    	writeValueInputAddress(address, number, ciudad, cp, bloque, escalera, planta, puerta, observaciones, "write");
    	
    	espera(500);
    	driver.findElement(By.xpath("//app-square-progress-button[@class='square-progress-button']")).click();
    	espera(500);
    	w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='btn-confirm']"))).click();
    	espera(500);
    	
  
    	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
    		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
    	}
    	
    	espera(500);
    	driver.navigate().back();
    	espera(500);
    	driver.navigate().back();
    	espera(500);
    	abrirMisDirecciones(perfile, misDirecciones);
    	espera(500);
  	
    	// Validar el ingreso de la direccion en el listado de mis direcciones
    	//abrirMisDirecciones(perfile, misDirecciones);
    	if(!isAddressValidated(direccion)) {
    		log("Error al ingresar la nueva dirección: " + direccion);
    		Assert.assertTrue(false);
    	}
    }

	@Test(description="Modificar una dirección", groups = {"modifyDireccion"},priority=9)
	@Parameters({"direccion", "numero", "bloque", "escalera", "planta", "puerta","ciudad","cp", "pais", "observaciones", "shop", "miPerfilString", "misDireccionesString", "NewDireccion"})
	public void modifyAddress(@Optional("Avinguda Balmes")String direccion, @Optional("10")String numero, @Optional("3")String bloque, @Optional("12")String escalera, @Optional("9")String planta, 
			@Optional("1")String puerta, @Optional("Lleida")String ciudad, @Optional("25001")String cp, @Optional("")String pais, 
			@Optional("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...")String observaciones, 
			@Optional("")String shop, @Optional("") String perfile, @Optional("") String misDirecciones, @Optional("") String NewDireccion) {
		
		espera(1000);
    	if(!isAddressValidated(direccion)) {
    		log("Error la dirección: " + direccion +" no existe para ser modificada");
    		Assert.assertTrue(false);
    	}
		espera(1000);
		
		if(isNullOrEmpty(oldDirection)) {
			oldDirection = direccion;
		}
		
		selectAddress("modify", oldDirection);
		espera(500);
		//Modificar la nueva direccion
		if(!isNullOrEmpty(NewDireccion)) {
			direccion = NewDireccion ;
		}
    	writeValueInputAddress(direccion, numero, ciudad, cp, bloque, escalera, planta, puerta, observaciones, "");
    	
    	espera(500);
    	driver.findElement(By.xpath("//app-square-progress-button[@class='square-progress-button']")).click();
    	espera(500);
    	w.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='btn-confirm']"))).click();
    	espera(500);
  
    	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
    		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
    	}
 
    	espera(500);
    	driver.navigate().back();
    	espera(500);
    	driver.navigate().back();
    	espera(500);
    	abrirMisDirecciones(perfile, misDirecciones);
    	espera(500);
    	
    	// Validar el ingreso de la direccion en el listado de mis direcciones
    	//abrirMisDirecciones(perfile, misDirecciones);
    	if(!isAddressValidated(direccion)) {
    		log("Error al modificar la dirección: " + direccion);
    		Assert.assertTrue(false);
    	}
		
	}	

    @Test(description="Borra una dirección", groups = {"deleteDireccion"},priority=6)
    @Parameters({"direccion", "numero","miPerfilString","misDireccionesString","email","shop"})
  	public void deleteAdress(@Optional("")String direccion, @Optional("")String numero, @Optional("")String miPefil, @Optional("")String misDireccoines, @Optional("")String email, @Optional("")String shop) {
    	espera(1500);
    	
		espera(1000);
    	if(!isAddressValidated(direccion)) {
    		log("Error la dirección: " + direccion +" no existe para supprimir en el listado.");
    		Assert.assertTrue(false);
    	}
		espera(1000);
    	
    	selectAddress("delete", direccion); //Supprimir la direccion
		espera(500);
		    	
    	atras();
    	espera(500);
    	atras();
    	espera(500);
    	abrirMisDirecciones(miPefil, misDireccoines);
    	espera(500);

    	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
    		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
    	}
	    
    	if(isAddressValidated(direccion)) {
    		log("Error. Sigo viendo la dirección despues de borrarla");
    		Assert.assertTrue(false);    		
    	}
    	
    	log("Se confirma visualmente que la dirección "+direccion+" ya no aparece en el listado");
    	
    	setAddressAndNumber(direccion, numero);
    	
    	if(isAddressValidatedDB(address, number, email, shop)) {
    		log("Error. La dirección se encuentra activa en BBDD");
			Assert.assertTrue(false);
    	}
    	log("Direccion ya no se encuentra activa en BBDD");	

	}
	
    /*
     * Abrir el apartado Mis direcciones
     */
	@Test(description="Consultar las direcciones del usuario", groups = {"showAddress"}, priority=1)
	@Parameters({"miPerfilString", "misDireccionesString"})
	public void openMyAddresses(@Optional("") String perfile, @Optional("") String misDirecciones) {//Consultar Direcciones	
		abrirMisDirecciones(perfile, misDirecciones);
	}
	
	/*
	 * Pulsar botones: Modificar o supprimir
	 */
	private void selectAddress(String action, String address) {
		By by = null;
    	if(isElementPresent(By.xpath("//div[contains(@class,'user-address-add')]"))) {
    		//atras();    		
    		//espera(2000);
    		
        	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
        		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
        	}
        	
    		List<WebElement> listaDirecciones = driver.findElements(By.xpath("//div[contains(@class,'user-address-container')]"));
    		
    		if(action.equalsIgnoreCase("delete")) {
    			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//img[contains(@class,'user-address-delete-icon')]")));  			
    			by = By.xpath(".//img[contains(@class,'user-address-delete-icon')]");
    		} else if (action.equalsIgnoreCase("modify")) {
    			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//img[contains(@class,'user-address-edit-icon')]")));
    			by = By.xpath(".//img[contains(@class,'user-address-edit-icon')]");
    		}
    		
    		for(int i = 0; i < listaDirecciones.size(); i++) {
        		String addressLine = listaDirecciones.get(i).findElements(By.xpath(".//div[contains(@class,'user-address-line')]")).get(0).getAttribute("innerText");
        		if(addressLine.equalsIgnoreCase(address) && isElementPresent(by)) {
        			clicJS(listaDirecciones.get(i).findElement(by)); //SELECCIONO BORRAR DIRECCION
        			espera(500);
        			
        			if(action.equalsIgnoreCase("delete")) {
	        			clicJS(driver.findElement(By.xpath("//div[@class='msg-dialog-buttons']//button[2]"))); //CONFIRMO QUE QUIERO BORRAR
	        			log("Dirección "+addressLine+" borrada");
	        			espera(500);
        			}
        			break;
        		}
        	}		
    	}
	}
	
	/*
	 *  Validar la direccion ya existe en la BD
	 */
	private boolean isAddressValidatedDB(String address, String number, String email, String shop) {
		boolean valueReturn = false;
    	String SQL = "select ca.RoadName,ca.RoadNumber  from Con__Address ca, Con__ContactAddress cca, Con__Contact cc "
    			+ "where ca.AddressId = cca.AddressId and cca.ContactId = cc.ContactId and cc.Email ='" + email + "'"
    			+ "and ca.isDiscontinued = 0 and ca.RoadName ='" + address + "' and ca.RoadNumber ='" + number + "'";
    	
    	ResultSet rs =  databaseConnection.ejecutarSQL(SQL,"DB" + shop); 
    	
    	if (rs!=null) {
     		 try {		
     			if (rs.first()) {
     				log("La dirección se encuentra activa en BBDD");
     				valueReturn = true;
     			}else {
     				log("Direccion ya no se encuentra activa en BBDD");	
     				log(SQL);
     				//return false;
     			}
     			
     		} catch (Exception e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}finally {
     			databaseConnection.desconectar();
     		}
     		 //Assert.assertTrue(false);
	    }
    	
    	return valueReturn;
	}
	
	/*
	 * Validar la direccion ya existe en la pantalla
	 */
    private boolean isAddressValidated(String address)  {
		By by = null;
		espera(100);
		w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'user-address-add')]")));
		
    	if(isElementPresent(By.xpath("//div[contains(@class,'user-address-add')]"))) {
    		
        	if (!isElementPresent(By.xpath("//div[contains(@class,'user-address-container')]"))) {   		
        		w2.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-simple-address-list")));
        	}
        	
    		List<WebElement> listaDirecciones = driver.findElements(By.xpath("//div[contains(@class,'user-address-container')]"));
    		
    		for(int i = 0; i < listaDirecciones.size(); i++) {
        		String addressLine = listaDirecciones.get(i).findElements(By.xpath(".//div[contains(@class,'user-address-line')]")).get(0).getAttribute("innerText");
        		if(addressLine.equalsIgnoreCase(address)) {
        			log("La direccion "+ address +" existe !!! ");
        			return true;
        		}
        	}
    		
			log("La direccion '"+ address +"' no existe en el listado de mis direcciones !!! ");
			return false;
    	}
    	
    	log("La direccion '"+ address +"' no existe en el listado de mis direcciones !!! ");
		log("La pantalla direccion no existe para buscar la direccion: " + address);
    	return false;
    	
    }	
	
	//Supprimir direccion en la BD
	private void deleteAddressBD(String address, String number, String shop) {
		String SQL="update  Con__Address set isDiscontinued=1 where RoadName ='"+ address +"' and RoadNumber ='"+number+"'";
		
		if (databaseConnection.ejecutaUpdate(SQL, "DB"+shop)==-1) {
			log("Error borrando direccion de pruebas");
			Assert.assertTrue(false);
		} else
			log("Limpiamos posibles direcciones de prueba antes de empezar.");
		
	}
	
	/*
	 * Para ingresar o modificar una direccion
	 * action: write -> para ingresar
	 * action: modificar -> para modificar
	 */
	private void writeValueInputAddress(@Optional("") String direccion, @Optional("") String numero,@Optional("") String ciudad, @Optional("") String cp, 
			@Optional("") String bloque, @Optional("") String escalera, @Optional("") String planta, @Optional("") String puerta, @Optional("") String observaciones, String action) {
		String errorString ;
		
		setAddressAndNumber(direccion, numero);
		
		if(action.equalsIgnoreCase("write"))
			errorString = "ingressar";
		else 
			errorString = "modificar";
		
    	if(!setInputValueJS(By.xpath("//textArea[@formControlName='roadName']"), address)) {
    		log("No se ha podido'"+ errorString + "' el nombre de la dirección.");
    		Assert.assertTrue(false);
    	}
    	
    	if(!setInputValueJS(By.xpath("//input[@formControlName='roadNumber']"), number)) {
    		log("No se ha podido'"+ errorString + "' el numero de la calle.");
    		Assert.assertTrue(false);
    	}
    	
    	if(!setInputValueJS(By.xpath("//input[@formControlName='city']"), ciudad)) {
    		log("No se ha podido'"+ errorString + "' el nombre de la ciudad.");
    		Assert.assertTrue(false);
    	}
    	
    	if(!setInputValueJS(By.xpath("//input[@formControlName='postalCode']"), cp)) {
    		log("No se ha podido'"+ errorString + "' el codígo postal.");
    		Assert.assertTrue(false);
    		
    	if(!setInputValueJS(By.xpath("//input[@formControlName='block']"), bloque))
    		log("No se ha podido'"+ errorString + "' el bloque.");
    		Assert.assertTrue(false);
    	}
    	
    	if(!setInputValueJS(By.xpath("//input[@formControlName='stairCase']"), escalera)) {
    		log("No se ha podido'"+ errorString + "' la escalera.");
    		Assert.assertTrue(false);
    	}
    	
    	if(!setInputValueJS(By.xpath("//input[@formControlName='floor']"), planta)) {
    		log("No se ha podido'"+ errorString + "' el nombre de la planta.");
    		Assert.assertTrue(false);
    	}
    	
    	if(!setInputValueJS(By.xpath("//input[@formControlName='door']"), puerta)) {
    		log("No se ha podido'"+ errorString + "' el nombre de la puerta.");
    		Assert.assertTrue(false);
    	}
    	
    	if(!setInputValueJS(By.xpath("//textArea[@formControlName='observations']"), observaciones)) {
    		log("No se ha podido'"+ errorString + "' las observaciones.");
    		Assert.assertTrue(false);
		}
	}
	
	/*
	 * Setear las propriedades direccion y numero
	 */
	private void setAddressAndNumber(String direccion, String numero) {
    	address = direccion;
    	
    	String[] direccionArray = direccion.split(", ");
    	
    	number = numero;
    	
    	if(direccionArray.length >= 1) {
    		address = direccionArray[0];    		
    		if(direccionArray.length > 1 && !isNullOrEmpty(direccionArray[1])) number =direccionArray[1];
    	}

	}
}
