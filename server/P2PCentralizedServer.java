
import java.io.BufferedInputStream;
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
         ServerSocket serveur_central = new ServerSocket(50000, 10, InetAddress.getByName("127.0.0.1"));
         Socket client = serveur_central.accept();
         BufferedInputStream bis = new BufferedInputStream(client.getInputStream());

         String content = "";
         int stream;
         byte[] b = new byte[1024];
         while((stream = bis.read(b)) != -1) {
            content += new String(b, 0, stream);
         }

         System.out.println("CONTENT : " + content);

      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
