package client;

import abstractions.Yoda;
import util.Checksum;
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
import java.util.Scanner;
import java.net.InetAddress;

public class FSPClient extends Yoda {

    /** Nom d'hôte de l'utilisateur */
    public String hostname;

    /** Identifiant de l'utilisateur */
    public String id = " ";

    /** Mot de passe */
    public String mdp = " ";

    /** Répertoire qui contient les descriptions des fichiers partagés par le serveur */
    public final String descriptionsFolder = "client/descriptions/";

    public FSPClient(String serverIP, int port) {
        super(serverIP, port);
        new File(descriptionsFolder).mkdirs();

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
     * @Server
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
                super.envoyerMessage("PASS " + Checksum.getMD5Hash(this.mdp));
                reponse = super.lireMessage();
                System.out.println(reponse);
            } while (!reponse.startsWith("2"));

            System.out.println("Authentification réussie !");
            envoyerDescriptions(descriptionsFolder);

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
}

