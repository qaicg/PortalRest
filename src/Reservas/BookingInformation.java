package Reservas;

import java.util.Map;

import org.testng.annotations.Optional;

import main.Correo;
import utils.TestBase;

/*This class is used to save booking information after creating reservation */
public class BookingInformation extends TestBase {
	/*user information */
	private String userName;
	private String userEmail;
	private String userPhone;
	private String customerType;
	private String customerObservation;

	/*Restaurant information */
	private String restaurantEmail;
	private String restaurantPhone;
	private String restaurantName;
	private String restaurantAddress;
	
	/*Booking information */
	private String reservationDay;
	private String reservationHour;
	private String reservationLocalizador;
	private String reservationRoom;
	private String pax;
	private String infoDetails;
	
	//private Map<String, String> reservationClientEmail;
	//private Map<String, String> reservationRestaurantEmail;
	private Correo reservationClientEmail;
	private Correo reservationRestaurantEmail;

	
	private boolean verificarReserva;
	private boolean verifcarReservaBBDD;
	private boolean verificarReservaEmailCliente;
	private boolean verificarReservaEmailRestaurante;

	public BookingInformation(String userName, String userEmail, String userPhone, String restaurantEmail,
			String restaurantPhone, String restaurantName, String restaurantAddress, String reservationDay,
			String reservationHour, String reservationLocalizador, String reservationRoom, String pax, @Optional("Estándar") String customerType, String customerObservation) {
		super();
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPhone = userPhone;
		this.restaurantEmail = restaurantEmail;
		this.restaurantPhone = restaurantPhone;
		this.restaurantName = restaurantName;
		this.restaurantAddress = restaurantAddress;
		this.reservationDay = reservationDay;
		this.reservationHour = reservationHour;
		this.reservationLocalizador = reservationLocalizador;
		this.reservationRoom = reservationRoom;
		this.pax = pax;
		this.customerType = customerType;
		this.customerObservation = customerObservation;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public String getUserPhone() {
		return userPhone;
	}
	
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	
	public String getRestaurantEmail() {
		return restaurantEmail;
	}
	
	public void setRestaurantEmail(String restaurantEmail) {
		this.restaurantEmail = restaurantEmail;
	}
	
	public String getRestaurantPhone() {
		return restaurantPhone;
	}
	
	public void setRestaurantPhone(String restaurantPhone) {
		this.restaurantPhone = restaurantPhone;
	}
	
	public String getRestaurantName() {
		return restaurantName;
	}
	
	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}
	
	public String getRestaurantAddress() {
		return restaurantAddress;
	}
	
	public void setRestaurantAddress(String restaurantAddress) {
		this.restaurantAddress = restaurantAddress;
	}
	
	public String getReservationDay() {
		return reservationDay;
	}
	
	public void setReservationDay(String reservationDay) {
		this.reservationDay = reservationDay;
	}
	
	public String getReservationHour() {
		return reservationHour;
	}
	
	public void setReservationHour(String reservationHour) {
		this.reservationHour = reservationHour;
	}
	
	public String getReservationLocalizador() {
		return reservationLocalizador;
	}
	
	public void setReservationLocalizador(String reservationLocalizador) {
		this.reservationLocalizador = reservationLocalizador;
	}
	
	public String getReservationRoom() {
		return reservationRoom;
	}
	
	public void setReservationRoom(String reservationRoom) {
		this.reservationRoom = reservationRoom;
	}
	
	public String getPax() {
		return pax;
	}

	public void setPax(String pax) {
		this.pax = pax;
	}
	
	//public Map<String, String> getReservationRestaurantEmail() {
	public Correo getReservationRestaurantEmail() {
		return reservationRestaurantEmail;
	}

	//public void setReservationRestaurantEmail(Map<String, String> reservationRestaurantEmail) {
	public void setReservationRestaurantEmail(Correo reservationRestaurantEmail) {
		this.reservationRestaurantEmail = reservationRestaurantEmail;
	}

	//public Map<String, String> getReservationClientEmail() {
	public Correo getReservationClientEmail() {
		return reservationClientEmail;
	}

	//public void setReservationClientEmail(Map<String, String> reservationClientEmail) {
	public void setReservationClientEmail(Correo reservationClientEmail) {
		this.reservationClientEmail = reservationClientEmail;
	}
	
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	
	public String getCustomerObservation() {
		return customerObservation;
	}

	public void setCustomerObservation(String customerObservation) {
		this.customerObservation = customerObservation;
	}
	
	public boolean isVerificarReserva() {
		return verificarReserva;
	}

	public void setVerificarReserva(boolean verificarReserva) {
		this.verificarReserva = verificarReserva;
	}

	public boolean isVerifcarReservaBBDD() {
		return verifcarReservaBBDD;
	}

	public void setVerifcarReservaBBDD(boolean verifcarReservaBBDD) {
		this.verifcarReservaBBDD = verifcarReservaBBDD;
	}
	
	public boolean isVerificarReservaEmailCliente() {
		return verificarReservaEmailCliente;
	}

	public void setVerificarReservaEmailCliente(boolean verificarReservaEmailCliente) {
		this.verificarReservaEmailCliente = verificarReservaEmailCliente;
	}

	public boolean isVerificarReservaEmailRestaurante() {
		return verificarReservaEmailRestaurante;
	}

	public void setVerificarReservaEmailRestaurante(boolean verificarReservaEmailRestaurante) {
		this.verificarReservaEmailRestaurante = verificarReservaEmailRestaurante;
	}

}
