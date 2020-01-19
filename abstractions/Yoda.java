package abstractions;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Yoda {

	// Ecriture dans une socket
	protected BufferedReader reader;
	// Lecture dans une socket
	protected PrintWriter writer;

	// Adresse IP de l'utilisateur
	protected String adresseIP = "127.0.0.1";
	// Adresse IP du serveur
	protected String adresseIPServeur = "127.0.0.1";
	protected int port = 50000;
	protected Socket socket;

	/**
	 * TODO Affectation du port et des adresses IP
	 */
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

	/**
	 * Envoie un fichier à travers une socket
	 * TODO mettre variables en champs
	 */
	public void sendFile(String filePath) throws IOException {
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		FileInputStream fis = new FileInputStream(filePath);
		byte[] buffer = new byte[4096];

		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}

		fis.close();
		dos.close();
	}

	/**
	 * Récupère un fichier envoyé par socket
	 */
	protected void saveFile(String filePath) throws IOException {
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream(filePath);
		byte[] buffer = new byte[4096];

		int filesize = 15123; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}

		fos.close();
		dis.close();
	}

	/**
	 * Lit un message (une ligne) envoyé par socket
	 */
	protected String lireMessage() throws IOException {
		return reader.readLine();
	}

	/**
	 * Envoie une message à travers une socket
	 */
	protected void envoyerMessage(String msg) {
		writer.println(msg);
	}

	/**
	 * Ferme la socket du client
	 * Libère les ressources
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

