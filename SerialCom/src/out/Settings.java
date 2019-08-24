package out;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class Settings {
	
	private static boolean DEFAULT_PING = true;
	private static boolean PING;
	
	private static File fichier = new File("options.txt");
	
	public static void setPing(boolean value) {
		PING = value;
	}
	
	public static boolean isPing() {
		return PING;
	}

	public static void checkSettings() {
		if(!fichier.exists()) {
			System.out.println("Création du fichier de configuration...");
			reset();
			saveSettings();
		}else {
			System.out.println("Lecture du fichier de configuration...");
			loadSettings();
		}
	}

	private static void reset() {
		PING = DEFAULT_PING;
	}

	private static void loadSettings() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fichier));
			String line;
			while((line = br.readLine()) != null){
				/*Pour le PING */
				if(line.substring(0, line.indexOf('=')).equals("PING")) {
					if(line.substring(line.indexOf('=')+1).equals("true")) {
						PING = true;
					}else {
						PING = false;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void saveSettings() {
		System.out.println("Enregistrement du fichier de configuration...");
		BufferedWriter bw = null;
		try {
			 bw = new BufferedWriter(new FileWriter(fichier));

			 /*-------Enregistrement du ping----------*/
			 bw.write("PING="+PING + "\n");
			 System.out.print("PING="+PING + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	 System.out.println("Enregistrement terminé");
	}

}
