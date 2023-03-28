package Reservas;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import AbrePortalRest.AbrirUrl;
import graphql.Assert;
import main.Correo;
import main.Reader;
import utils.TestBase;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Utilizamos la class para leer y descargar email desde yahoo / mailSac
 * Para yahoo. No se ha podido utilizar la Api de yahoo
 * TODO: Implentar la API de yahoo para poder descargar los email sin abrir el navegador
 */

public class MailReading extends TestBase {
    Properties properties = null;
    private Session session = null;
    private Store store = null;
    private Folder inbox = null;
    private String userName = null;// provide user name
    private String password = null;// provide password
    private String configImap = null; // la configuracion del imap del correo
    private String saveDirectory = System.getProperty("user.dir") + "\\SaveEmails";
    
    private boolean isSesionMailOpen = false; //determina si el usuario está connectado en su sesión mail
    
    private Map<String, String> infoEmailMap = new HashMap<>();
    
    
    public void setInfoEmailMap(String remitenteEmail, String asuntoDelEmail, String mensajeDelMail) {
    	infoEmailMap.put("remitenteEmail", remitenteEmail);
    	infoEmailMap.put("asuntoDelEmail", asuntoDelEmail);
    	infoEmailMap.put("mensajeDelMail", mensajeDelMail);
    }
    
    public Map<String, String> getInfoEmailMap() {
    	return infoEmailMap;
    }

    @Test(priority = 1)
    @Parameters({"webMailImapConfig", "emailCliente", "passwordCliente", "emailNotificacionReservas", "passwordRestaurante", "textConfirmacionReservaAlCliente"})
    public void readLastEmail(String webMailImapConfig, String emailCliente, String passwordCliente, String emailNotificacionReservas, String passwordRestaurante, String textConfirmacionReservaAlCliente) {
        MailReading sample = new MailReading();
        
        sample.readMails(webMailImapConfig, emailCliente, passwordCliente);
    	
    	textConfirmacionReservaAlCliente = textConfirmacionReservaAlCliente.replace("{ln}", "\n");
    	
    	log(textConfirmacionReservaAlCliente);
    	
    }
    
    @Test(priority = 1)
    @Parameters({"webMailImapConfig", "emailRestaurante", "passwordRestaurante"})
    public void readEmailRestaurante(String webMailImapConfig, String userName, String userPassword) {
        MailReading sample = new MailReading();
        sample.readMails(webMailImapConfig, userName, userPassword);
    }
    
    // 
    public void readMails(String webMailImapConfig, String userName, String userPassword) {
        properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");
        //properties.setProperty(userName, userPassword)
        try {
            session = Session.getDefaultInstance(properties, null);
            store = session.getStore("imaps");
            store.connect(webMailImapConfig, userName, userPassword);
            inbox = store.getFolder("INBOX");

            int unreadMailCount = inbox.getUnreadMessageCount();
            System.out.println("No. of Unread Mails = " + unreadMailCount);

            inbox.open(Folder.READ_WRITE);

            Message messages[] = inbox.getMessages();
            System.out.println("No. of Total Mails = " + messages.length);
            for (int i = messages.length; i > (messages.length - unreadMailCount); i--) {
                Message message = messages[i - 1];

                Address[] from = message.getFrom();
                System.out.println("====================================== Mail no.: " + i + " start ======================================");
                System.out.println("Date: " + message.getSentDate());
                System.out.println("From: " + from[0]);
                System.out.println("Subject: " + message.getSubject());

                String contentType = message.getContentType();
                String messageContent = "";

                // store attachment file name, separated by comma
                String attachFiles = "";

                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                            String fileName = part.getFileName();
                            attachFiles += fileName + ", ";
                            part.saveFile(saveDirectory + File.separator + fileName);
                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
                        }
                    }

                    if (attachFiles.length() > 1) {
                        attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                } else if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }
                System.out.println("Attachments: " + attachFiles);
      
