import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

public class P2PServer {

    // Identifiant de l'utilisateur
    private String id = "toto";
    // Mot de passe
    private String mdp = "changeme";
    // Hash du mot de passe
    private String hashMdp;
    // Adresse du serveur centralisé
    private String adrServeurCentral = "127.0.0.1";
    // Port de la socket
    private int port = 50000;

    private PrintWriter ecrivain;

    public P2PServer() {
    }

    /**
     * L'utilisateur entre son identifiant et mot de passe
     */
    public void entreIdentifiantEtMotDePasse() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Identifiant : ");
        id = scan.nextLine();
        System.out.print("Mot de passe : ");
        mdp = scan.nextLine();
    }

    public void envoyerCommande(String cmd) {
        cmd += "\r\n";
        ecrivain.write(cmd);
        ecrivain.flush();
    }


    public String getUserCmd() {
        return "USER " + id;
    }

    public String getPassCmd() {
        return "PASS " + hashMdp;
    }

    public static void main(String[] args) {
        P2PServer s= new P2PServer();
        Socket sock = null;

        try {
            // s.entreIdentifiantEtMotDePasse();
            System.out.println(s.id + " " + s.mdp);
            sock = new Socket(s.adrServeurCentral, s.port);

            // Chiffrement du mot de passe
            // -> méthode ?
            MessageDigest messageDigest;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(s.mdp.getBytes());
                byte[] messageDigestMD5 = messageDigest.digest();
                StringBuffer stringBuffer = new StringBuffer();
                for (byte bytes : messageDigestMD5) {
                    stringBuffer.append(String.format("%02x", bytes & 0xff));
                }

                s.hashMdp = stringBuffer.toString();
            } catch (NoSuchAlgorithmException exception) {
                exception.printStackTrace();
            }
            s.ecrivain = new PrintWriter(sock.getOutputStream());
            BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());

            // Envoi de l'identifiant et du mot de passe au serveur
            s.envoyerCommande(s.getUserCmd());
            s.envoyerCommande(s.getPassCmd());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sock != null) {
                try {
                    sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    sock = null;
                }
            }
        }
    }
}
