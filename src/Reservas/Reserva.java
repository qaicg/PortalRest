package Reservas;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import graphql.Assert;
import utils.TestBase;

//@Test
//@Parameters({"titleHeader", "resultatEsperado"})
public class Reserva extends TestBase {
	String diaReserva, hora, sala, localizador, observaciones, telefono, email = null;
	 String dummyUserName;
	 String dummyTelefono;
	 String dummyPostalCode;
	 String dummyEmail;
	 String dummyPassword;
	 boolean isValidatedInformationPersonalUser = true;
	 public boolean isCreatedBooking = false;
	 WebElement inputName, inputPhone, inputEmail, checkboxCondiciones, buttonCrear;
	
	@Test
	@Parameters({"titleHeader", "resultatEsperado"})
	public void realizarReserva(@Optional("") String titleHeader, @Optional("") boolean resultatEsperado) {
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'title-header')]")));
		//
		if(!driver.findElement(By.xpath("//div[contains(@class, 'title-header')]")).getText().equalsIgnoreCase(titleHeader)) {
			log("Error: no se ha encuentrado el titulo: " + titleHeader +"en pantalla Reserva");
			Assert.assertTrue(false);
		}
		
		String stringButton = "//button[contains(@class, 'button primary-color')]";
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringButton)));
		
		String sDisabled = driver.findElement(By.xpath(stringButton)).getAttribute("disabled");
		
		// Verificar que podemos seguir con la reserva
		if(sDisabled == "null") { 
			log("Error: hay algo que falla en el formulario");
			log("El botón siguiente está desabilitado hasta que el formulario sea valido");
			
			if(resultatEsperado) {
				Assert.assertTrue(false);
			} else {
				Assert.assertTrue(true);
			}
		} else if (sDisabled != "null" && resultatEsperado) {
			clicJS(driver.findElement(By.xpath(stringButton)));
			espera(2000);
		}
		
	}
	
	
	/*
	 * Rellenar datos de la reserva
	 * Prefijo
	 * Telefoneo
	 * Nombre
	 * Email
	 * Localizador
	 * Observaciones
	 * Aceptar condiciones y terminos
	 * Apceptar la politica de cancelacion
	 * Aceptar recibir comunicaciones comerciales y ofertas
	 */
	public void inputDatosBooking() {
		
	}
	
	public void abrirUrlReserva() {
		
	}
	
	public void verificarReservar () {
		
	}
	
	public void verificarCookiesReserva () {
		
	}
	
	@Test
	@Parameters({"dia"})
	public void selectedDay(@Optional("") String day) {
		// la fecha de la venta 
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String fechaHoy = dateFormat.format(date);
		espera();
		
		String stringDay;
		String stringValidateSelectedDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator mat-calendar-body-selected')]";
		
		if(isNullOrEmpty(day)) {
			//stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator mat-calendar-body-selected mat-calendar-body-today')]";
			day = fechaHoy.split("/")[0];
			stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator') and contains(text(), '"+ fechaHoy.split("/")[0] +"')]";
			
		} else {
			
			if(day.split("/").length > 0) {
				stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator') and contains(text(), '"+ day.split("/")[0] +"')]";
			} else {
				stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator') and contains(text(), '"+ day +"')]";
			}
		}
		
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringDay)));
		
		WebElement selectedDay = driver.findElement(By.xpath(stringDay));
		
		
		clicJS(selectedDay); //Seleccion de dia
		
		//Validar el dia seleccionado
		//("//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator mat-calendar-body-selected')]")
		if(isElementPresent(By.xpath(stringValidateSelectedDay))) {
			 
			if(!driver.findElement(By.xpath(stringValidateSelectedDay)).getText().equalsIgnoreCase(day)) {
				log("No se ha podido seleccionar el dia: "+ day);
				
				Assert.assertTrue(false);
			} else {
				log("Se ha seleccionado el dia: " + day);
				diaReserva = day;
			}
			
		}
	}
	
	@Test
	@Parameters({"room", "existeRoom", "stringLabelRoom", "resultatEsperado"})
	public void selectRoom(@Optional("") String room, @Optional("") String existeRoom, @Optional("") boolean stringLabelRoom, @Optional("") boolean resultatEsperado) {

		String stringElementSelectRoom = "//mat-select//child::div[contains(@class, 'mat-select-arrow-wrapper')]//child::div[contains(@class, 'mat-select-arrow')]";
		String stringRoom = "//mat-option[contains(@class, 'mat-option')]//child::span[contains(@class, 'mat-option-text')]";
		
		//Verificar que hay horas como parametro
		if(!isNullOrEmpty(room)) { 
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringElementSelectRoom)));
			int numberElementSelect = driver.findElements(By.xpath(stringElementSelectRoom)).size();
			
			if(numberElementSelect == 2) { // existed Room and Hours
				clicJS(driver.findElements(By.xpath(stringElementSelectRoom)).get(0));
			} else if(numberElementSelect == 1) { // just Hours
				clicJS(driver.findElement(By.xpath(stringElementSelectRoom)));
			}
			
			espera(500);
			List<WebElement> salas = new ArrayList<WebElement>();
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringRoom)));
			
			salas = driver.findElements(By.xpath(stringRoom));
			
			if(salas.size() > 0) {
				//buscar i seleccionar la hora definida como parametro
				boolean isSelectedRoom = false;
				for(int i = 0; i < salas.size(); i++) {
					log("Sala por " + i + " " + salas.get(i).getText());
					if(salas.get(i).getText().equalsIgnoreCase(room)) {
						clicJS(salas.get(i));
						log("la sala("+ room +") está seleccionada desde el listado ");
						isSelectedRoom = true;
						break;
					}
				}
				
				if(!isSelectedRoom) {
					log("No se ha podido seleccionar la sala("+ room +") en el listado");
					Assert.assertTrue(false);
				}
				
			} else {
				log("No hay sala en listado salas");
				Assert.assertTrue(false);
			}
						
		} else {
			log("El parametro sala es vacio");
			if(resultatEsperado) {
				Assert.assertTrue(false);
			}
			Assert.assertTrue(true);
		}
	}
	
	@Test
	@Parameters({"hora", "resultatEsperado", "existeRoom"})
	public void selectHour(@Optional("") String hour, @Optional("") boolean resultatEsperado, @Optional("") boolean existeRoom) {
		String stringElementSelectHour = "//mat-select//child::div[contains(@class, 'mat-select-arrow-wrapper')]//child::div[contains(@class, 'mat-select-arrow')]";
		String stringHours = "//mat-option[contains(@class, 'mat-option')]//child::span[contains(@class, 'mat-option-text')]";
		
		//Verificar que hay horas como parametro
		if(!isNullOrEmpty(hour)) { 
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringElementSelectHour)));
			int numberElementSelect = driver.findElements(By.xpath(stringElementSelectHour)).size();
			if(numberElementSelect == 2) { // existed Room and Hours
				clicJS(driver.findElements(By.xpath(stringElementSelectHour)).get(1));
			} else if(numberElementSelect == 1) { // just Hours
				clicJS(driver.findElement(By.xpath(stringElementSelectHour)));
			}
			
			espera(500);
			List<WebElement> horas = new ArrayList<WebElement>();
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringHours)));
			
			horas = driver.findElements(By.xpath(stringHours));
			
			if(horas.size() > 0) {
				//buscar i seleccionar la hora definida como parametro
				boolean isSelectedHour = false;
				for(int i = 0; i < horas.size(); i++) {
					log("hora por " + i + " " + horas.get(i).getText());
					if(horas.get(i).getText().equalsIgnoreCase(hour)) {
						clicJS(horas.get(i));
						log("la hora("+ hour +") está seleccionada desde el listado ");
						isSelectedHour = true;
						break;
					}
				}
				
				if(!isSelectedHour) {
					log("No se ha podido seleccionar la hora("+ hour +") en el listado");
					Assert.assertTrue(false);
				}
				
			} else {
				log("No hay hora en listado horas");
				Assert.assertTrue(false);
			}
						
		} else {
			log("El parametro hora es vacio");
			if(resultatEsperado) {
				Assert.assertTrue(false);
			}
			Assert.assertTrue(true);
		}
	}
	
	@Test
	@Parameters({"numeroCliente", "testPlusMenosBtn", "resultatEsperado"})
	public void inputNumberPerson(@Optional("") String numberCliente, @Optional("") boolean resultatEsperado, @Optional("") boolean testPlusMenosBtn) {
		String inputElmt = "//button[contains(text(), '-')]//following::button[contains(text(), '+')]//preceding::input";
		String btnPlus = "//button[contains(text(), '-')]//following::button[contains(text(), '+')]";
		String valueInput;
		boolean continueClicPlus = true;
		
		String valueInputTest = numberCliente;
		//Test btn plus
		if(Integer.parseInt(valueInputTest) > 1) {
			for(int i = 1; i <= Integer.parseInt(valueInputTest); i++) {
				clicJS(driver.findElement(By.xpath(btnPlus)));
				espera(200);
				valueInput = driver.findElement(By.xpath(inputElmt)).getAttribute("value");
				
				if(valueInput.equalsIgnoreCase(valueInputTest) ) {
					continueClicPlus = false;
					log("Se ha podido introducir el numero cliente vía el boton añadir ");
					Assert.assertTrue(true);
					break;
				}
				
				if( i == Integer.parseInt(numberCliente) && continueClicPlus) {
					log("Error: no se ha podido introducir el numero cliente vía el boton añadir ");
					Assert.assertTrue(false);
				}
			}
		} else {
			if(Integer.parseInt(valueInputTest) != 1) {
				driver.findElement(By.xpath(inputElmt)).clear();
				espera(100);
				driver.findElement(By.xpath(inputElmt)).sendKeys(numberCliente);
			}
		}
	}
	
	@Test
	@Parameters({"numeroCliente", "testPlusMenosBtn", "resultatEsperado"})
	public void verificarBotonesPlusMenos(@Optional("") String numberCliente, @Optional("") boolean resultatEsperado, @Optional("") boolean testPlusMenosBtn) {
		String inputElmt = "//button[contains(text(), '-')]//following::button[contains(text(), '+')]//preceding::input";
		String btnPlus = "//button[contains(text(), '-')]//following::button[contains(text(), '+')]";
		String btnMenos = "//button[contains(text(), '+')]//preceding::button[contains(text(), '-')]";
		
		if(testPlusMenosBtn) {
			boolean continueClicPlus = true;
			boolean continueClicMenus = true;
			String valueInput;
			
			String valueInputTest = Integer.parseInt(numberCliente) <= 5 ? numberCliente : "5";
			//Test btn plus
			for(int i = 1; i <= Integer.parseInt(valueInputTest); i++) {
				clicJS(driver.findElement(By.xpath(btnPlus)));
				espera(200);
				valueInput = driver.findElement(By.xpath(inputElmt)).getAttribute("value");
				
				if(valueInput.equalsIgnoreCase(valueInputTest) ) {
					continueClicPlus = false;
					log("Se ha podido introducir el numero cliente vía el boton añadir ");
					Assert.assertTrue(true);
					break;
				}
				
				if( i == Integer.parseInt(numberCliente) && continueClicPlus) {
					log("Error: no se ha podido introducir el numero cliente vía el boton añadir ");
					Assert.assertTrue(false);
				}
			}
			
			//Test btn menus
			for(int i = 1; i <= Integer.parseInt(valueInputTest); i++) {
				clicJS(driver.findElement(By.xpath(btnMenos)));
				espera(200);
				valueInput = driver.findElement(By.xpath(inputElmt)).getAttribute("value");
				
				if(valueInput.equalsIgnoreCase("1")) {
					continueClicMenus = false;
					log("Se ha podido testear el boton menos ");
					Assert.assertTrue(true);
					break;
				}
				
				if( i == Integer.parseInt(numberCliente) && continueClicMenus) {
					log("Error: no se ha podido testear el boton menos numero");
					Assert.assertTrue(false);
				}
			}
		}
	}
	
	
	public void createInputElement() {
		//$x("//div[contains(@class, 'user-data-wrapper')]//child::input");
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'user-data-wrapper')]//child::input")));
		
		
		List<WebElement> inputElements = driver.findElements(By.xpath("//div[contains(@class, 'user-data-wrapper')]//child::input"));
		
		for(int i=0; i <= inputElements.size(); i++) {
			if(isElementPresent(null)) {
				
			}
			
		}
		
		if(inputElements.size() >= 6) {
			//Verificar si el campos Localizado esta visibles en el formulario de la Reserva
			String labelElementLocalizado = "//mat-label[contains(text(), 'Localizador')]";
			if(isElementPresent(By.xpath(labelElementLocalizado))) {
				
			}
			
			
			//Verificar si el campos Observaciones esta visibles en el formulario de la Reserva
			String labelElementObservaciones = "//mat-label[contains(text(), 'Observación')]";
			if(isElementPresent(By.xpath(labelElementObservaciones))) {
				
			}
		}
		inputName = inputElements.get(0);
		inputPhone = inputElements.get(1);
		//inputPostalCode = inputElements.get(2);
		inputEmail = inputElements.get(3);
		//inputPassword = inputElements.get(4);
		//inputRepeatPassword = inputElements.get(5);
	}
}
