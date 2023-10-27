package configuration;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
//import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.event.ActionListener;

public class ConfigServerPanel extends JPanel  {
    private JButton jButtonRun;
    private JLabel jcomp2;
    private JLabel jcomp3;
    private JCheckBox cloudQuality03;
    private JCheckBox cloudQuality04;
    private JRadioButton optServerTestCloudQuality03;
    private JRadioButton optServerTestCloudQuality04;

    public ConfigServerPanel () {
        //construct components
        jButtonRun = new JButton ("RUN ");
       // jButtonRun.addActionListener(this);;
        
        jcomp2 = new JLabel ("Elección de entorno");
        jcomp3 = new JLabel ("Selecciona uno o los dos servidores de test");
        cloudQuality03 = new JCheckBox ("CloudQuality03");
        cloudQuality04 = new JCheckBox ("CloudQuality04");
        optServerTestCloudQuality03 = new JRadioButton ("");
        optServerTestCloudQuality04 = new JRadioButton ("");

        //adjust size and set layout
        setPreferredSize (new Dimension (533, 457));
        setLayout (null);

        //add components
        add (jButtonRun);
        add (jcomp2);
        add (jcomp3);
        add (cloudQuality03);
        add (cloudQuality04);
        add (optServerTestCloudQuality03);
        add (optServerTestCloudQuality04);

        //set component bounds (only needed by Absolute Positioning)
        jButtonRun.setBounds (200, 300, 130, 55);
        jcomp2.setBounds (195, -5, 335, 40);
        jcomp3.setBounds (135, 60, 310, 30);
        cloudQuality03.setBounds (135, 120, 155, 30);
        cloudQuality04.setBounds (140, 210, 125, 25);
        optServerTestCloudQuality03.setBounds (325, 120, 110, 35);
        optServerTestCloudQuality04.setBounds (325, 205, 100, 25);
    }

    public static void main (String[] args) {
        JFrame frame = new JFrame ("ConfigServerPanel");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new ConfigServerPanel());
        frame.pack();
        frame.setVisible (true);
        
        
    }
}
