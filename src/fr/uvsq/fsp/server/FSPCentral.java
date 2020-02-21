package fr.uvsq.fsp.server;

import fr.uvsq.fsp.abstractions.FSPCore;
import fr.uvsq.fsp.util.Command;
import fr.uvsq.fsp.util.CSVParser;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FSPCentral extends FSPCore implements Runnable {

	/**
	 * Folder containing the descriptions/ folder and the users file
	 */
	public static String configFolder;

	/**
	 * Chemin du fichier contenant les utilisateurs connus du serveur
	 * l'identifiant et le hash du mot de passe sont séparés par le
	 * séparateur sep
	 */
	public static String cheminUtilisateurs;

	/** Répertoire des fichiers partagés, créé au lancement du serveur */
	public static String descriptionsFolder;

	public ServerSocket serverSocket;

	/** Liste des utilisateurs connectés */
	public static ArrayList<String> usersConnected;
	static {
		usersConnected = new ArrayList<String>();
	}

	public static Map<String, String> users;

	private String id;
	private String mdp;

	private boolean nouvelUtilisateur = false;

	public String hostname;

	public String userDescriptionFolder;

	public boolean isClient;

	/**
	 * Crée le répertoire contenant les descriptions des fichiers partagés,
	 * s'il n'est pas déjà créé
	 */
	public FSPCentral(Socket sock) {
		super(sock);
		configFolder = "central/";
		cheminUtilisateurs = configFolder + "utilisateurs.csv";
		descriptionsFolder = configFolder + "descriptions/";
		try {
			users = CSVParser.read(cheminUtilisateurs);
		} catch (IOException e) {
			e.printStackTrace();
		}

		new File(descriptionsFolder).mkdirs();
	}

	public FSPCentral(Socket sock, String configDir) {
		super(sock);
		configFolder = configDir;
		cheminUtilisateurs = configFolder + "utilisateurs.csv";
		descriptionsFolder = configFolder + "descriptions/";
		try {
			users = CSVParser.read(cheminUtilisateurs);
		} catch (IOException e) {
			e.printStackTrace();
		}

		new File(descriptionsFolder).mkdirs();
	}

	public void disconnect() {
		try {
			serverSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void connect() throws IOException, UnknownHostException {
		serverSocket = new ServerSocket(port, 10);
	}

	/**
	 * Gestion des commandes
	 * En fonction de la commande, le serveur fait une action
	 * particulière;
	 * Par exemple, "USER toto"
	 * Le serveur vérifie que l'utilisateur toto existe;
	 * Si toto existe pas, il le crée
	 * "PASS fierhigmeruis"
	 * Le serveur vérifie que le mot de passe est correct en fonction
	 * de l'identifiant précédemment envoyé
	 */
	public boolean handleServerCommands(Command cmd) throws IOException {
		String contenu;
		boolean quit;

		contenu = cmd.content;
		quit = false;

		switch (cmd.command) {
			case "USER":
				id = contenu;
				handleUserCommand(contenu);
				break;

			case "PASS":
				handlePassCommand(contenu);
				break;

			case "HOSTNAME":
				handleHostnameCommand(contenu);
				break;

			case "FILECOUNT":
				saveDescriptions(userDescriptionFolder, Integer.parseInt(contenu));
				break;

			case "QUIT":
				quit = true;
			default:
		}

		return quit;
	}

	public boolean handleClientCommands(Command cmd) throws IOException {
		String contenu;
		boolean quit;

		contenu = cmd.content;
		quit = false;

		switch (cmd.command) {
			case "SEARCH":
				handleSearchCommand(contenu);
				break;

			case "HOST":
				hostname = contenu;
				handleHostCommand(contenu);
				break;

			case "STOP":
				quit = true;

			default:
		}

		return quit;
	}

	public boolean isClientLogged() {
		return usersConnected.contains(hostname);
	}

	public void handleHostCommand(String hostname) throws IOException {
		if (usersConnected.contains(hostname)) {
			super.envoyerMessage("25 Hostname existe");
		} else {
			super.envoyerMessage("32 Hostname n'existe pas");
			disconnect();
			close();
		}
	}

	/**
	 * Le serveur envoie ce message quand il n'a pas trouvé
	 * de fichiers correspondant au mot clef donné par un
	 * client
	 *
	 * NOTFOUND
	 */
	public void notFound() throws IOException {
		super.envoyerMessage("NOTFOUND");
	}

	/**
	 * Le serveur envoie les résultats correspondant au mot
	 * clef donné par le client. Il peut y avoir plusieurs
	 * fichiers
	 *
	 * FOUND dinfo/f1.txt dinfo/truc.txt
	 */
	public void found(ArrayList<String> files) throws IOException {
		String content;

		content = new String();

		for (String file : files) {
			content += file + " ";
		}

		System.out.println("FOUND " + content);
		envoyerMessage("FOUND " + content);
	}

	/**
	 * Cherche un mot dans un fichier
	 *
	 * Retourne un booléen indiquant si le mot clef a été trouvé
	 */
	public boolean searchByKeyword(String keyword, File fileToSearch) throws IOException {
		BufferedReader reader;
		String currentLine;
		boolean match;

		match = false;
		reader = new BufferedReader(new FileReader(fileToSearch));

		while ((currentLine = reader.readLine()) != null && !match) {
			if (currentLine.contains(keyword)) {
				match = true;
			}
		}

		reader.close();

		return match;
	}

	/**
	 * Cherche dans les fichiers d'un dossier un mot clef
	 *
	 * Retourne le chemin dess fichiers contenant le motif
	 */
	public ArrayList<String> searchFolderByKeyword(String keyword, String folderToSearch) throws IOException {
		File fileToSearch;
		ArrayList<String> filesMatching = new ArrayList<String>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderToSearch))) {
			for (Path path : stream) {
				fileToSearch = new File(path.toString());
				if (searchByKeyword(keyword, fileToSearch)) {
					filesMatching.add(path.toString());
				}
			}
		}

		return filesMatching;
	}

	/**
	 * Retourne le nom d'hôte et le chemin du fichier, fichier qui correspond
	 * au mot clef donné en argument.
	 * Plusieurs fichiers peuvent être renvoyés (on retourne un tableau)
	 * Si le mot clef se trouve dans un fichier 'yojinbo' de l'utilisateur 'dinfo' :
	 * Renvoie 'dinfo/yojinbo'
	 */
	public ArrayList<String> searchUsersFoldersByKeyword(String keyword) throws IOException {
		String path;
		ArrayList<String> files;
		ArrayList<String> allMatchingFiles;

		files = new ArrayList<String>();
		allMatchingFiles = new ArrayList<String>();

		for (String userConnected : usersConnected) {
			path = descriptionsFolder + userConnected + File.separator;

			files = searchFolderByKeyword(keyword, path);

			for (int j = 0; j != files.size(); j++) {
				int i = files.get(j).indexOf(userConnected);
				files.set(j, files.get(j).substring(i));
			}

			allMatchingFiles.addAll(files);
		}

		return allMatchingFiles;
	}


	/**
	 * Thread
	 */
	public void listen() throws IOException, UnknownHostException {
		String msg;
		Command ftpCmd;
		boolean quit;

		quit = false;

		while (!quit && (msg = lireMessage()) != null) {

			ftpCmd = Command.parseCommand(msg);
			System.out.println(msg);

			if (isClient) {
				quit = handleClientCommands(ftpCmd);
			} else {
				quit = handleServerCommands(ftpCmd);
			}
		}

	}

	@Override
	public void run() {
		open();

		try {
			waitForTypeCommand();
			listen();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!isClient) {
			// deconnexion
			synchronized (usersConnected) {
				usersConnected.remove(hostname);
			}
		}

		//disconnect();
		close();
	}

	/**
	 * Gère la connexion d'un utilisateur
	 * Crée un dossier désigné par son nom d'hôte, qui stockera toutes ses descriptions
	 */
	public void handleHostnameCommand(String hostname) throws IOException {
		synchronized (usersConnected) {
			usersConnected.add(hostname);
		}
		userDescriptionFolder = descriptionsFolder + hostname + File.separator ;
		new File(userDescriptionFolder).mkdirs();
	}

	/**
	 * Cherche dans le dossier descriptions/ un mot clef
	 * Renvoie les résultats au client
	 */
	public void handleSearchCommand(String keyword) throws IOException {
		ArrayList<String> files;

		files = new ArrayList<String>();

		files = searchUsersFoldersByKeyword(keyword);

		if (files.isEmpty()) {
			notFound();
		} else {
			found(files);
		}
	}

	public void handlePassCommand(String mdp) throws IOException {
		if (nouvelUtilisateur) {
			users.put(this.id, mdp);
			nouvelUtilisateur = false;
			saveUsers();
			envoyerMessage("24 Utilisateur créé : " + this.id + ", " + mdp);
		} else if (users.get(this.id).equals(mdp)) {
			envoyerMessage("23 Mot de passe correct");
		} else {
			envoyerMessage("31 Mot de passe incorrect pour " + this.id);
		}
	}

	public void handleUserCommand(String id) throws IOException {
		if (users.keySet().contains(id)) {
			envoyerMessage("21 Bon identifiant");
		} else {
			nouvelUtilisateur = true;
			envoyerMessage("22 Identifiant inconnu");
		}
	}

	public void waitForTypeCommand() throws IOException {
		String msg;
		Command cmd;

		do {
			do {
				msg = lireMessage();
				cmd = Command.parseCommand(msg);
			} while (!cmd.command.equals("TYPE"));
		} while (!handleTypeCommand(cmd.content));
	}

	public boolean handleTypeCommand(String type) throws IOException {
		if (type.equals("SERVER")) {
			isClient = false;
			return true;
		} else if (type.equals("CLIENT")) {
			isClient = true;
			return true;
		} else {
			return false;
		}
	}

	public void saveUsers() throws IOException {
		CSVParser.write(users, cheminUtilisateurs);
	}
}

