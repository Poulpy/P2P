package fr.uvsq.fsp.abstractions;

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
import fr.uvsq.fsp.util.FTPCommand;


public class Yoda {

    /** Ecriture dans une socket */
    public BufferedReader reader;

    /** Lecture dans une socket */
    public PrintWriter writer;

    // Fichier
    //public DataOutputStream dos;
    //public DataInputStream dis;

    /** Adresse IP de l'utilisateur */
    public String adresseIP = "127.0.0.1";

    /** Adresse IP du serveur */
    public String adresseIPServeur = "127.0.0.1";

    public int port = 50000;

    public Socket socket;

    /**
     */
    public Yoda(String serverIP, int portNumber) {
        adresseIPServeur = serverIP;
        port = portNumber;
    }


    /**
     * Ouvre des ressources pour écrire/lire dans des sockets
     */
    public void open() {
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
     * @Server
     */
    public void envoyerDescriptions(String descriptionsDir) throws IOException {
        File dir;

        dir = new File(descriptionsDir);

        // On envoie d'abord le nombre de fichiers à envoyer
        envoyerMessage("FILECOUNT " + dir.list().length);

        // On liste les fichiers partagés
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(descriptionsDir))) {
            for (Path path : stream) {
                envoyerFichier(path.toString());
            }
        }
    }

    /**
     * dir est le répertoire où sont les descriptions
     * @Central
     * @deprecated
     */
    public void recevoirDescriptions(String dir) throws IOException {
        String msg;
        FTPCommand ftpCmd;
        int fileCount;

        // On attend un message avec pour commande FILECOUNT
        do {
            msg = lireMessage();
            ftpCmd = FTPCommand.parseCommand(msg);
        } while (!ftpCmd.command.equals("FILECOUNT"));

        // On récupère le nombre de fichiers à recevoir
        fileCount = Integer.parseInt(ftpCmd.content);

        for (int i = 0; i != fileCount; i++) {
            lireFichier(dir);
        }
    }

    /**
     * dir est le répertoire où sont les descriptions
     * @Central
     */
    public void saveDescriptions(String dir, int fileCount) throws IOException {
        for (int i = 0; i != fileCount; i++) {
            lireFichier(dir);
        }
    }

    /**
     * Lit un message (une ligne) envoyé par socket
     */
    public String lireMessage() throws IOException {
        return reader.readLine();
    }

    /**
     * Envoie une message à travers une socket
     */
    public void envoyerMessage(String msg) throws IOException {
        System.out.println("> " + msg);
        writer.println(msg);
    }

    /**
     * Libère les ressources
     */
    public void close() {
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
     */
    public void envoyerContenu(String filePath) throws IOException {
        BufferedReader br;
        File file;
        FileReader fr;
        String line;
        int fileSize;

        file = new File(filePath);
        fileSize = (int) file.length();// pas utilisée
        fr = new FileReader(file);
        br = new BufferedReader(fr);

        // On envoie le fichier ligne par ligne
        while ((line = br.readLine()) != null) {
            envoyerMessage(line);
        }

        // L'étiquette END marque la fin du fichier
        // TODO Une alternative serait d'utiliser la taille du fichier,
        // mais c'est trop compliqué. Toutefois, qu'est-ce qu'il se passe
        // si le fichier contient END ?
        // Ou bien le nombre de lignes, mais obtenir une méthode efficace
        // qui compte le nombre de lignes, c'est pas facile
        envoyerMessage("END");
        fr.close();
    }

    /**
     * Récupère UN fichier envoyé par socket
     *
     * Ici on a un BufferedReader qu'on pourrait mettre en attribut
     */
    public void enregistrerContenu(String filePath) throws IOException {
        BufferedWriter bw;
        File file;
        FileWriter fw;
        String msg;

        file = new File(filePath);
        fw = new FileWriter(file);
        bw = new BufferedWriter(fw);

        // Chaque ligne du fichier nous est envoyée
        // L'étiquette END marque la fin de la transmission
        while (!(msg = lireMessage()).equals("END")) {
            bw.write(msg + "\n");
        }

        bw.close();
    }

    /**
     * On envoie : le nom du fichier et sa taille (en octets)
     * La taille n'est pas utile pour le moment, mais elle pourrait
     * l'être plus tard ! (voir enregistrerContenu() envoyerContenu())
     * Ensuite on envoie le contenu
     */
    public void envoyerFichier(String filePath) throws IOException {
        String fileName;
        File file;

        file = new File(filePath);
        fileName = file.getName();
        		// filePath.substring(filePath.lastIndexOf('/') + 1);

        // L'étiquette FILE va indiquer qu'on envoie le nom et la taille
        envoyerMessage("FILE " + fileName + " " + file.length());
        envoyerContenu(filePath);
    }


    /**
     * On récupère le nom de fichier et sa taille, ensuite le contenu du
     * fichier
     */
    public void lireFichier(String dir) throws IOException {
        String msg;
        FTPCommand ftpCmd;
        String fileName;
        int fileSize;

        // On attend un message avec pour commande FILE
        do {
            msg = lireMessage();
            ftpCmd = FTPCommand.parseCommand(msg);
        } while (!ftpCmd.command.equals("FILE"));

        fileName = ftpCmd.content.split(" ")[0];
        fileSize = Integer.parseInt(ftpCmd.content.split(" ")[1]);
        System.out.println(dir + fileName);
        enregistrerContenu(dir + fileName);
        
    }
}

