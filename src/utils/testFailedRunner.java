package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.StringReader;
//import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import java.util.logging.XMLFormatter;

//import javax.swing.text.Utilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.annotations.Optional;
import org.testng.internal.Utils;
//import org.testng.reporters.XMLUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;

//import freemarker.core.OutputFormat;
import graphql.Assert;

public class testFailedRunner {
	 //EJECUTA LOS ULTIMOS TESTS FALLADOS 
	//VARIABLES DE SELECCIÓN DE ENTORNO DE EJECUCIÓN//
	
																		//-------------------**********DEFINIDIR CONSTANTES***********--------------------------//
																							static boolean RUNTESTSFAILED = true;
																							static boolean ENTORNOTEST=true;
																							static boolean SINVENTANA=false;
																							
																							static boolean DELETE_FILE_FAILED_TESTS = true;
																							static boolean CREATE_FILE_FAILED_TESTS = true;
																	   //-------------------******************************************--------------------------//

	public static void main(String[] args) {
		//TestNG runner = new TestNG();
		//List<String> suitefiles = new ArrayList<String>();
				
		if(RUNTESTSFAILED) { // EJECUTA LOS TESTS EN LAS DOS VERSIONES, PRIMERO EN LA VERSION MASTER (QA08) Y DESPUES EN LA VERSION ESTABLE (QA07)
			Data.getInstance().setRunTestsFailed(RUNTESTSFAILED);
			//Data.getInstance().setEntornoTest(ENTORNOTEST);
			
			log("EJECUTA LOS TESTS que han fallido");
			runTestsFailed();
			
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
		}
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
	
	private static void runTestsFailed() {
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();		
		try {
						
			//testng-failed.xml
			String reportDirectoryFailedTest = new File(System.getProperty("user.dir")).getAbsolutePath() + "/test-output/old/Pruebas de ventas en PortalRest/testng.xml.html";
			File fileFailedText = new File(reportDirectoryFailedTest);
			Document doc = Jsoup.parse(fileFailedText, "UTF-8", Parser.xmlParser().toString());
			String text = doc.select("body").text();
			
			//crear nuevo fichero xml para tests fallado
			String directoryFailedTest =  new File(System.getProperty("user.dir")).getAbsoluteFile() + "/test-output/old/Pruebas de ventas en PortalRest/testngFailed.xml"; 
			
			
			
			if(CREATE_FILE_FAILED_TESTS) {
				Path fileFailedTest = Path.of(directoryFailedTest);
				
				if((Files.exists(fileFailedTest) && DELETE_FILE_FAILED_TESTS) || (Files.exists(fileFailedTest) && !DELETE_FILE_FAILED_TESTS)) {
					createFile(directoryFailedTest, DELETE_FILE_FAILED_TESTS);
					
				} else if(!Files.exists(fileFailedTest)) {
					createFile(directoryFailedTest, false);
				}
				
				Files.writeString(fileFailedTest, text);
				
				formatXMLFile(directoryFailedTest);
			}
			
			
			 // Reading the content of the file
	         //String fileFailedTest_content = Files.readString(fileFailedTest);
			//log("elemenet ****" + fileFailedTest_content);
			
			
			suitefiles.add(directoryFailedTest);
			runner.setTestSuites(suitefiles);
			runner.run();
			
			abrirReport(); // MUESTRA EL REPORT CON LOS RESULTADOS AL FINALIZAR.
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	private static void createFile(@Optional("") String fileString, @Optional("false") boolean delete) throws IOException {
	
		if(Utils.isStringNotBlank(fileString) && Utils.isStringNotEmpty(fileString)) {
			File file = new File(fileString); //initialize File object and passing path as argument  
			String pathFile = file.getCanonicalPath();
			boolean result;  
			try {
				if(file.exists() && delete) {
					Files.deleteIfExists(Path.of(pathFile));
					if(Files.notExists(Path.of(pathFile))) {
						log("File deleted: " + pathFile); 
					} else {
						log("Error: failed on delete file: " + pathFile);
						Assert.assertTrue(false);
					}
				}
				
				result = file.createNewFile();  //creates a new file  
				if(result)      // test if successfully created a new file  
				{  
					log("file created "+file.getCanonicalPath()); //returns the path string  
				}  
				else  
				{  
					log("File already exist at location: "+file.getCanonicalPath()); 						
				} 
				
			} catch (IOException e) {  
				e.printStackTrace();    //prints exception if any  
			}  
		} else {
			log("error: Falta parmetro para crear fichero.");
			Assert.assertTrue(false);
		}
	}
	
	//Formatting an XML file using Transformer
	private static void formatXMLFile(String file) throws Exception {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    org.w3c.dom.Document document =  builder.parse(new InputSource(new InputStreamReader(new FileInputStream(
	        file))));

	    Transformer xformer = TransformerFactory.newInstance().newTransformer();
	    xformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    xformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    Source source = new DOMSource((Node) document);
	    Result result = new StreamResult(new File(file));
	    xformer.transform(source, result);
	}
	

}