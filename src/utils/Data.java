package utils;

public class Data {

	private static Data INSTANCE;
    private int ultimoDocId;
    private String newUserMail;
    private boolean modoSinVentana;
    private String BD;
    
    
    
    public String getBD() {
		return BD;
	}

	public void setBD(String bD) {
		BD = bD;
	}

	private Data() {        
    }
    
    public static Data getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Data();
        }
        
        return INSTANCE;
    }
    
    
    
    public boolean isModoSinVentana() {
		return modoSinVentana;
	}

	public void setModoSinVentana(boolean modoSinVentana) {
		this.modoSinVentana = modoSinVentana;
	}

	public int getUltimoDocIdPlus() {
    	return ultimoDocId+1;
    }

	public int getUltimoDocId() {
		return ultimoDocId;
	}

	public void setUltimoDocId(int ultimoDocId) {
		this.ultimoDocId = ultimoDocId;
	}

	public String getNewUserMail() {
		return newUserMail;
	}

	public void setNewUserMail(String newUserMail) {
		this.newUserMail = newUserMail;
	}
	
	
    
    

}
