package serialCom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

import serialCom.Connexion.States;



public class ServerSocketConnexion extends SocketConnexion{
	

	public ServerSocketConnexion(int port) {
		super("localhost", port);
		bind();
	}
	
	protected void bind() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean connect() {
			notifyObserver(States.ATTENTE_CONNEXION);
			new Thread(){
				 @Override public void run () {
						try {
							socket = server.accept();
							init();
							notifyObserver(States.CONNECTE);
						} catch (IOException e) {
							e.printStackTrace();
							notifyObserver(States.ERREUR_CONNEXION);
						}	
				 }
			}.start();
 		return true;
	}
	
	@Override
	protected boolean close() {
		try {
			Running = false;
			if(socket != null) {
				socket.close();
				listenerThread.join();
				writerThread.join();
				if(Running==false && socket.isClosed() && !listenerThread.isAlive() && !writerThread.isAlive()) {
					notifyObserver(States.DECONNECTE);
					return true;
				}
			}else {
				server.close();
				notifyObserver(States.DECONNECTE);
				return true;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void connexionClosed() {
		Running = false;
		timer.cancel();
		try {
			server.close();
			log("ATTENTION: Le client a été déconnecté\nLe serveur va maintenant attendre une nouvelle connexion\n");
			bind();
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
