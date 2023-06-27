package enums;

import org.apache.commons.lang3.StringUtils;

import PaymentMethod.PaymentCard;
/*
 * Enumeración de las pasarelas de pago
 * implementacion de la classe PaymentCard para definir la tarjeta de pago utilizada
 */
public enum PaymentGateways {
	REDSYS(StringUtils.isNotBlank(PaymentCard.getPaymentName()) && PaymentCard.getPaymentName().contentEquals("Redsys") ? PaymentCard.getPaymentName() : "Redsys") {
		@Override
		public PaymentCard getPayment() {
			if(!PaymentCard.getPaymentName().contentEquals("Redsys")) PaymentCard.setPaymentName("Redsys");
			String name = "Redsys";
			String cardNumber = "4548812049400004";
			String[] expirationDate = {"12", "26"};
			String DatosCVV = "123";
			PaymentCard card = new PaymentCard(name, cardNumber, expirationDate, DatosCVV, CardType.DEFAULTS);
			return card;
		}
	},
	BIZUM(StringUtils.isNotBlank(PaymentCard.getPaymentName()) && PaymentCard.getPaymentName().contentEquals("Bizum") ? PaymentCard.getPaymentName() : "Bizum") {
		@Override
		public PaymentCard getPayment() {
			if(!PaymentCard.getPaymentName().contentEquals("Bizum")) PaymentCard.setPaymentName("Bizum");
			PaymentCard card = new PaymentCard("700000000", CardType.DEFAULTS);
			return card;
		}
		
	},
	PAYPAL(StringUtils.isNotBlank(PaymentCard.getPaymentName()) && PaymentCard.getPaymentName().contentEquals("Paypal") ? PaymentCard.getPaymentName() : "Paypal") {
		@Override
		public PaymentCard getPayment() {
			if(!PaymentCard.getPaymentName().contentEquals("Paypal")) PaymentCard.setPaymentName("Paypal");
			PaymentCard card = new PaymentCard(null, null, null, null, null, CardType.DEFAULTS);
			return card;
		}
	};
	
	private String payement;
	
	public String getPayement() {
		return payement;
	}

	public void setPayement(String payement) {
		this.payement = payement;
	}

	private PaymentGateways (String value) {
		this.payement = value;
		PaymentCard.setPaymentName(value);
	}
	
	private PaymentGateways () {
		
	}
	
	public PaymentCard getPayment() { return null; }

}
