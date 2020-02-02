package client;

import java.io.IOException;

public class Client {

    public static void main(String[] args) {
        FSPClient client;
        String msg;

        client = new FSPClient("127.0.0.1", 50000);
        client.connect();
        client.open();

        try {
            client.recevoirDescriptions(client.descriptionsFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.disconnect();
        client.close();
    }
}

