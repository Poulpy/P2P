
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
public class P2PServer {

   public static void main(String[] args){
      try {
          /*Scanner scan = new Scanner(System.in);
          System.out.print("Identifiant : ");
          String user_id = scan.nextLine();
          System.out.print("Mot de passe : ");
          String passwd = scan.nextLine();
          System.out.println(user_id + " " + passwd);*/
         Socket sock = new Socket("127.0.0.1", 50000);
         //String request = user_id + "," + passwd;
         String data = "truc" + "," + "bidule";

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(data.getBytes());
            byte[] messageDigestMD5 = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte bytes : messageDigestMD5) {
                stringBuffer.append(String.format("%02x", bytes & 0xff));
            }

            System.out.println("data:" + data);
            System.out.println("digestedMD5(hex):" + stringBuffer.toString());
        } catch (NoSuchAlgorithmException exception) {
            // TODO Auto-generated catch block
            exception.printStackTrace();
        }
         /*
         BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());

         bos.write(request.getBytes());
         bos.flush();*/


         /*Socket sock = new Socket("127.0.0.1", 20);

         //Nous allons faire une demande au serveur web
         //ATTENTION : les \r\n sont OBLIGATOIRES ! Sinon ça ne fonctionnera pas ! !
         String request = "GET /wiki/Digital_Learning HTTP/1.1\r\n";
         request += "Host: fr.wikipedia.org\r\n";
         request += "\r\n";

         //nous créons donc un flux en écriture
         BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());

         //nous écrivons notre requête
         bos.write(request.getBytes());
         //Vu que nous utilisons un buffer, nous devons utiliser la méthode flush
         //afin que les données soient bien écrite et envoyées au serveur
         bos.flush();

         //On récupère maintenant la réponse du serveur
         //dans un flux, comme pour les fichiers...
         BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());

         //Il ne nous reste plus qu'à le lire
         String content = "";
         int stream;
         byte[] b = new byte[1024];
         while((stream = bis.read(b)) != -1){
            content += new String(b, 0, stream);
         }

         //On affiche la page !
         Browser browser = new Browser("fr.wikipedia.org", content);*/

      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
