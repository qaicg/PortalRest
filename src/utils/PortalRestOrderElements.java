package utils;

import java.util.List;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Optional;

import graphql.Assert;

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
	

}
