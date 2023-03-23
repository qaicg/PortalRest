package Reservas;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.tools.DocumentationTool.Location;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import graphql.Assert;
import main.Correo;
import utils.Data;
import utils.TestBase;
import utils.getDummyData;


public class Reserva extends TestBase {
	String diaReserva, hora, sala, localizador, observaciones, prefijo, telefono, email, citaReserva, nombreCliente, emailCliente = null;
	String dummyLocalizador, dummyName, dummyTelefono, dummyEmail, dummyObservaciones = null; 
	
	public BookingInformation bookingInfo;
	
	public boolean isCreatedBooking = false;
	public WebElement inputLocalizador, inputName, inputPhone, inputEmail, inputObservaciones, 
				checkboxCondicionesTerminos, checkboxPoliticaCancelacion, checkboxComunicacionComerciales,
				buttonSiguiente, buttonAtras = null;
	
	@Test(description = "Comprobar si se puede realizar la reserva después de ingresar la información(Dia, Hora, Sala y Pax)", priority = 1)
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
	@Test(description = "Creación nueva reserva en Booking", priority = 1)
	@Parameters({"resultatEsperado", "localizador", "prefijo", "telefono", "nombreCliente", "emailCliente", "observaciones", 
				"aceptarTerminosCondiciones" , "aceptarPoliticaCancelacion", "aceptarComunicacionesComerciales", "titleHeader", 
				"literalLocalizador", "literalObserciones", "insertBooking", "iconeNotification", "confirmacionReseva", "nombreRestaurante", 
				"direccionRestaurante", "telefonoRestaurante", "numeroCliente", "infoDetails", "hora", "citaReserva", "urlEmail", "dia", "emailNotificacionReservas", "tipoCliente"})
	public void createBooking(@Optional ("true") boolean resultadoEsperado, @Optional("") String localizador, @Optional("") String prefijo, @Optional("") String telefono, 
			@Optional("") String nombreCliente, @Optional("") String emailCliente, @Optional("") String observaciones, 	
			@Optional ("true") boolean aceptoTerminos, @Optional ("true") boolean aceptoPoliticaCancelacion, @Optional ("true") boolean aceptoComunicacionesComerciales, 
			@Optional("") String titleHeader, @Optional() String literalLocalizador, @Optional("") String literalObserciones, @Optional("true") boolean insertBooking,
			String iconeNotification, String confirmacionReseva, String nombreRestaurante, String direccionRestaurante, String telefonoRestaurante, 
			String numeroCliente, String infoDetails, String horaReserva, String citaReserva, String urlEmail, String reservationDay, String emailNotificacionReservas, String tipoCliente) {
		
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
		
		//Verify and Completing booking information 
		nombreCliente = isNullOrEmpty(nombreCliente) ? this.nombreCliente : nombreCliente;
		emailCliente = isNullOrEmpty(emailCliente) ? this.emailCliente : emailCliente;
		
		//log("1 reservationDay ------> reservationDay " + reservationDay);
		
		reservationDay = isNullOrEmpty(reservationDay) ? this.diaReserva :reservationDay;
		
		horaReserva = isNullOrEmpty(horaReserva) ? this.hora : horaReserva;
		
		citaReserva = isNullOrEmpty(citaReserva) ? this.citaReserva : citaReserva;
		
		//log("2 reservationDay ------> this.diaReserva " + this.diaReserva);
		
		//log("3 reservationDay ------> reservationDay " + reservationDay);
		
		observaciones = isNullOrEmpty(observaciones) ? this.observaciones : observaciones;
		
		log("Localizador de la reserva this.localizador -> " + this.localizador + " / localizador dummyLocalizador -> " + this.dummyLocalizador);
		
		//Save booking information
		this.bookingInfo = new BookingInformation(nombreCliente, emailCliente, prefijo+telefono, emailNotificacionReservas, telefonoRestaurante, nombreRestaurante, 
				direccionRestaurante, reservationDay, horaReserva, this.localizador, this.sala, numeroCliente, tipoCliente, this.observaciones);
		
		Data.getInstance().setBookingInformation(bookingInfo);
		
		log("informacion de la nueva reserva  Addres Restaurant --> " + Data.getInstance().getBookingInformation().getRestaurantAddress());
		
		log("informacion de la nueva reserva  reservation day en CreateBooking  --> " + Data.getInstance().getBookingInformation().getReservationDay());
		
		if(insertBooking) {
			//Validar la reserva en Booking
			verificarReservarBooking (iconeNotification, confirmacionReseva, nombreRestaurante, direccionRestaurante, 
					telefonoRestaurante, nombreCliente, emailCliente, citaReserva, horaReserva, numeroCliente, infoDetails);
			
			Data.getInstance().getBookingInformation().setVerificarReserva(true);
		}
		
	}
	
