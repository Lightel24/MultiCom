package serialCom;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

import main.Fenetre;

public class ConnexionManager {
	
	protected static SerialPort portCom;
	protected static SerialListener listener;
	protected static SerialWriter writer;
	protected static Thread listenerThread;
	protected static Thread writerThread;
	static boolean Running;
	
	public static boolean connect(int nbr){
		System.out.println("Recherche du port: COM"+nbr);
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] noms = getAvailiblePortNames();
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
	
	public static boolean connect(String nom){
		System.out.println("Recherche du port: "+nom);
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] noms = getAvailiblePortNames();
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
	
	public static String[] getAvailiblePortNames() {
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] names = new String[ports.length];
		for(int i=0; i<ports.length;i++) {
			names[i] = ports[i].getSystemPortName();
		}
		return names;
	}
	
	private static void init() {
		System.out.println("Initialisation du Writer et du Listener...");		
		Running = true;
		listener = new SerialListener();
		writer = new SerialWriter();
		listenerThread = new Thread(listener);
		listenerThread.start();
		
		writerThread = new Thread(writer);
		writerThread.start();
	}
	
	public static void send(String message) {
		SerialWriter.send(message);
	}

	public static boolean close() {
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
	
	public static void addDataListener(SerialPortDataListener listener) {
		if(Running) {
			portCom.addDataListener(listener);
		}
	}
	
	public static void waitForAnswer(String message) {
		SerialListener.waitForAnswer(message);
	}
	
	

	private static class SerialListener implements Runnable{
		private static volatile String toWait = "";

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
						Fenetre.addText(portCom.getSystemPortName()+":  "+stringBuffer);	
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

		public synchronized static void waitForAnswer(String message) {
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
	
		
	
	private static class SerialWriter implements Runnable{
		private static volatile String buffer = "";

		@Override
		public void run() {
			while(Running) {
				if(buffer!="") {
					portCom.writeBytes(buffer.getBytes(), buffer.getBytes().length);
					Fenetre.addText("SENT: "+buffer+"\n");
					buffer = "";
				}
			}	
		}
		
		public static void send(String message) {
			buffer += message;
		}
	
	}
}
