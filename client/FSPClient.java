package client;

import abstractions.Yoda;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class FSPClient extends Yoda {

    // Identifiant de l'utilisateur
    public String id = " ";
    // Mot de passe
    public String mdp = " ";
    // Hash du mot de passe
    public String hashMdp = " 12333";
    // Répertoire qui contient les descriptions des fichiers partagés par le serveur
    // TODO renommer
    public String repPartage = "macewindu/";

    public String getRepPartage() {
        return repPartage;
    }

    public FSPClient() {
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void connect() {
        try {
            socket = new Socket(adresseIPServeur, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Authentification
     */
    public void login() {
        Scanner scan = new Scanner(System.in);
        String reponse;

        try {
            // On entre et on envoie l'identifiant ...
            do {
                System.out.print("Identifiant : ");
                id = scan.nextLine();
                super.envoyerMessage(getUserCmd());
                reponse = super.lireMessage();
                System.out.println(reponse);
            } while (!reponse.startsWith("2"));

            // ... puis le mot de passe
            do {
                System.out.print("Mot de passe : ");
                mdp = scan.nextLine();
                chiffreMdp();
                super.envoyerMessage(getPassCmd());
                reponse = super.lireMessage();
                System.out.println(reponse);
            } while (!reponse.startsWith("2"));

            System.out.println("Authentification réussie !");
            //super.saveFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * String correspondant à la commande USER
     * Commande pour envoyer son identifiant
     * TODO à renommer; 'get' est utilisé pour lire un attribut (ce n'est pas le cas ici)
     * Cela peut prêter à confusion
     */
    public String getUserCmd() {
        return "USER " + id;
    }

    /**
     * String correspondant à la commande PASS
     * Commande pour envoyer le hash de son mot de passe
     *
     * TODO à renommer; 'get' est utilisé pour lire un attribut (ce n'est pas le cas ici)
     * Cela peut prêter à confusion
     */
    public String getPassCmd() {
        return "PASS " + hashMdp;
    }

    public void quit() throws IOException {
        super.envoyerMessage("QUIT");
    }

    /**
     * Chiffrement du mot de passe
     * Le mot de passe chiffré est dans les champs
     * Je sais pas si c'est une bonne ou mauvaise idée
     * TODO voir String.hashCode()
     */
    public void chiffreMdp() {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(this.mdp.getBytes());
            byte[] messageDigestMD5 = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte bytes : messageDigestMD5) {
                stringBuffer.append(String.format("%02x", bytes & 0xff));
            }

            this.hashMdp = stringBuffer.toString();
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FSPClient client = new FSPClient();
        String msg;
        client.connect();
        client.open();
        try {
            client.recevoirDescriptions(client.repPartage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.disconnect();
        client.close();
    }
}
