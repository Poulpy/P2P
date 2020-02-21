package fr.uvsq.fsp.client;

import fr.uvsq.fsp.abstractions.FSPNode;
import fr.uvsq.fsp.util.Checksum;
import java.io.BufferedInputStream;
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

public class FSPServer extends FSPNode {

	/** Répertoire qui contient les descriptions des fichiers partagés par le serveur */
	public final String descriptionsFolder;

	public FSPServer(String serverIP, int port, String descFolder) {
		super(serverIP, port);
		descriptionsFolder = descFolder;
		new File(descriptionsFolder).mkdirs();
	}


	public void type() throws IOException {
		envoyerMessage("TYPE SERVER");
	}

	/**
	 * Authentification de l'utilisateur
	 * Envoie l'identifiant puis le le mot de passe
	 * Si l'authentification est réussie, renvoie true
	 */
	public boolean login(String id, String mdp) {
		String reponse;

		try {
			super.envoyerMessage("USER " + id);
			reponse = super.lireMessage();
			System.out.println(reponse);
			if (!reponse.startsWith("2")) return false;

			super.envoyerMessage("PASS " + Checksum.getMD5Hash(mdp));
			reponse = super.lireMessage();
			System.out.println(reponse);
			if (!reponse.startsWith("2")) return false;

			System.out.println("Authentification réussie !");
			hostname();
			envoyerDescriptions(descriptionsFolder);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Envoie le nom d'hôte de l'utilisateur
	 */
	public void hostname() throws IOException, UnknownHostException {
		String hostname;

		hostname = InetAddress.getLocalHost().getHostName();
		envoyerMessage("HOSTNAME " + hostname);
	}

	/**
	 * Méthode à appeler quand l'utilisateur veut quitter la session
	 * Doit recevoir un accusé réception
	 */
	public void quit() throws IOException {
		super.envoyerMessage("QUIT");
	}
}

