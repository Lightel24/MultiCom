package serialCom;

import java.util.HashMap;
import java.util.Set;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

public abstract class ConnexionManager {
	private static HashMap<ConnexionKey,Connexion> Connections = new HashMap<ConnexionKey,Connexion>();
	
	public static boolean connect(ConnexionKey key){
		return Connections.get(key).connect();
	}
	
	public static ConnexionKey createSocketConnexion(String nom, int port) {
		Connexion nouv = new SocketConnexion(nom, port);
		ConnexionKey key = new ConnexionKey(nom,port);
		Connections.put(key,nouv);
		return key;
	}
	
	public static ConnexionKey createServerSocketConnexion(int port) {
		Connexion nouv = new ServerSocketConnexion(port);
		ConnexionKey key = new ConnexionKey(port);
		Connections.put(key,nouv);
		return key;
	}
	
	public static ConnexionKey createSerialConnexion(String nom){
		Connexion nouv = new SerialConnexion(nom);
		ConnexionKey key = new ConnexionKey(nom);
		Connections.put(key,nouv);
		return key;
	}
	
	public static ConnexionKey createBridgeConnexion(String socketAdresse, int port, String serialPortName){
		Connexion nouv = new BridgeConnexion(socketAdresse, port, serialPortName);
		ConnexionKey key = new ConnexionKey(socketAdresse, port, serialPortName);
		Connections.put(key,nouv);
		return key;
	}
	
	
	public static String[] getAvailiblePortNames() {
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] names = new String[ports.length];
		for(int i=0; i<ports.length;i++) {
			names[i] = ports[i].getSystemPortName();
		}
		return names;
	}
	
	public static void send(ConnexionKey key,String message) {
		Connections.get(key).send(message);
	}

	public static String getLogs(ConnexionKey key) {
		if(Connections.get(key)==null)
			return "";
		return Connections.get(key).getLogs();
	}

	public static boolean close(ConnexionKey key) {
		return Connections.get(key).close();
	}
	
	public static void addDataListener(ConnexionKey key,SerialPortDataListener listener) {
		if(Connections.get(key) instanceof SerialConnexion) {
			((SerialConnexion)Connections.get(key)).addDataListener(listener);
		}
	}
	
	public static void waitForAnswer(ConnexionKey key,String message) {
		Connections.get(key).waitForAnswer(message);
	}
}
