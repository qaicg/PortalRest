package Reservas;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import AbrePortalRest.AbrirUrl;
import graphql.Assert;
import utils.TestBase;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailReading extends TestBase{
    Properties properties = null;
    private Session session = null;
    private Store store = null;
    private Folder inbox = null;
    private String userName = "prtreservascliente@yahoo.com";// provide user name
    private String password = ".1234abcd2023";// provide password
    private String configImap = "imap.mail.yahoo.com"; // la configuracion del imap del correo
    private String saveDirectory = System.getProperty("user.dir") + "\\SaveEmails";


    @Test
    @Parameters({"imapConfig", "emailRestaurante", "emailCliente"})
    public void readEmailClient() {
        MailReading sample = new MailReading();
        sample.readMails();
    }
    
    @Test
    @Parameters({"imapConfig", "emailRestaurante", "emailCliente"})
    public void readEmailRestaurante() {
        MailReading sample = new MailReading();
        sample.readMails();
    }

    public void readMails() {
        properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");
        try {
            session = Session.getDefaultInstance(properties, null);
            store = session.getStore("imaps");
            store.connect("imap.mail.yahoo.com", "prtreservascliente@yahoo.com", ".1234abcd2023");
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

    
    
    public void openWebMail(String userName, String password) throws InterruptedException {
    	
    	if(isElementPresent(By.id("ybarMailLink"))) {
    		//Sesión abierta
    		driver.findElement(By.id("ybarMailLink")).click();
    		espera(1500);
    		
    		//cerrarSesionMail
    		//cerrarSesionMail(false);
    		
    		//Verificar donde estamos 
    	} else {
    		//Verificar donde estamos 
    		if(isElementPresent(By.xpath("//ul[contains(@class, 'card-list has-menu clrfix')]/li[contains(@class, 'card has-desc loggedOut clrfix')]"))) {
        		String dataEmail = driver.findElement(By.xpath("//ul[contains(@class, 'card-list has-menu clrfix')]/li[contains(@class, 'card has-desc loggedOut clrfix')]")).getAttribute("data-email");
        		//w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[contains(@id, 'login-username')]")));
        		if(!isNullOrEmpty(dataEmail) && dataEmail.contains("@yahoo.com")) {
        			//Sesión abierta
        			if(isElementPresent(By.xpath("//input[contains(@id, 'login-username')]"))) {
        				log("Que hacemos ?");
        			} else {
        				w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[contains(@id, 'login-username')]")));
        				cerrarSesionMail(true);
        			}
        		} else {
        			log("Error: no se puede cerrar la sesión antes de abrirla");
        			Assert.assertTrue(false);
        		}
        		
        	}
    		
    		openSesionMail(userName, password);
    		//cerrarSesionMail(false);
    	}
    	
    	//Cerrar la sesion cuando terminamos de consultar la notificacion
         
    }
    
    public void cerrarSesionMail(@Optional("false") boolean dataEmail) throws InterruptedException {
    	String ybarAccountMenu = "//button[contains(@id, 'ybarAccountMenu')]//child::label[contains(@id, 'ybarAccountMenuOpener')]//child::span[contains(@role, 'presentation')]";
    	
    	if(dataEmail) {
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
	    		
	    		w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[contains(@id, 'profile-signout-link')]//child::span")));

	    		if(driver.findElements(By.xpath("//a[contains(@id, 'profile-signout-link')]//child::span")).get(1).getText()!= null)
	    			driver.findElements(By.xpath("//a[contains(@id, 'profile-signout-link')]//child::span")).get(1).click();
	    	}
    	}
    }
    
    public void openSesionMail(String userName, String password) throws InterruptedException {
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
	    		driver.findElement(By.id("ybarMailLink")).click();
	    		espera(2000);
	    		//Buscar y abrir el ultimo correo no leido
	    		lastEmailNotReading();
	    	}
	    	
    	} else {
    		log("No sabemos -----");
    		cerrarSesionMail(true);
    		
	    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[contains(@id, 'login-username')]")));
	   	 	
	    	driver.findElement(By.id("login-username")).clear();
	    	driver.findElement(By.id("login-username")).sendKeys(userName, Keys.ENTER);
	    	Thread.sleep(500);
		
	    	w2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("login-passwd")));
		 
	    	driver.findElement(By.id("login-passwd")).clear();
	    	driver.findElement(By.id("login-passwd")).sendKeys(password, Keys.ENTER);
	         
	    	Thread.sleep(500);
	    	if(isElementPresent(By.id("ybarMailLink"))) {
	    		driver.findElement(By.id("ybarMailLink")).click();
	    		espera(2000);
	    		//Buscar y abrir el ultimo correo no leido
	    		lastEmailNotReading();
	    	}
    		
    	}

    }
    
    //open recent email not reading
    public void lastEmailNotReading() {
    	driver.navigate().refresh();
    	espera(1500);
    	String lastEmail = "//ul[contains(@aria-label, 'Message list')]//child::a[contains(@role, 'article') and contains(@data-test-read, 'false')]";
    	if(driver.findElements(By.xpath(lastEmail)).size() > 0) {
    		driver.findElements(By.xpath(lastEmail)).get(0).click();
    		espera(1500);
    	} else {
    		log("No hay nuvea notificación");
    	}
    }
    
    //Verificarar Asunto del email
}
