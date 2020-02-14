package fr.uvsq.fsp.client;

import java.util.Scanner;
import javafx.application.Platform;
import fr.uvsq.fsp.controler.ClientControler;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
import fr.uvsq.fsp.view.ClientView;

public class Client extends Application {

	private Stage primaryStage;
	private static String serverIP;
	private static int port;
	private static FSPClient client;

	public static void main(String[] args) {
		Scanner scan;
		String msg;

		if (args.length == 2) {
			serverIP = args[0];
			port = Integer.parseInt(args[1]);
		} else {
			scan = new Scanner(System.in);
			System.out.print("Adresse IP du serveur : ");
			serverIP = scan.nextLine();
			System.out.print("Port : ");
			port = Integer.parseInt(scan.nextLine());
		}

		client = new FSPClient(serverIP, port);
		try {
			client.connect();
			client.open();

			if (!client.verifieHostname()) {
				System.out.println("Utilisateur pas connecté");
				Platform.exit();
			} else {
				Application.launch(Client.class);
			}

			client.quit();
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Platform.exit();
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

	@Override
	public void start(Stage stage) {
		ClientView view;
		Scene scene;
		ClientControler controler;

		view = new ClientView();
		scene = new Scene(view, 400, 400, Color.WHITE);

		primaryStage = stage;
		primaryStage.setMinWidth(400);
		primaryStage.setMinHeight(400);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("File Sharing");

		controler = new ClientControler(view, client);
	}
}

