# P2P

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


## En cours

- le serveur au lancement demande l'identifiant et le mot de passe
- après renseignement de l'identifiant et du mot de passe, le serveur les envoie au serveur centralisé
- le serveur chiffre le mot de passe
- le serveur centralisé enregistre les utilisateurs (identifiant et mot de passe chiffré) dans un fichier
- le serveur centralisé authentifie l'utilisateur et renvoie le résultat au serveur (succès échec)

## Todo


- le serveur centralisé garde les noms et descriptions des fichiers téléchargés
- le serveur après l'authentification télécharge à partir du serveur centralisé les noms des fichiers partagés et leurs descriptions
- recherche entre le client et le serveur centralisé et téléchargement
- le serveur notifie le serveur centralisé de son activité
- le client garde la liste des fichiers téléchargés
- le client met à jour les fichiers qu'il a téléchargé
- le serveur centralisé partage les fichiers d'un client si le serveur est actif

## Todo Code
- créer un fichier main pour le client et les 2 serveurs
- une classe Message, qui enverrait des messages à travers des sockets
- renommer les classes, leur donner de jolis noms
- refaire les constructeurs du client et du serveur (qui soit utiles quoi :p)


### Comment exécuter le projet ?

Compilez les fichiers java comme suit, à la racine du projet :

`javac client/P2PServer.java`

`javac server/P2PCentralizedServer.java`

Ensuite, dans 2 terminaux, exécutez le serveur puis le client :

`java server.P2PCentralizedServer`


`java client.P2PServer`

### Utilisateurs

Les utilisateurs sont dans le fichier utilisateurs.csv (à la racine du projet)


