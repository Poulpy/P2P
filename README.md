# FSP : File Sharing Protocol (nom provisoire)


FSP est un protocol similaire à FTP, en plus simple.


## Comment un client communique avec un serveur TCP ?

Le client envoie des *commandes* au serveur :
- USER : fournit l'identifiant de l'utilisateur
- PASS : fournit le mot de passe

Une communication TCP ressemble donc à ça :
Le client envoie le message suivant au serveur grâce à une socket :

`USER toto`


Si succès, le client envoie son mot de passe pour finaliser sa connexion :

`PASS password`

Si le mot de passe est correct, l'utilisateur est connecté au serveur et peut donc lui
envoyer d'autres commandes (pour voir quels fichiers sont partagés sur le serveur par exemple).


## En cours / Fait

- le serveur au lancement demande l'identifiant et le mot de passe
- après renseignement de l'identifiant et du mot de passe, le serveur les envoie au serveur centralisé
- le serveur chiffre le mot de passe
- le serveur centralisé enregistre les utilisateurs (identifiant et mot de passe chiffré) dans un fichier
- le serveur centralisé authentifie l'utilisateur et renvoie le résultat au serveur (succès échec)
- le serveur centralisé garde les noms et descriptions des fichiers téléchargés
- le serveur après l'authentification télécharge à partir du serveur centralisé les noms des fichiers partagés et leurs descriptions

## Todo


- plusieurs clients peuvent se connecter au serveur (multi thread)
- recherche entre le client et le serveur centralisé et téléchargement
- le serveur notifie le serveur centralisé de son activité
- le client garde la liste des fichiers téléchargés
- le client met à jour les fichiers qu'il a téléchargé
- le serveur centralisé partage les fichiers d'un client si le serveur est actif
- créer une spécification. Puisqu'on ne fait pas exactement une implémentation du protocol FTP, il est bien de spécifier ce qu'implémente notre application !

## Todo Code

- Revoir à quel niveau mettre les try/catch
- Utiliser la classe FTPCommand
- revoir la structure du projet : répertoire des descriptions, fichiers partagés, utilisateurs.csv (voir le standard ISO, ou la structure d'un projet Maven)
- une classe Message, qui enverrait des messages à travers des sockets (xml, json)
- renommer les classes et les variables, leur donner de jolis noms
- refaire les constructeurs du client et du serveur (qui soit utiles quoi :p)
- mettre les tests dans une classe à part
- remplacer les compareTo par des .equals() => + compréhensible
- `System.getProperty("line.separator");


### Comment exécuter le projet ?

Compilez les fichiers java comme suit, à la racine du projet :

`javac client/Client.java`

`javac server/Server.java`

Ensuite, dans 2 terminaux, exécutez le serveur. Après avoir exécuté le serveur, exécutez le client :

`java server.Server`


`java client.Client`

## Pourquoi un utilisateur a besoin d'installer un client et en plus un serveur ?

Le principe de cette application est de pouvoir partager des fichiers. Tant que le programme côté client est actif, on partage les fichiers contenus dans la machine côté client.
S'il ne tourne pas, on arrête de partager les fichiers.
Mais est-ce que l'utilisateur est tout le temps connecté au serveur ? Non pas forcément, c'est pour ça qu'on a besoin de 2 programmes côté client : un programme pour partager des fichiers auprès d'autres clients, et un autre qui traitera les requêtes du client.

### Utilisateurs

Les utilisateurs sont dans le fichier utilisateurs.csv (à la racine du projet)

## Pour exécuter les tests

### Unix

Il faut installer JUnit 4 :

```
apt install junit4
mkdir ~/junit
ln -s /usr/share/java/junit4.jar ~/junit
```
Ensuite, dans le `~/.bashrc` :

```
export JUNIT_HOME=~/junit
if [ ${#CLASSPATH} -eq 0 ]
then
    export CLASSPATH="$JUNIT_HOME/junit-4.jar"
else
    export CLASSPATH="$CLASSPATH:$JUNIT_HOME/junit-4.jar"
fi
```

