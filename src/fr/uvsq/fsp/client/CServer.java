package fr.uvsq.fsp.client;

import fr.uvsq.fsp.view.ClientView;
import fr.uvsq.fsp.abstractions.FSPCore;
import fr.uvsq.fsp.util.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CServer {

	public static void main(String[] args) {
		FSPServer client;
		boolean loop = true;
		Scanner scan;
		String query;
		String serverIP;
		String id;
		String mdp;
		int port;

		if (args.length == 4) {
			serverIP = args[0];
			port = Integer.parseInt(args[1]);
			id = args[2];
			mdp = args[3];
		} else {
			scan = new Scanner(System.in);
			System.out.print("Adresse IP du serveur : ");
			serverIP = scan.nextLine();
			System.out.print("Port : ");
			port = Integer.parseInt(scan.nextLine());
			System.out.print("Identifiant : ");
			id = scan.nextLine();
			System.out.print("Mot de passe : ");
			mdp = scan.nextLine();
		}

		client = new FSPServer(serverIP, port, "client/descriptions/");

		ServerSocket serverSocket;

		try {
			client.connect();
			client.open();
			client.type();

			if (client.login(id, mdp)) {
				System.out.println("Tapez QUIT pour quitter le programme.");
				scan = new Scanner(System.in);
				serverSocket = new ServerSocket(50000, 10);

				while (loop) {
					FSPCore fspCore = new FSPCore("DESKTOP-F5FEM34",50000);
					fspCore.socket = serverSocket.accept();
					System.out.println("hgfhfytf");
					fspCore.open();
					String msg = fspCore.lireMessage();
					System.out.println(msg);
					Command fcmd = Command.parseCommand(msg);
					if(fcmd.command.equals("Download")) {
						fspCore.envoyerContenu(client.descriptionsFolder + fcmd.content);
					}
					fspCore.close();
				}
				serverSocket.close();
			}

			client.quit();
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (client.socket != null) {
				try {
					client.socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					client.socket = null;
				}
			}
		}
	}
}
