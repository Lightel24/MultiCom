package observer;

import serialCom.Connexion;
import serialCom.ConnexionKey;

public interface Observable {
	public void addObserver(Observer ob);
	public void removeObserver(Observer ob);
	public void notifyObserver(Connexion.States state);
	public void notifyObserver(ConnexionKey ID);
}
