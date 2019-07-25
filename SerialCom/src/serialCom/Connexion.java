package serialCom;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

import main.Fenetre;


public abstract class Connexion {
	
	public String logs = "";
	protected Thread listenerThread;
	protected Thread writerThread;
	static boolean Running;
	
	public abstract boolean connect(String portName);

	protected abstract void send(String message);
	
	public abstract void log(String string);
	
	protected abstract String getLogs();

	protected abstract void waitForAnswer(String message);

	protected abstract boolean close();

}
