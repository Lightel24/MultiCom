package serialCom;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.fazecast.jSerialComm.SerialPort;

public class BridgeConnexion extends Connexion {
	static boolean Running;
	protected SerialPort portCom;
	protected Socket socket;
	protected SerialBridge serialBridge;
	protected SocketBridge socketBridge;
	
	protected String adresse;
	protected int port;
	protected String nom;
	
	
	public BridgeConnexion(String adresse, int port, String nom) {
		super();
		this.adresse = adresse;
		this.port = port;
		this.nom = nom;
	}
	
	@Override
	protected boolean connect() {
		//Connexion de la Socket
		try {
			socket = new Socket(adresse,port);
		} catch (UnknownHostException e) {
	        e.printStackTrace();
	 		return false;
	     }catch (IOException e) {
	 		return false;
	     }
		
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
			return true;
		}else {
			System.err.println("Erreur la connexion n'a pas été établie.");
			return false;
		}
	}

	protected void init() {
		System.out.println("Initialisation du Writer et du Listener...");		
		Running = true;
		serialBridge = new SerialBridge();
		socketBridge = new SocketBridge();
		listenerThread = new Thread(serialBridge);
		listenerThread.start();
		
		writerThread = new Thread(socketBridge);
		writerThread.start();
		notifyObserver(States.CONNECTE);
	}
	
	@Override
	protected void send(String message) {}

	@Override
	protected void waitForAnswer(String message) {}

	@Override
	protected boolean close() {
		Running = false;
		if(portCom.closePort() && !portCom.isOpen()) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				listenerThread.join();
				writerThread.join();
				notifyObserver(States.DECONNECTE);				
				return true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private class SocketBridge implements Runnable{
		
		private BufferedInputStream bis = null;

		@Override
		public void run() {
			try {
				bis = new BufferedInputStream(socket.getInputStream());
				while(Running) {
					 if(bis.available()>0) {
						 //Lecture de la socket
						 
						 byte[] b = new byte[bis.available()];
						 int stream = bis.read(b);
						
						log(socket.getInetAddress().getHostAddress()+":  "+ new String(b,0,stream)+"\n");	
						//Ecriture dans le port série
						portCom.writeBytes(b, stream);
						 }
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
	}
	}
	
		
	
	private class SerialBridge implements Runnable{
		private BufferedOutputStream out;
		@Override
		public void run() {
			try {
				out = new BufferedOutputStream(socket.getOutputStream());
				
				while(Running) {
					if(portCom.bytesAvailable()>1) {
						//On lit le port série
						byte[] buffer = new byte[portCom.bytesAvailable()];
						portCom.readBytes(buffer, buffer.length);
						log(portCom.getPortDescription() + ":  "+ new String(buffer));
						//On écrit la socket
						out.write(buffer);
						out.flush();
					}
				}	
			
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
