package server;

import java.io.IOException;
import java.io.File;
import java.net.UnknownHostException;

public class Central {

    public static void main(String[] args){
        FSPCentral server;

        server = new FSPCentral("127.0.0.1", 50000);

        try {
            server.connect("127.0.0.1");
            server.open();
            server.listen();

            server.disconnect();
            server.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

