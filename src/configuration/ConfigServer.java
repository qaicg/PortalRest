package configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


import utils.TestBase;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.NamedNodeMap;



public class ConfigServer {

	Server server;
	boolean productionServer;
	boolean testServer;
	String nameFileXmlConfig;
	
	List<Server> serverList = new ArrayList<Server>(); //retrive server form config xml
	
	private static File fileXmlConfigServer = new File("C:\\Users\\QA\\portalrestproject\\src\\configuration\\config.xml");
	
	public static File getFileXmlConfigServer() {
		return fileXmlConfigServer;
	}

	public static void setFileXmlConfigServer(File fileXmlConfigServer) {
		ConfigServer.fileXmlConfigServer = fileXmlConfigServer;
	}

	public List<Server> getServerList() {
		if(serverList.isEmpty())
			setServerList();
		
		return serverList;
	}

	public void setServerList(List<Server> serverList) {
		this.serverList = serverList;
	}
	
	//
	public void setServerList() {
		getDataConfigFromXmlFile();
	}

	ConfigServer productionEntorn;
	
	public ConfigServer() {
		super();
		setFileXmlConfigServer(getFileXmlConfigServer());
	}
	
	
	public ConfigServer(File fileXmlConfigServer) {
		super();
		setFileXmlConfigServer(fileXmlConfigServer);
	}
	
	public ConfigServer(Server server, boolean testServer, boolean productionServer) {
		super();
		this.server = server;
		this.productionServer = productionServer;
		this.testServer = testServer;
	}	
		
	public Server getServer() {
		return server;
	}
		
	public void setServer(Server server) {
		this.server = server;
	}
	
	public boolean isProductionServer() {
		return productionServer;
	}
	
	public void setProductionServer(boolean productionServer) {
		this.productionServer = productionServer;
	}
	
	public boolean isTestServer() {
		return testServer;
	}
	
	public void setTestServer(boolean testServer) {
		this.testServer = testServer;
	}
	
	public Server getServerTest() {
		Server serverTest = null;
		if(serverList.isEmpty()) 
			getDataConfigFromXmlFile();
		
		try {
			serverTest = serverList.stream().filter(srv -> srv.isTest()).findAny().get();
			
		} catch (NoSuchElementException e) {
			Assert.assertTrue(Objects.isNull(serverTest), "Error: ha sugerido el siguiente mensaje: "+ e.getMessage());
		}
		
		return serverTest;
	}
	
	public Server getServerProduction() {
		Server serverProduction = null;
		if(serverList.isEmpty()) 
			getDataConfigFromXmlFile();
		
		try {
			serverProduction = serverList.stream().filter(srv -> srv.isProduction()).findAny().get();
		} catch (NoSuchElementException e) {
			Assert.assertTrue(Objects.isNull(serverProduction), "Error: ha sugerido el siguiente mensaje: "+ e.getMessage());
		}
				
		return serverProduction;
	}
	
