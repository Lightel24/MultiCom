package serialCom;

public class ConnexionKey {
	
	public final static int NO_PORT_NUMBER = -255;
	public String adresse;
	public int port;
	
	public ConnexionKey(String adresse, int port) {
		this.adresse = adresse;
		this.port = port;
	}
	
	public ConnexionKey(String adresse) {
		this.adresse = adresse;
		this.port = NO_PORT_NUMBER;
	}

	public String getAdresse() {
		return adresse;
	}

	public int getPort() {
		return port;
	}
}
