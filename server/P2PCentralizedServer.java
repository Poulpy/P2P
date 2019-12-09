import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class P2PCentralizedServer {

    private String adresseIP = "127.0.0.1";
    private int port = 50000;

    public P2PCentralizedServer() {
    }

   public static void main(String[] args){
        P2PCentralizedServer s = new P2PCentralizedServer();
        String contenu = "";
        int stream;
        byte[] b = new byte[1024];
        int index;

        try {
            ServerSocket serveurCentral = new ServerSocket(s.port, 10, InetAddress.getByName(s.adresseIP));
            Socket client = serveurCentral.accept();
            BufferedInputStream lecteur = new BufferedInputStream(client.getInputStream());

            // le serveur écoute/reçoit ce qu'on lui envoie
            while ((stream = lecteur.read(b)) != -1) {
                contenu = new String(b, 0, stream);

                // Séparation de la commande et du texte. Exemple :
                // USER toto
                // PASS mot_de_passe
                // On récupère l'indice où apparaît le le permier espace

                index = contenu.indexOf(' ');
                String type = contenu.substring(0, index);
                String text = contenu.substring(index + 1, contenu.length() - 1);
                System.out.println("COMMAND " + type);
                System.out.println("TEXT " + text);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
