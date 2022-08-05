package Clientes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.TestBase;

public class CargarSaldoIcgCloud extends TestBase {
	
@Test(description="Dado un cliente logueado con ICGCloud carga saldo en su tarjeta de fidelización", priority=1)
@Parameters({"importeCarga","miMonederoString","cargarSaldoString"})
  public void cargarSaldo(double importe, String miMonederoString, String cargarSaldoString) {
	abrirMiMonedero(miMonederoString);
	WebElement cargarSaldoButton = driver.findElement(By.xpath("//button[contains(@class,'btn btn-confirm') and text()='"+cargarSaldoString+"']"));
	cargarSaldoButton.click();
	w.until(ExpectedConditions.presenceOfElementLocated (By.xpath("//div[contains(@class,'dialog-content')]")));
	driver.findElement(By.xpath("//input[@type='checkbox']")).click(); //MARCAMOS CHECKBOX PARA ACEPTAR PAGO
	
	
  }
}
