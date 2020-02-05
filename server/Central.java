package server;

import java.io.IOException;
import java.io.File;

public class Central {

    public static void main(String[] args){
        FSPCentral server;

        server = new FSPCentral("127.0.0.1", 50000);

        server.connect("127.0.0.1");
        server.open();
        server.listen();

        server.disconnect();
        server.close();
    }

}

