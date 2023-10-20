package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.Reporter;
import org.testng.TestNG;

public class testRunner {
	 //POR DEFECTO EJECUTA LOS TESTS DE LA VERSIÓN MASTER DE PORTALREST (QA09)
	//VARIABLES DE SELECCIÓN DE ENTORNO DE EJECUCIÓN//

	/*Cambio en las versiones: 19/09/2023
	 * VERISON ESTABLE --> QA09
	 * VERSION MASTER --> QA08 -- > QA10
	 * 
	 */
	
													//-------------------**********DEFINIDIR CONSTANTES***********--------------------------//
														static boolean RUNALLTESTS = true; //RUNALLTESTS: true --> VERSION ESTABLE (QA08) y VERSION MASTER (QA09)
														
														static boolean SINVENTANA = false;
														
														static boolean ENTORNOTEST = true; //ENTORNOTEST: false --> VERSION ESTABLE (QA09); true --> VERSION MASTER (QA10)
														
														static boolean BETATEST = false; //BETATEST: true -->Tests en CloudLicenceBeta, false --> Tests en CloudLicence
												   //-------------------******************************************--------------------------//
	private static List<String> suitefiles = new ArrayList<String>();
	
	public static void main(String[] args) {
		
		TestNG runner = new TestNG();
		
		if(BETATEST) {
			log("EJECUTA LOS TESTS EN LA VERSION BETA MASTER (QA09)");
			Data.getInstance().setRunTestOnCloudLicenseBeta(BETATEST);
			RUNALLTESTS = false;
			ENTORNOTEST = true;
		}
				
		if(RUNALLTESTS) { // EJECUTA LOS TESTS EN LAS DOS VERSIONES, PRIMERO EN LA VERSION MASTER (QA09) Y DESPUES EN LA VERSION ESTABLE (QA10)
			log("EJECUTA LOS TESTS EN LAS DOS VERSIONES, PRIMERO EN LA VERSION MASTER (QA09) Y DESPUES EN LA VERSION ESTABLE (QA10)");
			Data.getInstance().setRunAllTests(RUNALLTESTS);
			
			// EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA09)
			log("EJECUTA LOS TESTS VENTAS EN LA VERSION ESTABLE (QA09) del servidor CloudQuality03");
			
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\Clientes.xml");
			
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\Ventas.xml");
			
			//Test Ventas con propinas: Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality03
			addTestsVentasPropinas(true, false);
			
			//Test Booking 
			log("EJECUTA LOS Tests Booking en servidor cloudQuality03");
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Reservas\\Reservas.xml");
			
			
			// EJECUTA LOS TESTS EN LA VERSION MASTER (QA10)
			log("EJECUTA LOS TESTS EN LA VERSION MASTER (QA10)");
			
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\ClientesEstable.xml");
			
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\VentasEstable.xml");	
			
			//Test Ventas con propinas: Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality04
			addTestsVentasPropinas(false, true);
		
			//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Reservas\\ReservasEstable.xml");
			
		} else {
			Data.getInstance().setEntornoTest(ENTORNOTEST);
			
			if(ENTORNOTEST) {	
				// EJECUTA LOS TESTS EN LA VERSION MASTER (QA09)
				log("EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA09)");
				
				//Test clientes
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\Clientes.xml");
				
				//Test ventas
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\Ventas.xml");
				
				//Test Ventas con propinas: Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality03
				addTestsVentasPropinas(true, false);
				
				//Test Booking
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Reservas\\Reservas.xml");
			} else {
				// EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA10)
				log("EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA10)");
								
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\ClientesEstable.xml");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\VentasEstable.xml");
				
				//Test Ventas con propinas: Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality04
				addTestsVentasPropinas(false, true);
				
				//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Reservas\\ReservasEstable.xml");
			}
		}
		
		Data.getInstance().setModoSinVentana(SINVENTANA); // EJECUTA EL TEST SIN VENTANAS DE NAVEGADOR, MODO SILENCIOSO. 
		
		if(args.length != 0){
			 String firstParam = args[0]; 
			 System.out.println("Parametro dectectado: " + firstParam);
			 if(firstParam.equalsIgnoreCase("true")) {
				 Data.getInstance().setModoSinVentana(true); // EJECUTA EL TEST SIN VENTANAS DE NAVEGADOR, MODO SILENCIOSO.
			 }
			 else {
				 Data.getInstance().setModoSinVentana(false); // EJECUTA EL TEST CON VENTANAS DE NAVEGADOR
			 }
		}
		
		runner.setTestSuites(suitefiles);
		runner.run();
		abrirReport(); // MUESTRA EL REPORT CON LOS RESULTADOS AL FINALIZAR.
	}
	
	private static void abrirReport() {
		File oldFilePath = new File("C:\\Users\\QA\\portalrestproject\\test-output\\report.html");
		File filePath = new File("C:\\Users\\QA\\portalrestproject\\test-output\\"+ new Date().getTime());
		filePath.mkdir();
		File htmlFile = new File(filePath+"\\report.html");
		oldFilePath.renameTo(htmlFile);
		
		try {
			Desktop.getDesktop().browse(htmlFile.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void log(String s) {
		System.out.println(s);
		Reporter.log(s + "<br>");	
	}
	
	private static void addTestsVentasPropinas(boolean ServidorCloudQuality03, boolean ServidorCloudQuality04) {
		
		if(ServidorCloudQuality03) { // Servidor CloudQuality03 --> 
			log("Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality03");
			//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\VentasConPropinas\\Propinas.xml");
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\VentasConPropinas\\PropinasCloudQuality03.xml");
		}
		
		if(ServidorCloudQuality04) {
			//TODO: implementar los tests de propinas en Servidor de producción
			log("Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality04");
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\VentasConPropinas\\PropinasCloudQuality04.xml"); //Servidor Estable --> Produccion
		}
	}
}