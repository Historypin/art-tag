# Art tag game

## Requirements
Development of this project requires:
##### Backend
Java JDK 8, Apache Tomcat 8, Apache Maven 3.
##### Frontend
npm (bundled with node.js), grunt, bower, yeoman (not strictly required but useful)

## Build
##### Backend
Backend uses maven for dependency management and build cycle.
Running `mvn clean install` will trigger build and will produce WAR file ( in /target folder) for deployment in servlet container.

##### Frontend
Dependencies are handled by npm and bower. Automation (we can call it build) is handled by grunt.
After checking out sources:
1. Go to src/main/webapp.
2. Run `npm install` to install npm modules.
3. Run `bower install` to install bower components.
3. Run `grunt` to build/compile frontend.

## Deployment
##### Backend
After build, backend is intended to be deployed in any standard 3.0 or higher servlet container (Tomcat, Jetty, etc.). Just deploy the *.war file.
##### Frontend
Is bundled in war file. By the nature of the project it is possible to separate backend from frontend, e.g. frontend will be served from Apache HTTPD or Nginx and backend from servlet container. They will communicate through REST API.

### Environment properties
Application can be switched into different modes, depending on usage in development, testing or production environment. It uses mechanism of runtime attribute that specifies environment to use. The rules are simple:
1. If no attrubute is used - application will be run in production mode. It will read properties from file `prod.properties`.
2. If attribute `-Dspring.profiles.active=dev` is present as VM option - application will be run in development mode. File `dev.properties` will be used.
3. If attribute `-Dspring.profiles.active=test` is present as VM option - application will be run in testing mode. File `test.properties` will be used.

Properties placed in `default.properties` will be shared across all profiles, but they will be overridden if same property name will occur in dev/prod/test file.

## Development
This section contains some development guidelines and tips.

### Livereload

### Workflow with yeoman


