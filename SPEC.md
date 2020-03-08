# FSP

Version : 1

## Abstract

Peer to peer protocole for sharing files.
The user has two applications : a server and a client. The client can download files shared by other users.
The server can upload files to a client.

The centralized server (Central) can :
- authentify users
- store descriptions of users' shared files
- provide a filesearch through a keyword provided by a client

## Authentification
### Server
The authentification is made by the server on the user site. The server provides a id and a hash of the user's password. The central will check first is the id exists; then checks if the password is correct. The id is sent with the USER command. The password is sent with the PASS command.
If the authentification succeeds, the server finally sends the hostname of the user's machine.
The hostname will be used by the central to recognize the client.

```
   USER toto
      +
      |
      +------------+
      |            |
      |            |
      |            |
      v            v
     Correct    New user
      +            +
      |            |
      +<-----------+
      |
      v
PASS eozfu2fze41rg4
      +
      |
      +----------------+
      |                |
      |                |
      |                v
      |              Incorrect
      v
 Connection
 successful
      +
      |
      v
HOST dinfo

```
### Client

The client sends its hostname to the central. It is not the client that prompts the hostname, because a user could 'hack' another user account.


```
+------------------+
|                  |
|      Client      |
|                  |
+--------+---------+
         |
         v
    HOST dinfo
         +
         +----------------+
         v                v
      Correct        Authentification failed

         +
         |
         |
         |
         v
    Authentification
    successful

```

## After authentification
### Server

The server sends the descriptions of the files shred by the user. First is sent the number of descriptions the central will receive. That is done with the command `FILECOUNT 4`. Then, for each file, the server sends the name of the file and the size in bytes : `FILE starwars 23`. The datas of the file are sent afterwards. Is the central has received all bytes, it must return an `ACK` command.
```
                                          +----------+
+-------+                                 |          |
| Server|      FILECOUNT 1                |   Central|
|       +-------------------------------->+          |
|       |                                 |          |
|       |       FILE toto 23              |          |
|       +-------------------------------->+          |
|       |                                 |          |
|       |        File content of toto     |          |
|       +-------------------------------->+          |
|       |                                 |          |
|       |        ACK                      |          |
|       +<--------------------------------+          |
+-------+                                 +----------+
```

### Client

The client can send queries to the server. The client prompts a keyword. The central will search a word or a substring of it in the descriptions of shared files provided by the users' servers : `SEARCH client`. If a result is found, the central returns a list of files with the hostname of the user's matching file : `FOUND dinfo/FSPClient.java dinfo/FSPServer.java`. If no results are found, the central must return `NOTFOUND`.
```
    SEARCH client
        +
        +-------------------------------+
        |                               |
        |                               |
        |                               |
        v                               v
FOUND dinfo/FSPClient.java         NOTFOUND

```

## Download file

## Update shared file

## Add a shared file


## Deconnection

The server may send a `QUIT` command. The client then won't be able to query the central anymore, and the central musn't share anymore the files shared by the user (until next connection).

