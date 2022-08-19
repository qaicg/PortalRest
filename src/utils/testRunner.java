package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.TestNG;
import org.testng.annotations.BeforeSuite;
import org.testng.collections.Lists;

import AbrePortalRest.AbrirUrl;

public class testRunner {

	public static void main(String[] args) {
		
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\Ventas.xml"); // SE AÑADEN FICHEROS XML DE TESTS																					
		suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\Clientes.xml");
		Data.getInstance().setModoSinVentana(true); // EJECUTA EL TEST SIN VENTANAS DE NAVEGADOR, MODO SILENCIOSO. 
		
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
		File htmlFile = new File("C:\\Users\\QA\\portalrestproject\\test-output\\report.html");
		try {
			Desktop.getDesktop().browse(htmlFile.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}