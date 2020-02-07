# FSP : File Sharing Protocol (nom provisoire)


FSP est un protocol similaire à FTP, en plus simple.
La documentation est disponible [ici](https://poulpy.github.io/P2P/docs)

## Comment un client communique avec un serveur TCP ?

Le client envoie des *commandes* au serveur :
- USER : fournit l'identifiant de l'utilisateur
- PASS : fournit le mot de passe

Une communication TCP ressemble donc à ça :
Le client envoie le message suivant au serveur grâce à une socket :

`USER toto`


En fonction de la requête, la centrale peut renvoyer un code d'erreur ou de succès.
Si c'est un code de succès, le client envoie son mot de passe pour finaliser sa connexion :

`PASS password`

Si le mot de passe est correct, l'utilisateur est connecté au serveur et peut donc lui
envoyer d'autres commandes (pour voir quels fichiers sont partagés sur le serveur par exemple).

## Nomenclature

Commande (ou commande FTP) : désigne une étiquette et un contenu. `USER toto`, `PASS password` : USER et PASS sont des étiquettes. En fonction de l'étiquette, l'action demandée n'est pas la même.

Client : le programme exécuté par l'utilisateur. Envoie des requêtes auprès de la Centrale pour quérir l'adresse de fichiers recherchés.

Serveur : le programme exécuté par l'utilisateur. Gère l'authentification et l'envoi des descriptions des fichiers partagés par l'utilisateur.

Central : le programme faisant office de serveur centralisé. Gère :
- l'authentification
- la recherche de fichiers par mot-clef
- le stockage des descriptions des fichiers partagés

## En cours / Fait

- le serveur au lancement demande l'identifiant et le mot de passe
- après renseignement de l'identifiant et du mot de passe, le serveur les envoie au serveur centralisé
- le serveur chiffre le mot de passe
- le serveur centralisé enregistre les utilisateurs (identifiant et mot de passe chiffré) dans un fichier
- le serveur centralisé authentifie l'utilisateur et renvoie le résultat au serveur (succès échec)
- le serveur centralisé garde les noms et descriptions des fichiers téléchargés
- le serveur après l'authentification télécharge à partir du serveur centralisé les noms des fichiers partagés et leurs descriptions

## Todo


- Envoyer et récupérer le nom d'hôte
- Plusieurs clients peuvent se connecter au serveur (multi thread)
- Recherche entre le client et le serveur centralisé et téléchargement
- Le serveur notifie le serveur centralisé de son activité.
Création d'un chronomètre côté serveur :
si le client n'envoie aucun message depuis x secondes, on le considère comme déconnecté. (Il faut alors envoyer un message qui incite le client à fermer la socket.)
- Le client garde la liste des fichiers téléchargés
- Le client met à jour les fichiers qu'il a téléchargé
- Le serveur centralisé partage les fichiers d'un client si le serveur est actif
- Un Client et un Serveur côté utilisateur. Pour l'instant les 2 sont dans une seule classe 'Client'

## Todo Code

- Voir si toutes les ressources sont bien libérées quand on quitte l'application (`close()`) => try with resource.
- Eviter l'anti-pattern GodClass : une classe qui a trop de responsabilités.
- Utiliser la classe FTPCommand
- Renommer les classes et les variables, leur donner de jolis noms
- Refaire les constructeurs du client et du serveur (qui soit utiles quoi :p)
- Ecrire des tests.
- Plus de documentation (plus de tag : param, return, etc.)
- Gérer les exceptions
- Une classe de test pour tester une suite de tests


## Idées

- Créer une spécification.
Puisqu'on ne fait pas exactement une implémentation du protocol FTP,
il est bien de spécifier ce qu'implémente notre application !
- Dark mode
- Interface graphique : ajouter un fichier dans le répertoire des fichiers partagés
- Un checksum du contenu du message
- Revoir la structure du projet : répertoire des descriptions, fichiers partagés, utilisateurs.csv (voir le standard ISO, ou la structure d'un projet Maven)
- Un parseur pour analyser les messages (on en revient à une classe Message);
ça permettrait d'avoir un protocole plus extensible. Pour l'instant le contenu est séparé par des espaces :
`FILE truc.txt 23`. Ce serait bien de pouvoir stocker dans un `HashMap<String, Object>` les valeurs. On pourrait avoir des valeurs optionnelles.
Par ailleurs, on pourrait envoyer les données sous forme de texte brut, ou bien en xml, ou bien en json.
Mieux, on pourrait juxtaposer les données d'un paquet. Actullement, les données sont séparées par des espaces.

### Comment exécuter le projet ?


D'abord, écrire dans le `~/.bashrc` les lignes suivantes :

```
if [ ${#CLASSPATH} -eq 0 ]
then
    export CLASSPATH="/usr/share/java/junit4.jar:/usr/share/java/hamcrest-core.jar:./src/"
else
    export CLASSPATH="$CLASSPATH:/usr/share/java/junit4.jar:/usr/share/java/hamcrest-core.jar:./src/"
fi
```

Ouvrez un nouveau terminal. Compilez les fichiers java comme suit, à la racine du projet :

```
javac src/fr/uvsq/fsp/client/Client.java
javac src/fr/uvsq/fsp/server/Central.java
```

Ensuite, dans 2 terminaux différents, exécutez le serveur. Après avoir exécuté le serveur, exécutez le client :

```
java fr.uvsq.fsp.client.Client
java fr.uvsq.fsp.server.Central
```


## Faire marcher le projet sur Eclipse

Importez un projet

Projects from Git (with smart import)>Clone URI

Après avoir cloné le projet, ajoutez la librairie JUnit4 dans le BuildPath: 
Clic droit sur le projet>BuildPath>Configure>Library>Add library>JUnit4

Après, clic droit sur le répertoire src/, et ajoutez le au BuildPath.

Ensuite vérifiez que le projet a bien une icone J (pour java).

Dans Window>Preferences>General>Workspace, en bas, changez l'encodage en UTF-8.

Enfin, pour exécuter le projet : 
- clic droit sur Central.java, Run As>Java Application
- clic droit sur Client.java, Run As>Java Application

Et ça maaaarche !


## Pourquoi un utilisateur a besoin d'installer un client et en plus un serveur ?

Le principe de cette application est de pouvoir partager des fichiers.
Tant que le programme côté client est actif, on partage les fichiers contenus dans la machine côté client.
S'il ne tourne pas, on arrête de partager les fichiers.
Mais est-ce que l'utilisateur est tout le temps connecté au serveur ?
Non pas forcément, c'est pour ça qu'on a besoin de 2 programmes côté client :
un programme pour partager des fichiers auprès d'autres clients, et un autre qui traitera les requêtes du client.

### Utilisateurs

Les utilisateurs sont dans le fichier `server/utilisateurs.csv`

## Pour exécuter les tests

### Unix

Il faut installer JUnit 4 :

```
apt install junit4
```


Pour compiler les tests et les exécuter :
```
javac src/fr/uvsq/fsp/test/TestServer.java
java org.junit.runner.JUnitCore fr.uvsq.fsp.test.TestServer
```
Si ça marche pas, essayez : 
```
java -cp /usr/share/java/junit4.jar:/usr/share/java/hamcrest-core.jar:src/ org.junit.runner.JUnitCore src.fr.uvsq.fsp.test.TestServer
```

## Ecrire des tests

### Annotations

#### Test

Une méthode de test doit être systématiquement précédée d'une annotation @Test

```java
    @Test
    public void testUtilisateurExiste() throws Exception {
        Assert.assertEquals(0, 0);
    }
```

#### Before

Sera exécuté avant chaque test.

#### After

Sera exécuté après chaque test.

#### BeforeClass

Sera exécuté avant le premier test.

#### AfterClass

Sera exécuté après le dernier test.



## Je sais pas quoi faire

`find * -name *.java -type f | xargs grep -n TODO`

TODO Faire un script qui mettrait tous les TODO dans le README sous forme de tableau markdown

### Générer la documentation

A la racine du projet :

`javadoc -d docs/ */*.java`

Pour mettre à jour la documentation hébergée par Github, il faut faire un commit dans la branche `gh-pages`.

### Dépendances

Java 1.8

JavaFX 8

JUnit 4


