package fr.uvsq.fsp.controller;

import fr.uvsq.fsp.util.FileLister;
import fr.uvsq.fsp.client.FSPClient;
import fr.uvsq.fsp.util.Command;
import fr.uvsq.fsp.view.ClientView;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

public class ClientController {

	/**
	 * Files matching the search
	 */
	public ArrayList<String> filesMatching;
	public ArrayList<String> filesShared;
	public ArrayList<String> filesDownloaded;

	public ClientView scene;
	public FSPClient client;
	public boolean isConnected = false;

	public int swap = 0;

	public ClientController(ClientView view, FSPClient fspClient) {
		this.scene = view;
		this.client = fspClient;

		scene.serverIPField.setText(client.adresseIPServeur);
		if (client.port != 0)
		{
			scene.portField.setText(String.valueOf(client.port));
		}

		filesShared = FileLister.list(client.clientSharedFolder);
		scene.setListView(scene.sharedList, filesShared);

		filesDownloaded = FileLister.listWithLevel(client.clientDownloadsFolder, 1);
		scene.setListView(scene.downloadList, filesDownloaded);

		scene.refreshConnectionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (scene.serverIPField.getText() == null
					|| scene.portField.getText() == null
					|| scene.serverIPField.getText().equals("")
					|| scene.portField.getText().equals(""))
					return;


				if (swap++ % 2 == 0) {
					scene.connectionLabel.getStyleClass().remove("greenFont");
					scene.connectionLabel.getStyleClass().add("redFont");
					scene.connectionLabel.setText("Connection failed");
				} else {
					scene.connectionLabel.getStyleClass().remove("redFont");
					scene.connectionLabel.getStyleClass().add("greenFont");
					scene.connectionLabel.setText("Connection successful");
				}

				/*
				try {
					client.connect();
					client.open();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				*/
			}
		});

		// événements
		scene.searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (isConnected && !scene.searchField.getText().equals("")) {
					try {
						searchEvent();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		scene.searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (isConnected && !ke.getCode().equals(KeyCode.ENTER)) {
					if (!scene.searchField.getText().equals("")) {
						try {
							searchEvent();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		scene.fileList.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (isConnected && !ke.getCode().equals(KeyCode.ENTER)) {
					ObservableList<Integer> indices = scene.fileList.getSelectionModel().getSelectedIndices();

					for (Integer index : indices) {
					// TODO Télécharger un fichier
						System.out.println("GET " + filesMatching.get(index));
					}

					if (indices.size() > 0) {
						scene.downloadLabel.setText("Téléchargé " + indices.size() + " fichier(s)");
						scene.fadeAnimation(scene.downloadLabel, 2000);
					}

				}
			}
		});


		scene.downloadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (isConnected) {
					ObservableList<Integer> indices = scene.fileList.getSelectionModel().getSelectedIndices();

					if (indices.size() > 0) {
						for (Integer index : indices) {
							// TODO Télécharger un fichier
							System.out.println("GET " + filesMatching.get(index));
						}

						scene.downloadLabel.setText("Téléchargé " + indices.size() + " fichier(s)");
						scene.fadeAnimation(scene.downloadLabel, 2000);
					}
				}
			}
		});
	}

	/**
	 * Gives some file samples for the list view
	 */
	public ArrayList<String> samples() {
		ArrayList<String> a = new ArrayList<String>();
		a.add("localhost/truc.txt");
		a.add("poseidon/yojinbo");
		a.add("zeus/RAPPORT.pdf");

		return a;
	}

	/**
	 * Query the server for files and display the results in the view.
	 */
	public void searchEvent() throws IOException {
		String msg;
		Command ftpCmd;

		System.out.println("SEARCH " + scene.searchField.getText());

		client.search(scene.searchField.getText());
		msg = client.lireMessage();
		ftpCmd = Command.parseCommand(msg);

		if (ftpCmd.command.equals("FOUND")) {
			filesMatching = client.parseFilesFound(ftpCmd.content);
			scene.updateListView(filesMatching);
		}
	}
}

