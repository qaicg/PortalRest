package utils;

import Objects.Customer;
import Objects.LoyaltyCard;
import Reservas.BookingInformation;
import pedido.Pedido;

public class Data {

	private static Data INSTANCE;
    private int ultimoDocId;
    private String newUserMail;
    private boolean modoSinVentana;
    private String BD;
    private String reportDirectory;
    private boolean entornoTest=true;
    private boolean runAllTests=true;
    private boolean runTestsFailed = true;
    private boolean runTestOnCloudLicenseBeta = false;
    
    private boolean serverCloudQuality04 = false;
    
	private boolean serverCloudQuality03 = false;
    
	private Customer user;

	private String pedidoActual;
    private BookingInformation bookingInformation;

    public boolean isServerCloudQuality04() {
		return serverCloudQuality04;
	}

	public void setServerCloudQuality04(boolean serverCloudQuality04) {
		this.serverCloudQuality04 = serverCloudQuality04;
	}
	
    public boolean isServerCloudQuality03() {
		return serverCloudQuality03;
	}
    
	public void setServerCloudQuality03(boolean serverCloudQuality03) {
		this.serverCloudQuality03 = serverCloudQuality03;
	}   
    
    public boolean isRunTestOnCloudLicenseBeta() {
		return runTestOnCloudLicenseBeta;
	}

	public void setRunTestOnCloudLicenseBeta(boolean runTestOnCloudLicenseBeta) {
		this.runTestOnCloudLicenseBeta = runTestOnCloudLicenseBeta;
	}
	
	public Customer getUser() {
		return user;
	}

	public void setUser(Customer user) {
		this.user = user;
	}

	private Pedido pedido;
    
    public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public BookingInformation getBookingInformation() {
		return bookingInformation;
	}

	public void setBookingInformation(BookingInformation bookingInformation) {
		this.bookingInformation = bookingInformation;
	}

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
	
	public void setRunTestsFailed(boolean runTestsFailed) {
		this.runTestsFailed = runTestsFailed;
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
