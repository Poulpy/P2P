package abstractions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import outils.FTPCommand;


public class Yoda {
    // NOTE
    // protected : champs ou fonction qui sera présent dans la classe fille

    // Message
    // Ecriture dans une socket
    protected BufferedReader reader;
    // Lecture dans une socket
    protected PrintWriter writer;

    // Fichier
    //protected DataOutputStream dos;
    //protected DataInputStream dis;

    // Adresse IP de l'utilisateur
    protected String adresseIP = "127.0.0.1";
    // Adresse IP du serveur
    protected String adresseIPServeur = "127.0.0.1";
    protected int port = 50000;
    protected Socket socket;

    /**
     */
    protected Yoda(String serverIP, int portNumber) {
        adresseIPServeur = serverIP;
        port = portNumber;
    }


    /**
     * Ouvre une socket pour le client
     * TODO: gérer plusieurs clients, méthode pour ouvrir une socket seulement
     * pour le client ? Les sockets des clients seraient stockées dans un
     * tableau
     */
    protected void open() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8")), true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Envoie toutes les descriptions
     * Envoie d'abord le nombre de fichiers à envoyer
     *
     * descriptionsDir est le répertoire où sont les descriptions
     */
    protected void envoyerDescriptions(String descriptionsDir) throws IOException {
        File d = new File(descriptionsDir);

        // On envoie d'abord le nombre de fichiers à envoyer
        envoyerMessage("FILECOUNT " + d.list().length);

        // On liste les fichiers partagés
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(descriptionsDir))) {
            for (Path path : stream) {
                envoyerFichier(path.toString());
            }
        }
    }

    /**
     * dir est le répertoire où sont les descriptions
     */
    protected void recevoirDescriptions(String dir) throws IOException {
        String msg;
        FTPCommand ftpCmd;
        int fileCount;

        // On attend un message avec pour commande FILECOUNT
        do {
            msg = lireMessage();
            ftpCmd = FTPCommand.parseCommand(msg);
        } while (ftpCmd.command.compareTo("FILECOUNT") != 0);

        // On récupère le nombre de fichiers à recevoir
        fileCount = Integer.parseInt(ftpCmd.content);

        for (int i = 0; i != fileCount; i++) {
            lireFichier(dir);
        }
    }



    /**
     * Lit un message (une ligne) envoyé par socket
     */
    protected String lireMessage() throws IOException {
        return reader.readLine();
    }

    /**
     * Envoie une message à travers une socket
     */
    protected void envoyerMessage(String msg) throws IOException {
        System.out.println("> " + msg);
        writer.println(msg);
    }

    /**
     * Libère les ressources
     */
    protected void close() {
        try {
            reader.close();
            writer.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Envoie UN fichier par socket
     * TODO renommer envoyerContenu (parce qu'on n'envoit que le contenu, pas le nom du fichier)
     */
    protected void send(String filePath) throws IOException {
        File file = new File(filePath);
        int fileSize = (int) file.length();// pas utilisée
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;

        // On envoie le fichier ligne par ligne
        while ((line = br.readLine()) != null) {
            envoyerMessage(line);
        }

        // L'étiquette END marque la fin du fichier
        // TODO Une alternative serait d'utiliser la taille du fichier,
        // mais c'est trop compliqué. Toutefois, qu'est-ce qu'il se passe
        // si le fichier contient END ?
        envoyerMessage("END");
        fr.close();

    }

    /**
     * Récupère UN fichier envoyé par socket
     * TODO renommer enregistrerContenu (parce que là on récupère pas
     * le nom du fichier, juste le contenu
     *
     * Ici on a un BufferedReader qu'on pourrait mettre en attribut
     */
    protected void save(String filePath) throws IOException {
        File file = new File(filePath);
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        String msg;

        // Chaque ligne du fichier nous est envoyée
        // L'étiquette END marque la fin de la transmission
        while ((msg = lireMessage()).compareTo("END") != 0) {
            bw.write(msg + "\n");
        }

        bw.close();
    }

    /**
     * On envoie : le nom du fichier et sa taille (en octets)
     * La taille n'est pas utile pour le moment, mais elle pourrait
     * l'être plus tard ! (voir save() send())
     * Ensuite on envoie le contenu
     */
    protected void envoyerFichier(String filePath) throws IOException {
        String fileName;
        File file;

        file = new File(filePath);
        fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

        // L'étiquette FILE va indiquer qu'on envoie le nom et la taille
        envoyerMessage("FILE " + fileName + " " + file.length());
        send(filePath);
    }


    /**
     * On récupère le nom de fichier et sa taille, ensuite le contenu du
     * fichier
     */
    protected void lireFichier(String dir) throws IOException {
        String msg;
        FTPCommand ftpCmd;
        String fileName;
        int fileSize;

        // On attend un message avec pour commande FILE
        do {
            msg = lireMessage();
            ftpCmd = FTPCommand.parseCommand(msg);
        } while (ftpCmd.command.compareTo("FILE") != 0);

        fileName = ftpCmd.content.split(" ")[0];
        fileSize = Integer.parseInt(ftpCmd.content.split(" ")[1]);

        save(dir + fileName);
    }
}

