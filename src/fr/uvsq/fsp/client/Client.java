package fr.uvsq.fsp.client;

import fr.uvsq.fsp.controller.ClientController;
import fr.uvsq.fsp.view.ClientView;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
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
		}

		client = new FSPClient(serverIP, port, "src/fr/uvsq/fsp/client/");

		Application.launch(Client.class);
	}

	@Override
	public void start(Stage stage) {
		ClientView view;
		Scene scene;
		ClientController controller;

		view = new ClientView();
		scene = new Scene(view, 700, 580, Color.WHITE);

		primaryStage = stage;
		primaryStage.setMinWidth(700);
		primaryStage.setMinHeight(550);
		primaryStage.setMaxWidth(700);
		primaryStage.setMaxHeight(580);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("File Sharing");
		primaryStage.setX(100);
		primaryStage.setY(100);

		controller = new ClientController(primaryStage, view, client);
	}

	@Override
	public void stop() {
		if (client.toClose) {
			System.out.println("client.close();");
			try {
				client.envoyerMessage("STOP");
				client.disconnect();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			client.close();
		}
	}
}

