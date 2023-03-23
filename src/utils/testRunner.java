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
	
																		//-------------------**********DEFINIDIR CONSTANTES***********--------------------------//
																							static boolean RUNALLTESTS = false;
																							
																							static boolean SINVENTANA = false;
																							
																							static boolean ENTORNOTEST = false;
																							
																							static boolean BETATEST = false; //CloudLicenceBeta
																	   //-------------------******************************************--------------------------//

	public static void main(String[] args) {
		
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
				
		if(RUNALLTESTS) { // EJECUTA LOS TESTS EN LAS DOS VERSIONES, PRIMERO EN LA VERSION MASTER (QA08) Y DESPUES EN LA VERSION ESTABLE (QA07)
			log("EJECUTA LOS TESTS EN LAS DOS VERSIONES, PRIMERO EN LA VERSION MASTER (QA08) Y DESPUES EN LA VERSION ESTABLE (QA07)");
			Data.getInstance().setRunAllTests(RUNALLTESTS);
			
			if(!BETATEST) {
				// EJECUTA LOS TESTS EN LA VERSION MASTER (QA09)
				log("EJECUTA LOS TESTS EN LA VERSION MASTER (QA09)");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\Clientes.xml");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\Ventas.xml");
				
				//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Reservas\\Reservas.xml");
				
				
				// EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA08)
				log("EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA08)");
				
				//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\AbrePortalRest\\AbrirPortalRest.xml");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\ClientesEstable.xml");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\VentasEstable.xml");	
			
				//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Reservas\\ReservasEstable.xml");
			}
			else {
				// EJECUTA LOS TESTS EN LA VERSION BETA MASTER (QA09)
				log("EJECUTA LOS TESTS EN LA VERSION BETA MASTER (QA09)");
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\BetaVentas.xml");
				
				// EJECUTA LOS TESTS EN LA VERSION BETA ESTABLE (QA08)
				log("EJECUTA LOS TESTS EN LA VERSION BETA ESTABLE (QA08)");
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\BetaVentasEstable.xml");	
			}
			
		} else {
			Data.getInstance().setEntornoTest(ENTORNOTEST);
			
			if(ENTORNOTEST) {	
				// EJECUTA LOS TESTS EN LA VERSION MASTER (QA09)
				log("EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA09)");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\Clientes.xml");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\Ventas.xml");
				
				//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Reservas\\Reservas.xml");
			} else {
				// EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA08)
				log("EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA08)");
				
				//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\AbrePortalRest\\AbrirPortalRest.xml");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\ClientesEstable.xml");
				
				suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\VentasEstable.xml"); 
				
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

}