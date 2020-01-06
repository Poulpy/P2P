
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.*;


public class P2PCentralizedServer {

	private String adresseIP = "127.0.0.1";
	private int port = 50000;
	// Chemin du fichier contenant les utilisateurs connus du serveur
	// identifiant,hash du mot de passe
	private String cheminUtilisateurs = "utilisateurs.csv";

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
				if (utilisateurExiste(contenu)) {
					// Renvoyer code de succès
				} else {
					//EnregistrerUtilisateur("tmp", "tmp");// TODO: à remplacer, j'ai juste mis des valeurs bidons
				}
			case "PASS":
				if (mdpCorrect("tmp", "tmp")) {// TODO: à remplacer, j'ai juste mis des valeurs bidons
					// Renvoyer code succès
				} else {
					// Code erreur
				}
			default:
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
	 * identifiant    hash_du_mot_de_passe
	 */
	public boolean utilisateurExiste(String identifiant) {
		BufferedReader lecteur;
		String[] idMdp = new String[2];
		String ligne;

		try {
			lecteur = new BufferedReader(new FileReader(cheminUtilisateurs));

			// Lecture de chaque ligne
			while ((ligne = lecteur.readLine()) != null) {
				idMdp = ligne.split("    ");// TODO: constante pour séparateur

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
		return true;
	}

	/**
	 * TODO Création d'un utilisateur dans le fichier csv/tsv
	 * Est-ce qu'on donne l'identifiant ET le mot de passe ?
	 * Ou on renseigne d'abord l'identifiant puis on complète plus
	 * tard par le mot de passe ?
	 * On suppose que l'utilisateur n'existe pas déjà
	 */
	public	void EnregistrerUtilisateur(String s, String c ) throws IOException {
		try(FileWriter fw = new FileWriter(cheminUtilisateurs, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw))
		{
			out.println(s + "    " + c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testUtilisateurExiste() {
		if (utilisateurExiste("toto")) System.out.println("toto existe");
		if (utilisateurExiste("titi")) System.out.println("titi existe");
		if (utilisateurExiste("truc")) System.out.println("truc existe");
	}


	public static void main(String[] args){
		 P2PCentralizedServer s= new P2PCentralizedServer();

		//s.EnregistrerUtilisateur("toto", "qerghqmerguqhemgiu");
		s.testUtilisateurExiste();

	}
}
