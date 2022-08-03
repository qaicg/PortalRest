package utils;

import java.time.Duration;
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
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
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

public class TestBase {
	
	protected static WebDriver driver ; 
	protected DatabaseConnection databaseConnection = new DatabaseConnection();;
	
	protected static HashMap<String,String> biblioteca;
	ExtentReports extent;
	protected static WebDriverWait w ;
	Actions actions;
	
	@BeforeClass
	public void configure() {
		 
		 databaseConnection.ENTORNODEFINIDO=DatabaseConnection.ENTORNOTEST;
	}

	@BeforeSuite
	public void initialize() {
		 System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
		  ChromeOptions options = new ChromeOptions();	 
		  String pathprofile = "C:\\Users\\QA\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 7\\Default";
		  options.addArguments("user-data-dir=" + pathprofile);
		  options.addArguments("chrome.switches", "--disable-extensions");
		  options.addArguments("--start-maximized");
		  options.addArguments("profile-directory=Default");
		  options.addArguments("--disable-geolocation");
		  //options.addArguments("--headless");
		  //options.addArguments("--disable-gpu");//ESTOS PARAMETROS EJECUTAN EL NAVEGADOR SIN PANTALLA
				  
		  driver = new ChromeDriver(options); 
		  driver.manage().timeouts().setScriptTimeout(10,TimeUnit.SECONDS);
		  w = new WebDriverWait(TestBase.driver,Duration.ofSeconds(10));
		  actions = new Actions(driver);
		  
		  /*ExtentReports extent = new ExtentReports();
		  ExtentSparkReporter spark = new ExtentSparkReporter("target/Spark.html");
		  extent.attachReporter(spark);
		  extent.createTest("MyFirstTest")
		    .log(Status.PASS, "This is a logging event for MyFirstTest, and it passed!");
		  extent.flush();
		  https://www.extentreports.com/docs/versions/5/java/index.html*/ 
	}
	
	@AfterMethod
	@Parameters({ "expectedTitle"})
	  public void verifyHomepageTitle(String expectedTitle) {
		  espera();
	      String actualTitle = driver.getTitle();
	      Assert.assertEquals(actualTitle, expectedTitle);
	  }

	 @AfterSuite
	  public void endSession() {
		  System.out.println("Finalizando sesión");
		  espera();
		  //extent.flush();
		  driver.manage().deleteAllCookies();
		  driver.quit();
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
	
	  
	  public void clicAction(WebElement element) {  
			espera(500);
			actions.moveToElement(element).click().build().perform();
			espera(500);
	  }
	  
	  public void espera(long time) {
		  try {Thread.sleep(time);} catch (InterruptedException e) {e.printStackTrace();}  
	  }
}
