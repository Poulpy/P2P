package controler;

import java.io.IOException;
import javafx.application.Application;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import javafx.scene.layout.GridPane;
import javafx.geometry.Orientation;
import java.util.ArrayList;
import javafx.util.Duration;
import view.ClientView;

public class ClientControler {

    public ArrayList<String> filesMatching;
    public ClientView scene;

    public ClientControler(ClientView view) {
        this.scene = view;
        // événements
        scene.searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!scene.searchField.getText().equals("")) {
                    System.out.println("SEARCH " + scene.searchField.getText());
                    filesMatching = samples();
                    scene.updateListView(filesMatching);
                }
            }
        });
        scene.searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    if (!scene.searchField.getText().equals("")) {
                        System.out.println("SEARCH " + scene.searchField.getText());
                        filesMatching = samples();
                        scene.updateListView(filesMatching);
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
                    System.out.println("GET " + filesMatching.get(index));
                }

                if (indices.size() > 0) {
                    scene.downloadLabel.setText("Téléchargé " + indices.size() + " fichier(s)");
                    scene.fadeAnimation(scene.downloadLabel);
                }
            }
        });
    }

    public ArrayList<String> samples() {
        ArrayList<String> a = new ArrayList<String>();
        a.add("localhost/truc.txt");
        a.add("poseidon/yojinbo");
        a.add("zeus/RAPPORT.pdf");

        return a;
    }
}

