package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import out.Settings;
import ui.Fenetre;

public class Main {
	
	public static void main(String[] args) {
		Settings.checkSettings();
		Fenetre fenetre = new Fenetre();
	}	
}
