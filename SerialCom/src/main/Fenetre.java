package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import serialCom.ConnexionManager;

public class Fenetre extends JFrame{
	
	private JSplitPane JSPmain;	
	protected JFrame ref = this;
	
	private JPanel cons = new JPanel();
	private JPanel other = new JPanel();
	
	private static JTextArea JTAcons = new JTextArea();
	private JScrollPane scroll = new JScrollPane(JTAcons);
	private JPanel JPsend = new JPanel();
	private JTextField JTFenter = new JTextField();
	private JButton JBsend = new JButton("Envoyer");
	
	private JPanel JPcon = new JPanel();
	String[] choix;
	private JComboBox JCBcom;
	private JButton JBcon = new JButton("Connexion");
	private boolean stateJBcon = true;
	
	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu JMoption = new JMenu("Option");
	private JMenu JMpropos = new JMenu("A propos");
	private JMenuItem JMIparam = new JMenuItem("Paramètre");
	private JMenuItem JMIcredit = new JMenuItem("Credit");
	
	public Fenetre() {
		choix = ConnexionManager.getAvailiblePortNames();
		JCBcom = new JComboBox(choix);
		this.setName("SerialCom");
		this.setSize(1000, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Ajout et gestion contenu JPanel cons -> ensemble consol
		JTAcons.setEditable(false);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JBsend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//PROGRAME POUR ENVOYER LA COMMANDE
				ConnexionManager.send(JTFenter.getText());
			}			
		});
		/*
		 * Ajout composant aux JPanels
		 */
		JPsend.setLayout(new BorderLayout());
		JPsend.add(JTFenter, BorderLayout.CENTER);
		JPsend.add(JBsend, BorderLayout.LINE_END);	
		cons.setLayout(new BorderLayout());
		cons.add(scroll, BorderLayout.CENTER);
		cons.add(JPsend, BorderLayout.SOUTH);
		
		/*
		 * Ajout et gestion contenu JPanel other -> le reste de la JFrame (a définir)
		 */
		//1er partie : connexion imprimante
		JCBcom.setSelectedIndex(0); //On sélectionne l'item 1 par défaut soit COM5
		JBcon.addActionListener(new ActionListener() { //Bouton connexion/deconnexion --> connect a l'imprimante
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(stateJBcon){ //true = boutton affiche connexion
					if(ConnexionManager.connect(choix[JCBcom.getSelectedIndex()])) {
						JBcon.setText("Deconnexion");
						stateJBcon = !stateJBcon;
					}else{
						JOptionPane.showMessageDialog(null, "Une erreur est survenu lors de la connexion. Esseyez ultèrieurement", "Erreur", JOptionPane .ERROR_MESSAGE);
					}
					
				}else{
					if(ConnexionManager.close()) {
						JBcon.setText("Connexion");
						stateJBcon = !stateJBcon;
					}else{
						JOptionPane.showMessageDialog(null, "Une erreur est survenu lors de la déconnexion. Esseyez ultèrieurement", "Erreur", JOptionPane .ERROR_MESSAGE);
					}
				}
			}			
		});
		
		
		
		JPcon.add(JCBcom);
		JPcon.add(JBcon);
		JPcon.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		//Ajout JPanel dans other
		other.setLayout(new BorderLayout());
		other.add(JPcon, BorderLayout.NORTH);
		
		
		/*
		 * Gestion JMenuBar
		 */
		JMIparam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			//	optionMenu opt = new optionMenu();
			}
			
		});
		
		JMIcredit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showConfirmDialog(ref, "Sur GitHub:\n Lightel \n fjdhj", "Credits:", JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		
		JMoption.add(JMIparam);
		JMpropos.add(JMIcredit);
		
		menuBar.add(JMoption);
		menuBar.add(JMpropos);
		
		/*
		 * Gestion JFrame avec spliter
		 */
		//Definition paramètre Spliter
		JSPmain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, other, cons);
		JSPmain.setDividerLocation(675);
		//Ajout du tout dans la JFrame
		this.add(JSPmain);
		this.setJMenuBar(menuBar);
		this.setVisible(true);
	}
		
	public static void addText(String str) {
		JTAcons.append(str);

	}

}
