package fr.uvsq.fsp.server;
import java.io.IOException;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.uvsq.fsp.server.FSPCentral;

public class Central {

    public static void main(String[] args) throws Exception {
        FSPCentral server1;

        server1 = new FSPCentral("127.0.0.1", 60000, "src/fr/uvsq/fsp/server/utilisateurs.csv", "src/fr/uvsq/fsp/server/descriptions/");
    
             
             server1.connect();
            
             
            System.out.println("Listening");
            
            
               Socket sock = server1.serverSocket.accept();
               System.out.println("Connected");
               new Thread(new FSPCentral("127.0.0.1", 60000,sock)).start();
            
         
   
    }
}

