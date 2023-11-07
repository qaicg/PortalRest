package configuration;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
//import java.awt.event.ActionListener;

import utils.testRunner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

public class ConfigServerPanel extends JPanel  implements ActionListener {
    private testRunner runTest;
	
	private JButton jButtonRun;
    private JLabel jcomp2;
    private JLabel jcomp3;
    private JCheckBox cloudQuality03;
    private JCheckBox cloudQuality04;
    private JRadioButton optServerTestCloudQuality03;
    private JRadioButton optServerTestCloudQuality04;
    
    private boolean serverTestQuality03;
    private boolean serverTestQuality04;
    
    private static Server serverTest;
	private static Server serverProduction;
    
    public static JFrame frame;
    
    public static String[] testRunnerArgs;
    private JLabel lblNewLabel_1;
    private JLabel lblNewLabel_2;
    private JLabel lblNewLabel_3;
    private JLabel lblNewLabel_4;
    private JLabel lblNewLabel_6;
    private JLabel lblNewLabel_7;

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public ConfigServerPanel () {
        //construct components
        jButtonRun = new JButton ("RUN TEST ");
       // jButtonRun.addActionListener(this);;
        
        jcomp2 = new JLabel ("Elección de entorno");
        jcomp3 = new JLabel ("Selecciona uno o los dos servidores de test");
        cloudQuality03 = new JCheckBox ("");
        cloudQuality04 = new JCheckBox ("");
        optServerTestCloudQuality03 = new JRadioButton ("");
        optServerTestCloudQuality04 = new JRadioButton ("");

        //adjust size and set layout
        setPreferredSize (new Dimension(598, 409));
        setLayout (null);

        //Añadir componentes
        add (jButtonRun);
        add (jcomp2);
        add (jcomp3);
        add (cloudQuality03);
        add (cloudQuality04);
        add (optServerTestCloudQuality03);
        add (optServerTestCloudQuality04);

        //set component bounds (only needed by Absolute Positioning)
        jButtonRun.setBounds (225, 270, 130, 55);
        jcomp2.setBounds (225, 11, 130, 40);
        jcomp3.setBounds (150, 62, 301, 30);
        cloudQuality03.setBounds (135, 133, 21, 14);
        cloudQuality04.setBounds (135, 183, 21, 14);
        optServerTestCloudQuality03.setBounds (397, 128, 21, 25);
        optServerTestCloudQuality04.setBounds (397, 178, 21, 25);
        
        jButtonRun.addActionListener(this);
//        cloudQuality03.addActionListener(this);
//        cloudQuality04.removeActionListener(this);
//        
        optServerTestCloudQuality03.addActionListener(this);
        optServerTestCloudQuality04.addActionListener(this);
        
        JLabel lblNewLabel = new JLabel("CloudQuality03");
        lblNewLabel.setBounds(172, 133, 130, 14);
        add(lblNewLabel);
        
        lblNewLabel_1 = new JLabel("Run");
        lblNewLabel_1.setBounds(135, 103, 101, 18);
        add(lblNewLabel_1);
        
        lblNewLabel_2 = new JLabel("Servidor");
        lblNewLabel_2.setBounds(172, 103, 64, 18);
        add(lblNewLabel_2);
        
        lblNewLabel_3 = new JLabel("MASTER/DEVELOP/TEST");
        lblNewLabel_3.setBounds(364, 103, 162, 14);
        add(lblNewLabel_3);
        
        lblNewLabel_4 = new JLabel("CloudQuality04");
        lblNewLabel_4.setBounds(172, 183, 130, 14);
        add(lblNewLabel_4);
        
        JLabel lblNewLabel_5 = new JLabel("----");
        lblNewLabel_5.setBounds(135, 120, 38, 7);
        add(lblNewLabel_5);
        
        lblNewLabel_6 = new JLabel("------------");
        lblNewLabel_6.setBounds(172, 120, 64, 7);
        add(lblNewLabel_6);
        
        lblNewLabel_7 = new JLabel("-------------------------------");
        lblNewLabel_7.setBounds(364, 120, 130, 7);
        add(lblNewLabel_7);
        
        //Selecionar el servidor de test definido en el fichero XML
        //Selecionar el servidor de Produccion por defecto para ejecutar los tests
		ConfigServer configServer = new ConfigServer();
				
	  	if(configServer.getServerTest().isTest()  // verifcar que tenemos el servidor de test
	    	&& configServer.getServerTest().getName().equals(EnumServidor.QUALITY03.getServerName())) {
	  		serverTest = new Server(EnumServidor.QUALITY03, true, false);
	  		serverProduction = new Server(EnumServidor.QUALITY04, false, true);
	  		
	  		cloudQuality03.setSelected(serverTest.isProduction());
	  		cloudQuality04.setSelected(serverProduction.isProduction());
	  		
	  		optServerTestCloudQuality03.setSelected(serverTest.isTest());
	  		
	  	}
	  	
	  	if(configServer.getServerTest().isTest()  // verifcar que tenemos el servidor de test
	    	&& configServer.getServerTest().getName().equals(EnumServidor.QUALITY04.getServerName())) {
	  		serverTest = new Server(EnumServidor.QUALITY04, true, false);
	  		serverProduction = new Server(EnumServidor.QUALITY03, false, true);
	  		
	  		cloudQuality04.setSelected(serverTest.isProduction());
	  		cloudQuality03.setSelected(serverProduction.isProduction());
	  		
	  		optServerTestCloudQuality04.setSelected(serverTest.isTest());
	  		
	  	}
        
    }

