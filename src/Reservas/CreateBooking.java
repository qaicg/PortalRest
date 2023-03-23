package Reservas;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.Data;
import utils.TestBase;

public class CreateBooking extends TestBase {
	
	@Test(description="Prueba de creación nueva reserva", priority = 1)
	@Parameters({"dia", "numeroCliente", "testPlusMenosBtn", 
		"room", "existeRoom", "stringLabelRoom", "hora", "titleHeader",
		"localizador", "prefijo", "telefono", "nombreCliente", "emailCliente", "observaciones", 
		"aceptarTerminosCondiciones" , "aceptarPoliticaCancelacion", "aceptarComunicacionesComerciales", 
		"literalLocalizador", "literalObserciones", "insertBooking", "iconeNotification", "confirmacionReseva", "nombreRestaurante", 
		"direccionRestaurante", "telefonoRestaurante", "infoDetails", "citaReserva", "urlEmail", "emailNotificacionReservas", "tipoCliente",
		"ingresarDatosPorDefecto","resultatEsperado"
	})
	public void createReservation(@Optional("") String day, @Optional("") String numberCliente, @Optional("") boolean testPlusMenosBtn,
			@Optional("") String room, @Optional("") String existeRoom, @Optional("") boolean stringLabelRoom, @Optional("") String hour, @Optional("") String titleHeader,
			
			@Optional("") String localizador, @Optional("") String prefijo, @Optional("") String telefono, 
			@Optional("") String nombreCliente, @Optional("") String emailCliente, @Optional("") String observaciones, 	
			@Optional ("true") boolean aceptoTerminos, @Optional ("true") boolean aceptoPoliticaCancelacion, @Optional ("true") boolean aceptoComunicacionesComerciales, 
			@Optional() String literalLocalizador, @Optional("") String literalObserciones, @Optional("true") boolean insertBooking,
			String iconeNotification, String confirmacionReseva, String nombreRestaurante, String direccionRestaurante, String telefonoRestaurante, 
			String infoDetails, String citaReserva, String urlEmail, String emailNotificacionReservas, String tipoCliente,
						
			@Optional("false") boolean ingresarDatosPorDefecto, @Optional("") boolean resultatEsperado
			) {
		
		log("Prueba de creación reserva");
		
		Reserva newReserva = new Reserva();
		espera(500);
		
		newReserva.selectedDay(day, ingresarDatosPorDefecto);
		espera(500);
		
		newReserva.inputNumberPerson(numberCliente, resultatEsperado, testPlusMenosBtn);
		espera(500);
		
		newReserva.selectRoom(room, existeRoom, stringLabelRoom, resultatEsperado, ingresarDatosPorDefecto);
		espera(500);
		
		newReserva.selectHour(hour, resultatEsperado, ingresarDatosPorDefecto);
		espera(500);
		
		newReserva.realizarReserva(titleHeader, resultatEsperado);
		espera(2000);
		
		newReserva.createBooking(resultatEsperado, localizador, prefijo, telefono, nombreCliente, emailCliente, observaciones, 
				aceptoTerminos, aceptoPoliticaCancelacion, aceptoComunicacionesComerciales, titleHeader, literalLocalizador, literalObserciones, 
				insertBooking, iconeNotification, confirmacionReseva, nombreRestaurante, direccionRestaurante, telefonoRestaurante, numberCliente, 
				infoDetails, hour, citaReserva, urlEmail, day, emailNotificacionReservas, tipoCliente);
		espera(2000);
		
	}
	
