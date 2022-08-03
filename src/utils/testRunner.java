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
	
	
	public static void main(String[] args) {		// Create object of TestNG Class
		TestNG runner=new TestNG();

		// Create a list of String 
		List<String> suitefiles=new ArrayList<String>();
		Class[] testClasses = {AbrirUrl.class};

		// Add xml file which you have to execute
		//suitefiles.add("C:\\Users\\QA\\portalrestproject\\src\\AbrePortalRest\\AbrirPortalRest.xml"); 
		runner.setTestClasses(testClasses);
		runner.setTestSuites(suitefiles);

		// finally execute the runner using run method
		runner.run();
		}
	

	}


	


