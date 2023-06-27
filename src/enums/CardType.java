package enums;
/*
 * Tipo de tarjeta de pago en una pasarela de pago
 */
public enum CardType {
	DEFAULTS(0),
	MASTER(1),
	VISA(2),
	AMEX(3);
	
	int numCardType;
	CardType(int numCard) {
		this.numCardType = numCard;
	}
}
