package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Optional;

import graphql.Assert;
import pedido.Formato;

public class PortalRestOrderElements extends TestBase{
	
	public static class PedidoDiaHora {		
		
		public static final String horaDisponible = "//mat-icon[contains(text(), 'menu')]";
		public static final String diaSeleccionado = "//mat-calendar/div/mat-month-view/table/tbody/tr/td[contains(@class, 'mat-focus-indicator')]/div";
		
		//los dias del mes
		public static final String monthDays = "//mat-calendar/div/mat-month-view/table/tbody/tr/td[contains(@class, 'mat-focus-indicator')]";
		
		//devolve el dia de hoy
		public static final String toDayhoy = "//mat-calendar/div/mat-month-view/table/tbody/tr/td[contains(@class, 'mat-focus-indicator mat-calendar-body-active')]";
		
		public static final String diaNoDisponibleDelMes = "//mat-calendar/div/mat-month-view/table/tbody/tr/td[contains(@class, 'mat-focus-indicator mat-calendar-body-disabled')]";
		
		//Hora
		public static final String horaWrapper = "//div[contains(@class, 'shop-turn-button-wrapper')]";
		
		public static final String horasDisponible = "//div[contains(@class, 'shop-turn-button-wrapper')]/button[contains(@class, 'shop-turn-button')]";
		
		//Return days of month
		public static final List<WebElement> getMonthDays() {
			waitUntilPresence(monthDays);
			List<WebElement> listaDiaDisponibleDelMes= driver.findElements(By.xpath(monthDays));
			
			logStatic("Número de dias disponible en mes: " +  listaDiaDisponibleDelMes.size());
			
			return listaDiaDisponibleDelMes;
			
		}
		
		//Devolve el dia de hoy
		public static final WebElement getToDayHoy() {
			waitUntilPresence(toDayhoy);
			return driver.findElement(By.xpath(toDayhoy));
		}
		
		//Devuelve un dia seleccionado
		public static final WebElement selectDay(String selectDay, @Optional("true") boolean resultadoEsperado) {
			waitUntilPresence(monthDays);
			List<WebElement> listDays = getMonthDays();
			WebElement day = null;
			boolean elementDayfinded = false;
			
			if(Objects.isNull(selectDay)) {
				day = listDays.get(0);
				elementDayfinded = true;
			}
			
			else {
				for(int i=0; i<= listDays.size(); i++) {
					if(listDays.get(i).getAttribute("innerText").contentEquals(selectDay)) {
						day = listDays.get(i);
						elementDayfinded = true;
						break;
						
					}
					
				}
				
			}
			
			if(!elementDayfinded && resultadoEsperado) {
				logStatic("Error: No se ha podido encontrar la fecha a seleccionar para el pedido");
				Assert.assertTrue(false);
			}
			
			return day;
		}
		
		//Devolver los dias habilitados o deshabilitados del mes
		@SuppressWarnings("null")
		public static final List<WebElement> getMonthDaysEnabledDisabled(@Optional("true") boolean enabled){
			List<WebElement> listDaysEnabled = null;
			List<WebElement> listDays = getMonthDays();
			
			//Enabled
			if(enabled && listDays.size() > 0) {
				listDays.forEach(day -> {
					if(day.getAttribute("aria-disabled") == null) {
						listDaysEnabled.add(day);
					}
					
					
				});
				
				logStatic("Numero de dias habilitados al hacer pedido --> " + listDaysEnabled.size());
			}
			
			//Disabled
			if(!enabled && listDays.size() > 0) {
				listDays.forEach(day -> {
					if(day.getAttribute("aria-disabled") != null && day.getAttribute("aria-disabled").contains("true")) {
						listDaysEnabled.add(day);
					}
					
				});
				
				logStatic("Numero de dias deshabilitados al hacer pedido --> " + listDaysEnabled.size());
			}
						
			return listDaysEnabled;
			
		}
		
