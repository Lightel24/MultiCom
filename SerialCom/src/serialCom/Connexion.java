package serialCom;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

import main.Fenetre;

public class Connexion {

	public String logs = "";
	protected SerialPort portCom;
	protected SerialListener listener;
	protected SerialWriter writer;
	protected Thread listenerThread;
	protected Thread writerThread;
	static boolean Running;
	
	public boolean connect(int nbr){
		System.out.println("Recherche du port: COM"+nbr);
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] noms = ConnexionManager.getAvailiblePortNames();
		for(int i=0; i<ports.length;i++) {
			System.out.println("Port detecte: " + noms[i]);
			if(noms[i].contains("COM"+nbr)) {
				portCom = ports[i];
				System.out.println("Port COM"+nbr+" obtenu.\n Ouverture");
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
	
	public boolean connect(String nom){
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

	public String getPortName() {
		return portCom.getSystemPortName();
	}
	
	private void init() {
		System.out.println("Initialisation du Writer et du Listener...");		
		Running = true;
		listener = new SerialListener();
		writer = new SerialWriter();
		listenerThread = new Thread(listener);
		listenerThread.start();
		
		writerThread = new Thread(writer);
		writerThread.start();
	}
	
	public void send(String message) {
		writer.send(message);
	}

	public boolean close() {
		if(portCom.closePort()) {
			Running = false;
			try {
				listenerThread.join();
				writerThread.join();
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
	
	public void log(String string) {
		logs+=string;
		Fenetre.refreshJTA(logs);
	}
	
	public String getLogs() {
		return logs;
	}
	
	private class SerialListener implements Runnable{
		private volatile String toWait = "";

		@Override
		public synchronized  void run() {
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
							synchronized (toWait) {
								toWait.notifyAll();
							}
						}
					stringBuffer = "";
					}
			}
		}

		public synchronized void waitForAnswer(String message) {
			toWait = message;
			try {
				synchronized (toWait) {
					toWait.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
		
	
	private class SerialWriter implements Runnable{
		private volatile String buffer = "";

		@Override
		public void run() {
			while(Running) {
				if(buffer!="") {
					portCom.writeBytes(buffer.getBytes(), buffer.getBytes().length);
					log("SENT: "+buffer+"\n");
					buffer = "";
				}
			}	
		}
		
		public void send(String message) {
			buffer += message;
		}
	
	}
}
