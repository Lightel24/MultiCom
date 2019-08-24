package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import out.Settings;

public class Parametres extends JDialog {

	private final JPanel contentPanel = new JPanel();
	/**
	 * Create the dialog.
	 */
	public Parametres(JFrame owner) {
		super(owner);
		this.setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel Gauche = new JPanel();
		Gauche.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(Gauche);
		Gauche.setLayout(new BorderLayout(0, 0));
			
		JPanel EnTete = new JPanel();
		Gauche.add(EnTete, BorderLayout.NORTH);
				
		JLabel lblNewLabel = new JLabel("Socket");
		EnTete.add(lblNewLabel);
			
		JPanel Liste = new JPanel();
		Gauche.add(Liste, BorderLayout.CENTER);
		Liste.setLayout(new BoxLayout(Liste, BoxLayout.PAGE_AXIS));
		
		Component verticalStrut = Box.createVerticalStrut(20);
		verticalStrut.setMaximumSize(new Dimension(0, 20));
		Liste.add(verticalStrut);
		
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		Liste.add(horizontalBox);
			
		JCheckBox chckbxNewCheckBox = new JCheckBox("PING");
		chckbxNewCheckBox.setSelected(Settings.isPing());
		chckbxNewCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		chckbxNewCheckBox.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		horizontalBox.add(chckbxNewCheckBox);
					
		JPanel Droite = new JPanel();
		Droite.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(Droite);
		Droite.setLayout(new BorderLayout(0, 0));

		JPanel EnTete1 = new JPanel();
		Droite.add(EnTete1, BorderLayout.NORTH);

		JLabel lblNewLabel1 = new JLabel("Serial");
		EnTete1.add(lblNewLabel1);


		
		JPanel Liste1 = new JPanel();
		Droite.add(Liste1, BorderLayout.CENTER);
		Liste1.setLayout(new BoxLayout(Liste1, BoxLayout.PAGE_AXIS));
	
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("Appliquer");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Settings.setPing(chckbxNewCheckBox.isSelected());
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
