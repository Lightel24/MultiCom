package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import serialCom.ConnexionKey;
import serialCom.ConnexionManager;
import serialCom.DagoPrint;

public class Fenetre extends JFrame{
	
	private JSplitPane splitPane;	
	protected JFrame ref = this;
	
	private JPanel Gauche;
	private JPanel Droite;
	private JPanel Liste;

	private static JTextArea JTAcons;
	private JScrollPane scroll;
	private JScrollPane scrollPane;
	private JCheckBox AutoScroll;
	private JTextField JTFenter;
	private JButton JBsend;
	
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu JMoption = new JMenu("Option");
	private JMenu JMpropos = new JMenu("A propos");
	private JMenuItem JMIparam = new JMenuItem("Paramètre");
	private JMenuItem JMIadd = new JMenuItem("Ajouter");
	private JMenuItem JMIcredit = new JMenuItem("Credit");
	
	public ConnexionKey currentConnection;
	
	public Fenetre() {
		initialize();
		this.setVisible(true);
	}
	
	
	private void initialize() {
		String[] ports = ConnexionManager.getAvailiblePortNames();
		if(ports.length<=0) {
			int rep = JOptionPane.showConfirmDialog(ref, "Pas de port série actif detectés", "Erreur", JOptionPane.CANCEL_OPTION,JOptionPane.ERROR_MESSAGE);
			if(rep!=JOptionPane.OK_OPTION) {
				System.exit(0);
			}
		}
		
		//ports = new String[]{"COM3","COM4"};	//DEBUG
			
		ref = this;
		this.setTitle("MultiCom");
		this.setBounds(100, 100, 972, 517);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		
		splitPane = new JSplitPane();
		this.getContentPane().add(splitPane);
		
		Gauche = new JPanel();
		Gauche.setBackground(Color.DARK_GRAY);
		splitPane.setLeftComponent(Gauche);
		Gauche.setLayout(new BoxLayout(Gauche, BoxLayout.X_AXIS));
		
		scrollPane = new JScrollPane();
		Gauche.add(scrollPane);
		
		Liste = new JPanel();
		Liste.setBorder(new EmptyBorder(10, 10, 10, 10));
		Liste.setBounds(new Rectangle(10, 10, 10, 10));
		Liste.setBackground(Color.DARK_GRAY);
		scrollPane.setViewportView(Liste);
		
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
		Liste.setLayout(new BoxLayout(Liste, BoxLayout.PAGE_AXIS));
		
		Component horizontalStrut = Box.createHorizontalStrut(300);
		horizontalStrut.setMaximumSize(new Dimension(300, 0));
		Liste.add(horizontalStrut);
		
		/*Les multiples connections */
		ButtonGroup bg = new ButtonGroup();
		//Création des multiples UI de connection
		for(int i=0;i<ports.length;i++) {
			JRadioButton select = new JRadioButton("Selectionné");
			ConnexionPanel panel = new ConnexionPanel(ports[i],select);
			panel.setSerialConnexion(ports[i]);
			bg.add(select);
			Liste.add(panel);
			Component verticalStrut_1 = Box.createHorizontalStrut(3);
			verticalStrut_1.setMaximumSize(new Dimension(0, 3));
			Liste.add(verticalStrut_1);
		}		
		/*Fin*/
		
		
		Droite = new JPanel();
		splitPane.setRightComponent(Droite);
		Droite.setLayout(new BorderLayout(0, 0));
		
		scroll = new JScrollPane();
		Droite.add(scroll, BorderLayout.CENTER);
		
		JTAcons = new JTextArea();
		scroll.setViewportView(JTAcons);
		JTAcons.setEditable(false);

		JPanel panel = new JPanel();
		Droite.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		AutoScroll = new JCheckBox("Autoscroll");
		panel.add(AutoScroll, BorderLayout.WEST);
		
		JTFenter = new JTextField();
		panel.add(JTFenter, BorderLayout.CENTER);
		JTFenter.setColumns(10);
		
		JBsend = new JButton("Envoyer");
		panel.add(JBsend, BorderLayout.EAST);
		
		/*Actions Listeners*/
		//Bouton envoyer
		JBsend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				send();
			}		
		});
		
		AutoScroll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTAcons.setAutoscrolls(AutoScroll.isSelected());
			}		
		});
		
		//Appuie sur la touche entrée
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
		 * Gestion JMenuBar
		 */
		JMIparam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//optionMenu opt = new optionMenu();
			}
			
		});
		
		JMIadd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String adresse = JOptionPane.showInputDialog("Adresse de l'hote (vide pour annuler)");
				if((adresse != null) && (adresse.length() > 0)) {
					String port = JOptionPane.showInputDialog("Port de l'hote (vide pour annuler)");
					try{
						int port1 = Integer.parseInt(port);
							JRadioButton select = new JRadioButton("Selectionné");
							ConnexionPanel panel = new ConnexionPanel(adresse+": "+port1,select);
							panel.setSocketConnexion(adresse, port1);
							bg.add(select);
							Liste.add(panel);
							Component verticalStrut_1 = Box.createHorizontalStrut(3);
							verticalStrut_1.setMaximumSize(new Dimension(0, 3));
							Liste.add(verticalStrut_1);
							Liste.revalidate();
							Liste.repaint();
					} catch (NumberFormatException | NullPointerException nfe) {}
				}
			}
			
		});
		
		JMIcredit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showConfirmDialog(ref, "Sur GitHub:\n Lightel \n fjdhj", "Credits:", JOptionPane.PLAIN_MESSAGE);
			}
			
		});
		
		JMoption.add(JMIparam);
		JMoption.add(JMIadd);
		JMpropos.add(JMIcredit);
		
		menuBar.add(JMoption);
		menuBar.add(JMpropos);
		this.setJMenuBar(menuBar);
	}
	
	
	
	protected ConnexionKey getSelectedConnectionName() {
		return currentConnection;
	}
	
	protected void send() {
		//ENVOYER LA COMMANDE
		ConnexionKey slected = getSelectedConnectionName();
		if(slected!=null) {
		
		ConnexionManager.send(slected,JTFenter.getText());
		JTFenter.setText("");
		}
	}

	public static void refreshJTA(String str) {
		JTAcons.setText(str);
	}
	
	public void updateAllConectionPanel() {
		Component[] comp = Liste.getComponents();
		
		for(Component panel:comp) {
			if(panel instanceof ConnexionPanel && currentConnection!=null) {
				if(((ConnexionPanel) panel).getID().equals(currentConnection)) {
					((ConnexionPanel) panel).updateButtonState(true);
				}else {
					((ConnexionPanel) panel).updateButtonState(false);
				}
			}
		}
	}
	
	protected void setSelectedCOnnection(ConnexionKey text) {
		currentConnection = text;		
	}
	
	
	public class ConnexionPanel extends JPanel {
		
		protected JLabel JCBcom;
		protected JComboBox JDDBactions;
		protected String[] actions = new String[] {"Impression GCode","Test"};
		protected JButton JBcon ;
		protected JPanel ref = this;
		protected boolean stateJBcon = true;
		
		private ConnexionKey ID;

		
		public ConnexionPanel(String description, JRadioButton select) {
			this.setBorder(new EmptyBorder(10, 10, 10, 10));
			this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.add(select);
			
			/*Selection des comm*/
			select.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					if(select.isSelected()) {
						setSelectedCOnnection(ID);
						updateAllConectionPanel();
						Fenetre.refreshJTA(ConnexionManager.getLogs(ID));
					}
				}	
			});
			
			Component horizontalStrut = Box.createHorizontalStrut(10);
			horizontalStrut.setMaximumSize(new Dimension(20, 0));
			this.add(horizontalStrut);
			
			JCBcom = new JLabel(description);
			JCBcom.setBorder(new EmptyBorder(0, 0, 0, 0));
			JCBcom.setOpaque(true);
			this.add(JCBcom);
			
			Component horizontalStrut_1 = Box.createHorizontalStrut(10);
			horizontalStrut_1.setMaximumSize(new Dimension(20, 0));
			this.add(horizontalStrut_1);
			
			JBcon = new JButton("Connexion");
			JBcon.setBackground(new Color(255, 228, 181));
			JBcon.setBorderPainted(false);
			this.add(JBcon);
			

			/*Création du JComboBox*/
			JMenuItem dagoPrint = new JMenuItem("Impression GCode");
			JDDBactions = new JComboBox(actions);
			JDDBactions.setMaximumSize(new Dimension(32767, 20));
			JDDBactions.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(actions[JDDBactions.getSelectedIndex()].equals(actions[0])) {
						JFileChooser jfc = new JFileChooser();
						jfc.setMultiSelectionEnabled(false);
						jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						if(jfc.showDialog(ref.getParent(), "Fichier")==JFileChooser.APPROVE_OPTION) {
							DagoPrint impression = new DagoPrint(ID,jfc.getSelectedFile());
							Thread impressionThread = new Thread(impression);
							impressionThread.start();
						}
					}
				}		
			});
			JDDBactions.add(dagoPrint);
			JDDBactions.setEnabled(false);
			JBcon.setEnabled(false);
			this.add(JDDBactions);
			
			//Connexion
			
			JBcon.addActionListener(new ActionListener() { //Bouton connexion/deconnexion --> connect a l'imprimante
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(stateJBcon){ //true = boutton affiche connexion
							if(ConnexionManager.connect(ID)){
								JBcon.setText("Deconnexion");
								stateJBcon = !stateJBcon;
							}else{
								JOptionPane.showMessageDialog(null, "Une erreur est survenu lors de la connexion. Esseyez ultèrieurement", "Erreur", JOptionPane .ERROR_MESSAGE);
							}
					}else{
						if(ConnexionManager.close(ID)) {
							JBcon.setText("Connexion");
							stateJBcon = !stateJBcon;
						}else{
							JOptionPane.showMessageDialog(null, "Une erreur est survenu lors de la déconnexion. Esseyez ultèrieurement", "Erreur", JOptionPane .ERROR_MESSAGE);
						}
					}
					JDDBactions.setEnabled(!stateJBcon);
				}			
			});
		}
		
		public void setSocketConnexion(String adresse,int port) {
			ID = ConnexionManager.createSocketConnexion(adresse, port);
		}
		
		//Non implémenté
		public void setServerSocketConnexion(String adresse,int port) {
		//	ID = ConnexionManager.createServerSocketSocketConnexion(adresse, port);
		}
		
		public void setSerialConnexion(String adresse) {
			ID = ConnexionManager.createSerialConnexion(adresse);
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

		public ConnexionKey getID() {
			return ID;
		}
	}
}
