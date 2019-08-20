package serialCom;

import java.util.ArrayList;

import main.Fenetre;
import observer.Observable;
import observer.Observer;


public abstract class Connexion implements Observable{
	
	public static enum States{
		ATTENTE_CONNEXION,CONNECTE,DECONNECTE,CONNEXION,LOG,ERREUR_CONNEXION,ERREUR_COMM,ERREUR_CONSTRUCTEUR;
	}
	
	protected static final String PING = "$$ping$$";
	
	protected ArrayList<Observer> observers = new ArrayList<Observer>();
	protected String logs = "";
	protected Thread listenerThread;
	protected boolean Running;
	
	protected abstract void send(String message);
	
	protected void log(String string) {
		logs+=string;
		notifyObserver(States.LOG);
	}
	
	protected String getLogs() {
		return logs;
	}
	protected abstract void waitForAnswer(String message);

	protected abstract boolean close();

	protected abstract boolean connect();

	protected void delay() {
        try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void addObserver(Observer ob) {
		observers.add(ob);
	}
	
	public void removeObserver(Observer ob) {
		observers.remove(ob);
	}
	
	@Override
	public void notifyObserver(States state) {
		for(Observer obs : observers)
		      obs.update(state);
	}
	
	@Override
	public void notifyObserver(ConnexionKey ID) {}
}
	

