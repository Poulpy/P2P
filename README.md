# FSP : File Sharing Protocol (nom provisoire)


FSP est un protocol similaire à FTP, en plus simple.
La documentation est disponible [ici](https://poulpy.github.io/P2P/)

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
- Le client garde la liste des fichiers téléchargés

## Todo


- Tuer les threads du Central quand on quitte le programme. [Ici](https://docs.oracle.com/javase/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html)
Utiliser une pool de Thread.
- Utiliser l'interface graphique (Rercherche : fait; Téléchargement : à faire)
- Plusieurs clients peuvent se connecter au serveur (multi thread). TODO A TESTER
- Un Serveur attend une connexion d'un client, pour télécharger un ou plusieurs fichiers
- Le client met à jour les fichiers qu'il a téléchargé

## Todo Code

- Voir si toutes les ressources sont bien libérées quand on quitte l'application (`close()`) => try with resource.
- Ecrire des tests.
- Plus de documentation (plus de tag : param, return, etc.)
- Gérer les exceptions
- Controller : une méthode pour gérer chaque événement Actuellement, tous les événements sont dans le constructeur !
- ClientView ClientController : message qui marque Aucun fichier trouvé (recherche)
- Quand un client envoie une commande : vérifier qu'il est connecté



## Idées

- Tester avec une Raspberry
- Créer une spécification.
Puisqu'on ne fait pas exactement une implémentation du protocol FTP,
il est bien de spécifier ce qu'implémente notre application !
- Dark mode
- Interface graphique : ajouter un fichier dans le répertoire des fichiers partagés
- Un checksum du contenu du message
- Un parseur pour analyser les messages (on en revient à une classe Message);
ça permettrait d'avoir un protocole plus extensible. Pour l'instant le contenu est séparé par des espaces :
`FILE truc.txt 23`. Ce serait bien de pouvoir stocker dans un `HashMap<String, Object>` les valeurs. On pourrait avoir des valeurs optionnelles.
Par ailleurs, on pourrait envoyer les données sous forme de texte brut, ou bien en xml, ou bien en json.
Mieux, on pourrait juxtaposer les données d'un paquet. Actullement, les données sont séparées par des espaces.
- Fichier de configuration : Configration.json
- Un parseur pour analyser les arguments en ligne de commande

### Comment exécuter le projet ?


```
ant compile
```

Ensuite, dans 3 terminaux, exécutez le central. Après avoir exécuté le central, exécutez le serveur et ensuite le client.

Pour le central renseignez le port.

Pour le serveur l'adresse IP du serveur, le port, le login et le mot de passe

Pour le client l'adresse IP du serveur et le port.

```
cd build/
java fr.uvsq.fsp.server.Central 60000
java fr.uvsq.fsp.client.CServer 127.0.0.1 60000 toto admin
java fr.uvsq.fsp.client.Client
```

### Exécuter les tests


```
ant test
```

Les tests sont exécutés à chaque push.


### Générer la documentation


```
ant doc
```

### Mettre en ligne la documentation

```
git checkout gh-pages && git merge - && git push origin gh-pages && git checkout -
```

### Troubleshoting

Il se peut qu'en cours d'exécution, le central ou le client lance une exception "JVM Bind", "Connection refused, port already in use", "Adresse déjà utilisée".
C'est que de précédents processus n'ont pas été correctement terminés. Il faut donc soit terminer correctement les processus soit choisir un autre port.

## Faire marcher le projet sur Eclipse

**Pour faire marcher le projet sur Eclipse, il faut bien suivre les instructions suivantes.**

Importez un projet

Projects from Git (with smart import) > Clone URI

Après avoir cloné le projet, ajoutez la librairie JUnit4 dans le BuildPath: 
Project > Properties > Java BuildPath > Library > Add library > JUnit4

Après, Project > Properties > Java Build Path > Sources > Add Folder > et vous ajoutez le répertoire `src/`. Il n'y qu'une seule case à cocher. Si l'onglet Sources liste d'autres répertoires (`P2P/client` par exemple), vous pouvez les enlever.

Ensuite vérifiez que le projet a bien une icone J (pour java).

Dans Window > Preferences > General > Workspace, en bas, changez l'encodage en UTF-8.

Enfin, pour exécuter le projet, **d'abord** : 

**clic droit** sur Central.java, Run As > Java Application

et seulement **après** :

**clic droit** sur Client.java, Run As > Java Application

Et ça maaaarche !



## Je sais pas quoi faire

`find * -name *.java -type f | xargs grep -n TODO`

### Dépendances

Java 1.8

JavaFX 8

JUnit 4

Ant 10

