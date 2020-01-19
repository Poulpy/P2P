package abstractions;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Yoda {

	protected BufferedReader reader;
	protected PrintWriter writer;

	protected String adresseIP = "127.0.0.1";
	protected String adresseIPServeur = "127.0.0.1";
	protected int port = 50000;
	protected Socket socket;

    protected Yoda() {
    }


	/**
	 * Ouvre une socket pour le client
	 * TODO: gérer plusieurs clients, méthode pour ouvrir une socket seulement
	 * pour le client ? Les sockets des clients seraient stockées dans un
	 * tableau
	 */
	protected void open() {
		try {
			socket = new Socket(adresseIPServeur, port);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String lireMessage() throws IOException {
		String str;
		str = reader.readLine();          // lecture du message
		return str;
	}

	protected void envoyerMessage(String msg) {
		writer.println(msg);          // envoi d'un message
		writer.println("END");          // envoi d'un message
	}

	/**
	 * Ferme la socket du client
	 * Après que le client ait envoyé la commande QUIT
	 */
	protected void close() {
		try {
			socket.close();
			reader.close();
			writer.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