                System.out.println("====================================== Mail no.: " + i + " end ======================================");
            }

            // disconnect
            inbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for pop3.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }  

    
    //openlastEmailNotReading -> abrir el ultimo email no leido desde yahoo
    public void openWebMail(String userName, String password,  @Optional("false") boolean openlastEmailNotReading) throws InterruptedException {
    	
    	if(isElementPresent(By.id("ybarMailLink"))) {
    		//Sesión abierta
    		if(getElementByFluentWait(By.id("ybarMailLink"), 60, 5) == null) {
    			log("Error: algo pasa y no podemos pulsar el botón de id ybarMailLink");
    			Assert.assertTrue(false);
    		}
    		else {
    			clicJS(getElementByFluentWait(By.id("ybarMailLink"), 60, 5));
    		}
    		espera(1500);
    	} else {
    		//Verificar donde estamos 
    		if(isElementPresent(By.xpath("//ul[contains(@class, 'card-list has-menu clrfix')]/li[contains(@class, 'card has-desc loggedOut clrfix')]"))) {
        		String dataEmail = driver.findElement(By.xpath("//ul[contains(@class, 'card-list has-menu clrfix')]/li[contains(@class, 'card has-desc loggedOut clrfix')]")).getAttribute("data-email");
        	
        		if(!isNullOrEmpty(dataEmail) && dataEmail.contains("@yahoo.com")) {
        			//Sesión abierta
        			if(isElementPresent(By.xpath("//input[contains(@id, 'login-username')]"))) {
        				log("Que hacemos ? Continuar el proceso...");
        			} else {
        				cerrarSesionMail(true);
        			}
        		} else {
        			log("Error: no se puede cerrar la sesión antes de abrirla");
        			Assert.assertTrue(false);
        		}
        	}
    		
    		openSesionMail(userName, password, openlastEmailNotReading);
    	}
         
    }
    
    //Extract content e-mail and save it: Yahoo desde el navegador
    public void extractContentMail(String email) {
    	//Verificar que el usuario está connectado en su sesión email
    	if(!this.isSesionMailOpen) {
    		log("El usuario no está connectado- sesion cerrada ");
    		Assert.assertTrue(false);
    	}
    	
    	log("Extraemos el email -> " + email);
    	
    	driver.navigate().refresh();
    	espera(2000);
    	String lastEmail = "//ul[contains(@aria-label, 'Message list')]//child::a[contains(@role, 'article') and contains(@data-test-read, 'false')]";
    	
    	WebElement elmentLastEmail = getElementByFluentWait(By.xpath(lastEmail), 200, 5);
    	
    	if(driver.findElements(By.xpath(lastEmail)).size() > 0 || elmentLastEmail != null) {
    		//extración información del correo sin abrir la notificación
    		//remitente del email
    		String xpathRemitenteEmail = null;

    		if(isElementPresent(By.xpath("//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[1]/div[2]/child::span/strong"))) {
    			
    			xpathRemitenteEmail = "//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[1]/div[2]/child::span/strong";
    		}
    		else if(isElementPresent(By.xpath("//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[1]/div[3]/span/strong"))) {
    			xpathRemitenteEmail = "//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[1]/div[3]/span/strong";
    		}
    		//log("Valor xpath del element para remitente del ultimo correo -->" + xpathRemitenteEmail);
    		
    		waitUntilPresence(xpathRemitenteEmail);
    		
    		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathRemitenteEmail))).get(0);
    		String sRemitenteEmail = driver.findElements(By.xpath(xpathRemitenteEmail)).get(0).getText();
    		
    		//Asunto del email
    		String xpathAsunto = null;
    		if(isElementPresent(By.xpath("//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[2]/div[1]/div[1]/span[1]"))) {
    			xpathAsunto = "//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[2]/div[1]/div[1]/span[1]";
    		}
    		else if(isElementPresent(By.xpath("//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[3]/div[1]/div[1]/span[1]"))) {
    			xpathAsunto = "//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[3]/div[1]/div[1]/span[1]";
    		}
    		//log("Valor xpath del element para Asunto del ultimo correo -->" + xpathAsunto);
    		
    		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathAsunto))).get(0);
    		String sAsunto = driver.findElements(By.xpath(xpathAsunto)).get(0).getText();
    		
    		//Mensaje del correo de notificación
    		String xpathMensajeEmail = null;
    		if(isElementPresent(By.xpath("//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[2]/div[1]/div[2]/div"))) {
    			xpathMensajeEmail = "//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[2]/div[1]/div[2]/div";
    			
    		}
    		else if(isElementPresent(By.xpath("//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[3]/div[1]/div[2]/div"))) {
    			xpathMensajeEmail = "//a[contains(@role, 'article') and contains(@data-test-read, 'false')]/div/div[3]/div[1]/div[2]/div";
    		}
    		//log("Valor xpath del element para el mensaje del ultimo correo -->" + xpathMensajeEmail);
    		
    		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathMensajeEmail))).get(0);
    		
    		String sMensajeEmail = driver.findElements(By.xpath(xpathMensajeEmail)).get(0).getText();
    		
    		if(!isNullOrEmpty(sRemitenteEmail) && !isNullOrEmpty(sAsunto) && !isNullOrEmpty(sMensajeEmail)) {
    		//Guardar la informacion de la notificación para validarla después.
    			this.setInfoEmailMap(sRemitenteEmail, sAsunto, sMensajeEmail);
    			espera(1000);
    			log("Se ha podido extraer el correo de notificación para su validación");
    			log("-- Información de notificación para comprobar: ");
    			this.infoEmailMap.entrySet().forEach(entry -> {
    					log(entry.getKey() + " => " + entry.getValue());
    				}
    			);
    			log("FIN ---Información de notificación para comprobar--- > " + email);
    		}
    		else {
    			log("No se ha podido extraer el correo de notificación para su validación");
    			Assert.assertTrue(false);
    		}
    		
    		espera(1500);
    	} else {
    		log("No hay nuvea notificación");
    	}
    }
    
    //Yahoo
    public void cerrarSesionMail(@Optional("false") boolean dataEmail) throws InterruptedException {
    	String ybarAccountMenu = "//button[contains(@id, 'ybarAccountMenu')]//child::label[contains(@id, 'ybarAccountMenuOpener')]//child::span[contains(@role, 'presentation')]";
    	
    	if(dataEmail) {
    		//Eliminamos usuario encontrado en la pantalla de login
    		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul/li/div/label[contains(@class, 'icon-vertical-ellipsis')]")));
    		
    		clicJS(driver.findElement(By.xpath("//ul/li/div/label[contains(@class, 'icon-vertical-ellipsis')]")));
    		espera(1500);
    		
    		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul/li/div/button[contains(@class, 'card-menu-button') and contains(@data-action, 'remove-account')]")));
    		
    		if(driver.findElements(By.xpath("//ul/li/div/button[contains(@class, 'card-menu-button') and contains(@data-action, 'remove-account')]")).size() > 1) {
    			int count = driver.findElements(By.xpath("//ul/li/div/button[contains(@class, 'card-menu-button') and contains(@data-action, 'remove-account')]")).size() ;
    			for(int i=1; i <= count; i++) {
    				
    				clicJS(driver.findElement(By.xpath("//ul/li/div/button[contains(@class, 'card-menu-button') and contains(@data-action, 'remove-account')]")));
    			}
    			
    		} else {
    		
    			clicJS(driver.findElement(By.xpath("//ul/li/div/button[contains(@class, 'card-menu-button') and contains(@data-action, 'remove-account')]")));
    		}
    		
    		espera(1500);
    		
    	} else {
	    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ybarAccountMenu)));
	    	
	    	if(driver.findElement(By.xpath(ybarAccountMenu)).getText().equalsIgnoreCase("Calidad")) {
	    		driver.findElement(By.xpath(ybarAccountMenu)).click();
	    		Thread.sleep(500);
	    		
	    		waitUntilPresence("//a[contains(@id, 'profile-signout-link')]//child::span", true);
	    		
	    		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@id, 'profile-signout-link')]//child::span")));

	    		if(driver.findElements(By.xpath("//a[contains(@id, 'profile-signout-link')]//child::span")).get(1).getText()!= null) {
	    			driver.findElements(By.xpath("//a[contains(@id, 'profile-signout-link')]//child::span")).get(1).click();
	    			espera(500);
	    			log("Sesión no está abierta");
	    			this.setSesionMailOpen(false);
	    		} else {
	    			log("Error: No hemos podido cerrar la sesión después de consultar el correo electonico");
	    			
	    		}
	    	}
    	}
    }
    
    //userName
    //password
    //openlastEmailNotReading -> abrir el ultimo email no leido
    public void openSesionMail(String userName, String password, @Optional("false") boolean openlastEmailNotReading) throws InterruptedException {
    	espera(2000);
    	if(isElementPresent(By.xpath("//input[contains(@id, 'login-username')]"))) {
    		
	    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[contains(@id, 'login-username')]")));
	   	 	
	    	driver.findElement(By.id("login-username")).clear();
	    	driver.findElement(By.id("login-username")).sendKeys(userName, Keys.ENTER);
	    	Thread.sleep(500);
		
	    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("login-passwd")));
		 
	    	driver.findElement(By.id("login-passwd")).clear();
	    	driver.findElement(By.id("login-passwd")).sendKeys(password, Keys.ENTER);
	         
	    	Thread.sleep(500);
	    	if(isElementPresent(By.id("ybarMailLink"))) {
	    		Thread.sleep(1500);
	    		
	    		WebElement emailClick = getElementByFluentWait(By.id("ybarMailLink"), 30, 5);
	    		if(emailClick != null) {
	    			emailClick.click();
	    		}
	    		
	    		espera(2000);
	    		//Buscar y abrir el ultimo correo no leido
	    		if(openlastEmailNotReading)
	    			lastEmailNotReading();
	    		else
	    			this.isSesionMailOpen = true;
	    	}
	    	
    	} else {
    		cerrarSesionMail(true);
    		
    		waitUntilPresence("//input[contains(@id, 'login-username')]", true);
    		
	    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[contains(@id, 'login-username')]")));
	   	 	
	    	driver.findElement(By.id("login-username")).clear();
	    	driver.findElement(By.id("login-username")).sendKeys(userName, Keys.ENTER);
	    	Thread.sleep(500);
		
	    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("login-passwd")));
		 
	    	driver.findElement(By.id("login-passwd")).clear();
	    	driver.findElement(By.id("login-passwd")).sendKeys(password, Keys.ENTER);
	         
	    	Thread.sleep(1000);
	    	if(isElementPresent(By.id("ybarMailLink"))) {
	    		
	    		WebElement emailClick = getElementByFluentWait(By.id("ybarMailLink"), 30, 5);
	    		//driver.findElement(By.id("ybarMailLink")).click();
	    		if(emailClick != null) {
	    			emailClick.click();
	    		}
	    		espera(2000);
	    		//Buscar y abrir el ultimo correo no leido
	    		if(openlastEmailNotReading)
	    			lastEmailNotReading();
	    		else
	    			this.isSesionMailOpen = true;
	    	}
    	}
    }
    
    public boolean isSesionMailOpen() {
		return isSesionMailOpen;
	}

	public void setSesionMailOpen(boolean isSesionMailOpen) {
		this.isSesionMailOpen = isSesionMailOpen;
	}

	//open recent email not reading
    public void lastEmailNotReading() {
    	driver.navigate().refresh();
    	espera(2000);
    	String lastEmail = "//ul[contains(@aria-label, 'Message list')]//child::a[contains(@role, 'article') and contains(@data-test-read, 'false')]";
    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(lastEmail)));
    	if(driver.findElements(By.xpath(lastEmail)).size() > 0) {
    		driver.findElements(By.xpath(lastEmail)).get(0).click();
    		espera(1500);
    	} else {
    		log("No hay nuvea notificación");
    	}
    }
    
    /**
     * Test Utilización de MailSac
     */
    
    @Test(priority = 1)
    @Parameters({"webMailImapConfig", "emailCliente", "passwordCliente", "emailNotificacionReservas", "passwordRestaurante", "textConfirmacionReservaAlCliente"})
    public void readLastEmailFromMailSac(String webMailImapConfig, String emailCliente, String passwordCliente, String emailNotificacionReservas, String passwordRestaurante, String textConfirmacionReservaAlCliente) {
    	/*
    	 * Utilización de MailSac
    	 */
	 	Correo correoCliente = openLastMessageFromMailSac(emailCliente, passwordCliente);
	  	log("ultimo mensaje al Cliente: Asunto --> " + correoCliente.getAsunto());
	  	log("ultimo mensaje al Cliente: Remitente  --> " + correoCliente.getRemitente());
		log("ultimo mensaje al Cliente: Fecha  --> " +correoCliente.getFecha());
		log("ultimo mensaje al Cliente: Cuerpo --> " + correoCliente.getCuerpo());
		
	  	Correo correoRestaurante = openLastMessageFromMailSac(emailNotificacionReservas, passwordRestaurante);
	  	log("ultimo mensaje al Restaurante: Asunto --> " + correoRestaurante.getAsunto());
		log("ultimo mensaje al Restaurante: Remitente  --> " + correoRestaurante.getRemitente());
		log("ultimo mensaje al Restaurante: Fecha  --> " + correoRestaurante.getFecha());
	 	log("ultimo mensaje al Restaurante: Cuerpo --> " + correoRestaurante.getCuerpo());
    }
}
