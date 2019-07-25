package serialCom;

import java.util.HashMap;
import java.util.Set;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

public abstract class ConnexionManager {
	private static HashMap<String,Connexion> Connections = new HashMap<String,Connexion>();
	
	
	public static boolean connect(int nbr){
		Connexion nouv = new Connexion();
		if(nouv.connect(nbr)) {
			Connections.put(new String("COM"+nbr),nouv);
			return true;
		}
		return false;
	}
	
	
	public static boolean connect(String nom){
		Connexion nouv = new Connexion();
		if( nouv.connect(nom)) {
			Connections.put(nom,nouv);
			return true;
		}
		return false;
	}
	
	public static String[] getAvailiblePortNames() {
		SerialPort[] ports = SerialPort.getCommPorts();
		String[] names = new String[ports.length];
		for(int i=0; i<ports.length;i++) {
			names[i] = ports[i].getSystemPortName();
		}
		return names;
	}
	
	public static void send(String key,String message) {
		Connections.get(key).send(message);
	}

	public static String getLogs(String key) {
		if(Connections.get(key)==null)
			return "";
		return Connections.get(key).getLogs();
	}

	public static boolean close(String key) {
		boolean succes = Connections.get(key).close();
		if(succes) {
			Connections.remove(key);
			return true;
		}else {
			return false;
		}
	}
	
	public static void addDataListener(String key,SerialPortDataListener listener) {
		Connections.get(key).addDataListener(listener);
	}
	
	public static void waitForAnswer(String key,String message) {
		Connections.get(key).waitForAnswer(message);
	}
}
