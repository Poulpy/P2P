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
	public void disconnect() throws UnknownHostException, IOException {
		socket.close();
	}

	/**
	 * Ouverture d'une socket
	 */
	public void connect() throws UnknownHostException, IOException {
		socket = new Socket(adresseIPServeur, port);
	}
}

