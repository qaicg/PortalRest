package utils;

public enum CookiesPortalRest {
	LANGUAGE(cookie.getCookie(("porta-rest-web-language"))){
		@Override
		public String getLanguage() {
			return cookie.getCookie(("porta-rest-web-language"));
		}
	},
	LOGIN(cookie.getCookie(("portal-rest-web-login"))) {
		@Override
		public String  getLogin() {
			return cookie.getCookie(("portal-rest-web-login"));
		}
	},
	REMEMBER_MAP(cookie.getCookie(("portal-rest-web-remember-map"))) {
		@Override
		public String getRememberMap() {
			return cookie.getCookie(("portal-rest-web-remember-map"));
		}
		
	},
	COOKIE_ACEPT(cookie.getCookie(("portal-rest-web-cookie-accept"))) {
		@Override
		public String getCookieAcept (){
			return cookie.getCookie(("portal-rest-web-cookie-accept"));
		}
	};

	private String value;
	private CookiesPortalRest(String cookie) {
		// TODO Auto-generated constructor stub
		this.setValue(cookie);
	}
	
	public String getLanguage() { return null; }
	public String getLogin() { return null; }
	public String getRememberMap() { return null; }
	public String getCookieAcept() { return null; }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
