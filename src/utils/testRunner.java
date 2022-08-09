package utils;

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
		TestNG runner=new TestNG();
		List<String> suitefiles=new ArrayList<String>();
		// TESTS DE VENTAS //
		suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Ventas\\Ventas.xml"); 
		suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\Clientes\\Clientes.xml"); 
		 //EJECUTA EL TEST SIN VENTANAS DE NAVEGADOR, MODO SILENCIOSO.
		Data.getInstance().setModoSinVentana(true);
		runner.setTestSuites(suitefiles);
		runner.run();
		}
	}