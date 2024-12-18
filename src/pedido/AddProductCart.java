package pedido;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utils.PortalRestOrderElements;
import utils.PortalRestOrderElements.RestaurantMenuPage;
import utils.TestBase;
import utils.Utils;

public class AddProductCart extends TestBase {
	
	public int scrollCount = 0;
	
	public String[] orderProductList;
	

	public List<WebElement> productList = Arrays.asList(); //= PortalRestOrderElements.RestaurantMenuPage.productList;;
	
	public int getScrollCount() {
		return scrollCount;
	}

	public void setScrollCount(int scrollCount) {
		this.scrollCount = scrollCount;
	}
	
	public List<WebElement> getProductList() {
		return productList;
	}
	
	public void setProductList(List<WebElement> productList) {
		this.productList = productList;
	}
	
	public void setProductList() {
		RestaurantMenuPage cartPage = new RestaurantMenuPage();
		cartPage.setProductList();
		this.productList = cartPage.getProductList();
	}
	
	@Test (description="Añadir productos en el carrito del pedido" , priority=1)
	@Parameters({"productos","totalEsperado","opcionesMenu","unidades", "abrirFichaProducto", "formatos"})
	public void AddProductCart (String products) {
		//"Kirin TA,Inedit Damm,Oyako Don,Fanta Taronja 30 ml" 
		orderProductList = products.split(",");	
		this.setProductList();
		
		scrollByProduct("Sake Kuramoto");
		
//		for(int i = 0; i <= orderProductList.length; i++) {
//			
//		}
	}

	@Test
	public void browseMenuCartList() {
		espera(3000);
		scrollByCartPage();
	}
	
	@Test
	@Parameters({"productos"})
	public void seachProducto(String products) {
		//"Kirin TA,Inedit Damm,Oyako Don,Fanta Taronja 30 ml" 
		orderProductList = products.split(",");	
		
		for(int i = 0; i <= this.orderProductList.length; i++) {
			
			try {
				WebElement elem  =	scrollByProduct(orderProductList[i]);
				if(!Utils.isNullOrEmpty(elem.getText())) {
					log("Producto " +orderProductList[i] + " encontrado");
				}
			} catch (Exception e) {
				// TODO: handle exception
				log("Producto " +orderProductList[i] + " no ha sido encontrado");
			}
			
		}		
		
	}
	
	
	public WebElement scrollByProduct(String productName) {
		//This will scroll the web page till end.
		espera(2000);
		String sfindElement = "//div[contains(@class,'dish-name line-clamp-2') and contains(text(), '" + productName +"')]";
		String sFooter = PortalRestOrderElements.RestaurantMenuPage.sCartPageFooter;
		
		//RestaurantMenuPage oCartPage = new RestaurantMenuPage();
		
		try {
			
			if(isElementPresent(By.xpath(sfindElement)) && 
					(driver.findElement(By.xpath(sfindElement)).isDisplayed() && driver.findElement(By.xpath(sfindElement)).isEnabled())
					) { //check element exists
				 log("Hemos encontrado el producto " + productName +" en la carta del restaurante");
				 log("Hemos hecho " + getScrollCount() + " Scroll para encontralo");
				 
				// clicJS(driver.findElement(By.xpath(sfindElement)));
				// PortalRestOrderElements.RestaurantMenuPage.
				
			    // oCartPage.addFromProductSheet(driver.findElement(By.xpath(sfindElement)), productName);
				 
				 //espera(500);
				 
				 return driver.findElement(By.xpath(sfindElement));
				 
			} 
			else {
				
				if(getProductList().size() > 0) {
					int numElement = getProductList().size() - 1;
					setScrollCount(getScrollCount()+1);
					scrollPage(getProductList().get(numElement));
				}
				
				if(isElementPresent(By.xpath(sFooter))) { // Verificar si estamos al final de la página
					
					if(isElementPresent(By.xpath(sfindElement)) && 
							(driver.findElement(By.xpath(sfindElement)).isDisplayed() && driver.findElement(By.xpath(sfindElement)).isEnabled())
							) { // Verificar si tenemos el producto
						
						log("Hemos encontrado el producto " + productName +" en la carta del restaurante");
						log("Hemos hecho " + getScrollCount() + " Scroll para encontralo al final de la página");
						
						// clicJS(driver.findElement(By.xpath(sfindElement)));
						//oCartPage.addFromProductSheet(driver.findElement(By.xpath(sfindElement)), productName);
						//espera(500);
						
						return driver.findElement(By.xpath(sfindElement));
					}
					
					log("Hemos hecho " + getScrollCount() + " Scroll en total ");
					log("No hemo encontrado el producto hasta al final de la página");
					
					return null;
					
					
				} else {
					setProductList();
					scrollByProduct(productName);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			log("No se ha podido efectuar el scroll de la pagina");
			//setProductList();
		}
		
		return null;
	}
	
	//@Test
	public void scrollByCartPage() {
		//This will scroll the web page till end.
		espera(2000);
		
		String sFooter = PortalRestOrderElements.RestaurantMenuPage.sCartPageFooter;
		
		try {
			
			if(isElementPresent(By.xpath(sFooter))) { //check element exists
				 log("Hemos encontrado el ulitmo producto de la carta del restaurante");
				 log("Hemos hecho " + getScrollCount() + " Scroll para encontra el elemento");
				 
				 //print innerText
				 String elementText = getProductList().get(6).getAttribute("innerText").split("\n")[0];
				 log("innerText " + elementText);
				 
				 return;
			} else {
				
				if(getProductList().size() > 0) {
					int numElement = getProductList().size() - 1;
					setScrollCount(getScrollCount()+1);
					scrollPage(getProductList().get(numElement));
				}
				
				if(isElementPresent(By.xpath(sFooter))) { // check element exists
					log("Estamos al final de la página");
					log("Hemos hecho " + getScrollCount() + " Scroll en total ");
					 String elementText = getProductList().get(6).getAttribute("innerText").split("\n")[0];
					 log("innerText " + elementText);
					 
				} else {
					setProductList();
					scrollByCartPage();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			log("No se ha podido efectuar el scroll de la pagina");
			setProductList();
		}
	}
	
	@Test
	public void selectFamily() {
		w2.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[contains(@class,'familyItem')]")));	
		List<WebElement> familias;	
		//ArrayList<ProductItem> productosAddeds = new ArrayList<ProductItem> ();
		
		familias  = driver.findElements(By.xpath("//li[contains(@class,'familyItem')]"));
		//ENTRO EN LA PRIMERA FAMILIA
		if (familias.size()>0) {
			WebElement nombreFamilia = familias.get(0).findElement(By.xpath("//div[contains(@class,'familyName')]"));
			log("Entro en primera familia " + nombreFamilia.getAttribute("innerText"));
			espera(1000);
			clicJS(nombreFamilia);
			
		}
	}

	
}
