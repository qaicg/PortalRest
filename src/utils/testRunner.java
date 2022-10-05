package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.testng.TestNG;

public class testRunner {
	 //POR DEFECTO EJECUTA LOS TESTS DE LA VERSIÓN MASTER DE PORTALREST (QA08)
	//VARIABLES DE SELECCIÓN DE ENTORNO DE EJECUCIÓN//
	
	//-------------------************--------------------------//
	static boolean SINVENTANA=false;
	static boolean ENTORNOTEST=true;
	//-------------------************---------------------------//

	public static void main(String[] args) {
		
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		
		if(ENTORNOTEST) {
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\Ventas.xml");																				
			//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\Clientes.xml");
		}else {
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\VentasEstable.xml"); 																				
			suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\ClientesEstable.xml");
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

}