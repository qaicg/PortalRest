package menu;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utils.TestBase;

public class AbrirMenu extends TestBase{
	
	public static String  menuEnMesa = "//div[contains(@class, 'header-tbl-wrapper')]";
	public static String literalMenuEnMesa = "En la mesa";
	public static String lineaGrisFlojito = "1px solid rgb(89, 108, 132)"; //línea gris flojito debajo del literal del submenu en mesa
	
	public static void openMenu() {
		WebElement element = driver.findElement(By.xpath("//mat-icon[text()='menu']"));
		
		try {
			element.click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
		}
		
		waitFor(500);
	} 
	
	public static void waitFor(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
