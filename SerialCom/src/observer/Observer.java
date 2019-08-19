package observer;

import serialCom.Connexion;
import serialCom.ConnexionKey;

public interface Observer {
	public void update(Connexion.States state);
	public void update(ConnexionKey ID);
}
