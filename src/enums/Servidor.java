package enums;

public enum Servidor {
	QUALITY03("cloudquality03"),
	QUALITY04("cloudquality04");
	
	private String serverName;
	
	private Servidor(String serverName) {
		this.serverName = serverName;
	}
	
	public String getServerName() {
		return this.serverName;
	}
}
