package abstractions;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import outils.FTPCommand;


public class Yoda {

	// Message
	// Ecriture dans une socket
	protected BufferedReader reader;
	// Lecture dans une socket
	protected PrintWriter writer;

	// Fichier
	protected DataOutputStream dos;
	protected DataInputStream dis;

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
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
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
		FileInputStream fileWriter = new FileInputStream(filePath);
		byte[] buffer = new byte[4096];

		while (fileWriter.read(buffer) > 0) {
			dos.write(buffer);
		}

		fileWriter.close();
	}

	/**
	 * Récupère un fichier envoyé par socket
	 */
	protected void saveFile(String filePath) throws IOException {
		FileOutputStream fileReader = new FileOutputStream(filePath);
		byte[] buffer = new byte[4096];

		int filesize = 15123; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;

		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fileReader.write(buffer, 0, read);
		}

		fileReader.close();
	}

	/**
	 * Envoie la description d'un fichier, avec le nom du fichier
	 */
	protected void envoyerDescription(String filePath) {
		int index = filePath.lastIndexOf('/');
		String fileName = filePath.substring(index + 1, filePath.length());
		FTPCommand ftpCmd = new FTPCommand("FILE", fileName);

		envoyerMessage(ftpCmd.toString());
		System.out.println(ftpCmd);

		try {
			sendFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Réception de la description d'un fichier dans un répertoire dir
	 */
	protected void recevoirDescription(String dir) {
		String msg;
		FTPCommand ftpCmd;

		try {
			do {
				msg = lireMessage();
				ftpCmd = FTPCommand.parseCommand(msg);
			} while (ftpCmd.command.compareTo("FILE") != 0);
			System.out.println(msg);

			saveFile(dir + ftpCmd.content);
			System.out.println("Fichier sauvegardé");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			reader.close();
			writer.close();
			dos.close();
			dis.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

