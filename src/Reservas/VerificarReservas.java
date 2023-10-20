package Reservas;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mysql.cj.util.StringUtils;

import lombok.ToString;
import main.Correo;
import utils.Data;
import utils.TestBase;

public class VerificarReservas extends TestBase {
	
	@Test(description = "Validación del correo de notificación remetido al hacer reserva", priority = 1)
	@Parameters({"validateEmailCliente", "validateEmailRestaurante", "remitenteEmailCliente", "asuntoEmailCliente", "textConfirmacionReservaAlCliente", "remitenteEmailRestaurante",
		"asuntoEmailRestaurante", "textConfirmacionReservaAlRestaurante", "resultatEsperado"})
	public void validateBookingContentEmail(@Optional("false") boolean validateEmailCliente, @Optional("false") boolean validateEmailRestaurante, 
			String remitenteEmailCliente, String asuntoEmailCliente, String textConfirmacionReservaAlCliente, 
			String remitenteEmailRestaurante, String asuntoEmailRestaurante, String textConfirmacionReservaAlRestaurante,
			@Optional("true") String resultatEsperado) {
		
		if(Data.getInstance().getBookingInformation() == null) {
			log("validateEmailClientBooking --> No se puede verificar el email mientras que no hay copia de la reserva");
			Assert.assertTrue(false);
		} 
		
		if(!validateEmailCliente && !validateEmailRestaurante) {
			log("No tenemos que validar los correos de notificación de la reserva");
			return;
		}
		
		if(validateEmailCliente) {
			String paxPersona = Data.getInstance().getBookingInformation().getPax() + " personas";
			log("validar la notificacíon de la reserva enviada al cliente ");
			
			remitenteEmailCliente = isNullOrEmpty(remitenteEmailCliente) ? Data.getInstance().getBookingInformation().getRestaurantName() : remitenteEmailCliente;
			
			asuntoEmailCliente = asuntoEmailCliente.replace("{NombreRestaurante}", remitenteEmailCliente);
			
			//formatar fecha reservar para enviar en notificacion cliente 22-02-2023
			String fechaReserva = Data.getInstance().getBookingInformation().getReservationDay();
			if(fechaReserva.split("/").length > 1) {
				fechaReserva = fechaReserva.replace("/", "-");
			}
			
			if(fechaReserva.split("-").length < 1 || fechaReserva.split("/").length < 1) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date date = new Date();
				String fechaHoy = dateFormat.format(date);
				fechaReserva = fechaHoy;
			}
			
			//String salt = System.getProperty("Line.separator");
			log("validar la fechaReserva de la reserva enviada al cliente ==> " + fechaReserva);
			
			Data.getInstance().getBookingInformation().setReservationDay(fechaReserva);
			
			textConfirmacionReservaAlCliente = textConfirmacionReservaAlCliente.replace("{ln}", "\n");
			
			//log("Text 0001 de textConfirmacionReservaAlCliente --> " + textConfirmacionReservaAlCliente);
			
			textConfirmacionReservaAlCliente = textConfirmacionReservaAlCliente.replace("{nombreCliente}", Data.getInstance().getBookingInformation().getUserName())
					.replace("{fechaReserva}", fechaReserva).replace("{horaReserva}", Data.getInstance().getBookingInformation().getReservationHour())
					.replace("{paxPersona}", paxPersona).replace("{NombreRestaurante}", "'"+ Data.getInstance().getBookingInformation().getRestaurantName() + "'");
			
			//log("Text 00022 de textConfirmacionReservaAlCliente --> " + textConfirmacionReservaAlCliente);
			
			String newTextConfirmacionReservaAlCliente = textConfirmacionReservaAlCliente;
			
			//log("Text de ewTextConfirmacionReservaAlCliente --> " +newTextConfirmacionReservaAlCliente);
			
			log("Remitente de notificación al cliente -> " + remitenteEmailCliente);
			log("Asunto de la notificación al cliente -> " + asuntoEmailCliente);
			log("Texto de confirmacion reserva  al cliente -> " + new String(textConfirmacionReservaAlCliente));
			
			espera(3000);
			validateBookingNotificationEmail(validateEmailCliente, false, remitenteEmailCliente, asuntoEmailCliente, textConfirmacionReservaAlCliente, resultatEsperado);
			
			Data.getInstance().getBookingInformation().setVerificarReservaEmailCliente(true);
			
			log("*** FIN validar la notificacíon de la reserva enviada al cliente ****");
		}
		
