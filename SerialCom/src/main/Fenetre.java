package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import observer.Observer;
import serialCom.ConnexionKey;
import serialCom.ConnexionManager;
import serialCom.DagoPrint;
import serialCom.Connexion.States;

public class Fenetre extends JFrame implements Observer{
	
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
	private JMenu JMIadd = new JMenu("Ajouter");
	private JMenuItem JMIparam = new JMenuItem("Paramètre");
	private JMenuItem JMIserverbridge = new JMenuItem("Un pont serveur");
	private JMenuItem JMIbridge = new JMenuItem("Un pont client");
	private JMenuItem JMIaddSocket = new JMenuItem("Un client socket");
	private JMenuItem JMIaddServerSocket = new JMenuItem("Un serveur socket");
	private JMenuItem JMIcredit = new JMenuItem("Credit");
	
	public ConnexionKey currentConnection;
	private boolean autoScroll = false;
	
	public Fenetre() {
		initialize();
		this.setVisible(true);
	}
	
	
	private void initialize() {
		String[] ports = ConnexionManager.getAvailiblePortNames();
		if(ports.length<=0) {
			int rep = JOptionPane.showConfirmDialog(ref, "Pas de port série actif detectés", "Erreur", JOptionPane.CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
			if(rep!=JOptionPane.OK_OPTION) {
				System.exit(0);
			}
		}

//		ports = new String[]{"COM3","COM4"};	//DEBUG
			
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
			panel.addObserver(this);
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
				autoScroll = !autoScroll;
				JTAcons.setAutoscrolls(autoScroll);
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
		
		JMIaddSocket.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String adresse = JOptionPane.showInputDialog("Adresse de l'hote");
					if((adresse != null) && (adresse.length() > 0)) {
						String port = JOptionPane.showInputDialog("Port de l'hote");
						try{
							int port1 = Integer.parseInt(port);
								JRadioButton select = new JRadioButton("Selectionné");
								ConnexionPanel panel = new ConnexionPanel(adresse+": "+port1,select);
								panel.setSocketConnexion(adresse, port1);
								panel.addObserver(Fenetre.this);
								bg.add(select);
								Liste.add(panel);
								Component verticalStrut_1 = Box.createHorizontalStrut(3);
								verticalStrut_1.setMaximumSize(new Dimension(0, 3));
								Liste.add(verticalStrut_1);
								Liste.revalidate();
								Liste.repaint();
						} catch (NumberFormatException | NullPointerException nfe) {}
					}
			}});
		
		JMIaddServerSocket.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					
					String port = JOptionPane.showInputDialog("Port du server?");
					try{
						int port1 = Integer.parseInt(port);
							JRadioButton select = new JRadioButton("Selectionné");
							ConnexionPanel panel = new ConnexionPanel("(localhost): "+port1,select);
							panel.setServerSocketConnexion(port1);
							panel.addObserver(Fenetre.this);
							bg.add(select);
							Liste.add(panel);
							Component verticalStrut_1 = Box.createHorizontalStrut(3);
							verticalStrut_1.setMaximumSize(new Dimension(0, 3));
							Liste.add(verticalStrut_1);
							Liste.revalidate();
							Liste.repaint();
					} catch (NumberFormatException | NullPointerException nfe) {}
				}
			
		});
		
		JMIbridge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					String adresse = JOptionPane.showInputDialog("Adresse de l'hote");
					if((adresse != null) && (adresse.length() > 0)) {
						String port = JOptionPane.showInputDialog("Port de l'hote");
						try{
							int port1 = Integer.parseInt(port);
							
							String[] ports = ConnexionManager.getAvailiblePortNames();
							if(ports.length>0) {
							String rep = (String) JOptionPane.showInputDialog(null, "Choisir un port série",
							        "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null, // Use
							                                                                        // default
							                                                                        // icon
							        ports, // Array of choices
							        ports[0]); // Initial choice
							
									JRadioButton select = new JRadioButton("Selectionné");
									ConnexionPanel panel = new ConnexionPanel(adresse+": "+port1,select);
									panel.addObserver(Fenetre.this);
									panel.setBridgeConnexion(adresse, port1, rep);
									bg.add(select);
									Liste.add(panel);
									Component verticalStrut_1 = Box.createHorizontalStrut(3);
									verticalStrut_1.setMaximumSize(new Dimension(0, 3));
									Liste.add(verticalStrut_1);
									Liste.revalidate();
									Liste.repaint();
							}else {
								JOptionPane.showConfirmDialog(ref, "Pas de port série actif detectés", "Erreur", JOptionPane.OK_OPTION,JOptionPane.ERROR_MESSAGE);
							}
						} catch (NumberFormatException | NullPointerException nfe) {}
					}
			}
			
		});
		
		JMIserverbridge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
						String port = JOptionPane.showInputDialog("Port du serveur");
						try{
							int port1 = Integer.parseInt(port);
							
							String[] ports = ConnexionManager.getAvailiblePortNames();
							if(ports.length>0) {
							String rep = (String) JOptionPane.showInputDialog(null, "Choisir un port série",
							        "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null, // Use
							                                                                        // default
							                                                                        // icon
							        ports, // Array of choices
							        ports[0]); // Initial choice
							
									JRadioButton select = new JRadioButton("Selectionné");
									ConnexionPanel panel = new ConnexionPanel("(localhost): "+port1,select);
									panel.addObserver(Fenetre.this);
									panel.setServerBridgeConnexion(port1, rep);
									bg.add(select);
									Liste.add(panel);
									Component verticalStrut_1 = Box.createHorizontalStrut(3);
									verticalStrut_1.setMaximumSize(new Dimension(0, 3));
									Liste.add(verticalStrut_1);
									Liste.revalidate();
									Liste.repaint();
							}else {
								JOptionPane.showConfirmDialog(ref, "Pas de port série actif detectés", "Erreur", JOptionPane.OK_OPTION,JOptionPane.ERROR_MESSAGE);
							}
						} catch (NumberFormatException | NullPointerException nfe) {}
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
		
		JMIadd.add(JMIbridge);
		JMIadd.add(JMIserverbridge);
		JMIadd.add(JMIaddServerSocket);
		JMIadd.add(JMIaddSocket);
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

	private void refreshJTA(String str) {
		JTAcons.setText(str);
	}
	
	private void updateAllConectionPanel() {
		Component[] comp = Liste.getComponents();
		
		for(Component panel:comp) {
			if(panel instanceof ConnexionPanel && currentConnection!=null) {
				if(((ConnexionPanel) panel).getID().equals(currentConnection)) {
					((ConnexionPanel) panel).selectionne(true);
				}else {
					((ConnexionPanel) panel).selectionne(false);
				}
			}
		}
	}

	@Override
	public void update(ConnexionKey ID) {
		currentConnection = ID;		
		updateAllConectionPanel();
		refreshJTA(ConnexionManager.getLogs(ID));
	}
	
	@Override
	public void update(States str) {}
	
}
