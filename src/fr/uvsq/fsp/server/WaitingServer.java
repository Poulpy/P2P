package fr.uvsq.fsp.server;

import fr.uvsq.fsp.abstractions.Yoda;
import java.util.ArrayList;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import fr.uvsq.fsp.util.FTPCommand;
import java.nio.file.DirectoryStream;

public class WaitingServer extends Yoda implements Runnable {

	Boolean isStopped = false;

	/**
	 * Chemin du fichier contenant les utilisateurs connus du serveur
	 * l'identifiant et le hash du mot de passe sont séparés par le
	 * séparateur sep
	 */
	public static String cheminUtilisateurs;

	private String sep = ",";

	/** Répertoire des fichiers partagés, créé au lancement du serveur */
	public static String descriptionsFolder;

	public ServerSocket serverSocket;

	public WaitingServer(int portNumber) {
		super(portNumber);
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

	public synchronized void stopThread() {
		isStopped = true;
	}

	private synchronized boolean isStopped() {
		return isStopped;
	}

	@Override
	public void run() {
		Socket sock;
		Thread[] servers = new Thread[10];
		int count = 0;


		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!isStopped()) {

			try {
				sock = serverSocket.accept();
			} catch (IOException e) {
				if (isStopped()) {
					System.out.println("Server has stopped");

					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}

			servers[count] = new Thread(new FSPCentral(sock));
			servers[count++].start();
		}

		disconnect();

		for (Thread server : servers) {
			//server.stopThread();
		}
	}
}

