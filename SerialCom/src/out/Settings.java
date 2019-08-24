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
	private static int DELAY;
	private static int DEFAULT_DELAY = 150;
	
	private static File fichier = new File("options.txt");
	
	public static void setPing(boolean value) {
		PING = value;
	}
	
	public static boolean isPing() {
		return PING;
	}
	
	public static void setDelay(int delay) {
		DELAY = delay;
	}
	
	public static int getDelay() {
		return DELAY;
	}

	public static void checkSettings() {
		reset();
		if(!fichier.exists()) {
			System.out.println("Création du fichier de configuration...");
			saveSettings();
		}else {
			System.out.println("Lecture du fichier de configuration...");
			loadSettings();
		}
	}

	private static void reset() {
		PING = DEFAULT_PING;
		DELAY = DEFAULT_DELAY;
	}

	private static void loadSettings() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fichier));
			String line;
			while((line = br.readLine()) != null){
				System.out.println(line);
				switch(line.substring(0, line.indexOf('='))) {
					case "PING":
						if(line.substring(line.indexOf('=')+1).equals("true")) {
							PING = true;
						}else {
							PING = false;
						}
					break;
					
					case "DELAY":
						try {
							DELAY = Integer.parseInt(line.substring(line.indexOf('=')+1));
						}catch(NumberFormatException e) {
							e.printStackTrace();
						}
					break;
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
			 
			 /*-------Enregistrement du délai----------*/
			 bw.write("DELAY="+DELAY + "\n");
			 System.out.print("DELAY="+DELAY + "\n");

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
