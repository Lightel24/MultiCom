package serialCom;

import java.util.HashMap;
import java.util.Set;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

public abstract class ConnexionManager {
	private static HashMap<Object,Connexion> Connections = new HashMap<Object,Connexion>();
	
	
	public static Object connect(int nbr){
		Connexion nouv = new Connexion();
		if(nouv.connect(nbr)) {
			Object key = generateID();
			Connections.put(key,nouv);
			return key;
		}
		return null;
	}
	
	
	public static Object connect(String nom){
		Connexion nouv = new Connexion();
		if( nouv.connect(nom)) {
			Object key = generateID();
			Connections.put(key,nouv);
			return key;
		}
		return null;
	}
	
	private static Object generateID() {
		// TODO Auto-generated method stub
		return null;
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
		
	}
	
	public static void send(String message) {

	}

	public static boolean close(Object key) {
		return Connections.get(key).close();
	}
	
	public static boolean closeAll() {
		Set<Object> keys = Connections.keySet();
		for(Object key: keys) {
			close(key);
		}
		return false;	//TODO
	}
	
	public static void addDataListener(SerialPortDataListener listener) {
		
	}
	
	public static void waitForAnswer(String message) {

	}
	
	

	
}