	private void getDataConfigFromXmlFile() {
		
	      try {
	          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	          DocumentBuilder dBuilder;

	          dBuilder = dbFactory.newDocumentBuilder();

	          Document doc = dBuilder.parse(fileXmlConfigServer);
	          doc.getDocumentElement().normalize();

	          XPath xPath =  XPathFactory.newInstance().newXPath();

	          String expression = "/configuration/server";	        
	          NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
	             doc, XPathConstants.NODESET);

	          for (int i = 0; i < nodeList.getLength(); i++) {
	        	  Server serve = new Server();
	        	  
	             Node nNode = nodeList.item(i);
	             
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                Element eElement = (Element) nNode;
	                
	                serve.setName(eElement.getAttribute("name"));
	                	                
	                serve.setTest(eElement.getElementsByTagName("test").item(0).getTextContent().contains("1") ? true : false);
	                	                
	                serve.setProduction(eElement.getElementsByTagName("production").item(0).getTextContent().contains("1") ? true : false);
	                
	                serve.setUrlConnexion((eElement.getElementsByTagName("urlConnexion").item(0).getTextContent()));
	                
	                serve.setUserName((eElement.getElementsByTagName("userName").item(0).getTextContent()));
	                              
	                serve.setPassword((eElement.getElementsByTagName("password").item(0).getTextContent()));
	                	                
	                serverList.add(serve);
	             }
	          }
	       } catch (ParserConfigurationException e) {
	          e.printStackTrace();
	       } catch (SAXException e) {
	          e.printStackTrace();
	       } catch (IOException e) {
	          e.printStackTrace();
	       } catch (XPathExpressionException e) {
	          e.printStackTrace();
	       }
		
	}
	
	public void modifyXmlConfigServer(Server serverTest) {
	      try {
	          //Validamos el servidor 
	    	  Assert.assertTrue(EnumServidor.isValidServer(serverTest), "Error: No se ha encontrado el nuevo servidor de test: " + serverTest.getName());
	    	  
	    	  if(serverTest.isTest()  // verifcar que tenemos el nuevo servidor de test
          			&& serverTest.getName().equals(getServerTest().getName())) {
	    		 TestBase.logStatic("El servidor de test no se ha cambiado !!!");
	    		 TestBase.logStatic("El servidor de test definido desde el testRunner es el mismo que el definido en el fichero de configuración config.xml actual!!!");
	    		  
	    		  return;
	    	  }
	    	  	    	  
	          DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	          DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	          Document doc = docBuilder.parse(fileXmlConfigServer);
	          
	          int numItemNode = doc.getElementsByTagName("server").getLength();
	          
	          for(int i = 0; i < numItemNode; i ++) {
	        	  System.out.println("Numero de item xml " + i);
	        	  
	        	  Node itemNode = doc.getElementsByTagName("server").item(i);
	        	  NodeList list = itemNode.getChildNodes();
	        	  
        		  NamedNodeMap attr = itemNode.getAttributes();
    	          Node nodeAttrCloudQualoity = attr.getNamedItem("name");
    	          
    	          System.out.println("Nombre del attributo " + nodeAttrCloudQualoity);
	        	  
	        	  for (int temp = 0; temp < list.getLength(); temp++) {
	        		  	        		
	        		  Node node = list.item(temp);
	        		  	    	          
		              if (node.getNodeType() == Node.ELEMENT_NODE && EnumServidor.isValidServer(serverTest)) {
		            	  Element eElement = (Element) node;
		            	  
		            	  if(serverTest.getName().equals(nodeAttrCloudQualoity.getNodeValue()) 
		            			&& serverTest.isTest()  // verifcar que tenemos el nuevo servidor de test
		            			&& !serverTest.getName().equals(getServerTest().getName())//verificar que el nuevo servidor de test es diferente del anterio
	            			  ) {
		            		  
		        	    	  //Value of server 
		        	    	  String isTest = getServerTest().isTest() ? "1" : "0";
		        	    	  String isProduction = getServerTest().isProduction() ? "1" : "0";
		            		  
		            		  if ("test".equals(eElement.getNodeName())) {
		            			  eElement.setTextContent(isTest);
		            			  TestBase.logStatic("El valor de test del nuevo sevidor " + eElement.getTextContent());
		            		  }
		            		  
		            		  if ("production".equals(eElement.getNodeName())) {
		            			  eElement.setTextContent(isProduction);
		            			  TestBase.logStatic("El valor de  production del nuevo sevidor " + eElement.getTextContent());
		            		  }
		            	  }
		            	  else if(!serverTest.getName().equals(nodeAttrCloudQualoity.getNodeValue()) 
			            			&& serverTest.isTest()  // verifcar que tenemos el nuevo servidor de test
			            			&& !serverTest.getName().equals(getServerTest().getName())//verificar que el nuevo servidor de test es diferente del anterio
			            			&& serverTest.getName().equals(getServerProduction().getName())
		            			  ) {
		            		  
		            		  //Value of server 
		        	    	  String isTest = getServerProduction().isTest() ? "1" : "0";
		        	    	  String isProduction = getServerProduction().isProduction() ? "1" : "0";
		        	    	  
		        	    	  System.out.println("Aquí estamos para cambiar la configuración del servidor de Producción  + " + eElement.getNodeName() + " Value " + eElement.getTextContent());
		            		  
		            		  if ("test".equals(eElement.getNodeName())) {
		            			  eElement.setTextContent(isTest);
		            			  TestBase.logStatic("El valor de test del nuevo sevidor de production " + eElement.getTextContent());
		            		  }
		            		  
		            		  if ("production".equals(eElement.getNodeName())) {
		            			  eElement.setTextContent(isProduction);
		            			  TestBase.logStatic("El valor de  production del nuevo sevidor production " + eElement.getTextContent());
		            		  }
		            		  
		            	  }
		            	  else {
		            		  TestBase.logStatic("Error: fallo de configuración en el parametro o el fichero de configuración xml");
		            		  Assert.assertTrue(false, "Error: fallo de configuración en el parametro o el fichero de configuración xml");
		            	  }
		              }
	        	  }
	        	  
	          }
	          
	          // write the content on console
	          TransformerFactory transformerFactory = TransformerFactory.newInstance();
	          Transformer transformer = transformerFactory.newTransformer();
	          DOMSource source = new DOMSource(doc);
	          System.out.println("-----------Modified File-----------");
	          StreamResult consoleResult = new StreamResult(System.out);
	          transformer.transform(source, consoleResult);
	          
	          // Save modification dom document to a config.xml
	          try (FileOutputStream output =
	                       new FileOutputStream(getFileXmlConfigServer())) {
	              writeXml(doc, output);
	              
	          } catch (IOException e) {
	              e.printStackTrace();
	          }
	          
	       } catch (Exception e) {
	          e.printStackTrace();
	       }
	}
	
	//A uitlizar para modificar datos de la configuración en el fichero de xml
	public void setDataConfigFromXmlFile( Server serve) {
		
		setServerList(); //Reiniciar la configuración de los servidores
	}
	
	public Server getServerCloudQuality03() {
		return getServerList().stream().filter(srv -> srv.getName().contains(EnumServidor.QUALITY03.getServerName())).findAny().get();
	}
	
	public Server getServerCloudQuality04() {
		return getServerList().stream().filter(srv -> srv.getName().contains(EnumServidor.QUALITY04.getServerName())).findAny().get();
	}
	
	public Server findServer(String server) {
		return serverList.stream().filter(srv -> srv.getName().contains(server)).findAny().get();
	}
	
    // write doc to output stream
    private static void writeXml(Document doc, OutputStream output) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }
}