	@Parameters({"insertBooking", "resultatEsperado"})
	public void validFormBookingByButtonNext(@Optional("true") boolean insertBooking, @Optional("true") boolean resultatEsperado ) {
		log("si todos los campos requeridos del formulario Reserva estan ingresados luego pulsar el boton siguiente");
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
		if(inputLocalizador != null && inputLocalizador.getSize() != null) {
			if(!isNullOrEmpty(localizador)) {
				enviarTexto(inputLocalizador, localizador);
				this.localizador = localizador;
			} else {
				enviarTexto(inputLocalizador, dummyLocalizador);
				this.localizador = dummyLocalizador;
			}
			espera(500);
		}
		
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
		if(inputObservaciones.getSize() != null) {
			if(!isNullOrEmpty(observaciones)) {
				enviarTexto(inputObservaciones, observaciones);
				this.observaciones = observaciones;
			} else {
				enviarTexto(inputObservaciones, dummyObservaciones);
				this.observaciones = dummyObservaciones;
			}
		}
			
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
	
	
	@Test(description = "Verificar que los datos entrados en Booking, para la nueva reserva, son correctos" , priority = 1)
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
		
		//validamos la información de la reserva en Booking
		
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
			//if(!sInfoDetailValided.contentEquals(paxReserva)) {
			if(!sInfoDetailValided.substring(0, 1).contentEquals(paxReserva)) {
				log("Error en la Validación pax  -> " + paxReserva);
				log("Se ha encuentrado otra informción pax y hora: " + sInfoDetailValided);
				log("Se espera encuentrar pax: "+ paxReserva );
				log("sInfoDetailValided.substring(0, 0) --> " + sInfoDetailValided.substring(0, 0));
				Assert.assertTrue(false);
			}
			
			if(!sInfoDetailValided.contains(horaReserva)) {
				log("Error en la Validación hora  -> " + horaReserva);
				log("Se ha encuentrado otra informción pax y hora: " + sInfoDetailValided);
				log("Se espera encuentrar hora: "+ horaReserva );
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
			    
	    //log(" dateReserva ---> " + citaReserva);
		
		if(!sInfoCitaBookingValided.contentEquals(citaReserva)) {
			log("Error en la Validación de la cita  -> " + citaReserva);
			log("Se ha encuentrado otra cita: " + sInfoCitaBookingValided);
			log("Se espera encuentrar la cita: "+ citaReserva );
			Assert.assertTrue(false);
		}
		
		log("Validación: cita de la reserva: "+ citaReserva + " -> OK");	
	}
	
	@Test(description = "Consultación de la notificacion de la reserva enviada por email del cliente", priority = 1)
	@Parameters({"urlEmail", "emailCliente", "passwordCliente"})
	public void emailClientBooking( String urlEmail, String emailCliente, String passwordCliente) {
		
		if(Data.getInstance().getBookingInformation() == null) {
			log("emailClientBooking --> No se puede verificar el email mientras que la reserva no sea guardada");
			Assert.assertTrue(false);
		} 
		
		Correo correoCliente = openLastMessageFromMailSac(emailCliente, passwordCliente);
		
		if(correoCliente != null) {
			espera(500);
			Data.getInstance().getBookingInformation().setReservationClientEmail(correoCliente);
			espera(500);
		}
		else {
			Assert.assertTrue(false, () -> "No se ha podido consultar el correo del Cliente");
		}
		
		/*	
		openNewWindowTab(urlEmail);
		
		MailReading readCustomerEmail = new MailReading();
		
		try {
			readCustomerEmail.openWebMail(emailCliente, passwordCliente, false);
			
			readCustomerEmail.extractContentMail("Email del cliente");
			espera(500);
			
			Data.getInstance().getBookingInformation().setReservationClientEmail(readCustomerEmail.getInfoEmailMap());
			espera(500);
			//Open the last email not reading yet
			readCustomerEmail.lastEmailNotReading();
			espera(500);			
			
			readCustomerEmail.cerrarSesionMail(false);
			espera(1500);
			
			//close tab
			log("Current Url -> " + driver.switchTo().window(driver.getWindowHandle()).getCurrentUrl());
			closeWindowTab();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 */
	}
	
	
	@Test(description = "Consultación de la notificación de la reserva, enviada por correo electronico al restaurante", priority = 1)
	@Parameters({"urlEmail", "emailNotificacionReservas", "passwordRestaurante"})
	public void emailRestaurantBooking( String urlEmail, String emailRestaurante, String passwordRestaurante) {
		
		if(Data.getInstance().getBookingInformation() == null) {
			log("emailRestaurantBooking --> No se puede verificar el email mientras que la reserva no se ha guardada");
			Assert.assertTrue(false);
		} 
		
		Correo correoRestaurante = openLastMessageFromMailSac(emailRestaurante, passwordRestaurante);
		
		if(correoRestaurante != null) {
			espera(500);
			Data.getInstance().getBookingInformation().setReservationRestaurantEmail(correoRestaurante);
			espera(500);
		} 
		else {
			Assert.assertTrue(false, () -> "No se ha podido consultar el correo del Restauirante");
		}
		
		/*
		openNewWindowTab(urlEmail);
		espera(500);
		MailReading readRestaurantEmail = new MailReading();
		
		try {
			//readRestaurantEmail.openSesionMail(emailRestaurante, passwordRestaurante);
			readRestaurantEmail.openSesionMail(emailRestaurante, passwordRestaurante, false);
			//espera(500);
			
			readRestaurantEmail.extractContentMail("Email del Restaurante");
			espera(2000);
			
			Data.getInstance().getBookingInformation().setReservationRestaurantEmail(readRestaurantEmail.getInfoEmailMap());
			espera(2000);
			
			//Open the last email not reading yet
			readRestaurantEmail.lastEmailNotReading();
			espera(1500);
			
			readRestaurantEmail.cerrarSesionMail(false);
			espera(1500);
			
			//close tab
			log("Current Url -> " + driver.switchTo().window(driver.getWindowHandle()).getCurrentUrl());
			closeWindowTab();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		*/
	}
	
	@Test(description = "Comprobar los check box de aceptar Terminos condiciones, politica de cancelación y comunicaciones comerciales")
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
	@Test(description = "Comprobar que se puede seleccionar el prefijo del telefono")
	@Parameters({"prefijo", "resultatEsperado"})
	public void selectPrefijoTelefono(@Optional("") String prefijo, @Optional("") boolean resultatEsperado) {
		String stringElementPrefijo = "//div[contains(@class, 'mat-select-arrow-wrapper')]//child::div[contains(@class, 'mat-select-arrow')]";
		String stringListPrefijo = "//mat-option[contains(@class, 'mat-option')]//child::span[contains(@class, 'mat-option-text')]";
		if(!isNullOrEmpty(prefijo)) {
			String elemFijoSearch = "("+prefijo+")";
			//validar si es el prefijo que está seleccionado por defecto en el listado
			String valueSelectPrefijo = "//div//span//span[contains(@class, 'mat-select-min-line')]";
			w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(valueSelectPrefijo)));
			if(driver.findElement(By.xpath(valueSelectPrefijo)).getText().contains(elemFijoSearch)) {
				log("el prefijo "+ prefijo +" del telefono está seleccionado por defecto en el listado ");
				return;
			}
			
			//buscar y seleccionar el prefijo desde el listado
			w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringElementPrefijo)));
			
			clicJS(driver.findElement(By.xpath(stringElementPrefijo)));
			//espera(100);
			
			List<WebElement> prefijos = new ArrayList<WebElement>();			
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringListPrefijo)));

			prefijos = driver.findElements(By.xpath(stringListPrefijo));
			
			if(prefijos.size() > 0) {
				//buscar i seleccionar prefijo definido como parametro
				boolean isSelectedPrefijo = false;
				for(int i = 0; i < prefijos.size(); i++) {
					if(prefijos.get(i).getText().contains(elemFijoSearch)) {
						clicJS(prefijos.get(i));
						log("el prefijo "+ prefijo +" del telefono está seleccionado desde el listado ");
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
	
	@Test(description = "Comprobar que se puede seleccionar un día", priority=1)
	@Parameters({"dia", "ingresarDatosPorDefecto"})
	public void selectedDay(@Optional("") String day, @Optional("false") boolean ingresarDatosPorDefecto) {
		// la fecha de la venta 
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String fechaHoy = dateFormat.format(date);
		espera();
		
		String stringDay;
		String stringValidateSelectedDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator mat-calendar-body-selected')]";
		
		if(isNullOrEmpty(day) || day.equalsIgnoreCase("hoy") || ingresarDatosPorDefecto) {
			day = fechaHoy.split("/")[0].replaceFirst("^0+(?!$)", ""); // saccar los 0 de la izquerda replaceFirst("^0+(?!$)", "")
			stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator') and contains(text(), '"+ fechaHoy.split("/")[0].replaceFirst("^0+(?!$)", "") +"')]";
			
			this.citaReserva = getCitaReserva(fechaHoy, "/");
			
		} else {
			
			if(day.split("/").length > 0) {
				//comparar el mes de la reserva al mes del calendario, para ver si estamos en el mes valido para hacer la reserva. 
				//En el caso contrario cambiar el mes del calendario por el mes de la reserva
				
				//el mes y el año muestrado en el calendario
				String sWbElmtCalendarMounthYear = "//span[contains(@class, 'header-label')]";
				w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(sWbElmtCalendarMounthYear)));
				String calendarMounthYear = driver.findElement(By.xpath(sWbElmtCalendarMounthYear)).getText();
				//log("Se mostran en el calendario --> " + calendarMounthYear);
				
				Locale locale = getLocale();
		        espera(1500);
				String [] aDayReserva = day.split("/");
				int iDday = Integer.parseInt(aDayReserva[0]);
				int month = Integer.parseInt(aDayReserva[1]);
				int year = Integer.parseInt(aDayReserva[2]);
				
				LocalDate localDate = LocalDate.of(year,month, iDday);
				
			    String dayReserva = localDate.format(DateTimeFormatter.ofPattern("MMM'.' yyyy", locale)).toUpperCase();
			    log("dayReserva para la nueva reserva --> " + dayReserva);
			    
			    if(!calendarMounthYear.contains(dayReserva)) {
			    	//
			    	log("Camibar el MES.AÑO del calendario por ---> " + dayReserva);
			    	String sButtonMes = "//span[contains(@class, 'header-label')]//following-sibling::button[1]";
			    	WebElement btnNextMonth = driver.findElement(By.xpath(sButtonMes));
			    	
			    	while(!calendarMounthYear.contains(dayReserva)) {
			    		clicJS(btnNextMonth);
			    		espera(1000);
			    		calendarMounthYear = driver.findElement(By.xpath(sWbElmtCalendarMounthYear)).getText();
			    		if(calendarMounthYear.contains(dayReserva)) {
			    			break;
			    		}
			    	}
			    }
			    
			    // saccar los 0 de la izquerda replaceFirst("^0+(?!$)", "")
			    String searchDay = day.split("/")[0].replaceFirst("^0+(?!$)", "");
				
			    stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator') and contains(text(), '"+ searchDay +"')]";
			    
				this.citaReserva = getCitaReserva(day, "/");
				
			} else {
				stringDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator') and contains(text(), '"+ day +"')]";
				
				String newDay = day+ "/" +  fechaHoy.split("/")[1] + "/" +  fechaHoy.split("/")[2];
				this.citaReserva = getCitaReserva(newDay, "/");
			}
		}
		
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(stringDay)));
		
		WebElement selectedDay = driver.findElement(By.xpath(stringDay));
		
		
		clicJS(selectedDay); //Seleccion de dia
		espera(1500);
		//Validar el dia seleccionado
		if(isElementPresent(By.xpath(stringValidateSelectedDay))) {
			String searchDay =  day.split("/").length > 0 ? day.split("/")[0] : day;
			searchDay = searchDay.replaceFirst("^0+(?!$)", "");
			
			//log("searchDay --> " + searchDay);
			
			if(!driver.findElement(By.xpath(stringValidateSelectedDay)).getText().equalsIgnoreCase(searchDay)) {
				log("No se ha podido seleccionar el dia: "+ day);
				
				Assert.assertTrue(false);
			} else {
				log("Se ha seleccionado el dia: " + day);
				
				if(isNullOrEmpty(day) || day.equalsIgnoreCase("hoy") || ingresarDatosPorDefecto) {
					day = fechaHoy;
				}
				diaReserva = day;
			}
		}
	}
	
	@Test(description = "Comprobar que funcciona la selección de sala en Booking", priority = 1)
	@Parameters({"room", "existeRoom", "stringLabelRoom", "resultatEsperado", "ingresarDatosPorDefecto"})
	public void selectRoom(@Optional("") String room, @Optional("") String existeRoom, @Optional("") boolean stringLabelRoom, 
			@Optional("") boolean resultatEsperado, @Optional("false") boolean ingresarDatosPorDefecto) {

		String stringElementSelectRoom = "//mat-select//child::div[contains(@class, 'mat-select-arrow-wrapper')]//child::div[contains(@class, 'mat-select-arrow')]";
		String stringRoom = "//mat-option[contains(@class, 'mat-option')]//child::span[contains(@class, 'mat-option-text')]";
		
		//Verificar que hay sala como parametro o seleccionar la primera sala del listado, por defecto
		if(!isNullOrEmpty(room) || ingresarDatosPorDefecto) { 
			
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
				
				if(ingresarDatosPorDefecto) {
					room = salas.get(0).getText();
					clicJS(salas.get(0));
					log("la sala("+ room +") está seleccionada por defecto desde el listado ");
					isSelectedRoom = true;
				}
				else {
					for(int i = 0; i < salas.size(); i++) {
						log("Sala por " + i + " " + salas.get(i).getText());
						if(salas.get(i).getText().equalsIgnoreCase(room)) {
							clicJS(salas.get(i));
							log("la sala("+ room +") está seleccionada desde el listado ");
							isSelectedRoom = true;
							break;
						}
					 }
				}
				
				 if(!isSelectedRoom) {
					log("No se ha podido seleccionar la sala("+ room +") en el listado");
					Assert.assertTrue(false);
				 }
				
				 this.sala = room;
				
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
	
	@Test(description = "Comprobar la selección de hora en Booking", priority = 1)
	@Parameters({"hora", "resultatEsperado", "ingresarDatosPorDefecto"})
	public void selectHour(@Optional("") String hour, @Optional("") boolean resultatEsperado, @Optional("") boolean ingresarDatosPorDefecto) {
		String stringElementSelectHour = "//mat-select//child::div[contains(@class, 'mat-select-arrow-wrapper')]//child::div[contains(@class, 'mat-select-arrow')]";
		String stringHours = "//mat-option[contains(@class, 'mat-option')]//child::span[contains(@class, 'mat-option-text')]";
		
		//Verificar que hay horas como parametro
		if(!isNullOrEmpty(hour) || ingresarDatosPorDefecto) { 
			
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
			
			if(horas.size() > 0 || ingresarDatosPorDefecto) {
				//buscar i seleccionar la hora definida como parametro
				boolean isSelectedHour = false;
				
				if(ingresarDatosPorDefecto) {
					hour = horas.get(0).getText();
					clicJS(horas.get(0));
					log("la Hora("+ hour +") está seleccionada por defecto desde el listado ");
					isSelectedHour = true;
				}
				else {
				
					for(int i = 0; i < horas.size(); i++) {
						log("hora por " + i + " " + horas.get(i).getText());
						if(horas.get(i).getText().equalsIgnoreCase(hour)) {
							clicJS(horas.get(i));
							log("la hora("+ hour +") está seleccionada desde el listado ");
							isSelectedHour = true;
							break;
						}
					}
				}
				
				if(!isSelectedHour) {
					log("No se ha podido seleccionar la hora("+ hour +") en el listado");
					Assert.assertTrue(false);
				}
				
				this.hora = hour;
				
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
	
	@Test(description = "Comprobar que se puede introducir el pax de la reserva", priority = 1)
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
	
	@Test(description = "Comprobar el pax(Numero de persona por reserva)")
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
					log("Se ha podido introducir el numero cliente(PAX) vía el boton añadir ");
					Assert.assertTrue(true);
					break;
				}
				
				if( i == Integer.parseInt(numberCliente) && continueClicPlus) {
					log("Error: no se ha podido introducir el numero cliente(PAX) vía el boton añadir ");
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
	
	public String getCitaReserva(String citaReserva, String sDateSeparate) {
		espera(500);
		Locale locale = getLocale();
        espera(1500);
		String [] aCitaReserva = citaReserva.split(sDateSeparate);
		int day = Integer.parseInt(aCitaReserva[0]);
		int month = Integer.parseInt(aCitaReserva[1]);
		int year = Integer.parseInt(aCitaReserva[2]);
		
		LocalDate localDate = LocalDate.of(year,month, day);
		
	   // String dateReserva = localDate.format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", locale));
		 String dateReserva = localDate.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", locale));
	    log("Cita de la reserva --> " + dateReserva);
	    
	    
	    return dateReserva;
	}

}
