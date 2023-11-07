package configuration;

public enum EnumServidor {
	QUALITY03("cloudquality03", "jdbc:mysql://213.99.41.60:3306/", "cloud", "gKeQf6xfsIHLJXVy"),
	QUALITY04("cloudquality04", "jdbc:mysql://213.99.41.61:3306/", "cloud", "d4PKLWwrhFcdwnB1"),
	RUNALLTESTS("runAllTests", "", EnumServidor.QUALITY03.getServerName() 
			+ "-" + 
			EnumServidor.QUALITY04.getServerName(), "");//Definir para ejecutar todos los tests de los servidores cloudquality03 y cloudquality04
	
	private String serverName, urlConexion, userName, password;
	
	
	private EnumServidor(String serverName, String urlConexion, String userName, String password) {
		this.serverName = serverName;
		this.urlConexion = urlConexion;
		this.userName = userName;
		this.password = password;
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	public String getUrlConexion() {
		return urlConexion;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}
	//Verify that the server is valid from EnumServer
	public static boolean isValidServer(Server server) {
		return (server.getName().equals(EnumServidor.QUALITY03.getServerName()) || server.getName().equals(EnumServidor.QUALITY04.getServerName())) ? true : false;
	}
	
	
	public static  boolean isServerQUALITY03(Server server) {
		return (server.getName().equals(EnumServidor.QUALITY03.getServerName())) ? true : false;
	}
	
	public static boolean isServerQUALITY04(Server server) {
		return (server.getName().equals(EnumServidor.QUALITY04.getServerName())) ? true : false;
	}
}
