package serialCom;

public class SocketConnexion extends Connexion{

	protected SocketListener listener;
	protected SocketWriter writer;
	private String adresse;
	@Override
	public boolean connect(String portName) {
		return false;
	}

	@Override
	protected void send(String message) {
		
	}

	@Override
	protected String getLogs() {
		return null;
	}

	@Override
	protected void waitForAnswer(String message) {
		
	}

	@Override
	protected boolean close() {
		return false;
	}
	
	@Override
	public void log(String string) {
		
	}
	
	private class SocketListener implements Runnable{
		private volatile String toWait = "";

		@Override
		public synchronized  void run() {
			String stringBuffer = "";
			while(Running) {
				
					stringBuffer = new String();
				
					if(!stringBuffer.isEmpty()) {
						log(adresse+":  "+stringBuffer);	
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
		}

		public synchronized void waitForAnswer(String message) {
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

		@Override
		public void run() {
			while(Running) {
				if(buffer!="") {
					/*
					 * On écrit ici
					 * 
					 * */
					log("SENT: "+buffer+"\n");
					buffer = "";
				}
			}	
		}
		
		public void send(String message) {
			buffer += message;
		}
	}
}
