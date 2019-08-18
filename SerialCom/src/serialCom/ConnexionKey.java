package serialCom;

public class ConnexionKey {
	
	public final static int NO_PORT_NUMBER = -255;
	public String socketAdresse;
	public String serialPortName;
	public int port;
	
	public ConnexionKey(String socketAdresse, int port) {
		this.socketAdresse = socketAdresse;
		this.port = port;
	}
	
	public ConnexionKey(String serialPortName) {
		this.serialPortName = serialPortName;
		this.port = NO_PORT_NUMBER;
	}

	public ConnexionKey(int port) {
		this.port = port;
	}

	public ConnexionKey(String socketAdresse, int port, String serialPortName) {
		this.socketAdresse = socketAdresse;
		this.serialPortName = serialPortName;
		this.port = port;	}

	public int getPort() {
		return port;
	}
}
