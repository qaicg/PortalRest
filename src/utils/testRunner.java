package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.annotations.BeforeSuite;

import configuration.ConfigServer;
import configuration.ConfigServerPanel;
//import configuration.ConfigServerPanel;
import configuration.EnumServidor;
import configuration.Server;

public class testRunner {
	 //POR DEFECTO EJECUTA LOS TESTS DE LA VERSIÓN MASTER DE PORTALREST (QA09)
	//VARIABLES DE SELECCIÓN DE ENTORNO DE EJECUCIÓN//

	/*Cambio en las versiones: 19/09/2023
	 * VERISON ESTABLE --> QA09
	 * VERSION MASTER -->  QA10
	 * 
	 */
	
													//-------------------**********DEFINIDIR CONSTANTES***********--------------------------//
														static boolean RUNALLTESTS = true; //RUNALLTESTS: true --> VERSION ESTABLE (QA08) y VERSION MASTER (QA09)

														static boolean SINVENTANA = false;
														
														static boolean ENTORNOTEST = true; //ENTORNOTEST: false --> VERSION ESTABLE (QA09); true --> VERSION MASTER (QA10)
														
														static boolean BETATEST = false; //BETATEST: true -->Tests en CloudLicenceBeta, false --> Tests en CloudLicence
														
														static EnumServidor SERVERTEST = EnumServidor.QUALITY04; //Definimos el servidor de test(VERSION MASTER)
																																										
													//-------------------******************************************--------------------------//
														private static List<String> suitefiles = new ArrayList<String>();
													
														private static Server serverTest;
														
														private static Server serverProduction;

		//-------------------**********************Getters and Setters Constants*******************--------------------------//
				public static boolean isRUNALLTESTS() {
					return RUNALLTESTS;
				}
			
				public static void setRUNALLTESTS(boolean rUNALLTESTS) {
					RUNALLTESTS = rUNALLTESTS;
				}
				
				public static boolean isENTORNOTEST() {
					return ENTORNOTEST;
				}
			
				public static void setENTORNOTEST(boolean eNTORNOTEST) {
					ENTORNOTEST = eNTORNOTEST;
				}	
				
				public static EnumServidor getSERVERTEST() {
					return SERVERTEST;
				}
			
				public static void setSERVERTEST(EnumServidor sERVERTEST) {
					SERVERTEST = sERVERTEST;
				}	
				public Server getServerTest() {
					return serverTest;
				}
			
				public Server getServerProduction() {
					return serverProduction;
				}
		//-------------------**********************AND Getters and Setters Constants*******************--------------------------//
		
	public static void main(String[] args) {
		//
		//ConfigServerPanel configServer = new ConfigServerPanel();
		//configServer.main(args);
		//

		defineServerTest();
		
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
			
			getTests(true, true);
			
			
		} else {
			Data.getInstance().setEntornoTest(ENTORNOTEST);

			if(ENTORNOTEST) {
				if(serverTest.isTest()) {
					
					if(serverTest.getName().contains(EnumServidor.QUALITY03.getServerName())) {
						getTests(ENTORNOTEST, false); //Ejecuta los test del servidor cloudquality03
					}
					
					if(serverTest.getName().contains(EnumServidor.QUALITY04.getServerName())) {
						getTests(false, ENTORNOTEST); //Ejecuta los test del servidor cloudquality04
					}
				}			
				
			} else {
				// EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA10)
				log("EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA10)");
				if(serverProduction.isProduction()) {
					
					if(serverProduction.getName().contains(EnumServidor.QUALITY03.getServerName())) {
						getTests(true, ENTORNOTEST); //Ejecuta los test del servidor cloudquality03
					}
					
					if(serverProduction.getName().contains(EnumServidor.QUALITY04.getServerName())) {
						getTests(ENTORNOTEST, true); //Ejecuta los test del servidor cloudquality04
					}
				}
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
		File oldFilePath = new File("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\test-output\\report.html");
		File filePath = new File("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\test-output\\"+ new Date().getTime());
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
	
	private static void getTests(boolean cloudQuality03, boolean cloudQuality04) {
		if(cloudQuality03)
			getTestsFromCloudQuality03();
		
		if(cloudQuality04)
			getTestsFromCloudQuality04();
	}
	
	private static void getTestsFromCloudQuality03() {
		// EJECUTA LOS TESTS EN LA VERSION ESTABLE (QA09)
		log("EJECUTA LOS TESTS EN  servidor CloudQuality03");
		
		suitefiles.add("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\src\\Clientes\\ClientesCloudQuality03.xml");
		
		suitefiles.add("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\src\\Ventas\\VentasCloudQuality03.xml");
		
		//Test Ventas con propinas: Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality03
		addTestsVentasPropinas(true, false);
		
		//Test Booking 
		log("EJECUTA LOS Tests Booking en servidor cloudQuality03");
		suitefiles.add("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\src\\Reservas\\ReservasCloudQuality03.xml");
		
	}
	
	private static void getTestsFromCloudQuality04() {
		
		// EJECUTA LOS TESTS EN LA VERSION MASTER (QA10)
		log("EJECUTA LOS TESTS EN LA VERSION MASTER (QA10)");
		
		suitefiles.add("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\src\\Clientes\\ClientesCloudQuality04.xml");
		
		suitefiles.add("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\src\\Ventas\\VentasCloudQuality04.xml");	
		
		//Test Ventas con propinas: Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality04
		addTestsVentasPropinas(false, true);
		
		//Test Booking 
		log("EJECUTA LOS Tests Booking en servidor cloudQuality04");
		suitefiles.add("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\src\\Reservas\\ReservasCloudQuality04.xml");		
	}
	
	private static void addTestsVentasPropinas(boolean ServidorCloudQuality03, boolean ServidorCloudQuality04) {
		
		if(ServidorCloudQuality03) { // Servidor CloudQuality03 --> 
			log("Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality03");
			suitefiles.add("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\src\\VentasConPropinas\\PropinasCloudQuality03.xml");
		}
		
		if(ServidorCloudQuality04) {
			//TODO: implementar los tests de propinas en Servidor de producción
			log("Añadimos los Tests de Ventas con propinas en el Servidor CloudQuality04");
			suitefiles.add("C:\\Users\\"+TestBase.getCurrentUser()+"\\portalrestproject\\src\\VentasConPropinas\\PropinasCloudQuality04.xml"); //Servidor Estable --> Produccion
		}
	}
	
	//Defición del servidor de test para la configurción en el fichero config.xml
	public static void defineServerTest() {
		ConfigServer configServer = new ConfigServer();
		configServer.modifyXmlConfigServer(new Server(SERVERTEST, true, false));
		
	  	if(configServer.getServerTest().isTest()  // verifcar que tenemos el nuevo servidor de test: Cambiar de servidor de test
	    	&& !configServer.getServerTest().getName().equals(SERVERTEST.getServerName())) {
	  		configServer = new ConfigServer();
	  	}
		
	  	serverTest = configServer.getServerTest();
	  	serverProduction =  configServer.getServerProduction();
	}
	

}