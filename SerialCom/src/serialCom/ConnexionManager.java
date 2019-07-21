package serialCom;

import com.fazecast.jSerialComm.*;

public class ConnexionManager {
	
	protected static SerialPort portCom;
	protected static SerialListener listener;
	protected static SerialWriter writer;
	static boolean Running;
	
	public static boolean connect(int nbr){
		System.out.println("Recherche du port: COM"+nbr);
		SerialPort[] ports = SerialPort.getCommPorts();
		for(SerialPort port : ports) {
			System.out.println(port.getSystemPortName());
			if(port.getSystemPortName().contains("COM"+nbr)) {
				portCom = port;
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
	
	private static void init() {
		System.out.println("Initialisation du Writer et du Listener...");		
		Running = true;
	}
	
	

	private static class SerialListener implements Runnable{

		@Override
		public void run() {
			byte[] buffer = new byte[256];
			while(true) {
				portCom.readBytes(buffer, buffer.length);
			}			
		}
	
	}	
	
	private static class SerialWriter implements Runnable{
		
		@Override
		public void run() {
			while(true) {
				
			}	
		}
		
		public static void send() {
			
		}
	
	}	
}
