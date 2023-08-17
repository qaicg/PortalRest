package Objects;

public class Customer {
	public String userName, userEmail, userPassword;
	public String phone;
	public LoyaltyCard tarjetaSaldo = new LoyaltyCard();
	
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Customer(String userName, String userEmail, String userPassword, String phone, LoyaltyCard tarjetaSaldo) {
		super();
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.phone = phone;
		this.tarjetaSaldo = tarjetaSaldo;
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

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LoyaltyCard getTarjetaSaldo() {
		return tarjetaSaldo;
	}

	public void setTarjetaSaldo(LoyaltyCard tarjetaSaldo) {
		this.tarjetaSaldo = tarjetaSaldo;
	}
	
}
