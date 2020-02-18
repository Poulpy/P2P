package fr.uvsq.fsp.abstractions;

import fr.uvsq.fsp.abstractions.FSPCore;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class FSPNode extends FSPCore {

	public FSPNode(String serverIP, int port) {
		super(serverIP, port);
	}

	/**
	 * Fermeture de la socket
	 */
	public void disconnect() {
		try {
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ouverture d'une socket
	 */
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(adresseIPServeur, port);
	}

	/**
	 * Méthode à appeler quand l'utilisateur veut quitter la session
	 * Doit recevoir un accusé réception
	 */
	public void quit() throws IOException {
		super.envoyerMessage("QUIT");
	}
}

