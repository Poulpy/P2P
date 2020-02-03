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
    // TODO constante ?
    public String descriptionsFolder = "macewindu/";

    public FSPClient(String serverIP, int port) {
        super(serverIP, port);
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
     * TODO à modifier si on remplace l'interface en ligne de commande par une gui
     * TODO Server
     */
    public void login() {
        Scanner scan;
        String reponse;

        scan = new Scanner(System.in);

        try {
            // On entre et on envoie l'identifiant ...
            do {
                System.out.print("Identifiant : ");
                id = scan.nextLine();
                super.envoyerMessage("USER " + id);
                reponse = super.lireMessage();
                System.out.println(reponse);
            } while (!reponse.startsWith("2"));

            // ... puis le mot de passe
            do {
                System.out.print("Mot de passe : ");
                mdp = scan.nextLine();
                this.hashMdp = chiffrage(this.mdp);
                super.envoyerMessage("PASS " + hashMdp);
                reponse = super.lireMessage();
                System.out.println(reponse);
            } while (!reponse.startsWith("2"));

            System.out.println("Authentification réussie !");
            // recevoirDescriptions();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode à appeler quand l'utilisateur veut quitter la session
     */
    public void quit() throws IOException {
        super.envoyerMessage("QUIT");
    }

    /**
     * Chiffrement du mot de passe
     * Le mot de passe chiffré est dans les champs
     * Je sais pas si c'est une bonne ou mauvaise idée
     * TODO voir String.hashCode()
     */
    public String chiffrage(String str) {
        String result = new String();
        MessageDigest messageDigest;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            byte[] messageDigestMD5 = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte bytes : messageDigestMD5) {
                stringBuffer.append(String.format("%02x", bytes & 0xff));
            }

            result = stringBuffer.toString();
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }

        return result;
    }
}

