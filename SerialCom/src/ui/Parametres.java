package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.awt.GridLayout;
import javax.swing.border.LineBorder;

import out.Settings;

import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.ComponentOrientation;

public class Parametres extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtDelay;
	/**
	 * Create the dialog.
	 * @param ref 
	 */
	public Parametres(JFrame ref) {
		super(ref);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel Gauche = new JPanel();
		Gauche.setBackground(Color.DARK_GRAY);
		Gauche.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(Gauche);
		Gauche.setLayout(new BorderLayout(0, 0));
			
		JPanel EnTete = new JPanel();
		EnTete.setBackground(Color.GRAY);
		Gauche.add(EnTete, BorderLayout.NORTH);
				
		JLabel lblNewLabel = new JLabel("Socket");
		lblNewLabel.setForeground(Color.WHITE);
		EnTete.add(lblNewLabel);
			
			
		JPanel Liste = new JPanel();
		Liste.setBackground(Color.DARK_GRAY);
		Gauche.add(Liste, BorderLayout.CENTER);
		Liste.setLayout(new BoxLayout(Liste, BoxLayout.PAGE_AXIS));
		
		Component verticalStrut = Box.createVerticalStrut(5);
		verticalStrut.setMinimumSize(new Dimension(0, 5));
		verticalStrut.setMaximumSize(new Dimension(0, 5));
		Liste.add(verticalStrut);
		
		Component verticalGlue = Box.createVerticalGlue();
		verticalGlue.setMaximumSize(new Dimension(0, 15));
		Liste.add(verticalGlue);
		
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBorder(new LineBorder(Color.WHITE, 1, true));
		horizontalBox.setBackground(Color.DARK_GRAY);
		horizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		Liste.add(horizontalBox);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("PING");
		chckbxNewCheckBox.setBorder(new EmptyBorder(5, 5, 5, 5));
		chckbxNewCheckBox.setForeground(Color.WHITE);
		chckbxNewCheckBox.setBackground(Color.DARK_GRAY);
		chckbxNewCheckBox.setSelected(Settings.isPing());
		chckbxNewCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		horizontalBox.add(chckbxNewCheckBox);
		
		JTextPane txtpnCetteOptionEnvoie = new JTextPane();
		txtpnCetteOptionEnvoie.setForeground(Color.WHITE);
		txtpnCetteOptionEnvoie.setBackground(Color.DARK_GRAY);
		txtpnCetteOptionEnvoie.setMaximumSize(new Dimension(2147483647, 80));
		txtpnCetteOptionEnvoie.setText("Cette option permet de detecter une erreur de communication. A n'utiliser que lorsque connecté à un autre MultiCom.");
		horizontalBox.add(txtpnCetteOptionEnvoie);
		
		Component verticalGlue_1 = Box.createVerticalGlue();
		verticalGlue_1.setMaximumSize(new Dimension(0, 15));
		Liste.add(verticalGlue_1);
		
		Component verticalStrut_1 = Box.createVerticalStrut(5);
		verticalStrut_1.setMinimumSize(new Dimension(0, 5));
		verticalStrut_1.setMaximumSize(new Dimension(0, 5));
		Liste.add(verticalStrut_1);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		horizontalBox_1.setBorder(new LineBorder(Color.WHITE, 1, true));
		Liste.add(horizontalBox_1);
		horizontalBox_1.setBackground(Color.DARK_GRAY);
		horizontalBox_1.setAlignmentX(0.0f);
		
		txtDelay = new JTextField();
		txtDelay.setText(String.valueOf(Settings.getDelay()));
		txtDelay.setMaximumSize(new Dimension(2147483647, 25));
		txtDelay.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		txtDelay.setColumns(10);
		txtDelay.setMinimumSize(new Dimension(20, 2));
		txtDelay.setBorder(new EmptyBorder(5, 0, 5, 0));
		horizontalBox_1.add(txtDelay);
		
		JTextPane textPane = new JTextPane();
		textPane.setText("Délai entre chaque actualisation de la connexion. (ms)");
		textPane.setMaximumSize(new Dimension(2147483647, 50));
		textPane.setForeground(Color.WHITE);
		textPane.setBackground(Color.DARK_GRAY);
		horizontalBox_1.add(textPane);
			
		JPanel Droite = new JPanel();
		Droite.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(Droite);
		Droite.setLayout(new BorderLayout(0, 0));

		JPanel EnTete1 = new JPanel();
		EnTete1.setBackground(Color.GRAY);
		Droite.add(EnTete1, BorderLayout.NORTH);

		JLabel lblNewLabel1 = new JLabel("Serial");
		lblNewLabel1.setForeground(Color.WHITE);
		EnTete1.add(lblNewLabel1);
		
		JPanel Liste1 = new JPanel();
		Liste1.setBackground(Color.DARK_GRAY);
		Liste1.setMaximumSize(new Dimension(10, 10));
		Droite.add(Liste1, BorderLayout.CENTER);
		Liste1.setLayout(new BoxLayout(Liste1, BoxLayout.PAGE_AXIS));
	
		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(Color.GRAY);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		
		JButton okButton = new JButton("Appliquer");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Settings.setPing(chckbxNewCheckBox.isSelected());
				try {
				Settings.setDelay(Integer.parseInt(txtDelay.getText()));
				}catch(NumberFormatException e) {
					e.printStackTrace();
				}
				Settings.saveSettings();
				Parametres.this.dispose();
			}
		});
		
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	
	
		JButton cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Parametres.this.dispose();
			}
		});
		buttonPane.add(cancelButton);
	}

}
