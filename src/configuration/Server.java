package configuration;

public class Server {
	public String name, urlConnexion , userName, password;
	public boolean test, production;
	
	public Server() {
		super();
	}
	
	public Server(String name, String urlConnexion, String userName, String password, boolean test,
			boolean production) {
		super();
		this.name = name;
		this.urlConnexion = urlConnexion;
		this.userName = userName;
		this.password = password;
		this.test = test;
		this.production = production;
	}
	
	
	public Server(EnumServidor server, boolean test,
			boolean production) {
		super();
		this.name = server.getServerName();
		this.test = test;
		this.production = production;		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlConnexion() {
		return urlConnexion;
	}

	public void setUrlConnexion(String urlConnexion) {
		this.urlConnexion = urlConnexion;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public boolean isProduction() {
		return production;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}
	
	@Override
	public String toString() {
		return "Server [name=" + name + ", urlConnexion=" + urlConnexion + ", userName=" + userName + ", password="
				+ password + ", test=" + test + ", production=" + production + "]";
	}
	
}
