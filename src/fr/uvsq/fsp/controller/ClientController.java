package fr.uvsq.fsp.controller;

import fr.uvsq.fsp.util.FileLister;
import fr.uvsq.fsp.client.FSPClient;
import fr.uvsq.fsp.util.Command;
import fr.uvsq.fsp.view.ClientView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ClientController {

	/**
	 * Files matching the search
	 */
	public ArrayList<String> filesMatching;
	public ArrayList<String> filesShared;
	public ArrayList<String> filesDownloaded;
	
	public FileChooser fileChooser;

	public ClientView scene;
	public FSPClient client;
	public boolean isConnected = false;

	public int swap = 0;

	public ClientController(Stage stage, ClientView view, FSPClient fspClient) {
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

				client.port = Integer.parseInt(scene.portField.getText());
				client.adresseIPServeur = scene.serverIPField.getText();

				try {
					client.connect();
					client.open();
					client.type();
					scene.displayConnection(true);
					isConnected = true;
				} catch (IOException ex) {
					ex.printStackTrace();
					scene.displayConnection(false);
				}
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
				if (isConnected && ke.getCode().equals(KeyCode.ENTER)) {
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
				if (isConnected && ke.getCode().equals(KeyCode.ENTER)) {
					ObservableList<Integer> indices = scene.fileList.getSelectionModel().getSelectedIndices();
					ArrayList<String> downloads;

					downloads = new ArrayList<String>();

					for (Integer index : indices) {
					// TODO Télécharger un fichier
						System.out.println("GET " + filesMatching.get(index));
						try {
							client.download("127.0.0.1","starwars");
							client.lireFichier(client.clientDownloadsFolder);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						downloads = samples();
					}

					displayUploadMessage(downloads.size());

				}
			}
		});


		scene.downloadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (isConnected) {
					ObservableList<Integer> indices = scene.fileList.getSelectionModel().getSelectedIndices();
					ArrayList<String> downloads;

					downloads = new ArrayList<String>();

					for (Integer index : indices) {
						// TODO Télécharger un fichier
						System.out.println("GET " + filesMatching.get(index));
						try {
							client.download("127.0.0.1","starwars");
							client.lireFichier(client.clientDownloadsFolder);
						} catch (IOException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}

						downloads = samples();
					}

					displayUploadMessage(downloads.size());
				}
			}
		});

		fileChooser = new FileChooser();
		scene.uploadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (true) {
					// get the file selected 
	                File file = fileChooser.showOpenDialog(stage); 
	  
	                if (file != null) { 
	                      
	                    
	                    try {
	                        Path sourceDirectory = Paths.get(file.getAbsolutePath());
	                        Path targetDirectory = Paths.get(client.clientSharedFolder+sourceDirectory.getFileName().toString());

	                        //copy source to target using Files Class
	                        Files.copy(sourceDirectory, targetDirectory);
	                        filesShared = FileLister.list(client.clientSharedFolder);
	                		scene.setListView(scene.sharedList, filesShared);
	                    } catch (IOException ex) {
	                        ex.printStackTrace();
	                    }
	                } 
				}
			}
		});
		
		scene.downloadList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				System.out.println(scene.downloadList.getSelectionModel().getSelectedItem());
			}
		});
		
		scene.sendDescriptionButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				String file = scene.sharedList.getSelectionModel().getSelectedItem();
				System.out.println(file + " : " + scene.descriptionArea.getText());
				try (PrintWriter out = new PrintWriter(client.descriptionsFolder + file)) {
				    out.println(scene.descriptionArea.getText());
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
		System.out.println(msg);

		if (ftpCmd.command.equals("FOUND")) {
			filesMatching = client.parseFilesFound(ftpCmd.content);
			scene.updateListView(filesMatching);
		}
	}

	public void displayUploadMessage(int fileCount) {
		scene.messageLabel.getStyleClass().clear();
		if (fileCount > 0) {
			scene.messageLabel.getStyleClass().add("greenFont");
			scene.messageLabel.setText("Téléchargé " + fileCount + " fichier(s)");
		} else {
			scene.messageLabel.getStyleClass().add("redFont");
			scene.messageLabel.setText("Erreur");
		}
		scene.fadeAnimation(scene.messageLabel, 2000);
	}
}

