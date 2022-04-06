package AbrePortalRestPackage;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

public class TestBase {
	
	static WebDriver driver ; 
	
	@BeforeSuite
	public void initialize() {
		  System.setProperty("webdriver.chrome.driver", "C:\\driver\\chromedriver.exe");
		  driver = new ChromeDriver(); 
		  driver.manage().timeouts().setScriptTimeout(10,TimeUnit.SECONDS);
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
		  driver.quit();
	  }
	  
	
	  public void espera() {
		  espera(1000);
	  }
	  
	  public void espera(long time) {
		  try {Thread.sleep(time);} catch (InterruptedException e) {e.printStackTrace();}  
	  }
}
