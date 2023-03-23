package utils;

import javax.mail.Authenticator;


import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.SubjectTerm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.springframework.boot.SpringApplication;

public class MailHelper extends TestBase{
	//private  ResponseEntity<String> response;

public static void main(String[] args) throws Exception {
        
	connectIMAP("imap.mail.yahoo.com", "prtreservascliente@yahoo.com", ".1234abcd2023");
	
	/*
		Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
	
	        String host = "imap.mail.yahoo.com";
	        String username = "user";
	        String password = "passwd";
	        //Properties props = new Properties();
	        props.setProperty("mail.imap.ssl.enable", "true");
	        // set any other needed mail.imap.* properties here
	        Session session = Session.getInstance(props);
	        Store store = session.getStore("imap");
	        store.connect(host, "prtreservascliente@yahoo.com", ".1234abcd2023");
        
            //Session session = Session.getDefaultInstance(props, null);
            //Store store = session.getStore("imaps");
            //store.connect("imap.mail.yahoo.com", "prtreservascliente@yahoo.com", ".1234abcd2023");
           
            
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            System.out.println("Total Message:" + folder.getMessageCount());
            System.out.println("Unread Message:"
                    + folder.getUnreadMessageCount());
            
            Message[] messages = null;
            boolean isMailFound = false;
            Message mailFromToto= null;

            //Search for mail from toto
            for (int i = 0; i <=5; i++) {
                messages = folder.search(new SubjectTerm(
                        "Welcome to Heaven"),
                        folder.getMessages());
                //Wait for 10 seconds
                if (messages.length == 0) {
                    Thread.sleep(10000);
                }
            }

            //Search for unread mail from Toto
            //This is to avoid using the mail for which 
            //Registration is already done
            for (Message mail : messages) {
                if (!mail.isSet(Flags.Flag.SEEN)) {
                    mailFromToto = mail;
                    System.out.println("Message Count is: "
                            + mailFromToto.getMessageNumber());
                    isMailFound = true;
                }
            }

            //Test fails if no unread mail was found from Toto
            if (!isMailFound) {
                throw new Exception(
                        "Could not find new mail from Toto :-(");
            
            //Read the content of mail and launch registration URL                
            } else {
                String line;
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(mailFromToto
                                .getInputStream()));
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                System.out.println(buffer);

                //Your logic to split the message and get the Registration URL goes here
                String registrationURL = buffer.toString().split("http://yopmail.com")[0]
                        .split("href=")[1];
                System.out.println(registrationURL);                            
            }
            */
    }

	public static void connectIMAP(String host, String userEmail, String password){
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		Properties props= new Properties();
		
		//props.put("mail.imap.ssl.enable", "true");
		props.put("mail.imap.port", "993");
		
		/*props.put("mail.imap.auth.mechanisms", "XOAUTH2");
		props.put("mail.imap.sasl.mechanisms", "XOAUTH2");
		
		props.put("mail.imap.auth.login.disable", "true");
		props.put("mail.imap.auth.plain.disable", "true");
		
		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
	    props.setProperty("mail.imap.socketFactory.fallback", "false");
	    props.setProperty("mail.imap.socketFactory.port", "993");
		props.setProperty("mail.imap.starttls.enable", "true");*/
		
		
		props.put("mail.debug", "true");
		props.put("mail.debug.auth", "true");
	
		Session session = Session.getInstance(props);
		session.setDebug(true);
	
	
		try {
			final Store store = session.getStore("imap");					
			store.connect(host,userEmail, password);
			
			if(store.isConnected()){
				System.out.println("Connection Established using imap protocol successfully !");		
			}
		} catch (NoSuchProviderException e) {	// session.getStore()
			e.printStackTrace();
		} catch (MessagingException e) {		// store.connect()
			e.printStackTrace();
		}
	}
	
	//@GetMapping("/send_to_consent")
	public void getLogin(HttpServletRequest request, HttpServletResponse httpResponse) throws IOException {
	    String path = "https://api.login.yahoo.com/oauth2/request_auth_fe";
	    String clientId = "dj0yJmk9RVg4bzdsfdsdadU5aQk1NJmQ9WVdrOWFWUjNUM05wZW5RbWNHbzlNQT09JnM9Y29uc3snfslnfNLnkbKLNkbbKNlnLN";
	    String responseType = "code";
	    String redirectUri = "https://localhost:8080/dashboard";
	    String scope = "openid";
	    String lang = request.getLocale().toString();
	    String url = 
	        path+"?lang="+lang+"&client_id="+clientId+"&response_type="+responseType+"&redirect_uri="+redirectUri+"&scope="+scope;
	    httpResponse.sendRedirect(url);
	}
}