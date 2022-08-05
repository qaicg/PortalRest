package utils;

import java.awt.AWTException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentAventReporter;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;

import Cadenas.Es;
import Objects.ProductItem;
import Windows.TrayIconDemo;

public class TestBase {

	protected static WebDriver driver ; 
	protected DatabaseConnection databaseConnection = new DatabaseConnection();;
	TrayIconDemo td = new TrayIconDemo();
	protected static HashMap<String,String> biblioteca;
	ExtentReports extent;
	protected static WebDriverWait w,w2 ;
	Actions actions;
	String pathprofile ;
	ChromeOptions options;
	
	@BeforeClass
	public void configure() {

		databaseConnection.ENTORNODEFINIDO=DatabaseConnection.ENTORNOTEST;
	}

	@BeforeSuite
	public void initialize() {

		
		//options.addArguments("--headless");
		//options.addArguments("--disable-gpu");//ESTOS PARAMETROS EJECUTAN EL NAVEGADOR SIN PANTALLA

		/*ExtentReports extent = new ExtentReports();
		  ExtentSparkReporter spark = new ExtentSparkReporter("target/Spark.html");
		  extent.attachReporter(spark);
		  extent.createTest("MyFirstTest")
		    .log(Status.PASS, "This is a logging event for MyFirstTest, and it passed!");
		  extent.flush();
		  https://www.extentreports.com/docs/versions/5/java/index.html*/ 
	}

	

	@BeforeTest 
	public void beforeTest(final ITestContext testContext){

		System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
		options = new ChromeOptions();	 
		pathprofile = "C:\\Users\\QA\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 7\\Default";
		options.addArguments("user-data-dir=" + pathprofile);
		options.addArguments("chrome.switches", "--disable-extensions");
		options.addArguments("--start-maximized");
		options.addArguments("profile-directory=Default");
		options.addArguments("--disable-geolocation");
		//options.addArguments("--headless");//ESTOS PARAMETROS EJECUTAN EL NAVEGADOR SIN PANTALLA
		//options.addArguments("--disable-gpu");//ESTOS PARAMETROS EJECUTAN EL NAVEGADOR SIN PANTALLA
		driver = new ChromeDriver(options); 
		driver.manage().timeouts().setScriptTimeout(10,TimeUnit.SECONDS);
		w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
		w2 = new WebDriverWait(TestBase.driver,Duration.ofSeconds(60));	 
		actions = new Actions(driver);
		driver.manage().deleteAllCookies();

		try {
			td.displayTray("Iniciando test " + testContext.getName());
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterMethod
	@Parameters({ "expectedTitle"})
	public void verifyHomepageTitle(String expectedTitle) {
		espera();
		String actualTitle = driver.getTitle();
		Assert.assertEquals(actualTitle, expectedTitle);
	}

	@AfterTest
	public void afterTest() {
		espera(500);
		driver.manage().deleteAllCookies();
		driver.quit();
	}


	@AfterSuite
	public void endSession() {
		try {
			log("Ejecución de pruebas finalizada");
			td.displayTray("Ejecución de tests finalizada");		
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		espera();
	}

	public void espera() {
		espera(1000);
	}

	public boolean isElementPresent(By by){
		try{
			List<WebElement> elements;
			elements = driver.findElements(by);
			if(elements.size()==0)return false;
			return true;
		}
		catch(Exception exception){
			return false;
		}
	}

	public void log(String s) {
		System.out.println(s);
		Reporter.log(s+"<br>");
	}

	public void clicJS(WebElement element) {
		try {
			element.click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
		}
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
		clicJS(driver.findElement(By.xpath("//button[text()='"+miMonedero+"']")));
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='info']")));
		espera(500);
	}
	
	public void abrirMisDirecciones(String miPerfil, String misDirecciones) {
		clicJS(driver.findElement(By.xpath("//mat-icon[text()='menu']")));
		clicJS(driver.findElement(By.xpath("//button[text()='"+miPerfil+"']")));
		clicJS(driver.findElement(By.xpath("//button[contains(text(),'"+misDirecciones+"')]")));	
		espera(500);
	}

	public void clicAction(WebElement element) {  
		espera(500);
		actions.moveToElement(element).click().build().perform();
		espera(500);
	}

	public List<Integer> stringArrayToInteger(String values){

		String[] convertedRankArray = values.split(",");
		List<Integer> convertedRankList = new ArrayList<Integer>();
		for (String number : convertedRankArray) {
			convertedRankList.add(Integer.parseInt(number.trim()));
		}		
		return convertedRankList;	
	}

	
	public void espera(long time) {
		try {Thread.sleep(time);} catch (InterruptedException e) {e.printStackTrace();}  
	}
}
