package abstractions;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
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
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8")), true);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Envoie toutes les descriptions
	 */
	public void envoyerDescriptions(String dir) throws IOException {
		File d = new File(dir);

		// On liste les fichiers partagés
		envoyerMessage("FILECOUNT " + d.list().length);

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
			for (Path path : stream) {
				System.out.println("J'envoie " + path.toString());
				envoyerDescription(path.toString());
			}
		}
	}

	public void recevoirDescriptions(String dir) throws IOException {
		String msg;
		FTPCommand ftpCmd;
		int fileCount;

		ftpCmd = FTPCommand.parseCommand(lireMessage());
		fileCount = Integer.parseInt(ftpCmd.content);

		for (int i = 0; i != fileCount; i++)
			recevoirDescription(dir);
	}

	/**
	 * Envoie la description d'un fichier, avec le nom du fichier
	 */
	protected void envoyerDescription(String filePath) {
		int index = filePath.lastIndexOf('/');
		String fileName = filePath.substring(index + 1, filePath.length());
		FTPCommand ftpCmd = new FTPCommand("FILE", fileName);

		//System.out.println(">>> " + ftpCmd.command.compareTo("FILE"));
		//System.out.println(ftpCmd);
		envoyerMessage(ftpCmd.toString());

		try {
			envoyerFichier(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Réception de la description d'un fichier dans un répertoire dir
	 */
	protected void recevoirDescription(String dir) {
		try {
			lireFichier(dir);
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
		System.out.println("> " + msg);
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
			dis.close();
			dos.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Envoie UN fichier par socket
	 */
	protected void send(String filePath) throws IOException {
		File file = new File(filePath);
		int fileSize = (int) file.length();
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[fileSize];

		while (fis.read(buffer) > 0) {
			System.out.println("J'envoie...");
			dos.write(buffer);
		}

		fis.close();
	}

	/**
	 * Récupère UN fichier envoyé par socket
	 */
	protected void save(String filePath, int fileSize) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		byte[] buffer = new byte[fileSize];

		int read = 0;
		int totalRead = 0;
		int remaining = fileSize;

		while (remaining > 0 &&
			(read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}

		fos.close();
	}

	protected void envoyerFichier(String filePath) throws IOException {
		String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
		File file = new File(filePath);
		envoyerMessage("FILE " + fileName + " " + file.length());
		send(filePath);
	}
	protected void lireFichier(String dir) throws IOException {
		String msg;
		FTPCommand ftpCmd;
		String fileName;
		int fileSize;
		do {
			msg = lireMessage();
			System.out.println("> " + msg);
			ftpCmd = FTPCommand.parseCommand(msg);
		} while (ftpCmd.command.compareTo("FILE") != 0);
		fileName = ftpCmd.content.split(" ")[0];
		fileSize = Integer.parseInt(ftpCmd.content.split(" ")[1]);
		System.out.println("fileName " + fileName);
		System.out.println("dir " + dir + fileName);
		System.out.println("fileSize " + fileSize);
		save(dir + fileName, 4096);
	}
}

