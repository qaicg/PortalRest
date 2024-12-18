package Windows;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class TrayIconDemo {



    public void displayTray(String string) throws AWTException {
//        //Obtain only one instance of the SystemTray object
//        SystemTray tray = SystemTray.getSystemTray();
//
//        //If the icon is a file
//        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
//        //Alternative (if the icon is on the classpath):
//        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));
//
//        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
//        //Let the system resize the image if needed
//        trayIcon.setImageAutoSize(true);
//        //Set tooltip text for the tray icon
//        trayIcon.setToolTip(string);
//        tray.add(trayIcon);
//
//        trayIcon.displayMessage("PortalRest", string, MessageType.INFO);
    }
}