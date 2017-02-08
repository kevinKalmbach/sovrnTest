# Example Server

Run this project as a Spring Boot app


Compiling
========
Simply run mvn clean install. This will produce a jar file in the target directory.



Running
========
Variables to set:

| variable             | Description  |
|------------------|--------------|
| CLICKTIMER | Amount of time (in seconds before a ad goes "stale") |
| com.example.db.url | The url of the database (default is jdbc:mysql://localhost:3306/sovrn) |
| com.example.db.user| The database user (default is root) |
| com.example.db.password | The database password |

Examples of starting service:
=========
```
java -DCLICKTIMER=200 -Dcom.example.db.user=root -Dcom.example.db.password=my-secret-pw -Dcom.example.db.url=jdbc:mysql://localhost:3306/sovrn -jar target/sovrnTest-0.0.1-SNAPSHOT.jar
```

The variables can also be set by environment variables (this is identical to the previous call)
```
export CLICKTIMER=200
export COM_EXAMPLE_DB_USER=root
export COM_EXAMPLE_DB_PASSWORD=my-secret-pw
export COM_EXAMPLE_DB_URL=jdbc:mysql://localhost:3306/sovrn
java -jar target/sovrnTest-0.0.1-SNAPSHOT.jar
```
