package serialCom;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import main.Fenetre;


public class SocketConnexion extends Connexion{

	protected SocketListener listener;
	protected SocketWriter writer;
	private Socket socket;

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
	
	private void init() {
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
		writer.send(message);
	}

	@Override
	protected String getLogs() {
		return logs;
	}

	@Override
	protected void waitForAnswer(String message) {
		listener.waitForAnswer(message);
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
	public void log(String string) {
		logs+=string;
		Fenetre.refreshJTA(logs);
	}
	
	private class SocketListener implements Runnable{
		private volatile String toWait = "";
		private BufferedInputStream bis;

		@Override
		public void run() {
			String stringBuffer = "";
			try {
				bis = new BufferedInputStream(socket.getInputStream());
				while(Running) {
					
						int stream;
				         while((stream = bis.read()) != -1){
				        	 stringBuffer += (char)stream;
				         }
						if(!stringBuffer.isEmpty()) {
							log(socket.getInetAddress().getHostAddress()+":  "+stringBuffer);	
							stringBuffer= stringBuffer.replaceAll("\r", "");
							stringBuffer= stringBuffer.replaceAll("\n", "");
							stringBuffer= stringBuffer.replaceAll("\t", "");
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
				out = new PrintWriter(socket.getOutputStream(), true);
				
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
