import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class P2PCentralizedServer {

	private String adresseIP = "127.0.0.1";
	private int port = 50000;
	// Chemin du fichier contenant les utilisateurs connus du serveur
	// identifiant,hash du mot de passe
	private String cheminUtilisateurs = "utilisateurs.csv";
	// Répertoire des fichiers partagés
	private String repPartage = "shared/";
	private ServerSocket socket;
	private Socket client;
	private String sep = "    ";
	private String id;
	private String mdp;
	private PrintWriter ecrivain;
	private BufferedOutputStream bos;
	private FileInputStream fis;
	private BufferedInputStream bis;
	private OutputStream os;
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
	public void gererMessage(String commande, String contenu) {
		switch (commande) {
			case "USER":
				id = contenu;
				if (utilisateurExiste(id)) {
					envoyerMessage("200 Bon identifiant");
				} else {
					nouvelUtilisateur = true;
					envoyerMessage("201 Identifiant inconnu");
				}
				break;
			case "PASS":
				mdp = contenu;

				if (nouvelUtilisateur) {
					EnregistrerUtilisateur(id, mdp);
					nouvelUtilisateur = false;
					envoyerMessage("202 Utilisateur créé : " + id + ", " + mdp);
				} else if (mdpCorrect(id, mdp)) {
					envoyerMessage("200 Mot de passe correct");
					envoyerDescriptions();
				} else {
					envoyerMessage("300 Mot de passe incorrect pour " + id);
				}
				break;
			case "QUIT":
				// TODO: fermer la socket du client
			default:
		}
	}

	/**
	 * Envoie un message sur la socket
	 */
	public void envoyerMessage(String msg) {
		msg += "\r\n";
		ecrivain.write(msg);
		ecrivain.flush();
	}


	/**
	 * Envoi au client des descriptions des fichiers partagés
	 * Envoie la commande "DESC" puis une description
	 */
	public void envoyerDescriptions() {
		File fichier = new File(repPartage + "starwars");
		byte [] mybytearray = new byte [(int)fichier.length()];
		try {
			fis = new FileInputStream(fichier);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		bis = new BufferedInputStream(fis);
		do {
			try {
				bis.read(mybytearray,0,mybytearray.length);
				os = client.getOutputStream();
				System.out.println("Sending " + repPartage + "starwars" + "(" + mybytearray.length + " bytes)");
				os.write(mybytearray,0,mybytearray.length);
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} while (!recevoirMessage().startsWith("2"));
		System.out.println("Done.");
	}
	/**
	 * Retourne un message reçut par socket
	 */
	public String recevoirMessage() {
		int index;
		int stream;
		byte[] b = new byte[1024];
		String msg = new String();

		try {
			BufferedInputStream lecteur = new BufferedInputStream(client.getInputStream());

			// A revoir
			while ((stream = lecteur.read(b)) != -1) {
				msg = new String(b, 0, stream);
				break;// pas beau
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return msg;
	}
	/**
	 * Ouvre une socket pour le serveur
	 * Ouvre une socket pour le client
	 * TODO: gérer plusieurs clients, méthode pour ouvrir une socket seulement
	 * pour le client ? Les sockets des clients seraient stockées dans un
	 * tableau
	 */
	public void open() {
		try {
			socket = new ServerSocket(port, 10, InetAddress.getByName(adresseIP));
			client = socket.accept();
			ecrivain = new PrintWriter(client.getOutputStream());
			bos = new BufferedOutputStream(client.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Libère toutes les ressources
	 */
	public void closeServerSocket() {
		try {
			ecrivain.close();
			bos.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ferme la socket du client
	 * Après que le client ait envoyé la commande QUIT
	 */
	public void closeClientSocket() {
		try {
			client.close();
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
		BufferedReader lecteur;
		String[] idMdp = new String[2];
		String ligne;

		try {
			lecteur = new BufferedReader(new FileReader(cheminUtilisateurs));

			// Lecture de chaque ligne
			while ((ligne = lecteur.readLine()) != null) {
				idMdp = ligne.split(sep);// TODO: constante pour séparateur

				if (idMdp[0].compareTo(identifiant) == 0) {
					return true;
				}
			}

			lecteur.close();
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
		BufferedReader lecteur;
		String[] idMdp = new String[2];
		String ligne;

		try {
			lecteur = new BufferedReader(new FileReader(cheminUtilisateurs));

			// Lecture de chaque ligne
			while ((ligne = lecteur.readLine()) != null) {
				idMdp = ligne.split(sep);

				if (idMdp[0].compareTo(identifiant) == 0 && idMdp[1].compareTo(hashMdp) == 0) {
					return true;
				}
			}

			lecteur.close();
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

		String contenu = "";

		int stream;
		byte[] b = new byte[1024];
		int index;

		try {
			BufferedInputStream lecteur = new BufferedInputStream(s.client.getInputStream());

			// le serveur écoute/reçoit ce qu'on lui envoie
			while ((stream = lecteur.read(b)) != -1) {
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
				s.gererMessage(type, text);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//s.listen();
		s.closeServerSocket();
		s.closeClientSocket();

	}
}

