package Reservas;

import java.io.IOException;
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
import utils.Data;
import utils.TestBase;
import utils.getDummyData;

//@Test
//@Parameters({"titleHeader", "resultatEsperado"})
public class Reserva extends TestBase {
	String diaReserva, hora, sala, localizador, observaciones, prefijo, telefono, email = null;
	String dummyLocalizador, dummyName, dummyTelefono, dummyEmail, dummyObservaciones = null; 
	
	public boolean isCreatedBooking = false;
	public WebElement inputLocalizador, inputName, inputPhone, inputEmail, inputObservaciones, 
				checkboxCondicionesTerminos, checkboxPoliticaCancelacion, checkboxComunicacionComerciales,
				buttonSiguiente, buttonAtras = null;
	
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
	
	/**
	 * 
	 * @param resultadoEsperado
	 * @param localizador
	 * @param prefijo
	 * @param telefono
	 * @param nombreCliente
	 * @param emailCliente
	 * @param observaciones
	 * @param aceptoTerminos
	 * @param aceptoPoliticaCancelacion
	 * @param aceptoComunicacionesComerciales
	 */
	@Test
	@Parameters({"resultatEsperado", "localizador", "prefijo", "telefono", "nombreCliente", "emailCliente", "observaciones", 
				"aceptarTerminosCondiciones" , "aceptarPoliticaCancelacion", "aceptarComunicacionesComerciales", "titleHeader", "literalLocalizador", "literalObserciones"})
	public void createBooking(@Optional ("true") boolean resultadoEsperado, @Optional("") String localizador, @Optional("") String prefijo, @Optional("") String telefono, 
			@Optional("") String nombreCliente, @Optional("") String emailCliente, @Optional("") String observaciones, 	
			@Optional ("true") boolean aceptoTerminos, @Optional ("true") boolean aceptoPoliticaCancelacion, @Optional ("true") boolean aceptoComunicacionesComerciales, 
			@Optional("") String titleHeader, @Optional() String literalLocalizador, @Optional("") String literalObserciones) {
		
		//Validar el title de la pagina
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'title-header')]")));
		
		String tituloPaginaReserva = driver.findElement(By.xpath("//div[contains(@class, 'title-header')]")).getText();
		
		if(!tituloPaginaReserva.equalsIgnoreCase(titleHeader)) {
			log("El titulo de la pagina es: " + tituloPaginaReserva + " y esparamos que sea: " + titleHeader);
			Assert.assertTrue(false);
		}
		
		//espera();
		createInputElement(literalLocalizador, literalObserciones);
		//espera(2000);
		
		//Generar datos basicos del cliente
		setDummyDataInputsBooking(literalLocalizador, literalObserciones);
		//espera();
		
		//Insertar datos en el formulario de la reserva
		insertDataInputBooking(resultadoEsperado, localizador, prefijo, telefono, nombreCliente, emailCliente, observaciones);
		espera();
		
		//Aceptar codiciones
		checkConditionsBooking(aceptoTerminos, aceptoPoliticaCancelacion, aceptoComunicacionesComerciales, resultadoEsperado);
		
		// Verificar que no hay error que nos impide siguir el envio de la reserva
		
		
		//Validar la reserva en Booking
		
		//Validar la reserva en BD.
	}
	
	@Parameters({"resultatEsperado", "localizador", "prefijo", "telefono", "nombreCliente", "emailCliente", "observaciones"})
	public void insertDataInputBooking(@Optional ("true") boolean resultadoEsperado, @Optional("") String localizador, @Optional("") String prefijo, @Optional("") String telefono, 
			@Optional("") String nombreCliente, @Optional("") String emailCliente, @Optional("") String observaciones) {
		//Localizador
		if(inputLocalizador.getSize() != null)
			if(!isNullOrEmpty(localizador)) 
				enviarTexto(inputLocalizador, localizador);
			else
				enviarTexto(inputLocalizador, dummyLocalizador);
		
		//Prefijo
		if(!isNullOrEmpty(prefijo))
			selectPrefijoTelefono(prefijo, resultadoEsperado);
		else 
			selectPrefijoTelefono("Spain (+34)", resultadoEsperado);
		
		//Telefono
		if(!isNullOrEmpty(telefono))
			enviarTexto(inputPhone, telefono);
		else
			enviarTexto(inputPhone, dummyTelefono);		
		
		//Nombre
		if(!isNullOrEmpty(nombreCliente))
			enviarTexto(inputName, nombreCliente);
		else
			enviarTexto(inputName, dummyName);
		
		//Email
		if(!isNullOrEmpty(email))
			enviarTexto(inputEmail, email);
		else
			enviarTexto(inputEmail, dummyEmail);
		
		//Observaciones
		if(inputObservaciones.getSize() != null)
			if(!isNullOrEmpty(observaciones))
				enviarTexto(inputObservaciones, observaciones);
			else
				enviarTexto(inputObservaciones, dummyObservaciones);
			
	}
	
	
	
	/*
	 * Generar datos Reserva
	 * Localizador
	 * Telefoneo
	 * Nombre
	 * Email
	 * Observaciones
	 */
	public void setDummyDataInputsBooking(@Optional("") String literalLocalizador, @Optional("") String literalObserciones) {
		String labelElementLocalizado = "//mat-label[contains(text(), '" + literalLocalizador + "')]";
		if(getDummyData.getLimite()) {
			List<String> dummyInformation = getDummyData.getDummyInformation();
		  	dummyName = dummyInformation.get(0);
		  	dummyTelefono = dummyInformation.get(4);
		  	dummyEmail = stripAccents(dummyInformation.get(5));
		  	Data.getInstance().setNewUserMail(dummyEmail);//LO GUARDAMOS PARA PODER VALIDAR POSIBLES PEDIDOS
		} else {
			dummyName = getDummyData.getDummyUserName();
		  	dummyTelefono = getDummyData.getDummyTelefono();
		  	dummyEmail =stripAccents(dummyName) + "@yopmail.com";
		  	Data.getInstance().setNewUserMail(dummyEmail);//LO GUARDAMOS PARA PODER VALIDAR POSIBLES PEDIDOS
		}
		
		if(isElementPresent(By.xpath(labelElementLocalizado)) && isElementPresent(By.xpath("//span//mat-label[contains(text(), '" + literalLocalizador + "')]//preceding::input")))
			if(inputLocalizador.getSize() != null) {
				String generateCadenaAleatoria = generateCadenaAleatoria(11);
				dummyLocalizador = !isNullOrEmpty(generateCadenaAleatoria) ? generateCadenaAleatoria : "testLocalizador01p-47.";
			}
	
		String labelElementObservaciones = "//mat-label[contains(text(), '" + literalObserciones + "')]";
		if(isElementPresent(By.xpath(labelElementObservaciones)) && isElementPresent(By.xpath("//span//mat-label[contains(text(), '" + literalObserciones + "')]//preceding::textarea")))
			if(inputObservaciones.getSize() != null) {
				try {
					String generatePalabra = generatePalabra();
					dummyObservaciones = !isNullOrEmpty(generatePalabra) ? generatePalabra.substring(3, generatePalabra.toString().length() -3) : " Nullam vel viverra magna. Pellentesque at porta odio.";
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log("Error: no se ha podido generar datos para las Observaciones");
					e.printStackTrace();
					Assert.assertTrue(false);
				}
			}
	}
	
	public void abrirUrlReserva() {
		
	}
	
	public void verificarReservarBookink () {
		
	}
	
	public void verificarReservarBD () {
		
	}
	
	public void verificarCookiesReserva () {
		
	}
	
	@Test
	@Parameters({"aceptarTerminosCondiciones", "aceptarPoliticaCancelacion", "aceptarComunicacionesComerciales"})
	public void checkConditionsBooking(@Optional("") boolean aceptarCondicionesTerminos, @Optional("") boolean aceptarPoliticaCancelacion, @Optional("") boolean aceptarComunicacionComerciales,
				@Optional("") boolean resultatEsperado) {
		
		try {
			if(aceptarCondicionesTerminos && checkboxCondicionesTerminos.getSize() != null) clicJS(checkboxCondicionesTerminos);
			espera(100);
		} catch (Exception e) {
			// TODO: handle exception
			log("Se ha producido un error");
			Assert.assertTrue(false);
		}
		
		try {
			if(aceptarPoliticaCancelacion && checkboxPoliticaCancelacion.getSize() != null) clicJS(checkboxPoliticaCancelacion);
			espera(100);
		} catch (Exception e) {
			// TODO: handle exception
			log("Se ha producido un error");
			Assert.assertTrue(false);
		}
		
		try {
			if(aceptarComunicacionComerciales && checkboxComunicacionComerciales.getSize() != null) clicJS(checkboxComunicacionComerciales);
			espera(100);
		} catch (Exception e) {
			// TODO: handle exception
			log("Se ha producido un error");
			Assert.assertTrue(false);
		}
		
	}
	
	/*
	 * Selecionar un prefijo 
	 *
	 */
	@Test
	@Parameters({"prefijo", "resultatEsperado"})
	public void selectPrefijoTelefono(@Optional("") String prefijo, @Optional("") boolean resultatEsperado) {
		String stringElementPrefijo = "//div[contains(@class, 'mat-select-arrow-wrapper')]//child::div[contains(@class, 'mat-select-arrow')]";
		String stringListPrefijo = "//mat-option[contains(@class, 'mat-option')]//child::span[contains(@class, 'mat-option-text')]";
		if(!isNullOrEmpty(prefijo)) {
			w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringElementPrefijo)));
			
			clicJS(driver.findElement(By.xpath(stringElementPrefijo)));
			//espera(100);
			
			List<WebElement> prefijos = new ArrayList<WebElement>();			
			
			w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringListPrefijo)));

			prefijos = driver.findElements(By.xpath(stringListPrefijo));
			
			if(prefijos.size() > 0) {
				//buscar i seleccionar prefijo definido como parametro
				boolean isSelectedPrefijo = false;
				for(int i = 0; i < prefijos.size(); i++) {
					if(prefijos.get(i).getText().equals(prefijo)) {
						clicJS(prefijos.get(i));
						log("el prefijo("+ prefijo +") del telefono está seleccionada desde el listado ");
						isSelectedPrefijo = true;
						break;
					}
				}
				
				if(!isSelectedPrefijo) {
					log("No se ha podido seleccionar el prefijo ("+ prefijo +") en el listado");
					Assert.assertTrue(false);
				}
			}
			
		} else {
			log("El parametro Prefijo es vacio");
			if(resultatEsperado) {
				Assert.assertTrue(false);
			}
			Assert.assertTrue(true);
		}
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
	
	@Parameters({"literalLocalizador", "literalObserciones"})
	public void createInputElement(@Optional() String localizador, @Optional("") String observaciones) {
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'user-data-wrapper')]//child::input")));
		
		List<WebElement> inputElements = driver.findElements(By.xpath("//div[contains(@class, 'user-data-wrapper')]//child::input"));
		
		if(inputElements.size() == 7) {
			String labelElementLocalizado = "//mat-label[contains(text(), '" + localizador + "')]";
			if(isElementPresent(By.xpath(labelElementLocalizado)) && isElementPresent(By.xpath("//span//mat-label[contains(text(), '" + localizador + "')]//preceding::input"))) {
				inputLocalizador = inputElements.get(0);
			} else {
				log("Error: no se ha localizqdo el campo Localizador");
				Assert.assertTrue(false);
			}
			
			inputPhone = inputElements.get(1);
			inputName = inputElements.get(2);
			inputEmail = inputElements.get(3);
			checkboxCondicionesTerminos = inputElements.get(4);
			checkboxPoliticaCancelacion = inputElements.get(5);
			checkboxComunicacionComerciales = inputElements.get(6);
						
		} else if(inputElements.size() == 6) {
			inputPhone = inputElements.get(0);
			inputName = inputElements.get(1);
			inputEmail = inputElements.get(2);
			checkboxCondicionesTerminos = inputElements.get(3);
			checkboxPoliticaCancelacion = inputElements.get(4);
			checkboxComunicacionComerciales = inputElements.get(5);
		}
		
		String labelElementObservaciones = "//mat-label[contains(text(), '" + observaciones + "')]";
		if(isElementPresent(By.xpath(labelElementObservaciones)) && isElementPresent(By.xpath("//span//mat-label[contains(text(), '" + observaciones + "')]//preceding::textarea"))) {
			inputObservaciones = driver.findElement(By.xpath("//span//mat-label[contains(text(), '"+observaciones +"')]//preceding::textarea"));
			
		}
		
	}
}
