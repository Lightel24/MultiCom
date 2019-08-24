package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import observer.Observable;
import observer.Observer;
import serialCom.Connexion;
import serialCom.Connexion.States;
import serialCom.ConnexionKey;
import serialCom.ConnexionManager;
import serialCom.DagoPrint;

public class ConnexionPanel extends JPanel implements Observer, Observable{
	
	protected JLabel JCBcom;
	protected JComboBox JDDBactions;
	protected String[] actions = new String[] {"Impression GCode","Test"};
	protected JButton JBcon ;
	protected JPanel ref = this;
	protected boolean connecte = false;
	private boolean selectionne = false;
	private States etat = States.DECONNECTE;
	
	private ConnexionKey ID;
	protected ArrayList<Observer> observers = new ArrayList<Observer>();
	
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
					notifyObserver(ID);
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
				
				if(connecte || etat == States.ATTENTE_CONNEXION || etat == States.CONNEXION){ //true = boutton affiche connexion
					ConnexionManager.close(ID);
				}else{
					ConnexionManager.connect(ID);
				}
				JDDBactions.setEnabled(connecte);
				}
		});
	}
	
	public void setServerBridgeConnexion(int port1, String rep) {
		ID = ConnexionManager.createServerBridgeConnexion(port1, rep);
		ConnexionManager.addObserver(ID,this);			
	}

	public void setBridgeConnexion(String adresse, int port1, String rep) {
		ID = ConnexionManager.createBridgeConnexion(adresse, port1, rep);
		ConnexionManager.addObserver(ID,this);
	}

	public void setSocketConnexion(String adresse,int port) {
		ID = ConnexionManager.createSocketConnexion(adresse, port);
		ConnexionManager.addObserver(ID,this);
	}
	
	public void setServerSocketConnexion(int port) {
		ID = ConnexionManager.createServerSocketConnexion(port);
		ConnexionManager.addObserver(ID,this);
	}
	
	public void setSerialConnexion(String adresse) {
		ID = ConnexionManager.createSerialConnexion(adresse);
		ConnexionManager.addObserver(ID,this);
	}

	public void selectionne(boolean selectionne) {
		this.selectionne  = selectionne;
		updateButtonState();
	}

	private void updateButtonState() {
		if(selectionne) {
			if(etat == States.DECONNECTE || etat == States.ATTENTE_CONNEXION || etat == States.CONNEXION) {
				JBcon.setEnabled(true);				
			}else {
				JBcon.setEnabled(connecte);				
			}
			
			if(connecte) {
				JDDBactions.setEnabled(true);
			}else {
				JDDBactions.setEnabled(false);
			}
		}else {
			JBcon.setEnabled(false);
			JDDBactions.setEnabled(false);
		}
	}
	
	public ConnexionKey getID() {
		return ID;
	}

	@Override
	public void update(States state) {
		switch(state) {
		case ATTENTE_CONNEXION:
			connecte = false;
			JBcon.setText("Attente d'une connexion");
			etat = state;
			break;
			
		case CONNECTE:
			connecte = true;
			JBcon.setText("Deconnexion");
			etat = state;
			break;

		case CONNEXION:
			connecte = false;
			JBcon.setText("Connexion en cours");
			etat = state;
			break;
			
		case DECONNECTE:
			connecte = false;
			JBcon.setText("Connexion");
			etat = state;
			break;
			
		case ERREUR_COMM:
			connecte = false;
			JBcon.setText("Connexion");
			JOptionPane.showMessageDialog(this, "Une erreur de communication est survenue","Erreur",JOptionPane.OK_OPTION,null);
			etat = States.DECONNECTE;
			break;
			
		case ERREUR_CONNEXION:
			connecte = false;
			JBcon.setText("Connexion");
			JOptionPane.showMessageDialog(this, "La connexion a échoué","Erreur",JOptionPane.OK_OPTION,null);
			etat = States.DECONNECTE;
			break;
			
		case LOG:
			notifyObserver(ID);
			break;
			
		default:
			break;
		}
		updateButtonState();
	}

	public void addObserver(Observer ob) {
		observers.add(ob);
	}
	
	public void removeObserver(Observer ob) {
		observers.remove(ob);
	}

	@Override
	public void notifyObserver(ConnexionKey ID) {
		for(Observer obs : observers)
		      obs.update(ID);
	}
	
	@Override
	public void notifyObserver(Connexion.States state) {}

	@Override
	public void update(ConnexionKey ID) {}

}