
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.*;


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
    
    public  void EnregistrerUtilisateur(String s, String c ) throws IOException {
    	
    File file = new File("test.txt");     
    
    try (BufferedWriter bufferedWriter= new BufferedWriter(new FileWriter(file))) { 
    	bufferedWriter.write(s);
    	bufferedWriter.write("    ");
    	bufferedWriter.write(c);
    	bufferedWriter.newLine();
    
   
    	
    	
    }catch(IOException e) {
    	e.printStackTrace();
    }
    }
 

    public static void main(String[] args){ 
    	 P2PServer s= new P2PServer();
        
      
    }
}
