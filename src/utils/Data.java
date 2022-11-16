package utils;

public class Data {

	private static Data INSTANCE;
    private int ultimoDocId;
    private String newUserMail;
    private boolean modoSinVentana;
    private String BD;
    private String reportDirectory;
    private boolean entornoTest=true;
    private boolean runAllTests=true;
    private String pedidoActual;
    
    
    
    public String getBD() {
		return BD;
	}
    
    

	public boolean isEntornoTest() {
		return entornoTest;
	}
	
	public boolean isRunAllTest() {
		return runAllTests;
	}

	

	public String getPedidoActual() {
		return pedidoActual;
	}



	public void setPedidoActual(String pedidoActual) {
		this.pedidoActual = pedidoActual;
	}



	public void setEntornoTest(boolean entornoTest) {
		this.entornoTest = entornoTest;
	}
	
	public void setRunAllTests(boolean runAllTests) {
		this.runAllTests = runAllTests;
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
