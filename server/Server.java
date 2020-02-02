package server;

import java.io.IOException;
import java.io.File;

public class Server {

    public static void main(String[] args){
        FSPServer server;

        server = new FSPServer("127.0.0.1", 50000);
        // on créé le dossier s'il n'existe pas déjà
        // TODO mettre dans une fonction, mais où ?
        new File(server.descriptionsFolder).mkdirs();

        server.connect("127.0.0.1");
        server.open();

        try {
            server.envoyerDescriptions(server.descriptionsFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.disconnect();
        server.close();
    }

}

