package utils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentAventReporter;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;





public class TestBase {
	
	protected static WebDriver driver ; 
	protected static HashMap<String,String> biblioteca;
	ExtentReports extent;


	
	@BeforeSuite
	public void initialize() {
		  System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
		  driver = new ChromeDriver(); 
		  driver.manage().timeouts().setScriptTimeout(10,TimeUnit.SECONDS);
		  
		  ExtentAventReporter avent = new ExtentAventReporter("/report/");

		  extent = new ExtentReports();
		  extent.attachReporter(avent);
		  extent.createTest("MyFirstTest", "Test Description").pass("details");
		 	
	}
	
	@AfterMethod
	@Parameters({ "expectedTitle"})
	  public void verifyHomepageTitle(String expectedTitle) {
		  espera();
	      String actualTitle = driver.getTitle();
	      Assert.assertEquals(actualTitle, expectedTitle);
	      Reporter.log("Titulo de la página actual: " + actualTitle);
	  }
	
	 @AfterSuite
	  public void endSession() {
		  espera();
		  extent.flush();
		  driver.quit();
	  }
	 
	
	  public void espera() {
		  espera(1000);
	  }
	  
	  public void espera(long time) {
		  try {Thread.sleep(time);} catch (InterruptedException e) {e.printStackTrace();}  
	  }
}
