import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class P2PCentralizedServer {

    private String adresseIP = "127.0.0.1";
    private int port = 50000;
    // Chemin du fichier contenant les utilisateurs connus du serveur
    // identifiant,hash du mot de passe
    private String cheminUtilisateurs = "utilisateurs.csv";

    public P2PCentralizedServer() {
    }

    /**
     * TODO Gestion des commandes
     * En fonction de la commande, le serveur fait une action
     * particulière;
     * Par exemple, "USER toto"
     * Le serveur vérifie que l'utilisateur toto existe;
     * Si toto existe pas, il le crée
     * "PASS fierhigmeruis"
     * Le serveur vérifie que le mot de passe est correct en fonction
     * de l'identifiant précédemment envoyé
     */
    public void gererMessage(String commande, String contenu) {
        switch (commande) {
            case "USER":
                if (utilisateurExiste(contenu)) {
                    // Renvoyer code de succès
                } else {
                    creerUtilisateur(contenu);
                }
            case "PASS":
                if (mdpCorrect()) {
                    // Renvoyer code succès
                } else {
                    // Code erreur
                }
            default:
        }
    }

    /**
     * TODO Renvoie un code (succès / échec)
     */
    public void renvoyerCode() {
    }

    /**
     * TODO Création d'un utilisateur dans le fichier csv
     * Est-ce qu'on donne l'identifiant ET le mot de passe ?
     * Ou on renseigne d'abord l'identifiant puis on complète plus
     * tard par le mot de passe ?
     */
    public void creerUtilisateur(String identifiant) {
    }

    /**
     * TODO: Vérifier qu'un utilisateur existe
     * On regarde si son identifiant/nom est présent
     * dans le fichier utilisateurs.csv
     * Les utilisateurs sont stockés comme ça :
     * identifiant,hash_du_mot_de_passe
     */
    public boolean utilisateurExiste(String identifiant) {
        return true;
    }

    /**
     * TODO: Vérifier qu'un mot de passe est correct pour
     * un utilisateur; On regarde dans le fichier
     * utilisateurs.csv
     */
    public boolean mdpCorrect(String identifiant, String hashMdp) {
        return true;
    }

    public static void main(String[] args){
        P2PCentralizedServer s = new P2PCentralizedServer();
        String contenu = "";
        int stream;
        byte[] b = new byte[1024];
        int index;

        try {
            ServerSocket serveurCentral = new ServerSocket(s.port, 10, InetAddress.getByName(s.adresseIP));
            Socket client = serveurCentral.accept();
            BufferedInputStream lecteur = new BufferedInputStream(client.getInputStream());

            // le serveur écoute/reçoit ce qu'on lui envoie
            while ((stream = lecteur.read(b)) != -1) {
                contenu = new String(b, 0, stream);

                // Séparation de la commande et du texte. Exemple :
                // USER toto
                // PASS mot_de_passe
                // On récupère l'indice où apparaît le le permier espace

                index = contenu.indexOf(' ');
                String type = contenu.substring(0, index);
                String text = contenu.substring(index + 1, contenu.length() - 1);
                System.out.println("COMMAND " + type);
                System.out.println("TEXT " + text);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
