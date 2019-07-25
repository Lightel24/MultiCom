package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import serialCom.ConnexionManager;
import serialCom.DagoPrint;

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
	
	String[] ports;
	String[] actions = new String[]{"Envoi GCode"};
	
	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu JMoption = new JMenu("Option");
	private JMenu JMpropos = new JMenu("A propos");
	private JMenuItem JMIparam = new JMenuItem("Paramètre");
	private JMenuItem JMIcredit = new JMenuItem("Credit");
	
	public String currentConnection;
	
	public Fenetre() {
		do {
			ports = ConnexionManager.getAvailiblePortNames();
			if(ports.length<=0) {
				int rep = JOptionPane.showConfirmDialog(ref, "Pas de port actif detectés", "Erreur", JOptionPane.CANCEL_OPTION,JOptionPane.ERROR_MESSAGE);
				if(rep!=JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		}while(ports.length<=0);
		
 		
//		ports = new String[]{"COM3","COM4"};	//DEBUG
		
		//Parametres de la JFrame
		this.setName("SerialCom");
		this.setSize(1000, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		other.setLayout(new GridLayout(ports.length,1));
		
		    ButtonGroup bg = new ButtonGroup();
		//Création des multiples UI de connection
		for(int i=0;i<ports.length;i++) {
			JRadioButton select = new JRadioButton("Selectionné");
			ConnectionPanel panel = new ConnectionPanel(ports[i],select);
			bg.add(select);
	        other.add(panel);
		}
		
		
		
		//Ajout et gestion contenu JPanel cons -> ensemble console
		JTAcons.setEditable(false);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JBsend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				send();
			}		
		});
		
		JTFenter.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_ENTER) {
					send();
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {}
			
		});
		/*
		 * Ajout composants console aux JPanels
		 */
		JPsend.setLayout(new BorderLayout());
		JPsend.add(JTFenter, BorderLayout.CENTER);
		JPsend.add(JBsend, BorderLayout.LINE_END);	
		cons.setLayout(new BorderLayout());
		cons.add(scroll, BorderLayout.CENTER);
		cons.add(JPsend, BorderLayout.SOUTH);
		
		
		/*
		 * Gestion JMenuBar
		 */
		JMIparam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//optionMenu opt = new optionMenu();
			}
			
		});
		
		JMIcredit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showConfirmDialog(ref, "Sur GitHub:\n Lightel \n fjdhj", "Credits:", JOptionPane.PLAIN_MESSAGE);
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
	
	protected String getSelectedConnectionName() {
		return currentConnection;
	}
	
	protected void send() {
		//PROGRAME POUR ENVOYER LA COMMANDE
		String slected = getSelectedConnectionName();
		if(slected!=null) {
		
		ConnexionManager.send(slected,JTFenter.getText());
		JTFenter.setText("");
		}
	}

	public static void refreshJTA(String str) {
		JTAcons.setText(str);
	}
	
	public void updateAllConectionPanel() {
		Component[] comp = other.getComponents();
		
		for(Component panel:comp) {
			if(panel instanceof ConnectionPanel) {
				if(((ConnectionPanel) panel).getPortName().equals(currentConnection)) {
					((ConnectionPanel) panel).updateButtonState(true);
				}else {
					((ConnectionPanel) panel).updateButtonState(false);
				}
			}
		}
	}
	
	protected void setSelectedCOnnection(String text) {
		currentConnection = text;		
	}
	
	
	public class ConnectionPanel extends JPanel {
		
		protected JLabel JCBcom;
		protected JDropDownButton JDDBactions;
		protected JButton JBcon ;
		protected JPanel ref = this;
		protected boolean stateJBcon = true;

		
		public ConnectionPanel(String port, JRadioButton select) {
			
			JCBcom = new JLabel(port);
			JBcon = new JButton("Connexion");
			
			/*Création du JDropDownButton*/
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem dagoPrint = new JMenuItem("Impression GCode");
			dagoPrint.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser jfc = new JFileChooser();
					jfc.setMultiSelectionEnabled(false);
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					if(jfc.showDialog(ref.getParent(), "Fichier")==JFileChooser.APPROVE_OPTION) {
						DagoPrint impression = new DagoPrint(JCBcom.getText(),jfc.getSelectedFile());
						Thread impressionThread = new Thread(impression);
						impressionThread.start();
					}
				}		
			});
			popupMenu.add(dagoPrint);
			JDDBactions = new JDropDownButton("Actions",popupMenu);
			JDDBactions.setEnabled(false);
			JBcon.setEnabled(false);

			/*Selection des comm*/
			select.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					if(select.isSelected()) {
						setSelectedCOnnection(JCBcom.getText());
						updateAllConectionPanel();
						Fenetre.refreshJTA(ConnexionManager.getLogs(JCBcom.getText()));
					}
				}	
			});
			
			/*
			 * Ajout et gestion contenu JPanel other -> le reste de la JFrame (a définir)
			 */
			//1er partie : connexion imprimante
			
				JBcon.addActionListener(new ActionListener() { //Bouton connexion/deconnexion --> connect a l'imprimante
					@Override
					public void actionPerformed(ActionEvent e) {
						
						if(stateJBcon){ //true = boutton affiche connexion
							if(ConnexionManager.connect(JCBcom.getText())) {
								JBcon.setText("Deconnexion");
								stateJBcon = !stateJBcon;
							}else{
								JOptionPane.showMessageDialog(null, "Une erreur est survenu lors de la connexion. Esseyez ultèrieurement", "Erreur", JOptionPane .ERROR_MESSAGE);
							}
							
						}else{
							if(ConnexionManager.close(JCBcom.getText())) {
								JBcon.setText("Connexion");
								stateJBcon = !stateJBcon;
							}else{
								JOptionPane.showMessageDialog(null, "Une erreur est survenu lors de la déconnexion. Esseyez ultèrieurement", "Erreur", JOptionPane .ERROR_MESSAGE);
							}
						}
						JDDBactions.setEnabled(!stateJBcon);
					}			
				});
			
				
			this.add(select);
			this.add(JCBcom);
			this.add(JBcon);
			this.add(JDDBactions);
			this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}

		public void updateButtonState(boolean state) {
			if(state) {
				JBcon.setEnabled(true);
				JDDBactions.setEnabled(!stateJBcon);
			}else {
				JBcon.setEnabled(false);
				JDDBactions.setEnabled(false);
			}
		}

		public String getPortName() {
			return JCBcom.getText();
		}
	}
}
