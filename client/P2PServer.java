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

	public P2PServer() {
	}

	/**
	 * L'utilisateur entre son identifiant et mot de passe
	 */
	public void entreIdentifiantEtMotDePasse() {
		Scanner scan = new Scanner(System.in);
		System.out.print("Identifiant : ");
		id = scan.nextLine();
		System.out.print("Mot de passe : ");
		mdp = scan.nextLine();
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
	 */
	public	String getUserCmd() {
		return	id;
	}

	/**
	 * String correspondant à la commande PASS
	 */
	public	String getPassCmd() {
		return	hashMdp;
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
		P2PServer s= new P2PServer();
		//P2PCentralizedServer c= new P2PCentralizedServer();



		Socket sock = null;

		try {
			s.entreIdentifiantEtMotDePasse();
			System.out.println(s.id + " " + s.mdp);
			sock = new Socket(s.adrServeurCentral, s.port);

			// Chiffrement du mot de passe
			s.chiffreMdp();
			s.ecrivain = new PrintWriter(sock.getOutputStream());
			BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());

			// Faire un méthode login() ?
			// Envoi de l'identifiant ...
			s.envoyerCommande(s.getUserCmd());
			// ... et du mot de passe au serveur
			s.envoyerCommande(s.getPassCmd());

		// Gestion d'exceptions
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (sock != null) {
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
					sock = null;
				}
			}
		}

		/*
		try {
			c.EnregistrerUtilisateur(s.id, s.hashMdp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
	}
}
