package fr.uvsq.fsp.controler;

import java.io.IOException;
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
import java.net.UnknownHostException;
import javafx.stage.Stage;
import fr.uvsq.fsp.util.FTPCommand;
import javafx.util.Duration;
import fr.uvsq.fsp.view.ClientView;
import fr.uvsq.fsp.client.FSPClient;

public class ClientControler {

	/**
	 * Files matching the search
	 */
	public ArrayList<String> filesMatching;

	public ClientView scene;
	public FSPClient client;

	public ClientControler(ClientView view, FSPClient fspClient) {
		this.scene = view;
		this.client = fspClient;

		// événements
		scene.searchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (!scene.searchField.getText().equals("")) {
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
				if (ke.getCode().equals(KeyCode.ENTER)) {
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
				if (ke.getCode().equals(KeyCode.ENTER)) {
					ObservableList<Integer> indices = scene.fileList.getSelectionModel().getSelectedIndices();

					for (Integer index : indices) {
					// TODO Télécharger un fichier
						System.out.println("GET " + filesMatching.get(index));
					}

					if (indices.size() > 0) {
						scene.downloadLabel.setText("Téléchargé " + indices.size() + " fichier(s)");
						scene.fadeAnimation(scene.downloadLabel);
					}

				}
			}
		});


		scene.downloadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ObservableList<Integer> indices = scene.fileList.getSelectionModel().getSelectedIndices();

				for (Integer index : indices) {
					// TODO Télécharger un fichier
					System.out.println("GET " + filesMatching.get(index));
				}

				if (indices.size() > 0) {
					scene.downloadLabel.setText("Téléchargé " + indices.size() + " fichier(s)");
					scene.fadeAnimation(scene.downloadLabel);
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

	public void searchEvent() throws IOException {
		String msg;
		FTPCommand ftpCmd;

		System.out.println("SEARCH " + scene.searchField.getText());

		client.search(scene.searchField.getText());
		msg = client.lireMessage();
		ftpCmd = FTPCommand.parseCommand(msg);

		if (ftpCmd.command.equals("FOUND")) {
			filesMatching = client.parseFilesFound(ftpCmd.content);
			scene.updateListView(filesMatching);
		}
	}
}

