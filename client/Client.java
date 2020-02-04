package client;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        Group root = new Group();
        Scene scene = new Scene(root, 500, 500, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("Hello World");

        scene.getStylesheets().add("ss.css");
        Button searchButton = new Button("Rechercher");
        searchButton.setPrefWidth(120);
        searchButton.setPrefHeight(35);
        searchButton.getStyleClass().add("searchButton");
        searchButton.getStyleClass().add("removeLightGlow");
        searchButton.setLayoutX(30);
        searchButton.setLayoutY(50);

        TextField searchField = new TextField();
        searchField.getStyleClass().add("removeLightGlow");
        searchField.getStyleClass().add("searchField");
        searchField.setPrefWidth(150);
        searchField.setPrefHeight(35);
        searchField.setLayoutX(30);
        searchField.setLayoutY(10);

        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    //action !
                    System.out.println("I pressed Enter and something should be happening !");
                }
            }
        });


        ListView<String> list = new ListView<String>();
        ArrayList<String> filesMatching = samples();
        for (int i = 0; i != filesMatching.size(); i++) {
            list.getItems().add(filesMatching.get(i));
        }
        HBox hbox = new HBox(list);
        hbox.setLayoutY(100);
        hbox.setLayoutX(10);




        root.getChildren().add(hbox);
        root.getChildren().add(searchField);
        root.getChildren().add(searchButton);
    }

    public ArrayList<String> samples() {
        ArrayList<String> a = new ArrayList<String>();
        a.add("localhost/truc.txt");
        a.add("poseidon/yojinbo");
        a.add("zeus/RAPPORT.pdf");

        return a;
    }
}

