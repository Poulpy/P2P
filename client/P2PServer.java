package client;

import abstractions.Yoda;
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

public class P2PServer extends Yoda {

	public final static int FILE_SIZE = 6022386;

	// Identifiant de l'utilisateur
	public String id = " ";
	// Mot de passe
	public String mdp = " ";
	// Hash du mot de passe
	public String hashMdp = " 12333";
	//private BufferedOutputStream bos;
	private String repPartage = "shared2/";

	public P2PServer() {
	}

	private void saveFile() throws IOException {
		System.out.println("AVANT");
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		FileOutputStream fos = new FileOutputStream(repPartage + "starwars");
		byte[] buffer = new byte[4096];

		int filesize = 15123; // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		System.out.println("ATTENTION");
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			fos.write(buffer, 0, read);
			fos.flush();
		}

		fos.close();
		dis.close();
		System.out.println("read " + totalRead + " bytes.");
	}
	/**
	 * Authentification
	 */
	public void login() {
		Scanner scan = new Scanner(System.in);
		String reponse;

		try {
			// On entre et on envoie l'identifiant ...
			do {
				System.out.print("Identifiant : ");
				id = scan.nextLine();
				super.envoyerMessage(getUserCmd());
				reponse = super.lireMessage();
				System.out.println(reponse);
			} while (!reponse.startsWith("2"));

			// ... puis le mot de passe
			do {
				System.out.print("Mot de passe : ");
				mdp = scan.nextLine();
				chiffreMdp();
				super.envoyerMessage(getPassCmd());
				reponse = super.lireMessage();
				System.out.println(reponse);
			} while (!reponse.startsWith("2"));

			System.out.println("Authentification réussie !");
			saveFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Envoi d'une commande au serveur
	 */

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
		super.envoyerMessage("QUIT");
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
		//s.login();
		try {
			s.saveFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		s.close();
	}
}