		//Devuelve horio disponible para pedidos
		public static final List<WebElement> getHoraDisponible(){
			List<WebElement> listaHoraDisponible = driver.findElements(By.xpath(horasDisponible));
			return listaHoraDisponible;
		}
		
		//devuelve hora seleccionada para pedido
		public static final WebElement selectHoraPedido(String horaParaPedido, @Optional("true") boolean resultadoEsperado) {
			waitUntilPresence(horaWrapper);
			waitUntilPresence(horasDisponible);
			List<WebElement> listHoras = getHoraDisponible();
			WebElement hora = null;
			boolean elementHorafinded = false;
			
			boolean elementFinded = false;
			
			if(Objects.nonNull(hora)) {
				
				if (Objects.isNull(horaParaPedido)) {
					hora = listHoras.get(0);
					elementFinded = true;
				}
				else {
				
					for(int i=0; i<= listHoras.size(); i++) {
						if((listHoras.get(i)).getAttribute("innerText").contentEquals(horaParaPedido)) {
							hora = listHoras.get(i);
							elementHorafinded = true;
							break;
							
						}
						
					}
				}
				
			}
			else {
				logStatic("Error: No hay aparece hora para el dia seleccionado!");
				Assert.assertTrue(false);
			}
			
			if(!elementHorafinded && resultadoEsperado) {
				logStatic("Error: No se ha podido encontrar la fecha a seleccionar para el pedido");
				Assert.assertTrue(false);
			}
			
			return hora;
		}
		
	}
	
	public class PedidoMasTarde {
		
	}
	
	public class MailSac {
		public static final String email = "portalrestcliente@mailsac.com";
		public static final String passwordMailSac = "k_kJafj5UAsHjv3pY68wNCdpvhXbw7RMvUtZolnMQ1dfa";
		public static final String passwordPortalRest = ".1234abcd";
	}
	
	public static class Formatos {
		
		public static final String arrayFormatos = "//div[contains(@class,'format-element-wrapper')]";
		
		public static final List<WebElement> webElmtArrayFormatos = driver.findElements(By.xpath(arrayFormatos)); //m
		
		public static final String arrayNombre = "//div[contains(@class,'format-element-wrapper')]//div[contains(@class, 'format-element-name')]";
		
		public static final List<WebElement> webElmtArrayNombre = driver.findElements(By.xpath(arrayNombre)); //m
		
		public static final String arrayPrecio = "//div[contains(@class,'format-element-wrapper')]//div[contains(@class, 'format-element-price')]";
		
		public static final List<WebElement> webElmtArrayPrice = driver.findElements(By.xpath(arrayPrecio));
		
		public static final String AddToOrderButton = "//button[contains(@class,'basket-button')]";
		
		public List<Formato> formatList = new ArrayList<Formato>();		
		
		public Formatos() {
			super();
			// TODO Auto-generated constructor stub
			setFormatList();
		}

		public void setFormatList() {
			Formato formato;
			String nameFormat;
			String priceFormat;
			
			waitUntilPresence(arrayFormatos);

			waitUntilPresence(arrayNombre);
			
			waitUntilPresence(arrayPrecio);
			
			if(webElmtArrayFormatos.size() > 0) {
				
				for(int i = 0; i < webElmtArrayFormatos.size(); i++) {
					
					nameFormat = driver.findElements(By.xpath(arrayNombre)).get(i).getText();
					//Utils.logStatic("nombre del formato: " + nameFormat);
					
					priceFormat = driver.findElements(By.xpath(arrayPrecio)).get(i).getText();
					//Utils.logStatic("precio del formato: " + priceFormat);
					
					formato = new Formato(nameFormat, priceFormat);
					//formato = new Formato(webElmtArrayNombre.get(i).getText(),  webElmtArrayPrice.get(i).getText());
					//formato = new Formato(webElmtArrayNombre.get(i).getText(),  webElmtArrayPrice.get(i).getText());
					this.formatList.add(formato);
				}
			}
			
		}
		
		public List<Formato> getFormatList() {
			return this.formatList;
		}
		
	}



}
