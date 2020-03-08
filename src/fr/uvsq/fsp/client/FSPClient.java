package fr.uvsq.fsp.client;

import fr.uvsq.fsp.abstractions.FSPNode;
import fr.uvsq.fsp.util.Checksum;
import java.io.BufferedInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class FSPClient extends FSPNode {

	/** Répertoire qui contient les descriptions des fichiers partagés par le serveur */
	public final String clientFolder;
	public final String descriptionsFolder;
	public final String clientSharedFolder;
	public final String clientDownloadsFolder;

	public FSPClient(String serverIP, int port, String clientFolder) {
		super(serverIP, port);
		this.clientFolder = clientFolder;
		descriptionsFolder = clientFolder + "descriptions/";
		clientSharedFolder = clientFolder + "shared/";
		clientDownloadsFolder = clientFolder + "downloads/";
	}

	public void type() throws IOException {
		envoyerMessage("TYPE CLIENT");
	}

	public void download(String host, String fileName) throws IOException {
		FSPNode dClient = new FSPNode(host, 50000);
		dClient.connect();
		dClient.open();
		System.out.println("DOWNLOAD " + fileName);
		dClient.envoyerMessage("DOWNLOAD " + fileName);
		dClient.lireFichier(clientDownloadsFolder);
		dClient.close();
		dClient.disconnect();
	}

	/**
	 * Envoie le hostname au serveur centrale et
	 * verifie qu'il existe sur usersConnected
	 * s'il n'existe pas il ferme la connection
	 * @throws IOException
	 */
	public boolean verifieHostname() throws IOException {
		String reponse;
		String hostname;

		hostname = InetAddress.getLocalHost().getHostName();
		try {
			super.envoyerMessage("HOST " + hostname);
			reponse = super.lireMessage();
			System.out.println(reponse);

			// return reponse.startsWith("2");
			if (reponse.startsWith("2")) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Laisse l'utilisateur interroger le serveur centralisé
	 * si l'utilisateur n'a rien entré, rien n'est envoyé
	 * si l'utilisateur tape QUIT, on quitte la méthode
	 */
	public void queryCentral() {
		Scanner scan;
		String reponse;
		String query;
		boolean loop = true;

		System.out.println("Interrogez le serveur. Tapez QUIT pour quitter le programme.");
		scan = new Scanner(System.in);

		try {
			while (loop) {
				System.out.print("> ");
				query = scan.nextLine();

				if (query.equals("QUIT")) {
					loop = false;
				} else if (!query.isEmpty()) {
					search(query);
					System.out.println(lireMessage());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> parseFilesFound(String content) {
		ArrayList<String> filesMatching;
		String[] files;

		filesMatching = new ArrayList<String>();
		files = content.split(" ");

		for (String file : files) {
			filesMatching.add(file);
		}

		return filesMatching;
	}

	/**
	 * Interroge le serveur : est-ce qu'un fichier contient ce mot-clef ?
	 *
	 * SEARCH film
	 */
	public void search(String keyword) throws IOException {
		super.envoyerMessage("SEARCH " + keyword);
	}
}

