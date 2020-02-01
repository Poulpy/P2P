package abstractions;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.net.Socket;
import java.net.UnknownHostException;
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
    protected DataOutputStream dos;
    protected DataInputStream dis;

    // Adresse IP de l'utilisateur
    protected String adresseIP = "127.0.0.1";
    // Adresse IP du serveur
    protected String adresseIPServeur = "127.0.0.1";
    protected int port = 50000;
    protected Socket socket;

    /**
     * TODO Affectation du port et des adresses IP
     */
    protected Yoda() {
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
     */
    public void envoyerDescriptions(String dir) throws IOException {
        File d = new File(dir);

        // On liste les fichiers partagés
        envoyerMessage("FILECOUNT " + d.list().length);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                System.out.println("J'envoie " + path.toString());
                envoyerDescription(path.toString());
            }
        }
    }

    public void recevoirDescriptions(String dir) throws IOException {
        String msg;
        FTPCommand ftpCmd;
        int fileCount;

        ftpCmd = FTPCommand.parseCommand(lireMessage());
        fileCount = Integer.parseInt(ftpCmd.content);

        for (int i = 0; i != fileCount; i++)
            recevoirDescription(dir);
    }

    /**
     * Envoie la description d'un fichier, avec le nom du fichier
     */
    protected void envoyerDescription(String filePath) {
        int index = filePath.lastIndexOf('/');
        String fileName = filePath.substring(index + 1, filePath.length());
        FTPCommand ftpCmd = new FTPCommand("FILE", fileName);

        //System.out.println(">>> " + ftpCmd.command.compareTo("FILE"));
        //System.out.println(ftpCmd);

        try {
            envoyerMessage(ftpCmd.toString());
            envoyerFichier(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Réception de la description d'un fichier dans un répertoire dir
     */
    protected void recevoirDescription(String dir) {
        try {
            lireFichier(dir);
            System.out.println("Fichier sauvegardé");
        } catch (IOException e) {
            e.printStackTrace();
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
        // Une alternative serait d'utiliser la taille du fichier, mais c'est trop compliqué
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
    protected void save(String filePath/*, int fileSize*/) throws IOException {
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
     * La taille n'est pas utile pour le moment, mais elle pourrait l'être plus tard !
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

