package client;

import java.io.IOException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.util.ArrayList;

public class Client extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        FSPClient client;
        String msg;

        client = new FSPClient("127.0.0.1", 50000);

        Application.launch(Client.class, args);
        /*
        client.connect();
        client.open();

        try {
            client.recevoirDescriptions(client.descriptionsFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.disconnect();
        client.close();
        */
    }

    public ArrayList<String> filesMatching;

    @Override
    public void start(Stage stage) {
        Button downloadButton;
        Button searchButton;
        Group root;
        HBox hbox;
        ListView<String> list;
        Scene scene;
        TextField searchField;

        filesMatching = new ArrayList<String>();
        primaryStage = stage;
        root = new Group();
        scene = new Scene(root, 500, 500, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("File Sharing");
        scene.getStylesheets().add("client/stylesheet.css");

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
        list = new ListView<String>();
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        hbox = new HBox(list);
        hbox.setLayoutY(100);
        hbox.setLayoutX(10);
        hbox.setPrefHeight(200);
        hbox.setPrefWidth(150);

        // Bouton de téléchargement des fichiers
        downloadButton = new Button("Télécharger");
        downloadButton.setPrefWidth(120);
        downloadButton.setPrefHeight(35);
        downloadButton.getStyleClass().add("searchButton");
        downloadButton.getStyleClass().add("removeLightGlow");
        downloadButton.setLayoutX(250);
        downloadButton.setLayoutY(200);

        // événements
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!searchField.getText().equals("")) {
                    System.out.println("SEARCH " + searchField.getText());
                    filesMatching = samples();
                    updateListView(list, filesMatching);
                }
            }
        });
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    if (!searchField.getText().equals("")) {
                        System.out.println("SEARCH " + searchField.getText());
                        filesMatching = samples();
                        updateListView(list, filesMatching);
                    }
                }
            }
        });
        list.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ObservableList<Integer> indices = list.getSelectionModel().getSelectedIndices();

                    for (Integer index : indices) {
                        System.out.println("Item selected : " + filesMatching.get(index));
                    }

                }
            }
        });


        downloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                    ObservableList<Integer> indices = list.getSelectionModel().getSelectedIndices();

                    for (Integer index : indices) {
                        System.out.println("GET " + filesMatching.get(index));
                    }
            }
        });

        // On affiche les éléments dans la fenêtre
        root.getChildren().add(hbox);
        root.getChildren().add(searchField);
        root.getChildren().add(downloadButton);
        root.getChildren().add(searchButton);
    }

    public ArrayList<String> samples() {
        ArrayList<String> a = new ArrayList<String>();
        a.add("localhost/truc.txt");
        a.add("poseidon/yojinbo");
        a.add("zeus/RAPPORT.pdf");

        return a;
    }

    /**
     * Met à jour la liste de fichiers dans la vue
     * Supprime les éléments qui étaient déjà dans la liste
     */
    public void updateListView(ListView<String> list, ArrayList<String> filesMatching) {
        list.getItems().clear();

        for (int i = 0; i != filesMatching.size(); i++) {
            list.getItems().add(filesMatching.get(i));
        }
    }
}

