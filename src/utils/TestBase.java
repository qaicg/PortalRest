package utils;

import java.awt.AWTException;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.internal.Utils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
//import com.google.gson.annotations.Until;
//import com.google.inject.spi.Element;
import com.mysql.cj.util.StringUtils;
//import org.apache.commons.lang3.StringUtils.*;
//import com.vimalselvam.testng.listener.ExtentTestNgFormatter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

//import Cadenas.Es;
//import Objects.ProductItem;
import Windows.TrayIconDemo;
import configuration.ConfigServer;
import configuration.EnumServidor;
import configuration.Server;
//import enums.CookiesPortalRest;
//import lombok.var;


//import java.util.*;

public class TestBase extends StringUtils {

	protected static WebDriver driver;
	protected DatabaseConnection databaseConnection = new DatabaseConnection();

	public DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}

	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	TrayIconDemo td = new TrayIconDemo();
	protected static HashMap<String, String> biblioteca;
	protected static WebDriverWait w, w2;
	Actions actions;
	String pathprofile;
	ChromeOptions options;
	
	
	Faker fakePedio = new Faker();
	
	public ConfigServer configServer;
	
	public Faker getFakePedio() {
		return this.fakePedio;
	}
	
	@BeforeSuite
	@Parameters({"servidor"})
	public void configure(String servidor) {
		
		
		
		if(servidor.equals(EnumServidor.QUALITY03.getServerName())) {
			log("Estamos con el servidor --> " + EnumServidor.QUALITY03.getServerName());
			Data.getInstance().setServerCloudQuality03(true);
			Server server= new Server();
			server.setUrlConnexion("jdbc:mysql://213.99.41.60:3306/");
			server.setUserName("cloud");
			server.setPassword("gKeQf6xfsIHLJXVy");
			Data.getInstance().setConfigServer(server);
		}		
		else if(servidor.equals(EnumServidor.QUALITY04.getServerName())) {
			log("Estamos con el servidor --> " + EnumServidor.QUALITY04.getServerName());
			Data.getInstance().setServerCloudQuality04(true);	
			Server server= new Server();
			server.setUrlConnexion("jdbc:mysql://213.99.41.61:3306/");
			server.setUserName("cloud");
			server.setPassword("d4PKLWwrhFcdwnB1");
			Data.getInstance().setConfigServer(server);
		}
		else {
			System.out.println("No tenemos entorno definido, asumiremos que el entorno es Quality03");
			Data.getInstance().setServerCloudQuality03(true);
			Assert.assertTrue(false, "Error de configuración: No se ha encontrado el servidor definido en el testsuite: " + servidor);
		}

		
	}

	@BeforeSuite
	@Parameters({"suiteName","servidor"})
	public void initialize(String suiteName, String servidor) {
		
		    Data.getInstance().initializeExtentReport();
			Data.getInstance().setSparkReporter(new ExtentSparkReporter("target/Spark/Spark.html"));
			Data.getInstance().getSparkReporter().config().setEncoding("ANSI");
			Data.getInstance().getExtentReport().attachReporter(Data.getInstance().getSparkReporter());	
			Data.getInstance().setExtentTestSuite(Data.getInstance().getExtentReport().createTest(suiteName));
			
		String screenShotDirectory = new File(System.getProperty("user.dir")).getAbsolutePath()
				+ "/test-output/failure_screenshots/";
		File filePath = new File(screenShotDirectory);
		try {
			FileUtils.deleteDirectory(filePath);
			filePath.mkdir();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//VERSIONS
		ExtentTest wsServiceTest = Data.getInstance().getExtentTestSuite().createNode("Versions deployed");	
		Map<String,String> currentVersions = getCurrentVersions(servidor);
		
		 for (Map.Entry<String, String> entry : currentVersions.entrySet()) {
	            String key = entry.getKey();
	            String value = entry.getValue();
	            wsServiceTest.info(key + ":"+value);
	        }

	}

	private String getPortalRestWSVersion1(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String body = IOUtils.toString(in, encoding);
		return body;
	}
	
	

	@BeforeTest
	@Parameters({"modoSinVentana", "cloudLicenceBeta", "servidor"})
	public void beforeTest(final ITestContext testContext, @Optional("false") boolean modoSinVentana, @Optional("false") boolean cloudLicenceBeta, String servidor) {
		
		
		
		if(!isNullOrEmpty(Data.getInstance().getNewUserMail())){
			Data.getInstance().setNewUserMail(null);
		}
		
		if(!isNullOrEmpty(Data.getInstance().getPedidoActual())) {
			Data.getInstance().setPedidoActual(null);
		}

		//System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
		//System.setProperty("webdriver.chrome.driver", "C:\\driver\\ChromeDriver-104.0.5112.79\\chromedriver.exe");
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win")){
			System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
		}else {
			System.setProperty("webdriver.chrome.driver", "/home/qa/ChromeDriver/chromedriver");

		}
		
	    
		System.setProperty("webdriver.chrome.whitelistedIps", "");

		options = new ChromeOptions();
		//pathprofile = "C:\\Users\\"+TestBase.getCurrentUser()+"\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\";
		//options.addArguments("user-data-dir=" + pathprofile);
		options.addArguments("chrome.switches", "--disable-extensions");
		options.addArguments("--start-maximized");
		options.addArguments("--remote-allow-origins=*");

		//options.addArguments("profile-directory=Default");
		options.addArguments("--disable-geolocation");
		options.addArguments("--remote-allow-origins=*");
		
		//Clean cookies
		//options.addArguments("— disk-cache-size=0");

		if (Data.getInstance().isModoSinVentana() || modoSinVentana) {
			options.addArguments("--headless");// ESTOS PARAMETROS EJECUTAN EL NAVEGADOR SIN PANTALLA
			options.addArguments("--disable-gpu");// ESTOS PARAMETROS EJECUTAN EL NAVEGADOR SIN PANTALLA
		}
		
		//Ejecutar los tests desde CloudLicenseBeta
		if(cloudLicenceBeta && !Data.getInstance().isRunTestOnCloudLicenseBeta()) {
			Data.getInstance().setRunTestOnCloudLicenseBeta(cloudLicenceBeta);
		}

		driver = new ChromeDriver(options);
		
		
		
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
		w = new WebDriverWait(TestBase.driver, Duration.ofSeconds(10));
		w2 = new WebDriverWait(TestBase.driver, Duration.ofSeconds(90));
		actions = new Actions(driver);

		

		try {
			//Clean cookies
			driver.manage().deleteAllCookies();
			Data.getInstance().setExtentTest(Data.getInstance().getExtentTestSuite().createNode(testContext.getName()));
			td.displayTray("Iniciando test " + testContext.getName());
			log("Iniciando " + testContext.getName());

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			//clear_cache();
			e.printStackTrace();
			
		}
		
		initServerTestCloudQuality(servidor);
	}

	@AfterMethod
	public void getResult(ITestResult result) throws Exception {
		if (result.getStatus() == ITestResult.FAILURE) {
			
			log("ERROR. Test fallido, tomando imagen");
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
			String methodName = result.getName();
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			try {
				String reportDirectory = new File(System.getProperty("user.dir")).getAbsolutePath() + "/test-output/";
				File destFile = new File((String) reportDirectory + "/failure_screenshots/" + methodName + "_"
						+ formater.format(calendar.getTime()) + ".jpg");
				FileUtils.copyFile(scrFile, destFile);
				//Reporter.log("<a href='" + destFile.getAbsolutePath() + "'> <img src='" + destFile.getAbsolutePath()
				//		+ "' height='100' width='100'/> </a>");
			
				Data.getInstance().getExtentTest().fail("details", MediaEntityBuilder.createScreenCaptureFromPath(destFile.getAbsolutePath()).build());
				
			} catch (IOException e) {
				System.out.println("Failed in getResult function.");
				e.printStackTrace();
			}
			
			throw new Exception("was bound to fail!!!");
		}

	}

	@AfterTest
	public void afterTest(final ITestContext testContext) {
		espera(500);
		if(!isNullOrEmpty(Data.getInstance().getNewUserMail())){
			Data.getInstance().setNewUserMail(null);
		}
		
		if(!isNullOrEmpty(Data.getInstance().getPedidoActual())) {
			Data.getInstance().setPedidoActual(null);
		}
		
		//31
		Data.getInstance().getExtentTest().info("Test finished --> " + testContext.getName());
		
		
		//suprimir las cookies
		clear_cache();
		

		espera(500);
		
		Data.getInstance().getExtentReport().flush();
		espera(500);
		driver.manage().deleteAllCookies();
		driver.quit();

	}

	@AfterSuite
	public void endSession() {
		try {
			log("Ejecución de pruebas finalizada");
			td.displayTray("Ejecución de pruebas finalizada");
			Data.getInstance().getExtentTestSuite().createNode("Test Finalizado");
			Data.getInstance().getExtentReport().flush();
			
			//driver.manage().deleteAllCookies();
			
			File htmlFile = new File("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\test-output\\report"+new Date().getTime()+".html");
			//espera(2000);
			//Desktop.getDesktop().browse(htmlFile.toURI());
	
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		// }
		espera();
	}

	public static void espera() {
		espera(1000);
	}

	public static boolean isElementPresent(By by) {
		try {
			List<WebElement> elements;
			elements = driver.findElements(by);
			if (elements.size() == 0)
				return false;
			return true;
		} catch (Exception exception) {
			return false;
		}
	}
	
	public static boolean isElementPresent(List<WebElement> elements) {
		try {
			if (elements.size() == 0)
				return false;
			return true;
		} catch (Exception exception) {
			return false;
		}
	}
	
	public static boolean isElementPresent(WebElement element) {
		try {
			if (Objects.isNull(element))
				return false;
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public void log(String s) {
		System.out.println(s);
		try {
			Data.getInstance().getExtentTest().info(s);
		}catch(Exception e) {
			
		}

	}
	
	public static void logStatic(String s) {
		System.out.println(s);
		Reporter.log(s + "<br>");
		try {
			Data.getInstance().getExtentTest().info(s);
		}catch(Exception e) {
			
		}
	}

	public static void clicJS(WebElement element) {
		try {
			element.click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
		}
	}
	
	public void clicLogoEstablecimiento() {
		//Pulsar el logo tipo del establecimiento para volver al inicio(la pantalla principal de la tienda)
		By logoBy = By.xpath("//div[contains(@class, 'shop-name-wrapper')]//img[contains(@class, 'header-logo')]");
		WebElement logoElement = getElementByFluentWait(logoBy, 30, 5);
		if(logoElement != null) {
			logoElement.click();
			espera(2000);
		}
		else {
			log("Error: No se ha podido localizar el log del establecimiento ");
			Assert.assertTrue(false);
		}
	}
	
	public void clicLogoEstablecimientoDespuesDeCarga( By elementPantallaPrincipal) {
		clicLogoEstablecimiento();
		espera(5000);
		if(!isElementPresent(elementPantallaPrincipal)) {
			log("Error: No se ha podido volver a la página inicial tras actualizar la aplicación con F5 después de pulsar el logo del restaurante.");
			Data.getInstance().getExtentTest().fail("Error: No se ha podido volver a la página inicial tras actualizar la aplicación con F5 después de pulsar el logo del restaurante.");
			Assert.assertTrue(false);
		}
		
	}
	
	public void clicLogoEstablecimiento(WebElement elementForControlAlwaysPresent, By elementPantallaPrincipal) {//Controlar si se ha podido combiar de página tras pulsar en el botón del logo
		//Pulsar el logo tipo del establecimiento para volver al inicio(la pantalla principal de la tienda)
		boolean isElementForControlAlwaysPresent = false;
		clicLogoEstablecimiento();
		
		if(Objects.isNull(elementForControlAlwaysPresent))
			return;
		
		if(isElementPresent(elementForControlAlwaysPresent)) {
			
			log("Error: No se ha podido volver a la página inicial debido al fallo del botón logo");
			//extentTest.error("Error: No se ha podido volver a la página inicial debido al fallo del botón logo");
			Assert.assertTrue(true, "Error: No se ha podido volver a la página inicial debido al fallo del botón logo");
			isElementForControlAlwaysPresent = true;
			
			if(isElementForControlAlwaysPresent && !Objects.isNull(elementPantallaPrincipal)) {
				log("Actualizamos la aplicación con F5 y verirficamos que no se muestre una página en blanca en vez de la pantalla inicial de la tienda");
				driver.navigate().refresh();
				driver.navigate().refresh();
				espera(1500);
				
				if(!isElementPresent(elementPantallaPrincipal)) {
					log("Error: No se ha podido volver a la página inicial tras actualizar la aplicación con F5 después de pulsar el logo del restaurante.");
					Data.getInstance().getExtentTest().fail("Error: No se ha podido volver a la página inicial tras actualizar la aplicación con F5 después de pulsar el logo del restaurante.");
					
					Assert.assertTrue(false);
				}
			}
			
		}
		
	}
	
	
	public boolean setInputValueJS(By by, String value) {
		if(isElementPresent(by)) {
			WebElement elment = driver.findElement(by);		
			if(!isNullOrEmpty(elment.getAttribute("value"))){				
				elment.clear();
			}
			elment.sendKeys(value);			
			return true;
		}
		log("No se ha podido insertar datos("+value +") en el campo con By.xpath: " + by);
		return false;
	}
	
	//Se hace un metodo de envio de txto para que haga pausas necesarias.
	public void enviarTexto(WebElement elemento, String texto) {
		if(!isNullOrEmpty(elemento.getAttribute("value"))){				
			elemento.clear();
		}
		elemento.sendKeys(texto);
		//espera(100);
		//Testear que se ha insertado el texto
		if(!elemento.getAttribute("value").contentEquals(texto)) {
			log("Error: insercion del texto " + texto + " ha fallo en el elemento -> " + elemento);
			Assert.assertTrue(false);
		}
		//espera(100);
	}
	

	protected void atras() {
		WebElement back = driver.findElements(By.className("header-icon")).get(0);
		Actions actions = new Actions(driver);
		espera(500);
		actions.moveToElement(back).click().build().perform();
		espera(500);
	}

	public void abrirMiMonedero(String miMonedero) {
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
		espera(5000);
		
		
		if(isElementPresent(By.xpath("//*[text()='" + miMonedero + "']"))) {
			w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[text()='" + miMonedero + "']")));
			espera(500);
			clicJS(driver.findElement(By.xpath("//*[text()='" + miMonedero + "']")));
		}else {
			w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[text()='Mi perfil']")));
			espera(500);
			clicJS(driver.findElement(By.xpath("//*[text()='Mi perfil']")));
			w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[text()=' Mi monedero ']")));
			espera(500);
			clicJS(driver.findElement(By.xpath("//*[text()=' Mi monedero ']")));
		}
		
		
		
		
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='info']")));
		espera(500);
	}

	public void abrirMisDirecciones(String miPerfil, String misDirecciones) {
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
		clicJS(driver.findElement(By.xpath("//button[text()='" + miPerfil + "']")));
		clicJS(driver.findElement(By.xpath("//*[contains(text(),'" + misDirecciones + "')]")));
		espera(500);
	}

	public void abrirMisPedidos(@Optional("") String miPerfil, @Optional("") String misPedidos, @Optional("true") boolean fromMenu) {
		if(fromMenu) {
			clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
			espera(500);
			clicJS(driver.findElement(By.xpath("//button[text()='" + miPerfil + "']")));
			espera(500);
			//clicJS(driver.findElement(By.xpath("//button[contains(text(),'" + misPedidos + "')]")));
			clicJS(driver.findElement(By.xpath("//*[contains(text(),'" + misPedidos + "')]")));
			espera(500);
		} else {
			//Validar si estamos en la ficha principal del restaurante
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'rounded-buttons-wrapper')]")));
			
			if(isElementPresent(By.xpath("//div[contains(@class, 'rounded-buttons-wrapper')]"))) {
				
				try {
					w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(@class, 'main-text')  and text()='" + misPedidos + "']//ancestor::app-circle-progress-button")));

				}catch(Exception e) {
					e.printStackTrace();
					log(e.getMessage());
				}
				
				
				if(isElementPresent(By.xpath("//label[contains(@class, 'main-text')  and text()='" + misPedidos + "']//ancestor::app-circle-progress-button"))) {
					clicJS(driver.findElement(By.xpath("//label[contains(@class, 'main-text')  and text()='" + misPedidos + "']//ancestor::app-circle-progress-button")));
					espera(500);
				} else {
					log("Error. No hay el botón Mis pedidos en la ficha principal del restaurante.");
					Assert.assertTrue(false);
				}
				
			} else {
				log("No estamos en la ficha principal del restaurante para repetir el pedido");
				Assert.assertTrue(false);
			}
			
		}
		espera(500);
	}
	
	public void abrirInformacionPersonal(String miPerfil, String personal) {
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
		espera(1000);
		clicJS(driver.findElement(By.xpath("//button[text()='" + miPerfil + "']")));
		espera(1000);
		clicJS(driver.findElement(By.xpath("//*[contains(text(),'" + personal + "')]")));
		espera(1000);
	}
	
	public void abrirMenu() {
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
		espera(500);
	}
	
	public void clicAction(WebElement element) {
		espera(500);
		actions.moveToElement(element).click().build().perform();
		espera(500);
	}

	public List<Integer> stringArrayToInteger(String values) {

		String[] convertedRankArray = values.split(",");
		List<Integer> convertedRankList = new ArrayList<Integer>();
		for (String number : convertedRankArray) {
			convertedRankList.add(Integer.parseInt(number.trim()));
		}
		return convertedRankList;
	}

	public static void espera(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void waitTime(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static String stripAccents(String input) {
	    return input == null ? null :
	            Normalizer.normalize(input, Normalizer.Form.NFD)
	                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	//Formatting an XML file using Transformer
	public void formatXMLFile(String file) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    org.w3c.dom.Document document =  builder.parse(new InputSource(new InputStreamReader(new FileInputStream(
	        file))));

	    Transformer xformer = TransformerFactory.newInstance().newTransformer();
	    xformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    xformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    Source source = new DOMSource((Node) document);
	    Result result = new StreamResult(new File(file));
	    xformer.transform(source, result);
	}
	
	//Creating File
	private void createFile(@Optional("") String fileString, @Optional("false") boolean delete) throws IOException {
		if(Utils.isStringNotBlank(fileString) && Utils.isStringNotEmpty(fileString)) {
			File file = new File(fileString); //initialize File object and passing path as argument  
			String pathFile = file.getCanonicalPath();
			boolean result;  
			try {
				if(file.exists() && delete) {
					Files.deleteIfExists(Path.of(pathFile));
					if(Files.notExists(Path.of(pathFile))) {
						log("File deleted: " + pathFile); 
					} else {
						log("Error: failed on delete file: " + pathFile);
						Assert.assertTrue(false);
					}
				}
				
				result = file.createNewFile();  //creates a new file  
				if(result)      // test if successfully created a new file  
				{  
					log("file created "+file.getCanonicalPath()); //returns the path string  
				}  
				else  
				{  
					log("File already exist at location: "+file.getCanonicalPath()); 						
				} 
				
			} catch (IOException e) {  
				e.printStackTrace();    //prints exception if any  
			}  
		} else {
			log("error: Falta parmetro para crear fichero.");
			Assert.assertTrue(false);
		}
	}
	
	//Generar caracteres aleatotios
	public String generateCadenaAleatoria(int longitud) {
	    // El banco de caracteres
	    String banco = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	    // La cadena en donde iremos agregando un carácter aleatorio
	    String cadena = "";
	    for (int x = 0; x < longitud; x++) {
	        int indiceAleatorio = generateNumeroAleatorioEnRango(0, banco.length() - 1);
	        char caracterAleatorio = banco.charAt(indiceAleatorio);
	        cadena += caracterAleatorio;
	    }
	    return cadena;
	}
	
	//Generar numeros aleatotios
    public int generateNumeroAleatorioEnRango(int minimo, int maximo) {
        // nextInt regresa en rango pero con límite superior exclusivo, por eso sumamos 1
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
    }
    
    //Generar palabra
    public String generatePalabra() throws IOException {
    	StringBuilder result = new StringBuilder();
    	//URL url = new URL("https://palabras-aleatorias-public-api.herokuapp.com/random");
    	URL url = new URL("https://baconipsum.com/api/?type=all-meat&sentences=1&start-with-lorem=1");
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	conn.setRequestMethod("GET");
    	try ( BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
    	    for (String line; (line = reader.readLine()) != null; ) {
    	        result.append(line);
    	    }
    	}
    	return result.toString();
    	
    }
    
    //Abrir nueva pestaña en el navegador chrome
	public void openNewWindowTab(String url) {
		driver.switchTo().newWindow(WindowType.TAB);
		espera();
		driver.get(url);
		espera(500);
		log("Current Url -> " + driver.switchTo().window(driver.getWindowHandle()).getCurrentUrl());		
	}
	
	//Cerrar pestaña del navegador chrome
	public void closeWindowTab() {
		ArrayList<String> switchTabs= new ArrayList<String> (driver.getWindowHandles());
		driver.switchTo().window(switchTabs.get(1));
		driver.close();
		driver.switchTo().window(switchTabs.get(0));
	}
	
	public void closeWindowTab(int tabToClose, int getTab) {
		ArrayList<String> switchTabs= new ArrayList<String> (driver.getWindowHandles());
		driver.switchTo().window(switchTabs.get(tabToClose));
		driver.close();
		driver.switchTo().window(switchTabs.get(getTab));
	}

	
	public static String getNavigatorLanguage() {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
        String language = executor.executeScript("return window.navigator.userlanguage || window.navigator.language").toString();
        //espera(1500);
		
        return language;
	}
	
	public static Locale getLocale() {
		String language = getNavigatorLanguage();
		Locale locale = new Locale(language, language.toUpperCase());
		return locale;
	}
	

    
    public static void waitUntilPresence (String xpathCssSelector, boolean failIfNotPresent, @Optional("false") boolean useCssSelector) {
    	WebElement element = null;
    	try {
    		if(useCssSelector)
    			element = w2.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(xpathCssSelector)));
    		else 
    			element = w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathCssSelector)));
    	} catch (TimeoutException e) {
    		e.printStackTrace();
    		System.out.println("Failed -> unable to find: "+ xpathCssSelector );
    		if (failIfNotPresent) {
    			System.out.println("System exit becaouse is not present");
    			Assert.assertTrue(false);
    			System.exit(0);
    		}
    	}
 	
    }
    
    public static void waitUntilPresence (String xpathCssSelector, boolean failIfNotPresent) {
    	//WebElement element = null;
    	List<WebElement> element = null;
    	try {
    		//element = w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathCssSelector)));
    		element = w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathCssSelector)));
    	} catch (TimeoutException e) {
    		e.printStackTrace();
    		System.out.println("Failed unable to find: "+ xpathCssSelector );
    		if (failIfNotPresent) {
    			Assert.assertTrue(false);
    			System.exit(0);
    		}
    	}
 	
    }
    
    //Intenta a buscar elementos
    public static void waitUntilPresence (String xpathCssSelector) {
    	List<WebElement> element = null;
    	try {
    		element = w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathCssSelector)));
    	} catch (TimeoutException e) {
    		e.printStackTrace();
    		System.out.println("First Failed unable to find: "+ xpathCssSelector );
    		System.out.println("Try again to find: "+ xpathCssSelector );
    		
    		waitUntilPresence(xpathCssSelector, true);
    	}
 	
    }  
    
    /*
     * Waiting 30 seconds for an element to be present on the page, checking
     * for its presence once every 5 seconds.
     */
	public static WebElement getElementByFluentWait(By by, @Optional("30") int withTimeout, @Optional("5") int pollingEvery){
		// Waiting 30 seconds for an element to be present on the page, checking
		// for its presence once every 5 seconds.
		WebElement element = null;
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
		  .withTimeout(Duration.ofSeconds(withTimeout))
		  .pollingEvery(Duration.ofSeconds(pollingEvery))
		  .ignoring(NoSuchElementException.class);
	
		element = wait.until( driver -> {
			ExpectedConditions.presenceOfAllElementsLocatedBy(by);
			return driver.findElement(by);
		});
		
		return element;
	}
	
	public static List<WebElement> getElementsByFluentWait(By by, @Optional("30") int withTimeout, @Optional("5") int pollingEvery){
		// Waiting 30 seconds for an element to be present on the page, checking
		// for its presence once every 5 seconds.
		List<WebElement> elementList = null;
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
		  .withTimeout(Duration.ofSeconds(withTimeout))
		  .pollingEvery(Duration.ofSeconds(pollingEvery))
		  .ignoring(NoSuchElementException.class);
	
		elementList = wait.until( driver -> {
			ExpectedConditions.presenceOfAllElementsLocatedBy(by);
			return driver.findElements(by);
		});
		
		return elementList;
	}	
	
	//Methodo para eliminar las cookies de portalRest
    public void clear_cache() {
    	// """Clear the cookies and cache for the ChromeDriver instance."""
    	//Cookies de PortalRest: https://cloudquality04.hiopos.com
    	//portal-rest-web-cookie-geolocation
    	//cookie portal-rest-web-cookie-accept
    	//portal-rest-web-login
    	//porta-rest-web-language
    	//portal-rest-web-remember-map
    	
    	boolean refreshApp = false;
    	cookie.deleteAllCookiesPortalRest(refreshApp);

    }
    
    //Suprimir la cookie portal-rest-web-cookie-accept al inicio de la app 
    //Refrescar la página de portalrest
	public void set_cookie_accept() {
		String cookieAcceptName = "portal-rest-web-cookie-accept";
		boolean refreshApp = true;
		cookie.deleteCookiePortalRest(cookieAcceptName, refreshApp);
	}
	
    
    public WebElement get_clear_browsing_button(){
        //"""Find the "CLEAR BROWSING BUTTON" on the Chrome settings page."""
    	WebElement clearBrowsingButton = driver.findElement(By.cssSelector("* /deep/ #clearBrowsingDataConfirm"));
        return clearBrowsingButton;
    }
    
    public void waitUntilNotPresence(String sByXpathElement) {
    	w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(sByXpathElement)));
    }
    
    public void activeCurrentTabWindow() {
    	driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL, Keys.TAB);
    }
    
	public static double round (double value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
	}
	
	 public static void openSpark() {
			
			File filePath = new File("target/Spark/Spark.html");
			try {
				Desktop.getDesktop().browse(filePath.toURI());
			} catch (IOException e) {
				e.printStackTrace();
			}
	 }
	
	 //******** Getters y setters*************************
	 //***************************************************

	
	
	public void cerrarSesion(String login) {
		
		  List<WebElement> menuIcons = driver.findElements(By.xpath("//*[@class='header-icon']"));
		  
		  if (menuIcons.size()>=1) {
			  menuIcons.get(menuIcons.size()-1).click();	
			  
			  if(!isElementPresent(By.xpath("//*[contains(text(), '"+ login +"')]"))) {
				  List<WebElement> buttonSignOut = driver.findElements(By.xpath("//div[contains(@class, 'mat-menu-content')]//child::button"));
				  clicJS(buttonSignOut.get(buttonSignOut.size() -1)); //Cerrar la Sesión
				  espera(500);
			  }
		  }
	}
	
	public void cerrarSesion() {
		String login = "Registrarme";
		cerrarSesion(login);
	}
	
	public void scrollPage (WebElement element) {
		JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
		javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
	}
	 
	public int getLastDoc__Doc(String db) {

		String queryMaxPedido = "select MAX(DocId) as DocId from Doc__Doc dd";
		ResultSet rs =  databaseConnection.ejecutarSQL(queryMaxPedido,"DB"+db); 

		if (rs!=null) {		
			try {
				rs.first();
				return rs.getInt("DocId");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				databaseConnection.desconectar();
			}
		}

		return 0;
	}
	
	public static void back() {
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='keyboard_arrow_left']")));	
	}
		
	public void initServerTestCloudQuality(String servidor) {
		//log("\rDeterminamos el servidor donde se executa el test: \n" + getServerTestList() + "\r");
		
		if(!org.apache.commons.lang3.StringUtils.isBlank(servidor)  ) {
			log("El parametro del servido de test está definido en el fichero testsuite");
			
			if(servidor.contains(EnumServidor.QUALITY03.getServerName())) {
				Data.getInstance().setServerCloudQuality03(true);
				log("El test se ejecuta en el servidor: " + EnumServidor.QUALITY03.getServerName() + "\r");
			} 
			else if(servidor.contains(EnumServidor.QUALITY04.getServerName())) {
				Data.getInstance().setServerCloudQuality04(true);
				log("El test se ejecuta en el servidor: " + EnumServidor.QUALITY04.getServerName() + "\r");
			}
			else {
				log("Error: no se ha detectado el servidor de test\r");
				Assert.assertTrue(false, "Error: no se ha detectado el servidor");
			}
		}
		else if(driver.getCurrentUrl().contains(EnumServidor.QUALITY03.getServerName())) {
			Data.getInstance().setServerCloudQuality03(true);
			log("Se ha definido el servidor: " + EnumServidor.QUALITY03.getServerName() + "\r");
		} 
		else if(driver.getCurrentUrl().contains(EnumServidor.QUALITY04.getServerName())) {
			Data.getInstance().setServerCloudQuality04(true);
			log("Se ha definido el servidor: " + EnumServidor.QUALITY04.getServerName() + "\r");
			
		}
		else {
			log("Error: no se ha detectado el servidor de test" + "\r");
			Assert.assertTrue(false, "Error: no se ha detectado el servidor");
		}
		
	}
	
	public String getServerTestList() {
		String servidores = "";
		
		for(EnumServidor srv: EnumServidor.values()) {
			if(srv.ordinal() != EnumServidor.values().length - 1) {
				servidores += "Server " + srv.name() + " -> " + srv.getServerName() + " \n" ;
			}
			else
				servidores += "Server " + srv.name() + " -> " + srv.getServerName();
		}
				
		return servidores;
	}
	
	//Get the Current user
	public static String getCurrentUser() {
		String currentUser = System.getProperty("user.name");
		return currentUser;
	}
	

	 
	  public static Map<String,String> getCurrentVersions(String servidor){
		  Map<String,String> currentVersions = new HashMap<String,String>();

		  String server = "https://"+ servidor +".hiopos.com";
		  String license = setLicense(servidor);
		  
		  //Get ErpCloud version
		  currentVersions.put("ErpCloudVersion", getErpCloudVersion(server));
		  
		  //Get PortalRestWS
		  currentVersions.put("PortalRestWS", getPortalRestWSVersion(server));
		  
		  //Get portalECommerce
		  //currentVersions.put("PortalECommerce", getPortalECommerceVersion(server));
		  
		  //Get PortalRestWeb
		  currentVersions.put("PortalRestWeb", getPortalRestWebVersion(server));
		  
		  //Get cloudCentral
		  currentVersions.put("CloudCentral", getCloudCentral(server));
		  
		  //Get License 
		  currentVersions.put("License", getLicenseVersion(license));		  
		  
		  return currentVersions;
	  }
	  
	  public static String setLicense(String servidor) {
		  
		  String license = "1";
		  
		  if(servidor.equals(EnumServidor.QUALITY03.getServerName())) license = "https://cloudlicense.icg.eu/";
		  else if(servidor.equals(EnumServidor.QUALITY04.getServerName())) license = "https://cloudlicensebeta.icg.eu/";
		  
		  if(license.equals("1")) System.out.println("ERROR - Cannot set license url");
		  
		  return license;
	  }
	  
	  public static String getErpCloudVersion(String server) {		  
		  String service = "/ErpCloud/session/version";	  
		  String erpCloudVersion = response(server,service);	
		  
		  JsonObject json = json(erpCloudVersion);	
		  erpCloudVersion = json.get("result").getAsString();
	      return erpCloudVersion;       
	  }
	  
	  public static String getPortalRestWSVersion(String server) {
		  String service = "/PortalRestWS/session/version";
		  String PRTWS = response(server,service);	
		  
		  JsonObject json = json(PRTWS);	
		  PRTWS = json.get("result").getAsString();
	      return PRTWS;   
	  }
	  
	  public static String getPortalECommerceVersion(String server) {		  
		  String service = "/PortalECommerceWS/session/version";	  
		  String portalECommerceVersion = response(server,service);	
		  
		  JsonObject json = json(portalECommerceVersion);	
		  portalECommerceVersion = json.get("result").getAsString();
	      return portalECommerceVersion;       
	  }
	  
	  public static String getPortalRestWebVersion(String server) {
		  String service = "/portalrest/globalconfig.js";
		  String portalRestWeb = response(server,service);	
		  portalRestWeb = portalRestWeb.replace("  var globalUrls = ", "").replace(";", "");
		  
		  JsonObject json = json(portalRestWeb);	
		  portalRestWeb = json.get("version").getAsString();
	      return portalRestWeb;   
	  }    
	  
	  public static String getCloudCentral(String server) {
		  String service = "/CloudCentral/info/version";
		  String CloudCentral = response(server,service);	
		  
	      return CloudCentral;   
	  } 
	  
	  public static String getLicenseVersion(String licenseUrl) {
		  String service = "";
		  String license = response(licenseUrl,service);	
		  
		  Document doc = Jsoup.parse(license);
		  Elements version = doc.select("meta[name=version]");
		  String licenseVersion = version.attr("content");
		  
	      return licenseVersion;   
	  }
	  
	  
	  
	  public static  String response(String server, String service) {
		  String  versions = "";	  
		  OkHttpClient client = new OkHttpClient();
				Request request = new Request.Builder()
				  .url(server+service)
				  .addHeader("Content-Type", "application/json")
				  .build();
				try {
					Response response = client.newCall(request).execute();
					versions = response.body().string();
			        response.body().close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("ERROR - Cannot obtain "+ service +" version");
					e.printStackTrace();
					Assert.assertTrue(false);
				}  
		  
		  return versions;	  
	  }
	  
	  public static JsonObject json(String json) {
		  Gson gson = new Gson();
		  JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
		  return jsonObject;
	  }
	
}
