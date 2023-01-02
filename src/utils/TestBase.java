package utils;

import java.awt.AWTException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.internal.Utils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentAventReporter;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.inject.spi.Element;
import com.mysql.cj.util.StringUtils;
import com.vimalselvam.testng.listener.ExtentTestNgFormatter;

import Cadenas.Es;
import Objects.ProductItem;
import Windows.TrayIconDemo;

public class TestBase {

	protected static WebDriver driver;
	protected DatabaseConnection databaseConnection = new DatabaseConnection();;
	TrayIconDemo td = new TrayIconDemo();
	protected static HashMap<String, String> biblioteca;
	ExtentReports extent;
	ExtentSparkReporter spark;
	protected static WebDriverWait w, w2;
	Actions actions;
	String pathprofile;
	ChromeOptions options;
	
	@BeforeClass
	@Parameters({"test"})
	public void configure(@Optional ("true") boolean test) {
		
		if(test) {
			databaseConnection.ENTORNODEFINIDO = DatabaseConnection.ENTORNOTEST;

		}else {
			databaseConnection.ENTORNODEFINIDO = DatabaseConnection.ENTORNOPRODUCION;
		}
	}

	@BeforeSuite
	public void initialize() {
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

		// https://www.extentreports.com/docs/versions/5/java/index.html*/
	}

	@BeforeTest
	public void beforeTest(final ITestContext testContext) {
		
		if(!isNullOrEmpty(Data.getInstance().getNewUserMail())){
			Data.getInstance().setNewUserMail(null);
		}
		
		if(!isNullOrEmpty(Data.getInstance().getPedidoActual())) {
			Data.getInstance().setPedidoActual(null);
		}

		System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
		options = new ChromeOptions();
		pathprofile = "C:\\Users\\QA\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Default";
		options.addArguments("user-data-dir=" + pathprofile);
		options.addArguments("chrome.switches", "--disable-extensions");
		options.addArguments("--start-maximized");
		options.addArguments("profile-directory=Default");
		options.addArguments("--disable-geolocation");

		if (Data.getInstance().isModoSinVentana()) {
			options.addArguments("--headless");// ESTOS PARAMETROS EJECUTAN EL NAVEGADOR SIN PANTALLA
			options.addArguments("--disable-gpu");// ESTOS PARAMETROS EJECUTAN EL NAVEGADOR SIN PANTALLA
		}

		driver = new ChromeDriver(options);
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
		w = new WebDriverWait(TestBase.driver, Duration.ofSeconds(10));
		w2 = new WebDriverWait(TestBase.driver, Duration.ofSeconds(90));
		actions = new Actions(driver);
		

		extent = new ExtentReports();
		spark = new ExtentSparkReporter("target/Spark.html");
		extent.attachReporter(spark);

		try {
			td.displayTray("Iniciando test " + testContext.getName());
			log("Iniciando " + testContext.getName());
			extent.createTest(testContext.getName());

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				Reporter.log("<a href='" + destFile.getAbsolutePath() + "'> <img src='" + destFile.getAbsolutePath()
						+ "' height='100' width='100'/> </a>");
			} catch (IOException e) {
				e.printStackTrace();
			}

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

		espera(500);
		log("Test finalizado: " + testContext.getName());
		extent.flush();
		driver.manage().deleteAllCookies();
		driver.quit();
	}

	@AfterSuite
	public void endSession() {
		try {
			log("Ejecución de pruebas finalizada");
			td.displayTray("Ejecución de pruebas finalizada");
			//File htmlFile = new File("C:\\Users\\QA\\portalrestproject\\test-output\\report"+new Date().getTime()+".html");
			// espera(2000);
			// Desktop.getDesktop().browse(htmlFile.toURI());
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		// }
		espera();
	}

	public void espera() {
		espera(1000);
	}

	public boolean isElementPresent(By by) {
		try {
			List<WebElement> elements;
			//w.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
			elements = driver.findElements(by);
			if (elements.size() == 0)
				return false;
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public void log(String s) {
		System.out.println(s);
		Reporter.log(s + "<br>");
	}

	public void clicJS(WebElement element) {
		try {
			element.click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
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
		
		return false;
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
		clicJS(driver.findElement(By.xpath("//button[text()='" + miMonedero + "']")));
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='info']")));
		espera(500);
	}

	public void abrirMisDirecciones(String miPerfil, String misDirecciones) {
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
		clicJS(driver.findElement(By.xpath("//button[text()='" + miPerfil + "']")));
		clicJS(driver.findElement(By.xpath("//button[contains(text(),'" + misDirecciones + "')]")));
		espera(500);
	}

	public void abrirMisPedidos(@Optional("") String miPerfil, @Optional("") String misPedidos, @Optional("true") boolean fromMenu) {
		if(fromMenu) {
			clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
			espera(500);
			clicJS(driver.findElement(By.xpath("//button[text()='" + miPerfil + "']")));
			espera(500);
			clicJS(driver.findElement(By.xpath("//button[contains(text(),'" + misPedidos + "')]")));
			espera(500);
		} else {
			//Validar si estamos en la ficha principal del restaurante
			w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'rounded-buttons-wrapper')]")));
			
			if(isElementPresent(By.xpath("//div[contains(@class, 'rounded-buttons-wrapper')]"))) {
				w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//label[contains(@class, 'main-text')  and text()='" + misPedidos + "']//ancestor::app-circle-progress-button")));
				if(isElementPresent(By.xpath("//label[contains(@class, 'main-text')  and text()='" + misPedidos + "']//ancestor::app-circle-progress-button"))) {
					clicJS(driver.findElement(By.xpath("//label[contains(@class, 'main-text')  and text()='" + misPedidos + "']//ancestor::app-circle-progress-button")));
					espera(500);
				} else {
					log("No hay el botón Mis pedidos en la ficha principal del restaurante.");
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
		clicJS(driver.findElement(By.xpath("//button[contains(text(),'" + personal + "')]")));
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

	public void espera(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isNullOrEmpty(String variable) {
	   if(StringUtils.isNullOrEmpty(variable))
		   return true;
	   else
		   return false;
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
}
