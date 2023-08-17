package PaymentMethod;

import enums.CardType;

/*
 * Definición de la classe de Tarjetas de pago 
 */
public class PaymentCard {
	public static String paymentName;
	private static String claveBizum = "1234";
	private static String authentificationCodeBizum = "12345678";
	
	public String cardNumber;
	public String expirationDate[] = {null, null}; //mm-yy
	public String DatosCVV;
	public String email;
	public String password;
	public String telephone;
	public String fakePhone; // fakePhone usuario no activo para compra Bizum
	
	public String fakeIban; // fake numero de tajeta bancaria que nos debe funcionar en la plataforma del banco
	
	public String getFakeIban() {
		return fakeIban;
	}

	public void setFakeIban(String fakeIban) {
		this.fakeIban = fakeIban;
	}

	public CardType cardType;//indique el tipo de tarjeta de pagament si es Visa, Master o otra
	
	
	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public static String getClaveBizum() {
		return getPaymentName().equalsIgnoreCase("Bizum") ? claveBizum : null;
	}

	public static String getAuthentificationCodeBizum() {
		return  getPaymentName().equalsIgnoreCase("Bizum") ? authentificationCodeBizum : null;
	}	
	
	public static String getPaymentName() {
		return paymentName;
	}
	
	public static void setPaymentName(String payment) {
		paymentName = payment;
	}
	
	public String getCardNumber() {
		return cardNumber;
	}
	
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public String[] getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(String[] expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public String getDatosCVV() {
		return DatosCVV;
	}
	
	public void setDatosCVV(String datosCVV) {
		DatosCVV = datosCVV;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getFakePhone() {
		return fakePhone;
	}

	public void setFakePhone(String fakePhone) {
		this.fakePhone = fakePhone;
	}
	
	public PaymentCard() {
		super();
	}
	
	public PaymentCard(String pName, String cardNumber, String[] expirationDate, String datosCVV, CardType cardType) {
		super();
		paymentName = pName;
		this.cardNumber = cardNumber;
		this.expirationDate = expirationDate;
		this.DatosCVV = datosCVV;
		this.cardType = cardType;
	}
	
	public PaymentCard(String cardNumber, String[] expirationDate, String datosCVV, String email,
			String password, CardType cardType) {
		super();
		this.cardNumber = cardNumber;
		this.expirationDate = expirationDate;
		this.DatosCVV = datosCVV;
		this.email = email;
		this.password = password;
		this.cardType = cardType;
	}
	
	public PaymentCard(String telephone, CardType cardType) {
		super();
		this.telephone = telephone;
		this.cardType = cardType;
		
		if(getPaymentName().equalsIgnoreCase("Bizum")) 
			setFakePhone("765432019"); 
		else {
			setFakePhone(null);
		}
	}
	
}