		if(validateEmailRestaurante) {
			log("validar la notificacíon de la reserva enviada al restaurante ");
			
			remitenteEmailRestaurante = isNullOrEmpty(remitenteEmailRestaurante) ? Data.getInstance().getBookingInformation().getRestaurantName() : remitenteEmailRestaurante;
			
			asuntoEmailRestaurante = asuntoEmailRestaurante.replace("{NombreRestaurante}", remitenteEmailCliente);
			
			String[] afechaReserva = Data.getInstance().getBookingInformation().getReservationDay().replace("/", "-").split("-");
			String fechaReserva = afechaReserva[2] + "-" + afechaReserva[1] + "-" + afechaReserva[0];
			
			//log(" format date Reservation "  + fechaReserva);
			textConfirmacionReservaAlRestaurante = textConfirmacionReservaAlRestaurante.replace("{ln}", "\n");
			textConfirmacionReservaAlRestaurante = new String(textConfirmacionReservaAlRestaurante.replace("{NombreRestaurante}", Data.getInstance().getBookingInformation().getRestaurantName()).replace("{fechaReserva}", fechaReserva)
					.replace("{horaReserva}", Data.getInstance().getBookingInformation().getReservationHour()).replace("{numeroPersona}", Data.getInstance().getBookingInformation().getPax())
					.replace("{nombreCliente}", Data.getInstance().getBookingInformation().getUserName()).replace("{tipoCliente}", Data.getInstance().getBookingInformation().getCustomerType())
					.replace("{telefonoCliente}", Data.getInstance().getBookingInformation().getUserPhone()).replace("{emailCliente}", Data.getInstance().getBookingInformation().getUserEmail())
					.replace("{observaciones}", Data.getInstance().getBookingInformation().getCustomerObservation()));
			
			
			
			log("Remitente de notificación al restaurante -> " + remitenteEmailRestaurante);
			log("Asunto de la notificación al restaurante -> " + asuntoEmailRestaurante);
			log("Texto de confirmacion reserva al restaurante -> " + textConfirmacionReservaAlRestaurante);
			
			espera(3000);
			validateBookingNotificationEmail(false, validateEmailRestaurante, remitenteEmailRestaurante, asuntoEmailRestaurante, textConfirmacionReservaAlRestaurante, resultatEsperado);
			
			Data.getInstance().getBookingInformation().setVerificarReservaEmailRestaurante(true);
			
			log("*** FIN validar la notificacíon de la reserva enviada al restaurante ***");
		}
	}
	
	public void validateBookingNotificationEmail(@Optional("false") boolean validateEmailCliente, @Optional("false") boolean validateEmailRestaurante, String remitenteEmail, 
			String asuntoEmail, String mensajeEmail, @Optional("true") String resultatEsperado) {
		
		if(validateEmailCliente) {
			Correo infosClientEmail = Data.getInstance().getBookingInformation().getReservationClientEmail();
			espera(2000);
			log("ultimo mensaje al Cliente: Asunto --> " + infosClientEmail.getAsunto());
			//Validar el asunto
			if(!infosClientEmail.getAsunto().contains(asuntoEmail)) {
				log("Error: ha fallado el valor asunto del email enviado al cliente");
				Assert.assertTrue(false);
			}
			
			log("ultimo mensaje al Cliente: Remitente  --> " + infosClientEmail.getRemitente());
			//Validar remitente del email
			if(!infosClientEmail.getRemitente().contains(remitenteEmail)) {
				log("Error: ha fallado el valor remitente del email enviado al cliente");
				Assert.assertTrue(false);
			}
			
			log("ultimo mensaje al Cliente: Cuerpo --> " + infosClientEmail.getCuerpo());
			if(!infosClientEmail.getCuerpo().strip().contains(mensajeEmail.strip())) {
				log("Error: ha fallado el mensaje del email enviado al cliente");
				log("Mensaje encontrado --> \n " + mensajeEmail);
				Assert.assertTrue(false);
			}
			
			/*
			Map<String, String> infosClientEmail = Data.getInstance().getBookingInformation().getReservationClientEmail();
			
			infosClientEmail.get("remitenteEmail").contains(remitenteEmail);
			//Validar el remitente de la notificación
			if(!infosClientEmail.get("remitenteEmail").contains(remitenteEmail)) {
				log("Error: ha fallado el valor del remitente");
				log("Error: Valor remitente encontrado " + infosClientEmail.get("remitenteEmail") + " esperado " + remitenteEmail);
				Assert.assertTrue(false);
			}
			
			// Validar asunto de la notificación
			if(!infosClientEmail.get("asuntoDelEmail").contains(asuntoEmail)) {
				log("Error: ha fallado el valor asunto del email");
				log("Error: Valor asunto encontrado " + infosClientEmail.get("asuntoDelEmail") + " esperado " + asuntoEmail);
				Assert.assertTrue(false);
			}
			
			// Validar el mensaje del email de notitificación
			if(!infosClientEmail.get("mensajeDelMail").contains(mensajeEmail)) {
				log("Error: ha fallado el mensaje del email");
				log("Error: Mensaje encontrado " + infosClientEmail.get("mensajeDelMail") + " esperado " + mensajeEmail);
				Assert.assertTrue(false);
			}
			*/
			
		}
		
		if(validateEmailRestaurante) {
			Correo infosRestaurantEmail = Data.getInstance().getBookingInformation().getReservationRestaurantEmail();
			
			espera(2000);
			log("ultimo mensaje al Cliente: Asunto --> " + infosRestaurantEmail.getAsunto());
			//Validar el asunto del email enviado al restaurante
			if(!infosRestaurantEmail.getAsunto().contains(asuntoEmail)) {
				log("Error: ha fallado el valor asunto del email enviado al restaurante");
				Assert.assertTrue(false);
			}
			
			log("ultimo mensaje al Restaurante: Remitente  --> " + infosRestaurantEmail.getRemitente());
			//Validar remitente del email al restaurante
			if(!infosRestaurantEmail.getRemitente().contains(remitenteEmail)) {
				log("Error: ha fallado el valor remitente del email enviado al restaurante");
				Assert.assertTrue(false);
			}
			
			log("ultimo mensaje al Restaurante: Cuerpo --> " + infosRestaurantEmail.getCuerpo());
			//Validar el mensaje del email enviado al restaurante
			if(!infosRestaurantEmail.getCuerpo().contains(mensajeEmail)) {
				log("Error: ha fallado el mensaje del email enviado al restaurante");
				log("Mensaje encontrado al restaurante --> \n " + mensajeEmail);
				Assert.assertTrue(false);
			}
			
			/*
			Map<String, String> infosRestaurantEmail = Data.getInstance().getBookingInformation().getReservationRestaurantEmail();
			
			//Validar el remitente de la notificación
			if(!infosRestaurantEmail.get("remitenteEmail").contains(remitenteEmail)) {
				log("Error: ha fallado el valor del remitente");
				log("Error: Valor remitente encontrado " + infosRestaurantEmail.get("remitenteEmail") + " esperado " + remitenteEmail);
				Assert.assertTrue(false);
			} else {
				log("Valor remitente del email encontrado --> OK ");
			}
			
			// Validar asunto de la notificación
			if(!infosRestaurantEmail.get("asuntoDelEmail").contains(asuntoEmail)) {
				log("Error: ha fallado el valor asunto del email");
				log("Error: Valor asunto encontrado " + infosRestaurantEmail.get("asuntoDelEmail") + " esperado " + asuntoEmail);
				Assert.assertTrue(false);
			}
			
			// Validar el mensaje de notitificación del correo electronico
			if(!infosRestaurantEmail.get("mensajeDelMail").contains(mensajeEmail)) {
				log("Error: ha fallado el mensaje del email");
				log("Error: Mensaje encontrado " + infosRestaurantEmail.get("mensajeDelMail") + " esperado " + mensajeEmail);
				Assert.assertTrue(false);
			}
			*/

		}
	}
	
	@Test(description = "Validación de la nueva reserva en la BBDD: verificar que la reserva creada existe en la BBDD", priority = 1)
	@Parameters({"shop"})
	public void validationBookingBB(String shop) {
		
		if(Data.getInstance().getBookingInformation() == null) {
			log("No se ha podido guarda la reserva en Data.getInstance().getBookingInformation()");
			log("No se puede escribir SQL para consultar si la reserva existe en la BBDD");
			Assert.assertTrue(false);
		} 
		
		String dateReservation = Data.getInstance().getBookingInformation().getReservationDay();
		
		if(dateReservation.split("-").length > 1) {
			dateReservation = dateReservation.split("-")[2] + "-" + dateReservation.split("-")[1] + "-" +  dateReservation.split("-")[0];
		}
		
		if(dateReservation.split("/").length > 1) {
			dateReservation = dateReservation.split("/")[2] + "-" + dateReservation.split("/")[1] + "-" +  dateReservation.split("/")[0];
		}
		
		String SQL = "SELECT rr.ReservationId, rr.Name, rr.CreationDate, rr.CreationTime , rr.Phone , rr.EMail, rr.Pax, \r\n"
				+ "	rr.NotificationDate, rr.NotificationTime, rr.ReservationDate, rr.ReservationTime ,\r\n"
				+ "	rr.Comments observacionesCliente, rr.InternalComment observacionesAdmin, rr.`Locator` localizador, r.Name Sala\r\n"
				+ "FROM Rsv__Reservation rr \r\n"
				+ "LEFT JOIN \r\n"
				+ "Rsv__ReservationElement rre \r\n"
				+ "ON \r\n"
				+ "rre.ReservationId = rr.ReservationId \r\n"
				+ "LEFT JOIN \r\n"
				+ "Room r \r\n"
				+ "ON \r\n"
				+ "r.RoomId = rr.RoomId \r\n"
				+ "WHERE rr.Name = '" + Data.getInstance().getBookingInformation().getUserName() + "' AND rr.Phone = '"+ Data.getInstance().getBookingInformation().getUserPhone() +"' AND rr.EMail = '" + Data.getInstance().getBookingInformation().getUserEmail() +"' \r\n"
				+ "	AND rr.Pax = '" + Data.getInstance().getBookingInformation().getPax() + "'  AND rr.ReservationDate = '"+ dateReservation +"' \r\n"
				+ " AND DATE_FORMAT(rr.ReservationTime, '%H:%i') = '"+ Data.getInstance().getBookingInformation().getReservationHour() +"' \r\n"
				+ "	AND rr.`Locator` = '" + Data.getInstance().getBookingInformation().getReservationLocalizador() + "' AND r.Name = '"+ Data.getInstance().getBookingInformation().getReservationRoom() +"'\r\n"
				+ "	AND rr.CreationDate  = DATE_FORMAT(NOW(), '%Y-%m-%e')\r\n"
				+ "ORDER BY CreationDate DESC, CreationTime DESC\r\n";
		
		ResultSet rs =  databaseConnection.ejecutarSQL(SQL,"DB"+shop); 
	  	
	  	 if (rs!=null) {
	  		 try {		
	  			if (rs.first()) {
	  				log("La nueva reserva encontrado en la BBDD");
	  				log("Instruccion SQL para consultar la nueva reserva en la BBDD: \n\n"+ SQL);
	  				Data.getInstance().getBookingInformation().setVerifcarReservaBBDD(true);
	  			}else {
	  				log("No hemos encontrado la nueva reserva en la BBDD");
	  				log("Instruccion SQL para consultar la nueva reserva en la BBDD:  \n\n"+ SQL);
	  				Assert.assertTrue(false);
	  			}
	  			
	  		} catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			log("Instruccion SQL para consultar la nueva reserva en la BBDD:  \n\n "+ SQL);
	  			e.printStackTrace();
	  			Assert.assertTrue(false);
	  		}finally {
	  			databaseConnection.desconectar();
	  		}
	  	 } else {
	  		 Assert.assertTrue(false, "Error: No se ha podido validar la reserva en la BBDD");
	  		 log(SQL);
	  	 }
	}

}
