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
	}

	@Override
	public boolean connect() {
			notifyObserver(States.ATTENTE_CONNEXION);
			new Thread(){
				 @Override public void run () {
						try {
							//Connexion du port s�rie
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
								System.out.println("Connexion �tablie avec succ�s!");
								server = new ServerSocket(port);
								socket = server.accept();
								init();
							}else {
								System.err.println("Erreur la connexion n'a pas �t� �tablie.");
								notifyObserver(States.ERREUR_CONNEXION);
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
				timer.cancel();
				timer.purge();
				if(portCom.closePort() && !portCom.isOpen()) {
					socket.close();
					server.close();
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
	
	@Override
	protected void connexionClosed() {
		Running = false;
		timer.cancel();
		try {
			if(portCom.closePort() && !portCom.isOpen()) {
				server.close();
				socket.close();
				log("ATTENTION: Le client a �t� d�connect�\nLe serveur va maintenant attendre une nouvelle connexion\n");
				connect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
