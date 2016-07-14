# Art tag game

####Database
## Requirements
Postgres 9.4

To create and prepare database you can use prepared scripts. 

For example you can run them in PSQL in your terminal. First you have to run PSQL in your terminal (the method can vary depends of your operating system). Then run these commands:

\i tag_game/backend/src/main/resources/db/create_DB_and_user_commands.txt
\c arttag
\i tag_game/backend/src/main/resources/db/create_schema.sql
\i tag_game/backend/src/main/resources/db/init_schema.sql

##### Build
## Requirements
Java JDK 8, Apache Tomcat 8, Apache Maven 3.2.5.


Backend uses maven for dependency management and build cycle.
Running `mvn clean install` will trigger build and will produce WAR file ( in tag_game/backend/target folder) for deployment in servlet container.

#### Deployment
## Requirements
Java JDK 8, e.g. Apache Tomcat 8

After build, application is intended to be deployed in any standard 3.0 or higher servlet container (Tomcat, Jetty, etc.). Just deploy the *.war file.
When using Tomcat you can place the WAR (Webapp Archive) file for deployment to tomcat/webapps folder and then start Tomcat.

### Environment properties
Application can be switched into different modes, depending on usage in development, testing or production environment. It uses mechanism of runtime attribute that specifies environment to use. The rules are simple:
1. If attribute `-Dspring.profiles.active=prod` is present as VM option - application will be run in production mode. File `application-prod.properties` will be used.
2. If attribute `-Dspring.profiles.active=dev` is present as VM option - application will be run in development mode. File `application-dev.properties` will be used.
3. If attribute `-Dspring.profiles.active=test` is present as VM option - application will be run in testing mode. File `application-test.properties` will be used.

Properties placed in `application.properties` will be shared across all profiles, but they will be overridden if same property name will occur in dev/prod/test file.

## Development
For development purpose you don't need to deploy war file with servlet container. You can just run `mvn spring-boot:run` command.



