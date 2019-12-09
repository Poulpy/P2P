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

   public static void main(String[] args){
      try {
         ServerSocket serveurCentral = new ServerSocket(50000, 10, InetAddress.getByName("127.0.0.1"));
         Socket client = serveurCentral.accept();
         //PrintWriter ecrivain = new PrintWriter(client.getOutputStream());
         BufferedInputStream bis = new BufferedInputStream(client.getInputStream());

         String contenu = "";
         int stream;
         byte[] b = new byte[1024];
         int index;
         stream = bis.read(b);
         contenu = new String(b, 0, stream);

         // Affichage du message reçu
         System.out.println("CONTENT : " + contenu);

         // Séparation de la commande et du texte. Exemple :
         // USER toto
         // PASS mot_de_passe
         // On récupère l'indice où apparaît le le permier espace

         index = contenu.indexOf(' ');
         String type = contenu.substring(0, index);
         String text = contenu.substring(index + 1, contenu.length() - 1);
         System.out.println("COMMAND " + type);
         System.out.println("TEXT " + text);


         stream = bis.read(b);
         contenu = new String(b, 0, stream);

         // Affichage du message reçu
         System.out.println("CONTENT : " + contenu);

         // Séparation de la commande et du texte. Exemple :
         // USER toto
         // PASS mot_de_passe
         // On récupère l'indice où apparaît le le permier espace

         index = contenu.indexOf(' ');
         type = contenu.substring(0, index);
         text = contenu.substring(index + 1, contenu.length() - 1);
         System.out.println("COMMAND " + type);
         System.out.println("TEXT " + text);

      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
