package serialCom;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import com.fazecast.jSerialComm.SerialPort;

import serialCom.Connexion.States;

public class BridgeConnexion extends Connexion {
	static boolean Running;
	protected SerialPort portCom;
	protected Socket socket;
	protected SerialBridge serialBridge;
	protected SocketBridge socketBridge;
	protected Thread writerThread;

	protected String adresse;
	protected int port;
	protected String nom;
	protected Timer timer;
	
	public BridgeConnexion(String adresse, int port, String nom) {
		this.adresse = adresse;
		this.port = port;
		this.nom = nom;
	}
	
	@Override
	protected boolean connect() {
		//Connexion de la Socket
		notifyObserver(States.CONNEXION);
		new Thread(){
			 @Override public void run () {
					try {
						//Connexion du port s�rie
						System.out.println("Recherche du port: "+nom);
						SerialPort[] ports = SerialPort.getCommPorts();
						String[] noms = ConnexionManager.getAvailiblePortNames();
						for(int i=0; i<ports.length;i++) {
							System.out.println("Port detecte: " + noms[i]);
							if(noms[i].contains(nom)) {
								portCom = ports[i];
								System.out.println("Port: "+nom+" obtenu.\n Ouverture");
								break;
							}
						}
						if(portCom!= null && portCom.openPort()) {
							System.out.println("Connexion �tablie avec succ�s!");
							//Connexion de la socket
							socket = new Socket(adresse,port);
							init();
						}else {
							System.err.println("Erreur la connexion n'a pas �t� �tablie.");
							notifyObserver(States.ERREUR_CONNEXION);
						}
						} catch (UnknownHostException e) {
						notifyObserver(States.ERREUR_CONNEXION);
				        e.printStackTrace();
				     }catch (IOException e) {
				     }						
			 }
		}.start();
		
		return true;
	}

	protected void init() {
		System.out.println("Initialisation du Writer et du Listener...");		
		Running = true;
		timer = new Timer();
		serialBridge = new SerialBridge();
		socketBridge = new SocketBridge();
		listenerThread = new Thread(serialBridge);
		listenerThread.start();
		
		writerThread = new Thread(socketBridge);
		writerThread.start();
		notifyObserver(States.CONNECTE);
	}
	
	@Override
	protected void send(String message) {}

	@Override
	protected void waitForAnswer(String message) {}

	@Override
	protected boolean close() {
		Running = false;
		if(socket!=null){
			timer.cancel();
			timer.purge();
			if(portCom.closePort() && !portCom.isOpen()) {
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						return false;
					}
					try {
						listenerThread.join();
						writerThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
						return false;
					}
				}
		}
		notifyObserver(States.DECONNECTE);				
		return true;
	}

	protected void connexionClosed() {
		timer.cancel();
		timer.purge();
		Running = false;
		if(portCom.closePort() && !portCom.isOpen()) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		notifyObserver(States.ERREUR_COMM);
	}
	
	private class SocketBridge implements Runnable{
		
		private BufferedInputStream bis = null;

		@Override
		public void run() {
			try {
				bis = new BufferedInputStream(socket.getInputStream());
				String stringBuffer;
				while(Running) {
						 //Lecture de la socket
						 byte[] b = new byte[bis.available()];
						 int stream = bis.read(b);
						 if(b.length>0) {
							 stringBuffer = new String(b);
							 while(stringBuffer.contains(PING)) {
									stringBuffer = stringBuffer.substring(0, stringBuffer.indexOf(PING)) + stringBuffer.substring(stringBuffer.indexOf(PING)+PING.length());
								 }
								 if(stringBuffer.length()>0) {
										log(socket.getInetAddress().getHostAddress()+":  "+ stringBuffer+"\n");	
							//Ecriture dans le port s�rie
							portCom.writeBytes(b, stream);
						 }
					 }
					 delay();
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				connexionClosed();
			} catch (IOException e) {
				e.printStackTrace();
				connexionClosed();
			}finally{
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
	}
	}
	
		
	
	private class SerialBridge implements Runnable{
		private BufferedOutputStream out;
		@Override
		public void run() {
			try {
				out = new BufferedOutputStream(socket.getOutputStream());
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					  public void run() {
						try {
							if(!socket.isClosed()) {
								out.write(new String(PING).getBytes());
								out.flush();
							}
						} catch (IOException e) {
							e.printStackTrace();
							connexionClosed();
						}
					  }
				}, 2*1000, 2*1000);
				
				while(Running) {
					if(portCom.bytesAvailable()>1) {
						//On lit le port s�rie
						byte[] buffer = new byte[portCom.bytesAvailable()];
						portCom.readBytes(buffer, buffer.length);
						log(portCom.getPortDescription() + ":  "+ new String(buffer));
						//On �crit la socket
						out.write(buffer);
						out.flush();
					}					 
					delay();
				}	
			
			} catch (IOException e) {
				e.printStackTrace();
				connexionClosed();
			}finally{
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
