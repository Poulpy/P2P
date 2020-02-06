package fr.uvsq.fsp.view;

import fr.uvsq.fsp.controler.ClientControler;
import java.io.IOException;
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

public class ClientView extends Group {

    /**
     * Button used to query the server.
     * A keyword is sent, and the server tries to find files that matches the keyword.
     */
    public Button downloadButton;

    /**
     * Button to query the server.
     */
    public Button searchButton;

    /**
     * List showing the results/files sent by the server.
     */
    public ListView<String> fileList;

    /**
     * Receives the keyword to be sent to the server.
     */
    public TextField searchField;

    /**
     * The class that manages all events.
     */
    public ClientControler controler;

    /**
     * Pops when a user downloaded successfully.
     */
    public Label downloadLabel;

    public ClientView() {
        //controler = new ClientControler(this);
        GridPane grid = new GridPane();

        getStylesheets().add("fr/uvsq/fsp/client/stylesheet.css");

        // Bouton de recherche
        searchButton = new Button("Rechercher");
        searchButton.setPrefWidth(120);
        searchButton.setPrefHeight(35);
        searchButton.getStyleClass().add("searchButton");
        searchButton.getStyleClass().add("removeLightGlow");
        searchButton.setLayoutX(30);
        searchButton.setLayoutY(50);


        // Champ de recherche
        searchField = new TextField();
        searchField.getStyleClass().add("removeLightGlow");
        searchField.getStyleClass().add("searchField");
        searchField.setPrefWidth(150);
        searchField.setPrefHeight(35);
        searchField.setLayoutX(30);
        searchField.setLayoutY(10);



        // Liste affichant les fichiers qui correspondent à la recherche
        fileList = new ListView<String>();
        fileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileList.setPrefHeight(200);
        fileList.setPrefWidth(150);
        fileList.setOrientation(Orientation.VERTICAL);

        // Bouton de téléchargement des fichiers
        downloadButton = new Button("Télécharger");
        downloadButton.setPrefWidth(120);
        downloadButton.setPrefHeight(35);
        downloadButton.getStyleClass().add("searchButton");
        downloadButton.getStyleClass().add("removeLightGlow");

        downloadLabel = new Label();
        downloadLabel.getStyleClass().add("greenFont");

        grid.add(searchField, 0, 0, 1, 1);
        grid.add(searchButton, 1, 0, 1, 1);
        grid.add(fileList, 0, 2, 1, 2);
        grid.add(downloadButton, 1, 2, 1, 1);
        grid.add(downloadLabel, 1, 3, 1, 1);

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setLayoutX(10);
        grid.setLayoutY(10);

        // On affiche les éléments dans la fenêtre
        this.getChildren().add(grid);
    }

    /**
     * Met à jour la liste de fichiers dans la vue
     * Supprime les éléments qui étaient déjà dans la liste
     */
    public void updateListView(ArrayList<String> filesMatching) {
        fileList.getItems().clear();

        for (int i = 0; i != filesMatching.size(); i++) {
            fileList.getItems().add(filesMatching.get(i));
        }
    }

    /**
     * Animation sur un label : fait apparaître puis disparaître le text
     */
    public void fadeAnimation(Label text) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), text);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.playFromStart();
    }
}

