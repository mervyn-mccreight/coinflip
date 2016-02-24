# coinflip
Implement a non-server dependent coin flipper client as part of a IT-Security project in university.
This projects provides an executable JAR-File to either run the initial-sending (client) or initial-retrieving part of the coinflip-protocol exchange.

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
