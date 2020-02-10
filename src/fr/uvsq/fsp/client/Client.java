package fr.uvsq.fsp.client;

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

    public static void main(String[] args) {
        FSPClient client;

        client = new FSPClient("127.0.0.1", 50000);

        Application.launch(Client.class);
        try {
            client.connect();
            client.open();
            
            if( client.verifieHostname()) {
            	client.queryCentral();
            }else {
            	client.quit();
                client.close();
            }
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

    @Override
    public void start(Stage stage) {
        ClientView view;
        Scene scene;
        ClientControler controler;

        view = new ClientView();
        scene = new Scene(view, 400, 400, Color.WHITE);
        controler = new ClientControler(view);

        primaryStage = stage;
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(400);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("File Sharing");
    }
}