    public static void main (String[] args) {
    	testRunnerArgs = args;
    	frame = new JFrame ("Configuracion de los servidores y ejecutar los Test");
    	frame.setSize(320, 240);
		frame.setLocationRelativeTo(null);// Centralizamos la pantalla
        frame.setResizable(false); //No se puede redimensionar la pantalla
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //La aplicación se cierra al pulsar el botón x de la pantalla
        frame.getContentPane().add (new ConfigServerPanel());
        frame.pack();
                
        frame.setVisible (true);
        
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
				
		if(source == optServerTestCloudQuality03 || source == optServerTestCloudQuality04) {
			
			serverTestQuality03	= source == optServerTestCloudQuality03 ? true : false;
			serverTestQuality04	= source == optServerTestCloudQuality04 ? true : false;
			
			optServerTestCloudQuality03.setSelected(serverTestQuality03);
			optServerTestCloudQuality04.setSelected(serverTestQuality04);
		}
		
		if(source == jButtonRun) {
			//definir el servidor de test
			if(optServerTestCloudQuality03.isSelected()) {
				System.out.println("Servidor de Test: CloudQuality03 ");
				System.out.println("Servidor de producción: CloudQuality04 ");
				testRunner.setSERVERTEST(EnumServidor.QUALITY03);
			}
			else {
				System.out.println("Servidor de Test: CloudQuality04 ");
				System.out.println("Servidor de producción: CloudQuality03 ");
				testRunner.setSERVERTEST(EnumServidor.QUALITY04);
			}
			
			testRunner.defineServerTest();
			
			//System.out.println("Ejecutamos el test");
			//muestrar valores de los elementos en la pantalla
			if( cloudQuality03.isSelected() && cloudQuality04.isSelected()) {
				System.out.println("Ejecutar el test Estable y Master(Los dos servidores)" );
				testRunner.setRUNALLTESTS(true);
			}
			else if (cloudQuality03.isSelected()) {
				System.out.println("Ejecutar el test en el Servidor cloudQuality03" );
				testRunner.setRUNALLTESTS(false);
				if(testRunner.getSERVERTEST().getServerName() == EnumServidor.QUALITY03.getServerName()) {
					testRunner.setENTORNOTEST(true);
				}
				else {
					testRunner.setENTORNOTEST(false);
				}
					
			}
			
			else if( cloudQuality04.isSelected()) {
				System.out.println("Ejecutar el test en el Servidor cloudQuality04" );
				testRunner.setRUNALLTESTS(false);
				if(testRunner.getSERVERTEST().getServerName() == EnumServidor.QUALITY04.getServerName()) {
					testRunner.setENTORNOTEST(true);
				}
				else {
					testRunner.setENTORNOTEST(false);
				}
			}
			else {
				System.out.println("Error: no se ha seleccinado servidor para ejecutor el test ");
				System.out.println("Se lo pide definir el servidor de test ");
				
				JOptionPane.showMessageDialog(frame, "Se solicita soleccionar Servidor(es) para ejecutar los tests");
				return;
			}
			
			runTest = new testRunner();
			runTest.main(testRunnerArgs); //Ejecutar los tests
			frame.setVisible(false); //Cerrar la aplicacion cuando termine los tests
					
			
		}

	}
	
    public void enableJElement(boolean enable) {
		optServerTestCloudQuality03.setEnabled(enable);
		optServerTestCloudQuality04.setEnabled(enable);
		cloudQuality03.setEnabled(enable);
		cloudQuality04.setEnabled(enable);
    }
}
