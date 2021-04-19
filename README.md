# LibManager-Server
### A software to manage a library
##### Université de Bretagne-Sud - INF1503 - 2020

### Required
* A SQL database (e.g. MariaDB, JDBC is already declared in build.gradle)

### Configuration
* In `src/main/resources`, rename `application.properties.example` to `application.properties` and complete it
* Import the database dump

The default credentials for the admin account are:
* Username: admin
* Password: admin 
  
Please consider changing the password asap

### How to run ?
* Windows: `gradlew.bat bootRun`
* Linux/macOS: `./gradlew bootRun`

### To do
* Use correct HTTP methods and status code
