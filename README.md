# coinflip

## Summary
Implement a non-server dependent coin flipper client as part of a IT-security project in university.
This program follows a [protocol](#protocol) developed during the IT-security project.
This projects provides an executable JAR-File to either run the initial-sending (client) or initial-retrieving part of the coinflip-protocol exchange.

## Background
This program has been developed during an IT-security project in university. The project was lead by [Prof. Dr. Gerd Beuster](https://www.researchgate.net/profile/Gerd_Beuster).

### Project goal
The goal of the project was to develop a protocol for online games of chances. Most of the current protocols for such games needs the player to trust the server.
During the project we developed a protocol, where the clients could play a game of chances against each others without the need to trust a server for the evaluation.
To proof the concept of the protocol, we took the simplest game of changes - a coinflip - and developed a client using our protocol to play coinflips against each other.

### Protocol
#### Commutative public-key algorithm
The protocol we developed needs a _commutative_ public-key algorithm to work. A public-key algorithm is commutative, if the following equation holds.  
![Commutative public-key algorithm](https://latex.codecogs.com/gif.latex?%5Cdpi%7B100%7D%20%5Clarge%20D_%7BK1%7D%28E_%7BK2%7D%28E_%7BK1%7D%28M%29%29%29%20%3D%20E_%7BK2%7D%28M%29%5C%5C%5C%5C%20where%5C%20D_K%5C%20describes%5C%20an%5C%20decryption%5C%20with%5C%20key%5C%20K%5C%5C%20and%5C%20E_K%5C%20describes%5C%20an%5C%20encryption%5C%20with%5C%20key%5C%20K%5C%5C%20and%5C%20M%5C%20describes%5C%20an%5C%20arbitrary%5C%20message.)

which basically means, that the following is possible:
1. Encrypt M with K1
2. Encrypt the result with K2
3. Decrypting the result with K1 obtain M encrypted with K2

#### Do coinflipping using the protocol
Imagine we have two players who want to play a secure online coinflip against each other, without the need to trust a server to do correct randomness for the coinflip result.
In the following we name these two players _Alice_ and _Bob_.

1. _Alice_ generates two messages. _M1_ represents _Heads_ and _M2_ represents _Tails_. She adds a random string only she knows to both messages.
2. _Alice_ generates a key-pair _A_
3. _Alice_ sends __E[A](M)__ and __E[B](M)__ to _Bob_ in random order. _Bob_ __must not__ know the order.
4. _Bob_ picks one oif the messages. We call this message __E[A](M)__.
5. _Bob_ generates a key-pair _B_.
6. _Bob_ encrypts the picked message __E[A](M)__ with his key, which results in __E[B](E[A](M))__ and sends this to _Alice_.
7. _Alice_ decrypts the message _Bob_ sent during _6_, which results to __E[B](M)__ and sends this to _Bob_.
8. _Bob_ decrypts the message, which results in _M_. This represents the __result of the coinflip__. _Bob_ sends _M_ to _Alice_.
9. _Alice_ checks if the attached random-string is still correct.
10. _Alice_ and _Bob_ reveal their secret keys to each other.
11. _Alice_ and _Bob_ check the correctness of each others computation.

Following this protocol, we are now able to perform a secure online coinflip __without__ the need to have a server in which we have to trust not sending manipulated results.


What you need to compile & run the project
-------
0. open-jdk8 (the client/server does not run with oracle-java8, because of the unsigned bouncy castle fork)
0. Maven

Structure needed to run the jar
-------
The jar needs to have to be in the same directory as the directory "ssl-data".
The "ssl-data" directory contains key stores used to establish secure TLS-connections to other clients.
The "config" directory contains a configuration file.
The needed directory structure:
```
.
+-- <coinflip-jar-file>.jar
+-- config
|   +-- config.json
+-- ssl-data
|   +-- client
|   +-- keystore
|   +-- memc_keystore.jks
|   +-- root
|   +-- server
```

The needed files are provided in the repository.

Configuration
-------
The client is dependent on a configuration file. It is in JSON format. For now it contains the address of the broker-service.
Example configuration:
```
{
  "brokerAddress": "<broker-host>:<port>"
}
```


Building
-------
This project uses Maven as a building-tool.
All dependencies are resolved automatically (including the dependency to a release of [the projects own bouncy castle fork with SRA](https://github.com/timpauls/bc-java)).
To get the executable JAR-File run
```
mvn clean package
```
inside the project main directory.
The output jar will be in the <project-dir>/target folder, named "coinflip-1.0-SNAPSHOT-full.jar"
Please ensure to use the jar with the "-full" suffix, since this jar contains all the dependencies.

Available Modes
-------
0. a console client
0. an interactive server
0. a non-interactive server
0. a GUI client

Running
-------
To run the console-client execute
```
java -jar <path-to-jar> --client <hostname>:<port>
```
the hostname can either be an ip, or a domain-name.

To run the interactive-server execute
```
java -jar <path-to-jar> --server <port> <servername>
```
where the port specifies the port the server is listening on. To stop the server type "exit" to the upcoming server-shell and press "ENTER".

To run the silent-server (non-interactive) execute
```
java -jar <path-to-jar> --server-silent <port> <servername>
```
where the port specifies the port the server is listening on.

To run the GUI-client execute
```
java -jar <path-to-jar> --gui
```

Testing
--------
To run the tests use
```
mvn clean test
```
in the projects base directory.
