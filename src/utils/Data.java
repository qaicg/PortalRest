package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import Objects.Customer;
import Objects.LoyaltyCard;
//import configuration.ConfigServer;
import configuration.Server;
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
	private static ExtentReports extentReport;
	private ExtentTest extentTestSuite;
	private ExtentTest extentTestNode;
    private boolean serverCloudQuality04 = false;
    private ExtentSparkReporter sparkReporter;
	private boolean serverCloudQuality03 = false;
    
	private Customer user;

	private String pedidoActual;
    
 //   private ConfigServer configServer;
    private Server configServer;    

//    public ConfigServer getConfigServer() {
//		return configServer;
//	}
    public Server getConfigServer() {
		return configServer;
	}   
    

	public ExtentTest getExtentTest() {
		return extentTestNode;
	}



	public void setExtentTest(ExtentTest extentTest) {
		this.extentTestNode = extentTest;
	}

   
    






public ExtentTest getExtentTestSuite() {
		return extentTestSuite;
	}


	public void setExtentTestSuite(ExtentTest extentTestSuite) {
		this.extentTestSuite = extentTestSuite;
	}


public ExtentReports getExtentReport() {
		return extentReport;
	}



	public  void setExtentReport(ExtentReports extentReport) {
		Data.extentReport = extentReport;
	}

	
	




	public ExtentSparkReporter getSparkReporter() {
		return sparkReporter;
	}



	public void setSparkReporter(ExtentSparkReporter sparkReporter) {
		this.sparkReporter = sparkReporter;
	}



	//	public void setConfigServer(ConfigServer configServer) {
//		this.configServer = configServer;
//	}
	public void setConfigServer(Server configServer) {
		this.configServer = configServer;
	}    

	public boolean isServerCloudQuality04() {
		return serverCloudQuality04;
	}

	public void setServerCloudQuality04(boolean serverCloudQuality04) {
		this.serverCloudQuality04 = serverCloudQuality04;
	}
	
	public void initializeExtentReport() {
		if (extentReport == null) {
			extentReport = new ExtentReports();
		}
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
