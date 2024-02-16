package Launcher;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;


public class launcher {

	private static String FILENAME = "";

	public static void main(String[] args) {
		
				
		FILENAME = System.getProperty("user.dir") +"/src/Ventas/VentasCloudQuality03.xml";

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(FILENAME));
			doc.getDocumentElement().normalize();
			System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
			System.out.println("------");
		}catch(Exception e) {
			e.printStackTrace();
		}





	}

}
