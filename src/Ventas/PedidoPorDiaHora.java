package Ventas;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Reservas.MailReading;
import utils.Data;
import utils.PortalRestOrderElements;
import utils.TestBase;

public class PedidoPorDiaHora extends TestBase {
	String diaReserva, hora, sala, localizador, observaciones, prefijo, telefono, email, citaReserva, nombreCliente, emailCliente = null;
	
	@Test(description = "Comprobar que se puede seleccionar un día", priority=1)
	@Parameters({"dia", "onlyHoyDisplonibled","ingresarDatosPorDefecto", "resultatEsperado"})
	public void selectedDay(@Optional("") String day, @Optional("false")boolean onlyHoyDisplonibled, @Optional("false") boolean ingresarDatosPorDefecto, @Optional("true") boolean resultatEsperado) {
		// la fecha de la venta 
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String fechaHoy = dateFormat.format(date);
		espera();
		
		String stringDay;
		String stringValidateSelectedDay = "//div[contains(@class,'mat-calendar-body-cell-content mat-focus-indicator mat-calendar-body-selected')]";
		
		List<WebElement> diasDelMes = PortalRestOrderElements.PedidoDiaHora.getMonthDays();
		log("Número de dias del mes: " +diasDelMes.size());		
		
//		List<WebElement> diasDisponibles = PortalRestOrderElements.PedidoDiaHora.getMonthDaysEnabledDisabled(true);
//		log("Número de dias disponibles: " +diasDisponibles.size());
//		
//		List<WebElement> disNoDisponibles = PortalRestOrderElements.PedidoDiaHora.getMonthDaysEnabledDisabled(false);
//		log("Número de dias no disponibles: " + disNoDisponibles.size());
		
		WebElement selectedDay = null;
		
		if(isNullOrEmpty(day) || day.equalsIgnoreCase("hoy") || ingresarDatosPorDefecto) {
			
			if(onlyHoyDisplonibled) { 
				//Disponibilidad solo el dia de hoy
				selectedDay = PortalRestOrderElements.PedidoDiaHora.getToDayHoy();
			}
			else {
				// saccar los 0 de la izquerda replaceFirst("^0+(?!$)", "")
				String fecha = fechaHoy.split("/")[0].replaceFirst("^0+(?!$)", "") +"')]";
				selectedDay = PortalRestOrderElements.PedidoDiaHora.selectDay(fecha, resultatEsperado);
			}		
			
		} else {
			
			if(day.split("/").length > 0) {			    
			    // saccar los 0 de la izquerda replaceFirst("^0+(?!$)", "")
			    String searchDay = day.split("/")[0].replaceFirst("^0+(?!$)", "");
				
			    selectedDay  = PortalRestOrderElements.PedidoDiaHora.selectDay(searchDay, resultatEsperado);
				
			} else {
				selectedDay  = PortalRestOrderElements.PedidoDiaHora.selectDay(day, resultatEsperado);
			}
		}
		
		clicJS(selectedDay); //Seleccion de dia
		espera(2000);
	}
	
	@Test(description = "Comprobar la selección de hora en Booking", priority = 1)
	@Parameters({"hora", "resultatEsperado", "ingresarDatosPorDefecto"})
	public void selectHour(@Optional("") String hour, @Optional("") boolean resultatEsperado, @Optional("") boolean ingresarDatosPorDefecto) {
		
		waitUntilPresence(PortalRestOrderElements.PedidoDiaHora.horasDisponible);	
		
		//Verificar que hay horas como parametro
		if(!isNullOrEmpty(hour) || ingresarDatosPorDefecto) { 
						
			espera(500);
			List<WebElement> horas = new ArrayList<WebElement>();
						
			horas = PortalRestOrderElements.PedidoDiaHora.getHoraDisponible();
			
			if(horas.size() > 0 || ingresarDatosPorDefecto) {
				//buscar i seleccionar la hora definida como parametro
				boolean isSelectedHour = false;
				
				if(ingresarDatosPorDefecto) {
					hour = horas.get(0).getText();
					clicJS(horas.get(0));
					espera(2000);
					log("la Hora("+ hour +") está seleccionada por defecto desde el listado ");
					isSelectedHour = true;
				}
				else {
					
					if(Objects.nonNull(PortalRestOrderElements.PedidoDiaHora.selectHoraPedido(hour, resultatEsperado))) {
						clicJS(PortalRestOrderElements.PedidoDiaHora.selectHoraPedido(hour, resultatEsperado));
						espera(2000);
						log("la hora("+ hour +") está seleccionada desde el listado ");
						isSelectedHour = true;
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
	
	@Test(priority = 1)
	public void verificarDiaDispoible () {
		
	}
	
	@Test(priority = 1)
	public void verificarHoraDisponible () {
		
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
