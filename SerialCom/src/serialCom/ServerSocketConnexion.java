package serialCom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;



public class ServerSocketConnexion extends SocketConnexion{

	public ServerSocketConnexion(int port) {
		super("localhost", port);
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean connect() {
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
}