	@Test(description="Prueba de creación múltiple reserva con datos distintos", priority = 1)
	@Parameters({"dia", "numeroCliente", "testPlusMenosBtn", 
		"room", "existeRoom", "stringLabelRoom", "hora", "titleHeader",
		"localizador", "prefijo", "telefono", "nombreCliente", "emailCliente", "observaciones", 
		"aceptarTerminosCondiciones" , "aceptarPoliticaCancelacion", "aceptarComunicacionesComerciales", 
		"literalLocalizador", "literalObserciones", "insertBooking", "iconeNotification", "confirmacionReseva", "nombreRestaurante", 
		"direccionRestaurante", "telefonoRestaurante", "infoDetails", "citaReserva", "urlEmail", "emailNotificacionReservas", "tipoCliente",
		"ingresarDatosPorDefecto", "resultatEsperado", "repeatBookingCount"
	})
	public void repeatReserva(@Optional("") String day, @Optional("") String numberCliente, @Optional("") boolean testPlusMenosBtn,
			@Optional("") String room, @Optional("") String existeRoom, @Optional("") boolean stringLabelRoom, @Optional("") String hour, @Optional("") String titleHeader,
			
			@Optional("") String localizador, @Optional("") String prefijo, @Optional("") String telefono, 
			@Optional("") String nombreCliente, @Optional("") String emailCliente, @Optional("") String observaciones, 	
			@Optional ("true") boolean aceptoTerminos, @Optional ("true") boolean aceptoPoliticaCancelacion, @Optional ("true") boolean aceptoComunicacionesComerciales, 
			@Optional() String literalLocalizador, @Optional("") String literalObserciones, @Optional("true") boolean insertBooking,
			String iconeNotification, String confirmacionReseva, String nombreRestaurante, String direccionRestaurante, String telefonoRestaurante, 
			String infoDetails, String citaReserva, String urlEmail, String emailNotificacionReservas, String tipoCliente,
						
			@Optional("false") boolean ingresarDatosPorDefecto, @Optional("") boolean resultatEsperado, @Optional("2") String repeatBookingCount
			) {
		int repeatBookingCounts = Integer.parseInt(repeatBookingCount);
		
		if(repeatBookingCounts < 2 ) {
			Assert.assertTrue(false, "No se puede crear múltiple reserva con el valor del parametro repeatBookingCount inferior a 2");
		}
		
		this.createReservation(day, numberCliente, testPlusMenosBtn, room, existeRoom, stringLabelRoom, hour, titleHeader, localizador,
				prefijo, telefono, nombreCliente, emailCliente, observaciones, aceptoTerminos, aceptoPoliticaCancelacion, aceptoComunicacionesComerciales, 
				literalLocalizador, literalObserciones, insertBooking, iconeNotification, confirmacionReseva, nombreRestaurante, direccionRestaurante, telefonoRestaurante, 
				infoDetails, citaReserva, urlEmail, emailNotificacionReservas, tipoCliente, ingresarDatosPorDefecto, resultatEsperado);
		
		if(!Data.getInstance().getBookingInformation().isVerificarReserva()) {
			Assert.assertTrue(false, "No se ha podido verificar la creación de la nueva reserva en Booking");
		}
		
		String sButton = "//div[contains(@class, 'button-wrapper')]//button[contains(@class, 'button secondary-color')]";
		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(sButton)));
		
		clicJS(driver.findElement(By.xpath(sButton)));
		
		for(int i = 1; i < repeatBookingCounts; i++ ) {
			
			this.createReservation(day, numberCliente, testPlusMenosBtn, room, existeRoom, stringLabelRoom, hour, titleHeader, localizador,
					prefijo, telefono, nombreCliente, emailCliente, observaciones, aceptoTerminos, aceptoPoliticaCancelacion, aceptoComunicacionesComerciales, 
					literalLocalizador, literalObserciones, insertBooking, iconeNotification, confirmacionReseva, nombreRestaurante, direccionRestaurante, telefonoRestaurante, 
					infoDetails, citaReserva, urlEmail, emailNotificacionReservas, tipoCliente, ingresarDatosPorDefecto, resultatEsperado);
			
			if(!Data.getInstance().getBookingInformation().isVerificarReserva()) {
				Assert.assertTrue(false, "No se ha podido continuar verificar la creación de la nueva reserva en Booking --> i( " + i + " )");
			}
			
			w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(sButton)));
			clicJS(driver.findElement(By.xpath(sButton)));
		}
	}

}
