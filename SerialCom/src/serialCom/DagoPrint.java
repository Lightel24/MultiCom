package serialCom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class DagoPrint extends Protocole {
	
	static boolean operations = false;
	private File fichier;
	private String portName;
	
	public DagoPrint(String portName,File fichier) {
		this.fichier = fichier;
		this.portName = portName;
	}
	
	public void run(){
		if(!operations) {
			operations = true;
			BufferedReader br;
			try {
			      br = new BufferedReader(new FileReader(fichier)); 
			      String ligne = "";
			      while ((ligne = br.readLine()) != null){
			    	  if(!ligne.isEmpty() && !ligne.startsWith(";")) {
			    		  ConnexionManager.send(portName,ligne);
			    		  System.out.println("Attente de la fin de l'operation...");
			    		  ConnexionManager.waitForAnswer(portName,"ok");
			    		  System.out.println("Opération terminée");
			    	  }
			     } 
			    } catch (IOException e) {
			      e.printStackTrace();
			    }
			operations = false;
		}else {
			System.err.println("Une impression est deja en cours...");
		}

	}

}
