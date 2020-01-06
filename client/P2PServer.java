import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

public class P2PServer {

	// Identifiant de l'utilisateur
	public	 String id = " ";
	// Mot de passe
	public	String mdp = " ";
	// Hash du mot de passe
	public	String hashMdp = " 12333";
	// Adresse du serveur centralisé
	private String adrServeurCentral = "127.0.0.1";
	// Port de la socket
	private int port = 50000;
	// Sert à envoyer des messages à travers une socket
	private PrintWriter ecrivain;
	private Socket socket;

	public P2PServer() {
	}



	public void open() {
		try {
			socket = new Socket(adrServeurCentral, port);
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
	public void close() {
		try {
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			BufferedInputStream lecteur = new BufferedInputStream(socket.getInputStream());

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
	 * Authentification
	 */
	public void login() {
		Scanner scan = new Scanner(System.in);
		String reponse;

		try {
			ecrivain = new PrintWriter(socket.getOutputStream());
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			BufferedInputStream lecteur = new BufferedInputStream(socket.getInputStream());

			// On entre et on envoie l'identifiant ...
			do {
				System.out.print("Identifiant : ");
				id = scan.nextLine();
				System.out.println("ID envoyé " + id);
				envoyerCommande(getUserCmd());
				reponse = recevoirMessage();
				System.out.println(reponse);
			} while (!reponse.startsWith("2"));

			// ... puis le mot de passe
			do {
				System.out.print("Mot de passe : ");
				mdp = scan.nextLine();
				chiffreMdp();
				System.out.println("Mot de passe envoyé " + mdp + "/" + hashMdp);
				envoyerCommande(getPassCmd());
				reponse = recevoirMessage();
				System.out.println(reponse);
			} while (!reponse.startsWith("2"));

			// Authentification réussie

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Envoi d'une commande au serveur
	 */
	public void envoyerCommande(String cmd) {
		cmd += "\r\n";
		ecrivain.write(cmd);
		ecrivain.flush();
	}

	/**
	 * String correspondant à la commande USER
	 * Commande pour envoyer son identifiant
	 */
	public String getUserCmd() {
		return "USER " + id;
	}

	/**
	 * String correspondant à la commande PASS
	 * Commande pour envoyer le hash de son mot de passe
	 */
	public String getPassCmd() {
		return "PASS " + hashMdp;
	}

	// Chiffrement du mot de passe
	// Le mot de passe chiffré est dans les champs
	// Je sais pas si c'est une bonne ou mauvaise idée

	public void chiffreMdp() {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(this.mdp.getBytes());
			byte[] messageDigestMD5 = messageDigest.digest();
			StringBuffer stringBuffer = new StringBuffer();
			for (byte bytes : messageDigestMD5) {
				stringBuffer.append(String.format("%02x", bytes & 0xff));
			}

			this.hashMdp = stringBuffer.toString();
		} catch (NoSuchAlgorithmException exception) {
			exception.printStackTrace();
		}
	}

	public static void main(String[] args) {
		P2PServer s = new P2PServer();
		s.open();
		s.login();
		s.close();
	}
}
