package Reservas;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.tools.DocumentationTool.Location;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
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
	String diaReserva, hora, sala, localizador, observaciones, prefijo, telefono, email, citaReserva, nombreCliente, emailCliente = null;
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
				"aceptarTerminosCondiciones" , "aceptarPoliticaCancelacion", "aceptarComunicacionesComerciales", "titleHeader", 
				"literalLocalizador", "literalObserciones", "insertBooking", "iconeNotification", "confirmacionReseva", "nombreRestaurante", 
				"direccionRestaurante", "telefonoRestaurante", "numeroCliente", "infoDetails", "hora", "citaReserva", "urlEmail"})
	public void createBooking(@Optional ("true") boolean resultadoEsperado, @Optional("") String localizador, @Optional("") String prefijo, @Optional("") String telefono, 
			@Optional("") String nombreCliente, @Optional("") String emailCliente, @Optional("") String observaciones, 	
			@Optional ("true") boolean aceptoTerminos, @Optional ("true") boolean aceptoPoliticaCancelacion, @Optional ("true") boolean aceptoComunicacionesComerciales, 
			@Optional("") String titleHeader, @Optional() String literalLocalizador, @Optional("") String literalObserciones, @Optional("true") boolean insertBooking,
			String iconeNotification, String confirmacionReseva, String nombreRestaurante, String direccionRestaurante, String telefonoRestaurante, 
			String numeroCliente, String infoDetails, String horaReserva, String citaReserva, String urlEmail) {
		
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
		espera();
		
		// Verificar que no hay error que nos impide siguir la finalizacion de la reserva
		validFormBookingByButtonNext(insertBooking, resultadoEsperado);
		
		//Validar la reserva en Booking
		nombreCliente = isNullOrEmpty(nombreCliente) ? this.nombreCliente : nombreCliente;
		emailCliente = isNullOrEmpty(emailCliente) ? this.emailCliente : emailCliente;
		horaReserva = isNullOrEmpty(horaReserva) ? this.hora : horaReserva;
		citaReserva = isNullOrEmpty(citaReserva) ? this.citaReserva : citaReserva;
		verificarReservarBooking (iconeNotification, confirmacionReseva, nombreRestaurante, direccionRestaurante, 
				telefonoRestaurante, nombreCliente, emailCliente, citaReserva, horaReserva, numeroCliente, infoDetails);
		
		//Validar la reserva en BD.
		
		
		//Validación por mail cliente
		//validateEmailClientBooking(urlEmail);
		
		//Validación por mail restaurante
		//validateEmailRestaurantBooking(urlEmail);
		
	}
	
	
	public void validFormBookingByButtonNext(@Optional("true") boolean insertBooking, @Optional("true") boolean resultatEsperado ) {
		log("si todos los campos requeridos del formulario estan ingresados pulsar el boton siguiente");
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[contains(@class, 'button primary-color')]")));
		
		WebElement buttonNext = driver.findElement(By.xpath("//button[contains(@class, 'button primary-color')]"));
		
		if(isNullOrEmpty(buttonNext.getAttribute("disabled"))) {
			//El boton está activado
			log("El boton está activado");
			
			if(!resultatEsperado) { //No crear la reserva en booking
				log("El boton deberia ser desactivado por que falta dato requerido en el formulario reserva");
				Assert.assertTrue(false);
			}
			
			if(insertBooking) {
				buttonNext.click();
				espera(2000);
			}
			
		} else {
			//El boton está desactivado
			log("El boton está desactivado");
			
			if(insertBooking && resultatEsperado) {
				log("Falla el boton Siguiente mientras que los campos requeridos estan ingresados");
				Assert.assertTrue(false);
			}
		}

	}
	
	
	@Parameters({"resultatEsperado", "localizador", "prefijo", "telefono", "nombreCliente", "emailCliente", "observaciones"})
	public void insertDataInputBooking(@Optional ("true") boolean resultadoEsperado, @Optional("") String localizador, @Optional("") String prefijo, @Optional("") String telefono, 
			@Optional("") String nombreCliente, @Optional("") String emailCliente, @Optional("") String observaciones) {
		//Localizador
		if(inputLocalizador != null && inputLocalizador.getSize() != null)
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
		else {
			enviarTexto(inputName, dummyName);
			this.nombreCliente = dummyName;
		}
		
		//Email
		if(!isNullOrEmpty(email) || !isNullOrEmpty(emailCliente)) {
			email = !isNullOrEmpty(emailCliente) ? emailCliente : email;
			enviarTexto(inputEmail, email);
			this.emailCliente = email;
		}
		else {
			enviarTexto(inputEmail, dummyEmail);
			this.emailCliente = dummyEmail;
		}
		
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
	
	@Test
	@Parameters({"iconeNotification", "confirmacionReseva", "nombreRestaurante", "direccionRestaurante", 
				"telefonoRestaurante", "nombreCliente", "emailCliente", "citaReserva", "hora", "numeroCliente", "infoDetails"})
	public void verificarReservarBooking (String iconeNotification, String messageConfirmation, String nombreRestaurante, String direccionRestaurante, 
			String telefonoRestaurante, String nombreCliente, String emailCliente, String citaReserva, String horaReserva, String paxReserva, String infoDetails) {
		log("validacion de la nueva reserva desde la pagina de confirmacion de la reserva");
		WebElement informacionReservaWbmt = null;
		String newReservaInformation = null;
		
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//app-ticket")));
		
		informacionReservaWbmt = driver.findElement(By.xpath("//app-ticket"));
		
		if(isNullOrEmpty(informacionReservaWbmt.getAttribute("outerText"))) {
			log("Error: No hay información en la nueva reserva");
			Assert.assertTrue(false);
		}
		
		newReservaInformation = informacionReservaWbmt.getAttribute("outerText");
		
		log("información de la nueva reserva en Booking: " + newReservaInformation);
		
		//validamos la información de la pagina
		
		/* Icono de confirmacion */ 
		validateBookingIcon(iconeNotification);
		
		/* message de notificacion de la reserva */
		validateBookingNotification(messageConfirmation);
		
		/* el nombre del restaurante */ 
		validateRestaurantName(nombreRestaurante);
		
		/* la dirección del restaurante */ 
		validateRestaurantAddress(direccionRestaurante);
		
		/* telefono del restaurante | nombre del restaurante */
		validateRestaurantPhone(telefonoRestaurante);
		
		/* nombre del cliente */
		validateCustomerName(nombreCliente);
		
		/* el email del cliente */
		validateCustomerEmail(emailCliente);
		
		/* pax y hora de la reserva: 1 persona a las 15:30 */
		validateBookingPaxAndTime(paxReserva, infoDetails, horaReserva);
		
		/* cita de la reserva en el formato: viernes, 3 febrero de 2023  */
		validateBookingAppointment(citaReserva);
		
	}
	
	/* Icono de confirmacion */ 
	public void validateBookingIcon(String iconeNotification) {
		/* Icono de confirmacion */ 
		log("Validación: Icono de confirmacion -> " + iconeNotification);
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-icon")));
		
		if(!driver.findElement(By.xpath("//mat-icon")).getAttribute("innerText").contentEquals(iconeNotification)) {
			log("Error en la Validación Icono de confirmacion: " + iconeNotification);
			log("Se ha encuentrado Icono de confirmacion: " + driver.findElement(By.xpath("//mat-icon")).getAttribute("innerText"));
			log("Se espera encuentrar el Icono: "+ iconeNotification );
			Assert.assertTrue(false); 
		}
		log("Validación: Icono de confirmacion: " + iconeNotification + " -> OK");
	}
	
	/* message de notificacion de la reserva */
	public void validateBookingNotification(String messageConfirmation) {
		/* message de notificacion de la reserva */
		log("Validación: message de notificacion de la reserva -> "+ messageConfirmation);
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h2[contains(@class, 'booking-ok')]")));
		
		if(!driver.findElement(By.xpath("//h2[contains(@class, 'booking-ok')]")).getAttribute("innerText").contentEquals(messageConfirmation)) {
			log("Error en la Validación del mensaje de confirmacion: " + messageConfirmation);
			log("Se ha encuentrado otro mensaje de notificación de confirmación: " + driver.findElement(By.xpath("//h2[contains(@class, 'booking-ok')]")).getAttribute("innerText"));
			log("Se espera encuentrar el siguiente mensaje de notificación: "+ messageConfirmation );
			Assert.assertTrue(false);
		}
		log("Validación: message de notificacion de la reserva " + messageConfirmation + " -> OK");
	}
	
	public void validateRestaurantName(String nombreRestaurante) {
		/* el nombre del restaurante */ 
		log("Validación: El nombre del restaurante -> " + nombreRestaurante);
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'info-wrapper border-content')]/h3[contains(text(), '" + nombreRestaurante +"')]")));
		
		if(driver.findElements(By.xpath("//div[contains(@class, 'info-wrapper border-content')]/h3[contains(text(), '" + nombreRestaurante + "')]")).size() > 1) {
			
			String sNombreRestauranteValided = driver.findElements(By.xpath("//div[contains(@class, 'info-wrapper border-content')]/h3[contains(text(), '" + nombreRestaurante +"')]")).get(0).getAttribute("innerText");
			
			if(!sNombreRestauranteValided.contentEquals(nombreRestaurante)) {
				log("Error en la Validación: nombre del restaurante -> " + nombreRestaurante);
				log("Se ha encuentrado otro nombre para el restaurante: " + sNombreRestauranteValided);
				log("Se espera encuentrar el siguiente nombre: "+ nombreRestaurante );
				Assert.assertTrue(false);
			}
		} else {
			String sNombreRestauranteValided = driver.findElements(By.xpath("//div[contains(@class, 'info-wrapper border-content')]/h3[contains(text(), '" + nombreRestaurante +"')]")).get(0).getAttribute("innerText");
			if(!sNombreRestauranteValided.contentEquals(nombreRestaurante)) {
				log("Error en la Validación: nombre del restaurante -> " + nombreRestaurante);
				log("Se ha encuentrado otro nombre para el restaurante: " + sNombreRestauranteValided);
				log("Se espera encuentrar el siguiente nombre para el restaurante: "+ nombreRestaurante );
				Assert.assertTrue(false);
			}
		}
		log("Validación: El nombre del restaurante " + nombreRestaurante + " -> OK");
	}
	
	public void validateRestaurantAddress(String direccionRestaurante) {
		/* la dirección del restaurante */ 
		log("Validación de la dirección del restaurante -> " + direccionRestaurante);
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'info-wrapper border-content')]/h4[contains(text(), '" + direccionRestaurante +"')]")));
		
		String sDireccionRestauranteValided = driver.findElement(By.xpath("//div[contains(@class, 'info-wrapper border-content')]/h4[contains(text(), '" + direccionRestaurante +"')]")).getAttribute("innerText");
		
		if(!sDireccionRestauranteValided.contentEquals(direccionRestaurante)) {
			log("Error en la Validación de la dirección del restaurante -> " + direccionRestaurante);
			log("Se ha encuentrado otra dirección: " + sDireccionRestauranteValided);
			log("Se espera encuentrar la dirección: "+ direccionRestaurante );
			Assert.assertTrue(false);
		}
		log("Validación de la dirección del restaurante " + direccionRestaurante + " -> OK");

	}
	
	public void validateRestaurantPhone(String telefonoRestaurante) {
		/* telefono del restaurante | nombre del restaurante */
		log("Validación del telefono del restaurante -> " + telefonoRestaurante);
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'info-wrapper border-content')]/h3[contains(text(), '" + telefonoRestaurante +"')]")));

		String stelefonoRestauranteValided = driver.findElement(By.xpath("//div[contains(@class, 'info-wrapper border-content')]/h3[contains(text(), '" + telefonoRestaurante +"')]")).getText();
		
		
		if(!stelefonoRestauranteValided.contains(telefonoRestaurante)) {
			log("Error en la Validación del telefono del restaurante -> " + telefonoRestaurante);
			log("Se ha encuentrado otro telefono: " + stelefonoRestauranteValided);
			log("Se espera encuentrar el telefono: "+ telefonoRestaurante );
			Assert.assertTrue(false);
		}
		log("Validación del telefono del restaurante " + telefonoRestaurante + " -> OK");

	}
	
	public void validateCustomerName(String nombreCliente) {
		/* nombre del cliente */
		log("Validación: el nombre del cliente -> " + nombreCliente);
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'info-wrapper')]/h3[contains(text(), '" + nombreCliente +"')]")));
		
		if(driver.findElements(By.xpath("//div[contains(@class, 'info-wrapper')]/h3[contains(text(), '"+ nombreCliente + "')]")).size() > 1) {
			
			String sNombreClienteValided = driver.findElements(By.xpath("//div[contains(@class, 'info-wrapper')]/h3[contains(text(), '" + nombreCliente +"')]")).get(0).getAttribute("innerText");
			
			if(!sNombreClienteValided.contentEquals(nombreCliente)) {
				log("Error en la Validación: nombre del cliente -> " + nombreCliente);
				log("Se ha encuentrado otro nombre para el cliente: " + sNombreClienteValided);
				log("Se espera encuentrar el siguiente nombre para el cliente: "+ nombreCliente );
				Assert.assertTrue(false);
			}
		} else {
			String sNombreClienteValided = driver.findElements(By.xpath("//div[contains(@class, 'info-wrapper')]/h3[contains(text(), '" + nombreCliente +"')]")).get(0).getAttribute("innerText");
			if(!sNombreClienteValided.contentEquals(nombreCliente)) {
				log("Error en la Validación: nombre del cliente -> " + nombreCliente);
				log("Se ha encuentrado otro nombre para el cliente: " + sNombreClienteValided);
				log("Se espera encuentrar el siguiente nombre para el cliente: "+ nombreCliente );
				Assert.assertTrue(false);
			}
		}
		log("Validación: El nombre del cliente " + nombreCliente + " -> OK");

	}
	
	public void validateCustomerEmail(String emailCliente) {
		/* el email del cliente */
		log("Validación: email del cliente -> " + emailCliente);
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'info-wrapper')]/h4[contains(text(), '" + emailCliente +"')]")));

		String sEmailClienteValided = driver.findElement(By.xpath("//div[contains(@class, 'info-wrapper')]/h4[contains(text(), '" + emailCliente +"')]")).getAttribute("innerText");
		
		if(!sEmailClienteValided.contains(emailCliente)) {
			log("Error en la Validación del email del cliente -> " + emailCliente);
			log("Se ha encuentrado otro email: " + sEmailClienteValided);
			log("Se espera encuentrar el email: "+ emailCliente );
			Assert.assertTrue(false);
		}
		log("Validación del email del cliente " + emailCliente + " -> OK");

	}
	
	public void validateBookingPaxAndTime(String paxReserva, String infoDetails, String horaReserva) {
		/* pax y hora de la reserva: 1 persona a las 15:30 */
		log("Validación: pax y hora de la reserva");
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'details-info-wrapper')]//div")));
		
		WebElement webelmtDetailInfo = driver.findElements(By.xpath("//div[contains(@class, 'details-info-wrapper')]//div")).get(1);
		String sInfoDetailValided = webelmtDetailInfo.getText();
		
		if(!isNullOrEmpty(infoDetails)) {
			String sVerificarInfoDetail = paxReserva + " " + infoDetails + " " + horaReserva;
			
			if(!sInfoDetailValided.contentEquals(sVerificarInfoDetail)) {
				log("Error en la Validación pax y hora  -> " + infoDetails);
				log("Se ha encuentrado otra informción pax y hora: " + sInfoDetailValided);
				log("Se espera encuentrar pax y hora : "+ infoDetails );
				Assert.assertTrue(false);
			}
			
		} else {
			if(!sInfoDetailValided.contentEquals(paxReserva)) {
				log("Error en la Validación pax  -> " + paxReserva);
				log("Se ha encuentrado otra informción pax y hora: " + sInfoDetailValided);
				log("Se espera encuentrar pax: "+ paxReserva );
				Assert.assertTrue(false);
			}
			
			if(!sInfoDetailValided.contentEquals(horaReserva)) {
				log("Error en la Validación pax  -> " + horaReserva);
				log("Se ha encuentrado otra informción pax y hora: " + sInfoDetailValided);
				log("Se espera encuentrar pax: "+ horaReserva );
				Assert.assertTrue(false);
			}
			
		}
		log("Validación pax y hora de la reserva  -> OK");

	}
	
	public void validateBookingAppointment(String citaReserva) {
		/* cita de la reserva en el formato: viernes, 3 febrero de 2023  */
		log("Validación: cita de la reserva por dia, mes de año");		
		w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'details-info-wrapper')]//div")));
		
		WebElement webelmtInfoCitaBooking = driver.findElements(By.xpath("//div[contains(@class, 'details-info-wrapper')]//div")).get(0);
		String sInfoCitaBookingValided = webelmtInfoCitaBooking.getText();
		
		if(!sInfoCitaBookingValided.contentEquals(citaReserva)) {
			log("Error en la Validación de la cita  -> " + citaReserva);
			log("Se ha encuentrado otra cita: " + sInfoCitaBookingValided);
			log("Se espera encuentrar la cita: "+ citaReserva );
			Assert.assertTrue(false);
		}
		log("Validación: cita de la reserva: "+ citaReserva + " -> OK");	

	}
	
	@Test
	@Parameters({"urlEmail", "emailCliente", "passwordCliente"})
	public void validateEmailClientBooking( String urlEmail, String emailCliente, String passwordCliente) {
		openNewWindowTab(urlEmail);
		
		MailReading readCustomerEmail = new MailReading();
		
		try {
			readCustomerEmail.openWebMail(emailCliente, passwordCliente);
			
			readCustomerEmail.cerrarSesionMail(false);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	@Test
	@Parameters({"urlEmail", "emailNotificacionReservas", "passwordRestaurante"})
	public void validateEmailRestaurantBooking( String urlEmail, String emailRestaurante, String passwordRestaurante) {
		openNewWindowTab(urlEmail);
		
		MailReading readRestaurantEmail = new MailReading();
		
		try {
			readRestaurantEmail.openSesionMail(emailRestaurante, passwordRestaurante);
			readRestaurantEmail.cerrarSesionMail(false);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	
	public void verificarReservarBD () {
		log("validacion de la nueva reserva desde la BBDD en las tablas:");
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
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringListPrefijo)));

			prefijos = driver.findElements(By.xpath(stringListPrefijo));
			
			if(prefijos.size() > 0) {
				//buscar i seleccionar prefijo definido como parametro
				boolean isSelectedPrefijo = false;
				String elemFijoSearch = "("+prefijo+")";
				for(int i = 0; i < prefijos.size(); i++) {
					if(prefijos.get(i).getText().contains(elemFijoSearch)) {
						clicJS(prefijos.get(i));
						log("el prefijo "+ prefijo +" del telefono está seleccionada desde el listado ");
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
		
		if(isNullOrEmpty(day) || day.equalsIgnoreCase("hoy")) {
			//stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator mat-calendar-body-selected mat-calendar-body-today')]";
			day = fechaHoy.split("/")[0].replaceFirst("^0+(?!$)", ""); // saccar los 0 de la izquerda replaceFirst("^0+(?!$)", "")
			stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator') and contains(text(), '"+ fechaHoy.split("/")[0].replaceFirst("^0+(?!$)", "") +"')]";
			
			this.citaReserva = fechaHoy;
			
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
			day =  day.split("/").length > 0 ? day.split("/")[0] : day;
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
