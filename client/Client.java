package client;

import java.io.IOException;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;

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
        Scene scene = new Scene(root, 300, 300, Color.LIGHTBLUE);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("Hello World");
    }
}

