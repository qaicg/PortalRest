package Objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import org.testng.Assert;

import utils.Data;
import utils.DatabaseConnection;
import utils.TestBase;

public class LoyaltyCard {
	public String saldoACargar, formaPago, balanceBeforeLoading, balanceAfterLoading, balanceDB, aliasOrName, cardId;
	
	public boolean cargarSaldoSiempre;

	public LoyaltyCard() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoyaltyCard(String saldoACargar, boolean cargarSaldoSiempre, String formaPago, String balanceBeforeLoading,
			String balanceAfterLoading, String balanceDB, String aliasOrName, String cardId) {
		super();
		this.saldoACargar = saldoACargar;
		this.cargarSaldoSiempre = cargarSaldoSiempre;
		this.formaPago = formaPago;
		this.balanceBeforeLoading = balanceBeforeLoading;
		this.balanceAfterLoading = balanceAfterLoading;
		this.balanceDB = balanceDB;
		this.aliasOrName = aliasOrName;
		this.cardId = cardId;
	}
		
	public boolean isCargarSaldoSiempre() {
		return cargarSaldoSiempre;
	}

	public void setCargarSaldoSiempre(boolean cargarSaldoSiempre) {
		this.cargarSaldoSiempre = cargarSaldoSiempre;
	}

	public String getSaldoACargar() {
		return saldoACargar;
	}

	public void setSaldoACargar(String saldoACargar) {
		this.saldoACargar = saldoACargar;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public String getBalanceBeforeLoading() {
		return balanceBeforeLoading;
	}

	public void setBalanceBeforeLoading(String balanceBeforeLoading) {
		this.balanceBeforeLoading = balanceBeforeLoading;
	}

	public String getBalanceAfterLoading() {
		return balanceAfterLoading;
	}

	public void setBalanceAfterLoading(String balanceAfterLoading) {
		this.balanceAfterLoading = balanceAfterLoading;
	}

	public String getBalanceDB() {
		return balanceDB;
	}

	public void setBalanceDB(String balanceDB) {
		this.balanceDB = balanceDB;
	}
	
	public String getAliasOrName() {
		return aliasOrName;
	}

	public void setAliasOrName(String aliasOrName) {
		this.aliasOrName = aliasOrName;
	}
	
	

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public  static String getScriptSqlLoyaltyCard (String userEmail) {
		
		return "SELECT lcm.CardId cardId, cc.name userName, cc.email userEmail, lc.Alias loyaltyCardName,  SUM(lcm.Amount) as cardBalance\r\n"
				+ "FROM Con__Contact cc\r\n"
				+ "INNER JOIN Con__Customer ccu ON cc.contactId = ccu.contactId\r\n"
				+ "INNER JOIN Loy__Card lc ON ccu.CustomerId = lc.CustomerId\r\n"
				+ "INNER JOIN Loy__CardMovement lcm ON  lc.CardId = lcm.CardId\r\n"
				+ "WHERE cc.email = '"+userEmail+"' AND  lc.CardTypeId = 3";
	}
	
	public static String getScriptLastCardMovementId() {
		return "SELECT CardMovementId FROM Loy__CardMovement \r\n"
				+ "ORDER BY CardMovementId DESC\r\n"
				+ "LIMIT 1";
		
	}
	
	public static String getScriptInsertIntoLoyCardMovement(double balance, double totalEsperado, double amount, int cardMovementID, int cardId) {
		
		return  "INSERT INTO Loy__CardMovement(CardMovementId,CardId, CardMovementTypeId, Date, Time, Amount, CurrencyId, ExchangeRate, Points, ShopId, PosId, SellerId, Transferred)\r\n"
				+ "VALUES (" +cardMovementID + ", \r\n"
				+ ""+cardId+", \r\n"
				+ "2, \r\n"
				+ "CURDATE(), \r\n"
				+ "CURTIME(), \r\n"
				+ ""+amount+", \r\n"
				+ "1, \r\n"
				+ "0.0, \r\n"
				+ "0.0, \r\n"
				+ "1, \r\n"
				+ "6, \r\n"
				+ "0, \r\n"
				+ "0)";		
	}
}
