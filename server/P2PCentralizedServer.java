package server;

import abstractions.Yoda;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class P2PCentralizedServer extends Yoda {

	// Chemin du fichier contenant les utilisateurs connus du serveur
	// identifiant,hash du mot de passe
	private String cheminUtilisateurs = "utilisateurs.csv";
	// Répertoire des fichiers partagés
	private String repPartage = "shared/";
	private ServerSocket serverSocket;
	private String sep = "    ";
	private String id;
	private String mdp;
	private FileInputStream fis;
	//private BufferedInputStream bis;
	//private OutputStream os;
	// voir méthode gererMessage
	private boolean nouvelUtilisateur = false;

	public P2PCentralizedServer() {
	}

	/**
	 * TODO Gestion des commandes
	 * En fonction de la commande, le serveur fait une action
	 * particulière;
	 * Par exemple, "USER toto"
	 * Le serveur vérifie que l'utilisateur toto existe;
	 * Si toto existe pas, il le crée
	 * "PASS fierhigmeruis"
	 * Le serveur vérifie que le mot de passe est correct en fonction
	 * de l'identifiant précédemment envoyé
	 */
	public int gererMessage(String commande, String contenu) {
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
				} else if (mdpCorrect(id, mdp)) {
					super.envoyerMessage("200 Mot de passe correct");
					//envoyerDescriptions();
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
					sendFile(repPartage + "starwars");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					super.envoyerMessage("300 Mot de passe incorrect pour " + id);
				}
				break;
			case "QUIT":
				// TODO: fermer la socket du socket
				return 0;
			default:
		}
		return 1;
	}

	public void sendFile(String file) throws IOException {
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];

		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}

		fis.close();
		dos.close();
		System.out.println("Fichier envoyé");
	}



	/**
	 * Ouvre une socket pour le serveur
	 * Ouvre une socket pour le socket
	 * TODO: gérer plusieurs sockets, méthode pour ouvrir une socket seulement
	 * pour le socket ? Les sockets des sockets seraient stockées dans un
	 * tableau
	 */
	public void open() {
		try {
			serverSocket = new ServerSocket(port, 10, InetAddress.getByName(adresseIP));
			super.socket = serverSocket.accept();
			super.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			super.writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Libère toutes les ressources
	 */
	public void close() {
		try {
			super.close();
			serverSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * TODO Renvoie un code (succès / échec)
	 */
	public void renvoyerCode() {
	}

	/**
	 * TODO: Vérifier qu'un utilisateur existe
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
				idMdp = ligne.split(sep);// TODO: constante pour séparateur

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
	 * TODO: Vérifier qu'un mot de passe est correct pour
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
	 * TODO Création d'un utilisateur dans le fichier csv/tsv
	 * Est-ce qu'on donne l'identifiant ET le mot de passe ?
	 * Ou on renseigne d'abord l'identifiant puis on complète plus
	 * tard par le mot de passe ?
	 * On suppose que l'utilisateur n'existe pas déjà
	 * TODO : relire le code, le try est un peu bizare
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

	/* Tests
	 * TODO: mettre dans une classe à part */

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

	/* Main */

	public static void main(String[] args){
		P2PCentralizedServer s = new P2PCentralizedServer();
		s.open();

			// le serveur écoute/reçoit ce qu'on lui envoie
			/*while ((stream = reader.read(b)) != -1) {
				contenu = new String(b, 0, stream);

				// Séparation de la commande et du texte. Exemple :
				// USER toto
				// PASS mot_de_passe
				// On récupère l'indice où apparaît le le permier espace
				// On ne peut utiliser split car le message peut contenir aussi des espaces

				index = contenu.indexOf(' ');
				String type = contenu.substring(0, index);
				String text = contenu.substring(index + 1, contenu.length() - 1);
				System.out.println("COMMAND " + type);
				System.out.println("TEXT " + text);
				text = text.substring(0, text.length() - 1);
				if (s.gererMessage(type, text) == 0) break;
			}*/
		/*
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/


		s.close();
	}
}

