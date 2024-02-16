package menu;

import java.lang.reflect.Field;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.Cookie;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.idealized.Javascript;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import PaymentMethod.PaymentCard;
import enums.PaymentGateways;
import graphql.Assert;
import utils.CookiesPortalRest;
import utils.Data;
import utils.TestBase;
import utils.cookie;

public class MenuValidation extends TestBase{
	
	@Test(description = "Comprobar que se muestre, en el menú hamburgesa, la opción En mesa con numero")
	@Parameters({"mesa"})
	public void validacionMenuEnMesa(String numeroMesa) {
		AbrirMenu.openMenu();
		
		// Test PaymentGateways
		//PaymentCard.setPaymentName("Redsys");
		PaymentGateways paymentRedSys = PaymentGateways.REDSYS;
		log("Test payment  " + paymentRedSys.getPayment().getCardNumber());
		
		log("Test payment  " + paymentRedSys.getPayment().getPaymentName());
		//Fin test PaymentGateways
		
		//validar que se muestre el submenu en mesa con el numero
		if(!isElementPresent(By.xpath(AbrirMenu.menuEnMesa))) {
			log("Error: no homos encontrado el submenu en mesa");
			Data.getInstance().getExtentTest().fail("Error: no homos encontrado el submenu en mesa");
			Assert.assertTrue(false);
		}
		
		WebElement menuEnMesa = driver.findElement(By.xpath(AbrirMenu.menuEnMesa));
		
		//Validar el literal del submenu en mesa
		if(!menuEnMesa.getAttribute("innerText").equalsIgnoreCase(AbrirMenu.literalMenuEnMesa + numeroMesa)) {
			log("Error en la validación del literal y del numero de mesa, encontrado ");
			Data.getInstance().getExtentTest().fail("Error en la validación del literal y del numero de mesa, encontrado ");
			Assert.assertTrue(false);
		}
		
		// Validar la línea gris flojito debajo del literal en mesa
		
		String valueBorderBottom = menuEnMesa.getCssValue("border-bottom"); // 1px solid rgb(89, 108, 132)
		
		if(!valueBorderBottom.equalsIgnoreCase(AbrirMenu.lineaGrisFlojito)) {
			log("Error en la validación de la linea gris flojito seguido del submenu en Mesa ");
			Data.getInstance().getExtentTest().fail("Error en la validación de la linea gris flojito seguido del submenu en Mesa ");
			Assert.assertTrue(false);
		}
		
		
		try {
			String valueCookies = CookiesPortalRest.LOGIN.getLogin(); //cookie.getCookie("portal-rest-web-login");
			
			String decodeCookie  =  cookie.decodeCookieBase64(valueCookies);
			String decodeCookieFormaJason = (String) decodeCookie.subSequence(StringUtils.indexOf(decodeCookie, "{"), StringUtils.indexOf(decodeCookie, "}")+1);
						
			String[] decCookies = decodeCookie.subSequence(StringUtils.indexOf(decodeCookie, "{")+1, StringUtils.indexOf(decodeCookie, "}")).toString().split(",");
			
			int vueltas = 1;
			boolean checkMesa = false;
			for(String keyValue: decCookies) {
				if(keyValue.split(":")[0].replace("\"", "").equalsIgnoreCase("tbl") && (String.valueOf(keyValue.split(":")[1]).equals(numeroMesa))) {
					log("Hemos encontrado la mesa en los parametros del link");
					checkMesa = true;
					break;
				}
				
				if(vueltas == decCookies.length && !checkMesa ) {
					log("Error: No hemos encontrado la mesa en los parametros del link");
					Data.getInstance().getExtentTest().fail("Error: No hemos encontrado la mesa en los parametros del link");
					org.testng.Assert.assertTrue(false, "Error: No hemos encontrado la mesa en los parametros del link");
				}
				vueltas ++;
			}
						
		} catch (Exception e) {
			log("error e => " +e.getMessage());
			Data.getInstance().getExtentTest().fail("error e => " +e.getMessage());
			org.testng.Assert.assertTrue(false, "error e => " +e.getMessage());
		}
		
	}
	
}
