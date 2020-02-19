package fr.uvsq.fsp.abstractions;

import fr.uvsq.fsp.util.Command;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FSPCore {

	public DataOutputStream dos;
	public DataInputStream dis;

	/** Adresse IP du serveur */
	public String adresseIPServeur = "127.0.0.1";

	public int port = 50000;

	public Socket socket;

	/**
	 */
	public FSPCore(String serverIP, int portNumber) {
		adresseIPServeur = serverIP;
		port = portNumber;
	}

	public FSPCore(int portNumber) {
		port = portNumber;
	}

	public FSPCore(Socket sock) {
		socket = sock;
	}

	/**
	 * Ouvre des ressources pour écrire/lire dans des sockets
	 */
	public void open() {
		try {
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
	 * Envoie d'abord le nombre de fichiers à envoyer
	 *
	 * descriptionsDir est le répertoire où sont les descriptions
	 * Server
	 */
	public void envoyerDescriptions(String descriptionsDir) throws IOException {
		File dir;

		dir = new File(descriptionsDir);

		// On envoie d'abord le nombre de fichiers à envoyer
		envoyerMessage("FILECOUNT " + dir.list().length);

		// On liste les fichiers partagés
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(descriptionsDir))) {
			for (Path path : stream) {
				envoyerFichier(path.toString());
			}
		}
	}

	/**
	 * dir est le répertoire où sont les descriptions
	 * Central
	 */
	public void saveDescriptions(String dir, int fileCount) throws IOException {
		for (int i = 0; i != fileCount; i++) {
			lireFichier(dir);
		}
	}

	/**
	 * Lit un message envoyé par socket
	 */
	public String lireMessage() throws IOException {
		return dis.readUTF();
	}

	/**
	 * Envoie une message à travers une socket
	 */
	public void envoyerMessage(String msg) throws IOException {
		dos.writeUTF(msg);
		dos.flush();
	}

	public void close() {
		try {
			dos.close();
			dis.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Envoie UN fichier par socket
	 */
	public void envoyerContenu(String filePath) throws IOException {
		File file;
		InputStream in;
		int fileSize;
		byte[] bytes;
		int count;

		file = new File(filePath);
		fileSize = (int) file.length();// pas utilisée
		in = new FileInputStream(file);
		bytes = new byte[fileSize];

		// On envoie le fichier ligne par ligne
		while ((count = in.read(bytes)) > 0) {
			dos.write(bytes, 0, count);
			dos.flush();
		}

		System.out.println("Envoi fini");
		in.close();
	}

	public void enregistrerContenu(String filePath, int fileSize) throws IOException {
		int count;
		byte[] bytes;
		FileOutputStream fos;
		int sum;

		bytes = new byte[fileSize];
		fos = new FileOutputStream(filePath);
		sum = 0;

		while (sum < fileSize) {
			if ((count = dis.read(bytes)) > 0) {
				sum += count;
				fos.write(bytes, 0, count);
				fos.flush();
			}
		}

		System.out.println("Lecture finie : " + filePath);
		fos.close();
	}

	/**
	 * On envoie : le nom du fichier et sa taille (en octets)
	 * La taille n'est pas utile pour le moment, mais elle pourrait
	 * l'être plus tard ! (voir enregistrerContenu() envoyerContenu())
	 * Ensuite on envoie le contenu
	 */
	public void envoyerFichier(String filePath) throws IOException {
		String msg;
		Command cmd;
		String fileName;
		File file;

		file = new File(filePath);
		fileName = file.getName();

		// L'étiquette FILE va indiquer qu'on envoie le nom et la taille
		envoyerMessage("FILE " + fileName + " " + file.length());
		envoyerContenu(filePath);

		// On attend l'accusé réception
		do {
			System.out.println("Waiting...");
			msg = lireMessage();
			cmd = Command.parseCommand(msg);
		} while (!cmd.command.equals("ACK"));
	}


	/**
	 * On récupère le nom de fichier et sa taille, ensuite le contenu du
	 * fichier
	 */
	public void lireFichier(String dir) throws IOException {
		String msg;
		Command ftpCmd;
		String fileName;
		int fileSize;

		// On attend un message avec pour commande FILE
		msg = lireMessage();
		ftpCmd = Command.parseCommand(msg);

		fileName = ftpCmd.content.split(" ")[0];
		fileSize = Integer.parseInt(ftpCmd.content.split(" ")[1]);
		System.out.println(dir + fileName + ", " + fileSize);
		enregistrerContenu(dir + fileName, fileSize);
		envoyerMessage("ACK");
	}
}

