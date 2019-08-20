package serialCom;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import main.Fenetre;
import serialCom.Connexion.States;


public class SerialConnexion extends Connexion{

	protected SerialPort portCom;
	protected SerialListener listener;
	
	private String nom;
	
	public SerialConnexion(String nom) {
		this.nom = nom;
	}

	public boolean connect(){
		notifyObserver(States.CONNEXION);
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
			notifyObserver(States.ERREUR_CONNEXION);
			return false;
		}
	}
	
	public String getPortName() {
		return portCom.getSystemPortName();
	}
	
	private void init() {
		System.out.println("Initialisation du Writer et du Listener...");		
		Running = true;
		listener = new SerialListener();
		listenerThread = new Thread(listener);
		listenerThread.start();
		
		notifyObserver(States.CONNECTE);
	}
	
	public void send(String message) {
		portCom.writeBytes(message.getBytes(), message.getBytes().length);
		log("SENT: "+message+"\n");
	}

	public boolean close() {
		if(portCom.closePort() && !portCom.isOpen()) {
			Running = false;
			try {
				listenerThread.join();
				notifyObserver(States.DECONNECTE);
				return true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void addDataListener(SerialPortDataListener listener) {
		if(Running) {
			portCom.addDataListener(listener);
		}
	}
	
	public void waitForAnswer(String message) {
		listener.waitForAnswer(message);
	}
	
	public String getLogs() {
		return logs;
	}
	
	private class SerialListener implements Runnable{
		private volatile String toWait = "";

		@Override
		public void run() {
			String stringBuffer = "";
			while(Running) {
				if(portCom.bytesAvailable()>1) {
					byte[] buffer = new byte[portCom.bytesAvailable()];
					portCom.readBytes(buffer, buffer.length);
					stringBuffer = new String(buffer);
					}
				
					if(!stringBuffer.isEmpty()) {
						log(portCom.getSystemPortName()+":  "+stringBuffer);	
						stringBuffer= stringBuffer.replaceAll("\r", "");
						stringBuffer= stringBuffer.replaceAll("\n", "");
						stringBuffer= stringBuffer.replaceAll("\t", "");
						if(toWait.equals(stringBuffer)) {
							synchronized (this) {
								this.notifyAll();
							}
						}
					stringBuffer = "";
					}
				delay();
			}
		}

		public synchronized void waitForAnswer(String message) {
			toWait = message;
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
}
