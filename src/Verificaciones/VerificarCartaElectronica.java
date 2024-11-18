package Verificaciones;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import enums.DocumentType;
import utils.TestBase;

public class VerificarCartaElectronica extends TestBase {
	private String fileName;
	private int actualizar;
	private String textFromDocPdf; // permite verificar si se ha cargado el doc pdf
	
	
	//Verificar que se muestre la carta en pdf a la hora de abre portalrest en modo consulta
	@Test(description = "Prueba de mostrar Carta electronica en format(imagen o PDF) en PortalRest modo consulta", priority = 1, 
			groups = {"validarCartaModoConsultaEnFormaImagenPdf"})
	@Parameters({"documentName", "documentType", "refrecaPaginaSinoFallaElTest"})
	public void verifcarCartaEnModoConsulta(String docName, String docType, @Optional("1") int refresc) {
		fileName = docName + docType;
		actualizar = refresc;
		//verificar se abre la página 
		List<String> docTypeList = new ArrayList();
		
		for(DocumentType doct: DocumentType.values()) {
			docTypeList.add(doct.getExtension());
			
		}

		Assert.assertTrue(!docTypeList.stream().filter(ext -> ext.equals(docType)).findFirst().isEmpty(), "No se ha encontrado la carta en el formato indicado");
		
		if(DocumentType.PDF.getExtension().equals(docType)) {
			verificarCartaEnPDF (docName, docType);
		}
		else if(DocumentType.JPEG.getExtension().equals(docType)) {
			verificarCartaEnImagen(docName, docType);
						
		}
		else {
			Assert.assertTrue(false, "Error: No hemos encontrado el typo de la carta: Imagen o PDF ");
			
		}
		
		//Si no falla la premera vez al abrirse volver a refrescar trs veces y comprobar que se ve la carta
		if(actualizar >= 1) {//Refrescar la página x veces antes de volver a testear 
			refrescarPagina(actualizar);
			verifcarCartaEnModoConsulta(docName, docType, 0);
		}
	}
	
	//Verificar que se muestre las carta en Imagen a la hora de abrir portalrest en modo consulta
	public void verificarCartaEnImagen(String docName, String docType) {
		String elementCartaDivXpath = "//app-iframe-page/div/div"; // botón pulsar para ampliar la carta
		String elementCartaImgXpath = "//app-iframe-page/div/img[contains(@src, '.jpeg')]"; // la carta en imagen
		
		String elementCartaMatDialogContainer = "//mat-dialog-container/app-base-dialog/div/div[2]/app-product-image/div/img";
		String elementCartaMatDialogMatIcocn = "//mat-dialog-container/app-base-dialog/div/div[1]/mat-icon";
		
		Assert.assertTrue(isElementPresent(By.xpath(elementCartaDivXpath)) || isElementPresent(By.xpath(elementCartaImgXpath)), "Error: No se carga la carta " + docName+docType + " en consulta al abrir portalrest en modo consulta");
		
		//Vericar que se puede ampliar la carta abrir la carta pulsando en la carta
		
		clicJS(driver.findElement(By.xpath(elementCartaImgXpath)));
		espera(500);
		
		//Verificar que aparece la carta ampliada y el botón x para cerra la pestaña.
		Assert.assertTrue(isElementPresent(By.xpath(elementCartaMatDialogContainer)) || isElementPresent(By.xpath(elementCartaMatDialogMatIcocn)), "Error: No se amplia la carta " + docName+docType + "/ No se ve el botón x para cerrar la pestaña");
		
		//Cerrar el dialogo 
		clicJS(driver.findElement(By.xpath(elementCartaMatDialogMatIcocn)));
	}
	
	//Verificar que se muestre las carta en PDF a la hora de abrir portalrest en modo consulta
	public void verificarCartaEnPDF (String docName, String docType) {
		String elementIframeXpath = "//app-pdf-viewer/div/pdf-viewer[contains(@class, 'pdf-viewer')]//div[contains(@class, 'page')]";
		
		String textToExpectFromPDFXpath = "//div[contains(@class, 'page')]/div[contains(@class, 'textLayer')]/span[text()='" + this.textFromDocPdf +"']"; 
		
		String elementBotonAmpliarCarta = "//app-floating-button/div/button/span/mat-icon";
		
		Assert.assertTrue(isElementPresent(By.xpath(elementIframeXpath)), "Error: No se carga la carta " + docName+docType + " en consulta");

		String cartaEnForma = DocumentType.PDF.getExtension().equals(docType) ? " carta en PDF " : " carta en imagen ";
				
		Assert.assertTrue(isElementPresent(By.xpath(textToExpectFromPDFXpath)), "Error: no se ve la carta al abrir la página del restaurante!!!");
		
		Assert.assertTrue(isElementPresent(By.xpath(elementBotonAmpliarCarta)), "Error: No se encontra el botón pulsar para abrir la carta en ventana externa Google "); //Ventana externa
		
		clicJS(driver.findElement(By.xpath(elementBotonAmpliarCarta)));
		espera(1000);
		
		ArrayList<String> switchTabs= new ArrayList<String> (driver.getWindowHandles());
		driver.switchTo().window(switchTabs.get(1));
		closeWindowTab(1, 0);
		
		log("La carta en el formato indicado se muestra bien en portalrest modo consulta");
		
	}

	public void refrescarPagina(int actualizar) {
		for(int i = 1; i <= actualizar; i++) {
			driver.navigate().refresh();
			espera(1000);
			log("Actualización " + i);
		}
	}
	
	@Test(description = "Prueba que no aparezca el botón back a la hora de pulsar en el logotipo tras abrir portalrest con tipo de servicio")
	@Parameters({"mostrarBotonBack"})
	public void pulsarLogoTipoSinMostrarBotonBack(@Optional("1") boolean mostrarBotonBack) {
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current absolute path is: " + s);
	}
	
	@BeforeMethod
	@Parameters({"textFromDocPdf"}) 
	public void sendTextOFDocPDF(@Optional("") String textFromDocPdf)
	{
		this.textFromDocPdf = textFromDocPdf;
	}

}
