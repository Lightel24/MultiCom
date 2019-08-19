package serialCom;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class SocketConnexion extends Connexion{

	protected SocketListener listener;
	protected SocketWriter writer;
	protected Socket socket;
	protected ServerSocket server;
	
	private String adresse;
	protected Timer timer;
	protected int port;

	public SocketConnexion(String adresse, int port) {
		this.adresse = adresse;
		this.port = port;
	}

	public boolean connect() {		
		notifyObserver(States.CONNEXION);
		new Thread(){
			 @Override public void run () {
					try {
						socket = new Socket(adresse,port);
						init();
						notifyObserver(States.CONNECTE);
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
		timer = new Timer();
		Running = true;
		notifyObserver(States.CONNECTE);
		listener = new SocketListener();
		writer = new SocketWriter();
		listenerThread = new Thread(listener);
		listenerThread.start();
		
		writerThread = new Thread(writer);
		writerThread.start();
	}

	@Override
	protected void send(String message) {
		if(Running) {
			writer.send(message);
		}
	}

	@Override
	protected String getLogs() {
		return logs;
	}

	@Override
	protected void waitForAnswer(String message) {
		if(Running) {
			listener.waitForAnswer(message);
		}
	}

	@Override
	protected boolean close() {
		try {
			Running = false;
			socket.close();
			listenerThread.join();
			writerThread.join();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		if(Running==false && socket.isClosed() && !listenerThread.isAlive() && !writerThread.isAlive()) {
			notifyObserver(States.DECONNECTE);
			return true;
		}
		return false;
	}
	
	protected void connexionClosed() {
		try {
			Running = false;
			timer.cancel();
			socket.close();
			listenerThread.join();
			writerThread.join();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		notifyObserver(States.ERREUR_COMM);
	}
	
	private class SocketListener implements Runnable{
		private volatile String toWait = "";
		private BufferedInputStream bis = null;

		@Override
		public void run() {
			String stringBuffer = "";
			try {
				bis = new BufferedInputStream(socket.getInputStream());
				while(Running) {
					 if(bis.available()>0) {
						 byte[] b = new byte[bis.available()];
						 int stream = bis.read(b);
						 stringBuffer = new String(b,0,stream);
						 
						 /*FILTRAGE DU PING*/
						 if(stringBuffer.contains(PING)) {
							stringBuffer = stringBuffer.substring(0, stringBuffer.indexOf(PING)) + stringBuffer.substring(stringBuffer.indexOf(PING)+PING.length());
						 }
						 if(stringBuffer.length()>0) {
							log(socket.getInetAddress().getHostAddress()+":  "+stringBuffer+"\n");	
							if(toWait.equals(stringBuffer)) {
								synchronized (toWait) {
									toWait.notifyAll();
								}
							}
						 }
					stringBuffer = "";
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					bis.close();
				} catch (IOException e) {}
			}
		}

		public void waitForAnswer(String message) {
			toWait = message;
			try {
				synchronized (toWait) {
					toWait.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
		
	
	private class SocketWriter implements Runnable{
		private volatile String buffer = "";
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
					if(buffer!="") {
						out.write(buffer.getBytes());
						out.flush();
						log("SENT: "+buffer+"\n");
						buffer = "";
					}
				}	
			
			} catch (SocketException e) {
				e.printStackTrace();
				connexionClosed();
			}catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void send(String message) {
			buffer += message;
		}

	}
}
