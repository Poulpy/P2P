import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class P2PServer {

	public final static int FILE_SIZE = 6022386;

	// Identifiant de l'utilisateur
	public String id = " ";
	// Mot de passe
	public String mdp = " ";
	// Hash du mot de passe
	public String hashMdp = " 12333";
	// Adresse du serveur centralisé
	private String adrServeurCentral = "127.0.0.1";
	// Port de la socket
	private int port = 50000;
	// Sert à envoyer des messages à travers une socket
	private PrintWriter ecrivain;
	private Socket socket;
	//private FileOutputStream fos;
	//private BufferedOutputStream bos;
	private String repPartage = "shared/";

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

	public void recevoirDescriptions() {
		/*
		try {
			byte[] mybytearray = new byte[1024];
			InputStream is = socket.getInputStream();
			FileOutputStream fos = new FileOutputStream("download.txt");
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int bytesRead = is.read(mybytearray, 0, mybytearray.length);
			bos.write(mybytearray, 0, bytesRead);
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done.");
		envoyerMessage("200 fichier reçu");
		*/

	}
	private void saveFile(Socket clientSock) throws IOException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		FileOutputStream fos = new FileOutputStream(repPartage + "starwars");
		byte[] buffer = new byte[4096];

		int filesize = 15123; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}

		fos.close();
		dis.close();
	}
	public void envoyerMessage(String msg) {
		msg += "\r\n";
		ecrivain.write(msg);
		ecrivain.flush();
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
				envoyerCommande(getUserCmd());
				reponse = recevoirMessage();
				System.out.println(reponse);
			} while (!reponse.startsWith("2"));

			// ... puis le mot de passe
			do {
				System.out.print("Mot de passe : ");
				mdp = scan.nextLine();
				chiffreMdp();
				envoyerCommande(getPassCmd());
				reponse = recevoirMessage();
				System.out.println(reponse);
			} while (!reponse.startsWith("2"));

			// Authentification réussie
			//recevoirDescriptions();
			saveFile(socket);

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

	public void quit() {
		envoyerMessage("QUIT a");
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
		s.quit();
		s.close();
	}
}
