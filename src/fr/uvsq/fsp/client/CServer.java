package fr.uvsq.fsp.client;

import fr.uvsq.fsp.view.ClientView;
import java.io.IOException;
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

		try {
			client.connect();
			client.open();
			client.type();

			if (client.login(id, mdp)) {
				System.out.println("Tapez QUIT pour quitter le programme.");
				scan = new Scanner(System.in);

				while (loop) {
					System.out.print("> ");
					query = scan.nextLine();

					if (query.equals("QUIT")) {
						loop = false;
					}
				}
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
