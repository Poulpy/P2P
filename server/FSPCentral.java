package server;

import abstractions.Yoda;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import outils.FTPCommand;

public class FSPCentral extends Yoda {

    // Chemin du fichier contenant les utilisateurs connus du serveur
    // l'identifiant et le hash du mot de passe sont séparés par le
    // séparateur sep
    private String cheminUtilisateurs = "server/utilisateurs.csv";
    private String sep = ",";
    // Répertoire des fichiers partagés, créé au lancement du serveur
    // TODO en faire une constante ?
    public String descriptionsFolder = "quigon/";
    private ServerSocket serverSocket;
    // voir méthode gererMessage
    private String id;
    private String mdp;
    private boolean nouvelUtilisateur = false;

    public FSPCentral(String serverIP, int port) {
        super(serverIP, port);
    }

    public void disconnect() {
        try {
            socket.close();
            serverSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void connect(String clientIP) {
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName(clientIP));
            socket = serverSocket.accept();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestion des commandes
     * En fonction de la commande, le serveur fait une action
     * particulière;
     * Par exemple, "USER toto"
     * Le serveur vérifie que l'utilisateur toto existe;
     * Si toto existe pas, il le crée
     * "PASS fierhigmeruis"
     * Le serveur vérifie que le mot de passe est correct en fonction
     * de l'identifiant précédemment envoyé
     * TODO mettre FTPCommand en param
     */
    public void gererMessage(String commande, String contenu) throws IOException {
        switch (commande) {
            case "USER":
                id = contenu;
                if (utilisateurExiste(id)) {
                    super.envoyerMessage("200 Bon identifiant");
                } else {
                    nouvelUtilisateur = true;
                    super.envoyerMessage("201 Identifiant inconnu");
                }
                break;
            case "PASS":
                mdp = contenu;

                if (nouvelUtilisateur) {
                    EnregistrerUtilisateur(id, mdp);
                    nouvelUtilisateur = false;
                    super.envoyerMessage("202 Utilisateur créé : " + id + ", " + mdp);
                    //super.sendFile(descriptionsFolder + "starwars");
                } else if (mdpCorrect(id, mdp)) {
                    super.envoyerMessage("200 Mot de passe correct");
                    //super.sendFile(descriptionsFolder + "starwars");
                } else {
                    super.envoyerMessage("300 Mot de passe incorrect pour " + id);
                }
                break;
            default:
        }
    }

    /**
     * Vérifier qu'un utilisateur existe
     * On regarde si son identifiant/nom est présent
     * dans le fichier utilisateurs.csv
     * Les utilisateurs sont stockés comme ça :
     * identifiant,hash_du_mot_de_passe
     * TODO autre classe ?
     */
    public boolean utilisateurExiste(String identifiant) {
        BufferedReader reader;
        String[] idMdp;
        String ligne;

        idMdp = new String[2];

        try {
            reader = new BufferedReader(new FileReader(cheminUtilisateurs));

            // Lecture de chaque ligne
            while ((ligne = reader.readLine()) != null) {
                idMdp = ligne.split(sep);

                if (idMdp[0].compareTo(identifiant) == 0) {
                    return true;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Vérifier qu'un mot de passe est correct pour
     * un utilisateur; On regarde dans le fichier
     * utilisateurs.csv
     */
    public boolean mdpCorrect(String identifiant, String hashMdp) {
        BufferedReader reader;
        String[] idMdp;
        String ligne;

        idMdp = new String[2];

        try {
            reader = new BufferedReader(new FileReader(cheminUtilisateurs));

            // Lecture de chaque ligne
            while ((ligne = reader.readLine()) != null) {
                idMdp = ligne.split(sep);

                if (idMdp[0].compareTo(identifiant) == 0 && idMdp[1].compareTo(hashMdp) == 0) {
                    return true;
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Supprime un utilisateur du fichier des utilisateurs
     * en fonction de son identifiant
     */
    public void enleverUtilisateur(String userID) throws IOException {
        File fileBeforeRemove;
        File fileAfterRemove;
        String currentLine;
        String someID;
        BufferedWriter writer;
        BufferedReader reader;

        fileBeforeRemove = new File(cheminUtilisateurs);
        fileAfterRemove = new File("users.csv");

        writer = new BufferedWriter(new FileWriter(fileAfterRemove));
        reader = new BufferedReader(new FileReader(fileBeforeRemove));

        while ((currentLine = reader.readLine()) != null) {
            someID = currentLine.split(sep)[0];

            if (someID.equals(userID)) {
                continue;
            } else {
                writer.write(currentLine);
            }
        }

        reader.close();
        writer.close();
    }

    /**
     * Création d'un utilisateur dans le fichier csv/tsv
     * TODO relire le code, le try est un peu bizare
     */
    public void EnregistrerUtilisateur(String s, String c) {
        try (FileWriter fw = new FileWriter(cheminUtilisateurs, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(s + sep + c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void listen() {
        String msg;
        FTPCommand ftpCmd;

        try {
            while ((msg = lireMessage()) != null) {
                if (msg.compareTo("QUIT") == 0) break;

                ftpCmd = FTPCommand.parseCommand(msg);
                System.out.println(msg);

                gererMessage(ftpCmd.command, ftpCmd.content);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

