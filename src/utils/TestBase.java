package utils;

import java.awt.AWTException;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
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
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
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
	public void configure() {

		databaseConnection.ENTORNODEFINIDO = DatabaseConnection.ENTORNOTEST;
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
		driver.manage().deleteAllCookies();

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
			driver.manage().deleteAllCookies();
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
			File htmlFile = new File("C:\\Users\\QA\\portalrestproject\\test-output\\report.html");
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
}
