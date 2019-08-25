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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.KeyEvent;
import java.awt.Cursor;
import java.awt.Font;

public class Parametres extends JDialog {

	private final JPanel contentPanel = new JPanel();
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
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel EnTete = new JPanel();
		contentPanel.add(EnTete, BorderLayout.NORTH);
		EnTete.setBackground(Color.GRAY);
		
		JLabel lblParametres = new JLabel("Parametres");
		lblParametres.setFont(new Font("Verdana", Font.BOLD, 13));
		lblParametres.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		lblParametres.setForeground(Color.WHITE);
		EnTete.add(lblParametres);
		
		JPanel Content = new JPanel();
		contentPanel.add(Content, BorderLayout.CENTER);
		Content.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel Gauche = new JPanel();
		Content.add(Gauche);
		Gauche.setBackground(Color.DARK_GRAY);
		Gauche.setBorder(new LineBorder(new Color(0, 0, 0)));
		Gauche.setLayout(new BorderLayout(0, 0));
		
		
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
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY);
		horizontalBox.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("PING");
		panel.add(chckbxNewCheckBox);
		chckbxNewCheckBox.setBorder(new EmptyBorder(5, 5, 5, 5));
		chckbxNewCheckBox.setForeground(Color.WHITE);
		chckbxNewCheckBox.setBackground(Color.GRAY);
		chckbxNewCheckBox.setSelected(Settings.isPing());
		chckbxNewCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JTextPane txtpnCetteOptionEnvoie = new JTextPane();
		panel.add(txtpnCetteOptionEnvoie);
		txtpnCetteOptionEnvoie.setForeground(Color.WHITE);
		txtpnCetteOptionEnvoie.setBackground(Color.GRAY);
		txtpnCetteOptionEnvoie.setMaximumSize(new Dimension(2147483647, 80));
		txtpnCetteOptionEnvoie.setText("Cette option permet de detecter une erreur de communication. A n'utiliser que lorsque connecté à un autre MultiCom.");
		
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
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.GRAY);
		horizontalBox_1.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.LINE_AXIS));
		
		JSpinner spinner = new JSpinner();
		spinner.setMinimumSize(new Dimension(100, 20));
		spinner.setModel(new SpinnerNumberModel(Settings.getDelay(), new Integer(0), null, new Integer(5)));
		spinner.setMaximumSize(new Dimension(32767, 30));
		panel_1.add(spinner);
		
		JTextPane textPane = new JTextPane();
		panel_1.add(textPane);
		textPane.setText("Délai entre chaque actualisation de la connexion. (ms)");
		textPane.setMaximumSize(new Dimension(2147483647, 50));
		textPane.setForeground(Color.WHITE);
		textPane.setBackground(Color.GRAY);
		
		JPanel Droite = new JPanel();
		Content.add(Droite);
		Droite.setBorder(new LineBorder(new Color(0, 0, 0)));
		Droite.setLayout(new BorderLayout(0, 0));
		
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
				Settings.setDelay((int)spinner.getValue());
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
