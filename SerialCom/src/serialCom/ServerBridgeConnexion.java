package serialCom;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

import com.fazecast.jSerialComm.SerialPort;

import serialCom.Connexion.States;

public class ServerBridgeConnexion extends BridgeConnexion {
	
	ServerSocket server;
	
	public ServerBridgeConnexion(int port, String nom) {
		super("localhost", port, nom);
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean connect() {
			notifyObserver(States.ATTENTE_CONNEXION);
			new Thread(){
				 @Override public void run () {
						try {
							socket = server.accept();
							
							//Connexion du port série
							System.out.println("Recherche du port: "+nom);
							SerialPort[] ports = SerialPort.getCommPorts();
							String[] noms = ConnexionManager.getAvailiblePortNames();
							for(int i=0; i<ports.length;i++) {
								System.out.println("Port detecte: " + noms[i]);
								if(noms[i].contains(nom)) {
									portCom = ports[i];
									System.out.println("Port: "+nom+" obtenu.\n Ouverture");
									break;
								}
							}
							if(portCom!= null && portCom.openPort()) {
								System.out.println("Connexion établie avec succès!");
								init();
							}else {
								System.err.println("Erreur la connexion n'a pas été établie.");
								notifyObserver(States.DECONNECTE);
							}
						}catch (IOException e) {
							e.printStackTrace();
							notifyObserver(States.ERREUR_CONNEXION);
						}	
				 }
			}.start();
		
 		return true;
	}
	
	@Override
	protected boolean close() {
		try {
			Running = false;
			if(socket != null) {
				if(portCom.closePort() && !portCom.isOpen()) {
					socket.close();
					listenerThread.join();
					writerThread.join();
					if(Running==false && socket.isClosed() && !listenerThread.isAlive() && !writerThread.isAlive()) {
						notifyObserver(States.DECONNECTE);
						return true;
					}
				}
				}else {
					server.close();
					notifyObserver(States.DECONNECTE);
					return true;
				}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
}
