package server;

import abstractions.Yoda;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import outils.FTPCommand;


public class P2PCentralizedServer extends Yoda {

	// Chemin du fichier contenant les utilisateurs connus du serveur
	// l'identifiant et le hash du mot de passe sont séparés par le
	// séparateur sep
	private String cheminUtilisateurs = "utilisateurs.csv";
	private String sep = "    ";
	// Répertoire des fichiers partagés
	// TODO renommer, et le créer au lancement du serveur
	public String repPartage = "quigon/";
	private ServerSocket serverSocket;
	// voir méthode gererMessage
	private String id;
	private String mdp;
	private boolean nouvelUtilisateur = false;

	public P2PCentralizedServer() {
	}

	public void disconnect() {
		try {
			socket.close();
			serverSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void connect() {
		try {
			serverSocket = new ServerSocket(port, 10, InetAddress.getByName(adresseIP));
			socket = serverSocket.accept();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * TODO mettre FTPCommand en param
	 */
	public void gererMessage(String commande, String contenu) throws IOException {
		switch (commande) {
			case "USER":
				id = contenu;
				if (utilisateurExiste(id)) {
					super.envoyerMessage("200 Bon identifiant");
				} else {
					nouvelUtilisateur = true;
					super.envoyerMessage("201 Identifiant inconnu");
				}
				break;
			case "PASS":
				mdp = contenu;

				if (nouvelUtilisateur) {
					EnregistrerUtilisateur(id, mdp);
					nouvelUtilisateur = false;
					super.envoyerMessage("202 Utilisateur créé : " + id + ", " + mdp);
					//super.sendFile(repPartage + "starwars");
				} else if (mdpCorrect(id, mdp)) {
					super.envoyerMessage("200 Mot de passe correct");
					//super.sendFile(repPartage + "starwars");
				} else {
					super.envoyerMessage("300 Mot de passe incorrect pour " + id);
				}
				break;
			default:
		}
	}

	/**
	 * Vérifier qu'un utilisateur existe
	 * On regarde si son identifiant/nom est présent
	 * dans le fichier utilisateurs.csv
	 * Les utilisateurs sont stockés comme ça :
	 * identifiant	  hash_du_mot_de_passe
	 */
	public boolean utilisateurExiste(String identifiant) {
		BufferedReader reader;
		String[] idMdp = new String[2];
		String ligne;

		try {
			reader = new BufferedReader(new FileReader(cheminUtilisateurs));

			// Lecture de chaque ligne
			while ((ligne = reader.readLine()) != null) {
				idMdp = ligne.split(sep);

				if (idMdp[0].compareTo(identifiant) == 0) {
					return true;
				}
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Vérifier qu'un mot de passe est correct pour
	 * un utilisateur; On regarde dans le fichier
	 * utilisateurs.csv
	 */
	public boolean mdpCorrect(String identifiant, String hashMdp) {
		BufferedReader reader;
		String[] idMdp = new String[2];
		String ligne;

		try {
			reader = new BufferedReader(new FileReader(cheminUtilisateurs));

			// Lecture de chaque ligne
			while ((ligne = reader.readLine()) != null) {
				idMdp = ligne.split(sep);

				if (idMdp[0].compareTo(identifiant) == 0 && idMdp[1].compareTo(hashMdp) == 0) {
					return true;
				}
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Création d'un utilisateur dans le fichier csv/tsv
	 * TODO relire le code, le try est un peu bizare
	 */
	public void EnregistrerUtilisateur(String s, String c) {
		try (FileWriter fw = new FileWriter(cheminUtilisateurs, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw))
		{
			out.println(s + sep + c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Main */

	public static void main(String[] args){
		P2PCentralizedServer s = new P2PCentralizedServer();
		s.connect();
		s.open();

		try {
			s.send(s.repPartage + "starwars");
			s.envoyerMessage("Après le fichier");
			s.send(s.repPartage + "yojinbo");
			//s.send(s.repPartage + "yojinbo");
			//s.envoyerFichier(s.repPartage + "yojinbo");
		} catch (IOException e) {
			e.printStackTrace();
		}

		s.disconnect();
		s.close();
	}


	public void listen() {
		String msg;
		FTPCommand ftpCmd;
		try {
			while ((msg = lireMessage()) != null) {
				if (msg.compareTo("QUIT") == 0) break;

				ftpCmd = FTPCommand.parseCommand(msg);
				System.out.println(msg);

				gererMessage(ftpCmd.command, ftpCmd.content);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/* Tests
	 * TODO: mettre dans une classe à part
	 */

	public void testUtilisateurExiste() {
		if (utilisateurExiste("toto")) System.out.println("toto existe");
		if (utilisateurExiste("titi")) System.out.println("titi existe");
		if (utilisateurExiste("truc")) System.out.println("truc existe");
	}

	public void testMdpCorrect() {
		if (mdpCorrect("toto", "4cb9c8a8048fd02294477fcb1a41191a"))
			System.out.println("toto 4cb9c8a8048fd02294477fcb1a41191a");
		if (mdpCorrect("toto", "a"))
			System.out.println("toto a");
		if (mdpCorrect("titi", "21232f297a57a5a743894a0e4a801fc3"))
			System.out.println("titi 21232f297a57a5a743894a0e4a801fc3");
	}
}

