package serialCom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;



public class ServerSocketConnexion extends SocketConnexion{

	@Override
	public boolean connect(String adresse,int port) {
		try {
			socket = server.accept();
		} catch (UnknownHostException e) {
	        e.printStackTrace();
	 		return false;
	     }catch (IOException e) {
	 		return false;
	     }
		init();
 		return socket.isConnected();
	}
	
	public void setServer(int port) {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
