package serialCom;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import main.Fenetre;


public class SocketConnexion extends Connexion{

	protected SocketListener listener;
	protected SocketWriter writer;
	protected Socket socket;
	protected ServerSocket server;

	public boolean connect(String adresse,int port) {
		try {
			socket = new Socket(adresse,port);
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
	
	public boolean startServer() {
		try {
			socket = server.accept();
			init();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected void init() {
		System.out.println("Initialisation du Writer et du Listener...");		
		Running = true;
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
		return Running==false && socket.isClosed() && !listenerThread.isAlive() && !writerThread.isAlive() ;
	}
	
	@Override
	protected void log(String string) {
		logs+=string;
		Fenetre.refreshJTA(logs);
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
							log(socket.getInetAddress().getHostAddress()+":  "+stringBuffer+"\n");	
							if(toWait.equals(stringBuffer)) {
								synchronized (toWait) {
									toWait.notifyAll();
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
		private PrintWriter out;
		@Override
		public void run() {
			try {
				out = new PrintWriter(socket.getOutputStream());
				
				while(Running) {
					if(buffer!="") {
						out.write(buffer);
						out.flush();
						log("SENT: "+buffer+"\n");
						buffer = "";
					}
				}	
			
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				out.close();
			}
		}
		
		public void send(String message) {
			buffer += message;
		}

	}
}
