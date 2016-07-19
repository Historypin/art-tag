# Art tag game

####Database
## Requirements
Postgres 9.4

To create and prepare database you can use prepared scripts (project_dir}/tag_game/backend/src/main/resources/db/create_DB_and_user_commands.txt, project_dir}/tag_game/backend/src/main/resources/db/create_schema.sql, project_dir}/tag_game/backend/src/main/resources/db/init_schema.sql).


##### Build
## Requirements
Java JDK 8, Apache Tomcat 8, Apache Maven 3.2.5.

Create opt/td/conf folder in your root path. Copy file from {project_dir}/tag_game/backend/src/main/resources/arttag.properties.example to this new folder and delete .example from this file's name. If needed, you can also adjust this configuration file.
Create system variable (e.g. for Tomcat - CATALINA_OPTS=-Dworking.directory=/opt/td).
Backend uses maven for dependency management and build cycle.
Running `mvn clean install` will trigger build and will produce WAR file ( in {project_dir}/tag_game/backend/target folder) for deployment in servlet container.

#### Deployment
## Requirements
Java JDK 8, e.g. Apache Tomcat 8

After build, application is intended to be deployed in any standard 3.0 or higher servlet container (Tomcat, Jetty, etc.). Just deploy the *.war file.
When using Tomcat you can place the WAR (Webapp Archive) file for deployment to tomcat/webapps folder and then start Tomcat.

## Development
For development purpose you don't need to deploy war file with servlet container. You can just run `mvn spring-boot:run` command.



