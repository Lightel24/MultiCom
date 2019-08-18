package serialCom;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;

import main.Fenetre;


public abstract class Connexion {
	
	public String logs = "";
	protected Thread listenerThread;
	protected Thread writerThread;
	protected boolean Running;
	
	protected abstract void send(String message);
	
	protected void log(String string) {
		logs+=string;
		Fenetre.refreshJTA(logs);
	}
	
	protected String getLogs() {
		return logs;
	}
	protected abstract void waitForAnswer(String message);

	protected abstract boolean close();

	protected abstract boolean connect();
}
