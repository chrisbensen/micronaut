# Load Data into an Instance

## Introduction

In this lab you will build an already build Micronaut application that connects to Oracle Autonomous Database.

Estimated Lab Time: 30 minutes

Objectives

In this lab you will:

   * Create a Micronaut application with the Micronaut project wizard
   * Configure the application to connect to an Autonomous Database
   * Create Micronaut Data entities that map Oracle Database tables
   * Define Micronaut Data repositories to implement queries
   * Expose Micronaut Controllers as REST endpoints
   * Populate Data on Application Startup
   * Run the Micronaut application locally
   * Write tests for the Micronaut application

Prerequisites

   * Followed the [Setup Development](setup_development/setup_development.md) lab
   * Setup an ATP and Compute Instance
   * DATASOURCES_DEFAULT_PASSWORD environment variable set to the DB password
   * TNS_ADMIN environment variable set to the wallet


## Build the Micronaut application

1. The code to this lab can be found here. There are TODO steps and each one is available as a branch.
   ```
   TODO update to be a branch
   git clone https://github.com/chrisbensen/micronaut
   cd micronaut/project
   ```

## Step 1.1 - Create the Micronaut application

1. Use the Micronaut wizard to generate an empty project:
   ```
   mn create-app example-atp --features oracle,data-jdbc
   cd example-atp
   ```

1. Configure the Micronaut application to work with Autonomous Database open the `src/main/resources/application.yml` file and modify it. Currently the file looks like this:
   ```
   micronaut:
    application:
      name: exampleAtp
   datasources:
     default:
       url: jdbc:oracle:thin:@localhost:1521/xe
       driverClassName: oracle.jdbc.OracleDriver
       username: system
       password: oracle
       schema-generate: CREATE_DROP
       dialect: ORACLE
   ```

   Modify `application.yml` to look like this:
   ```
   micronaut:
     http.services:
       pet-health:
         urls: "http://localhost:8081"
       application:
         name: exampleAtp
       http:
         services:
           pet-health:
             url: http://localhost:8081
   datasources:
     default:
       url: jdbc:oracle:thin:@mnociatp_high
       driverClassName: oracle.jdbc.OracleDriver
       username: mnocidemo
       password: ''
       dialect: ORACLE
       data-source-properties:
         oracle:
           jdbc:
             fanEnabled: false
   ```

1. Delete the existing src/main/resources/application-test.yml file so that you can run tests against the Autonomous database instance.
   ```
   rm src/main/resources/application-test.yml
   ```

1. Open `build.gradle` in the root of the project. In the `dependencies` block below the line ```runtimeOnly("com.oracle.database.jdbc:ojdbc8")``` add the following additional dependencies required to connect to Autonomous Database:
   ```
   runtimeOnly('com.oracle.database.security:oraclepki:19.7.0.0')
   runtimeOnly('com.oracle.database.security:osdt_cert:19.7.0.0')
   runtimeOnly('com.oracle.database.security:osdt_core:19.7.0.0')
   ```

The `dependencies` block will now look like this:
   ```
   dependencies {
       annotationProcessor(platform("io.micronaut:micronaut-bom:$micronautVersion"))
       annotationProcessor("io.micronaut:micronaut-inject-java")
       annotationProcessor("io.micronaut:micronaut-validation")
       annotationProcessor("io.micronaut.data:micronaut-data-processor")
       implementation(platform("io.micronaut:micronaut-bom:$micronautVersion"))
       implementation("io.micronaut:micronaut-inject")
       implementation("io.micronaut:micronaut-validation")
       implementation("io.micronaut:micronaut-runtime")
       implementation("io.micronaut:micronaut-http-server-netty")
       implementation("io.micronaut:micronaut-http-client")
       implementation("io.micronaut.sql:micronaut-jdbc-hikari")
       implementation("io.micronaut.data:micronaut-data-jdbc")
       developmentOnly("io.micronaut:micronaut-runtime-osx:$micronautVersion")
       runtimeOnly("ch.qos.logback:logback-classic")
   	 runtimeOnly("com.oracle.database.jdbc:ojdbc8")
       runtimeOnly('com.oracle.database.security:oraclepki:19.7.0.0')
       runtimeOnly('com.oracle.database.security:osdt_cert:19.7.0.0')
       runtimeOnly('com.oracle.database.security:osdt_core:19.7.0.0')
       testAnnotationProcessor(enforcedPlatform("io.micronaut:micronaut-bom:$micronautVersion"))
       testAnnotationProcessor("io.micronaut:micronaut-inject-java")
       testImplementation(enforcedPlatform("io.micronaut:micronaut-bom:$micronautVersion"))
       testImplementation("org.junit.jupiter:junit-jupiter-api")
       testImplementation("io.micronaut.test:micronaut-test-junit5")
       testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
   }
   ```

1. Make the following directories:
   ```
   mkdir src/main/java/example/atp/domain
   mkdir src/main/java/example/atp/repositories
   mkdir src/main/java/example/atp/controllers
   mkdir data
   ```

1. `src/main/java/example/atp/domain/Owner.java`
   Since there are two tables, PET and OWNER, we define entity classes that can be used to read/write data to the database tables.

   The @MappedEntity annotation is used to indicate that the entity is mapped to a database table. By default this will be a table using the same name as the class (in this case owner).

   The columns of the table are represented by each Java property. In the above case an id column will be used to represent the primary key and by using @GeneratedValue this sets up the mapping to assume the use of an identity column in Autonomous Database.

   The @Creator annotation is used on the constructor that will be used to instantiate the mapped entity and is also used to express required columns. In this case the name column is required and immutable whilst the age column is not and can be set independently using the setAge setter.

   ```
   package example.atp.domain;

   import io.micronaut.core.annotation.Creator;
   import io.micronaut.data.annotation.GeneratedValue;
   import io.micronaut.data.annotation.Id;
   import io.micronaut.data.annotation.MappedEntity;
   import io.micronaut.data.annotation.GeneratedValue;


   @MappedEntity
   public class Owner {

       @Id
       @GeneratedValue(GeneratedValue.Type.IDENTITY)
       private Long id;
       private String name;
       private int age;

       @Creator
       public Owner(String name) {
           this.name = name;
       }

       public Long getId() {
           return id;
       }

       public void setId(Long value) {
           id = value;
       }

       public String getName() {
           return name;
       }

       public int getAge() {
           return age;
       }

       public void setAge(int value) {
           age = value;
       }
   }
   ```

1. `src/main/java/example/atp/repositories/OwnerRepository.java`
   Repository interfaces allow you to implement queries. The CrudRepository interface takes two generic argument types. The first is the type of the entity, in this case Owner, and the second is the type if the ID, a Long.

   The CrudRepository interface defines methods that allow you to create, read, update and delete (CRUD) entities from the database with the appropriate SQL inserts, selects, updates and deletes computed for you at compilation time. For more information see the javadoc for [CrudRepository](https://micronaut-projects.github.io/micronaut-data/latest/api/io/micronaut/data/repository/CrudRepository.html).

   You can define methods within the interface that perform JDBC queries and automatically handle all the intricate details for you such as defining correct transaction semantics such as read-only transactions for queries, executing the query and mapping the result set to the Owner entity class you defined earlier.

   The findByName method in the OwnerRepsitory class will produce a query such as SELECT ID, NAME, AGE FROM OWNER WHERE NAME = ? automatically at compilation time.

   For more information on query methods and the types of queries you can define see the [documentation for query methods](https://micronaut-projects.github.io/micronaut-data/latest/guide/index.html#querying) in the Micronaut Data documentation.

   ```
   package example.atp.repositories;

   import java.util.List;
   import java.util.Optional;

   import io.micronaut.data.jdbc.annotation.JdbcRepository;
   import io.micronaut.data.model.query.builder.sql.Dialect;
   import io.micronaut.data.repository.CrudRepository;

   import example.atp.domain.Owner;


   @JdbcRepository(dialect = Dialect.ORACLE)
   public interface OwnerRepository extends CrudRepository<Owner, Long> {

       @Override
       List<Owner> findAll();

       Optional<Owner> findByName(String name);
   }
   ```

1. The application `src/main/java/example/atp/Application.java` currently looks like this:
   ```
   package example.atp;

   import io.micronaut.runtime.Micronaut;

   public class Application {

       public static void main(String[] args) {
           Micronaut.run(Application.class);
       }
   }
   ```
   Modify `Application.java` to look like this to populate some data for the OWNER table on startup. To do this you can use [Micronaut application events](https://docs.micronaut.io/latest/guide/index.html#contextEvents).

   ```
   package example.atp;

   import javax.inject.Singleton;
   import javax.transaction.Transactional;
   import java.util.Arrays;

   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   import io.micronaut.context.event.StartupEvent;
   import io.micronaut.runtime.Micronaut;
   import io.micronaut.runtime.event.annotation.EventListener;

   import example.atp.domain.Owner;
   import example.atp.repositories.OwnerRepository;


   @Singleton
   public class Application {
       private static final Logger LOG = LoggerFactory.getLogger(Application.class);

       private final OwnerRepository ownerRepository;

       Application(OwnerRepository ownerRepository) {
           this.ownerRepository = ownerRepository;
       }

       public static void main(String[] args) {
           Micronaut.run(Application.class);
       }

       @EventListener
       @Transactional
       void init(StartupEvent event) {
           if (LOG.isInfoEnabled()) {
               LOG.info("Populating data");
           }

           ownerRepository.deleteAll();
           Owner fred = new Owner("Fred");
           fred.setAge(45);
           Owner barney = new Owner("Barney");
           barney.setAge(40);
           ownerRepository.saveAll(Arrays.asList(fred, barney));
       }
   }
   ```

1. Create the database user schema by creating the `data/createUser.sql` file with the following contents:
   ```
   CREATE USER mnocidemo IDENTIFIED BY HandsOnLabUser1;

   GRANT
   CREATE SESSION,
   RESOURCE,
   UNLIMITED TABLESPACE,
   CREATE TABLE,
   CREATE VIEW,
   CREATE SEQUENCE,
   CREATE PROCEDURE,
   CREATE TYPE,
   CREATE SYNONYM
   TO mnocidemo;

   /* for SQL Developer Web */
   BEGIN
    ords_admin.enable_schema(
     p_enabled => TRUE,
     p_schema => 'mnocidemo',
     p_url_mapping_type => 'BASE_PATH',
     p_url_mapping_pattern => 'mnocidemo',
     p_auto_rest_auth => NULL
    );
    COMMIT;
   END;
   /

   EXIT;
   ```

2. Create the OWNER table by creating the `data/createOwner.sql` file with the following contents:
   ```
   CREATE TABLE OWNER (ID NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
                       AGE NUMBER(10) NOT NULL,
                       NAME VARCHAR(255) NOT NULL)
   /

   EXIT;
   ```

## Step 1.2 - Setup the database and build the application

1. Run this SQL to setup the user in the ADB for the app:
   ```
   /opt/oracle/sqlcl/bin/sql admin/Commodore-64@mnociatp_high @data/createUser.sql
   ```

1. Run this SQL to setup the schema in the database for the app. This will create the OWNER table:
   ```
   /opt/oracle/sqlcl/bin/sql mnocidemo/${DATASOURCES_DEFAULT_PASSWORD}@mnociatp_high @data/createOwner.sql
   ```

1. Build the Micronaut application.
   ```
   ./gradlew assemble
   ```

1. Run the Micronaut application.
   ```
   java -jar java -jar build/libs/example-atp-0.1-all.jar
   ```

   The output will be something like this:
   ```
   21:02:27.836 [main] INFO  i.m.context.env.DefaultEnvironment - Established active environments: [oraclecloud, cloud]
   21:02:29.219 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
   21:02:30.207 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
   21:02:30.497 [main] INFO  example.atp.Application - Populating data
   21:02:31.058 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 3395ms. Server Running: http://instance-20201012-0929:8080
   ```

   Press CTRL+C to terminate the Micronaut demo.

## Step 2.1 - Add the PET table

1. Create the PET table by creating the `data/createPet.sql` file with the following contents:
   ```
   CREATE TABLE PET (ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
                     OWNER_ID NUMBER(19) NOT NULL,
                     NAME VARCHAR(255) NOT NULL,
                     TYPE VARCHAR(255) NOT NULL)
   /

   ALTER TABLE PET ADD (CONSTRAINT fk_owner
                        FOREIGN KEY (OWNER_ID)
                        REFERENCES OWNER (ID)
                        ON DELETE CASCADE
                        ENABLE VALIDATE)
   /

   EXIT;
   ```


1. `src/main/java/example/atp/domain/Pet.java`
   Note that the Pet class uses an automatically populated identity column as the primary key to demonstrate differing approaches to ID generation.

   A relationship between the Pet class and the Owner class is also defined using the @Relation(Relation.Kind.MANY_TO_ONE) annotation, indicating this is a many-to-one relationship.
   ```
   package example.atp.domain;

   import javax.annotation.Nullable;
   import java.util.UUID;

   import io.micronaut.core.annotation.Creator;
   import io.micronaut.data.annotation.AutoPopulated;
   import io.micronaut.data.annotation.Id;
   import io.micronaut.data.annotation.MappedEntity;
   import io.micronaut.data.annotation.Relation;
   import io.micronaut.data.annotation.GeneratedValue;


   @MappedEntity
   public class Pet {

       public enum PetType {DOG, CAT}

       @Id
       @GeneratedValue(GeneratedValue.Type.IDENTITY)
       private Long id;
       private String name;
       @Relation(Relation.Kind.MANY_TO_ONE)
       private Owner owner;
       private PetType type = PetType.DOG;

       @Creator
       public Pet(String name, @Nullable Owner owner) {
           this.name = name;
           this.owner = owner;
       }

       public Long getId() {
           return id;
       }

       public void setId(Long value) {
           id = value;
       }

       public String getName() {
           return name;
       }

       public Owner getOwner() {
           return owner;
       }

       public PetType getType() {
           return type;
       }

       public void setType(PetType value) {
           type = value;
       }
   }
   ```

1. `src/main/java/example/atp/domain/NameDTO.java`
   The second repository for the Pet table uses a data transfer object (DTO) to perform an optimized query. A DTO is a simple POJO that allows you to select only the columns a particular query needs, thus producing a more optimized query.

   ```
   package example.atp.domain;

   import io.micronaut.core.annotation.Introspected;


   @Introspected
   public class NameDTO {
       private String name;

       public String getName() {
           return name;
       }

       public void setName(String name) {
           this.name = name;
       }
   }
   ```

1. `src/main/java/example/atp/repositories/PetRepository.java`
   Take note of the list method that returns the DTO. This method will again be implemented for you at compilation time, but this time instead of retrieving all the columns of the Pet column it will only retrieve the name column and any other columns you may define.
   ```
   package example.atp.repositories;

   import java.util.List;
   import java.util.Optional;
   import java.util.UUID;

   import io.micronaut.data.annotation.Join;
   import io.micronaut.data.jdbc.annotation.JdbcRepository;
   import io.micronaut.data.model.query.builder.sql.Dialect;
   import io.micronaut.data.repository.PageableRepository;

   import example.atp.domain.NameDTO;
   import example.atp.domain.Pet;


   @JdbcRepository(dialect = Dialect.ORACLE)
   public interface PetRepository extends PageableRepository<Pet, UUID> {

       List<NameDTO> list();

       @Join("owner")
       Optional<Pet> findByName(String name);
   }
   ```

1. The application `src/main/java/example/atp/Application.java` currently looks like this:
   ```
   package example.atp;

   import javax.inject.Singleton;
   import javax.transaction.Transactional;
   import java.util.Arrays;

   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   import io.micronaut.context.event.StartupEvent;
   import io.micronaut.runtime.Micronaut;
   import io.micronaut.runtime.event.annotation.EventListener;

   import example.atp.domain.Owner;
   import example.atp.repositories.OwnerRepository;


   @Singleton
   public class Application {
       private static final Logger LOG = LoggerFactory.getLogger(Application.class);

       private final OwnerRepository ownerRepository;

       Application(OwnerRepository ownerRepository) {
           this.ownerRepository = ownerRepository;
       }

       public static void main(String[] args) {
           Micronaut.run(Application.class);
       }

       @EventListener
       @Transactional
       void init(StartupEvent event) {
           if (LOG.isInfoEnabled()) {
               LOG.info("Populating data");
           }

           ownerRepository.deleteAll();
           Owner fred = new Owner("Fred");
           fred.setAge(45);
           Owner barney = new Owner("Barney");
           barney.setAge(40);
           ownerRepository.saveAll(Arrays.asList(fred, barney));
       }
   }
   ```
   Modify `Application.java` to look like this to populate some data for the OWNER table on startup. To do this you can use [Micronaut application events](https://docs.micronaut.io/latest/guide/index.html#contextEvents).

   ```
   package example.atp;

   import javax.inject.Singleton;
   import javax.transaction.Transactional;
   import java.util.Arrays;

   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   import io.micronaut.context.event.StartupEvent;
   import io.micronaut.runtime.Micronaut;
   import io.micronaut.runtime.event.annotation.EventListener;

   import example.atp.domain.Owner;
   import example.atp.domain.Pet;
   import example.atp.repositories.OwnerRepository;
   import example.atp.repositories.PetRepository;


   @Singleton
   public class Application {
       private static final Logger LOG = LoggerFactory.getLogger(Application.class);

       private final OwnerRepository ownerRepository;
       private final PetRepository petRepository;

       Application(OwnerRepository ownerRepository, PetRepository petRepository) {
           this.ownerRepository = ownerRepository;
           this.petRepository = petRepository;
       }

       public static void main(String[] args) {
           Micronaut.run(Application.class);
       }

       @EventListener
       @Transactional
       void init(StartupEvent event) {
           if (LOG.isInfoEnabled()) {
               LOG.info("Populating data");
           }

           petRepository.deleteAll();
           ownerRepository.deleteAll();
           Owner fred = new Owner("Fred");
           fred.setAge(45);
           Owner barney = new Owner("Barney");
           barney.setAge(40);
           ownerRepository.saveAll(Arrays.asList(fred, barney));

           Pet dino = new Pet("Dino", fred);
           Pet bp = new Pet("Baby Puss", fred);
           bp.setType(Pet.PetType.CAT);
           Pet hoppy = new Pet("Hoppy", barney);

           petRepository.saveAll(Arrays.asList(dino, bp, hoppy));
       }
   }
   ```

## Step 2.2 - Setup the database and build the application

1. Run this SQL to setup the schema in the database for the app. This will create the OWNER table:
   ```
   /opt/oracle/sqlcl/bin/sql mnocidemo/${DATASOURCES_DEFAULT_PASSWORD}@mnociatp_high @data/createPet.sql
   ```

1. Build the Micronaut application.
   ```
   ./gradlew assemble
   ```

1. Run the Micronaut application.
   ```
   java -jar build/libs/example-atp-0.1-all.jar
   ```

   Press CTRL+C to terminate the Micronaut demo.

## Step 3.1 - Add the REST API

1. `src/main/java/example/atp/controllers/OwnerController.java`

   REST endpoints in Micronaut are easy to write and defined as [controllers](https://docs.micronaut.io/latest/guide/index.html#httpServer) using the MVC pattern. A controller class is defined with the @Controller annotation which you can use to define the root URI that the controller maps to, in this case **/owners**.

   The @ExecuteOn annotation is used to tell Micronaut that the controller performs I/O communication with a database and therefore operations should [run on the I/O thread pool](https://docs.micronaut.io/latest/guide/index.html#reactiveServer).

   The OwnerController class uses [Micronaut dependency injection](https://docs.micronaut.io/latest/guide/index.html#ioc) to obtain a reference to the OwnerRepository repository interface you defined earlier and is used to implement two endpoints:

   / - The root endpoint lists all the owners
   /{name} - The second endpoint uses a [URI template](https://docs.micronaut.io/latest/guide/index.html#routing) to allow looking up an owner by name. The value of the URI variable {name} is provided as a parameter to the byName method.
   ```
   package example.atp.controllers;

   import java.util.List;
   import java.util.Optional;

   import javax.validation.constraints.NotBlank;

   import io.micronaut.http.annotation.Controller;
   import io.micronaut.http.annotation.Get;
   import io.micronaut.scheduling.TaskExecutors;
   import io.micronaut.scheduling.annotation.ExecuteOn;

   import example.atp.domain.Owner;
   import example.atp.repositories.OwnerRepository;


   @Controller("/owners")
   @ExecuteOn(TaskExecutors.IO)
   class OwnerController {

       private final OwnerRepository ownerRepository;

       OwnerController(OwnerRepository ownerRepository) {
           this.ownerRepository = ownerRepository;
       }

       @Get("/")
       List<Owner> all() {
           return ownerRepository.findAll();
       }

       @Get("/{name}")
       Optional<Owner> byName(@NotBlank String name) {
           return ownerRepository.findByName(name);
       }
   }
   ```

1. `src/main/java/example/atp/controllers/PetController.java`

   PetController is a second REST endpoint in this example and where PetRepository is injected to expose a list of pets and pets by name.

   ```
   package example.atp.controllers;

   import java.util.List;
   import java.util.Optional;

   import io.micronaut.http.annotation.Controller;
   import io.micronaut.http.annotation.Get;

   import example.atp.domain.NameDTO;
   import example.atp.domain.Pet;
   import example.atp.repositories.PetRepository;


   @Controller("/pets")
   class PetController {

       private final PetRepository petRepository;

       PetController(PetRepository petRepository) {
           this.petRepository = petRepository;
       }

       @Get("/")
       List<NameDTO> all() {
           return petRepository.list();
       }

       @Get("/{name}")
       Optional<Pet> byName(String name) {
           return petRepository.findByName(name);
       }
   }
   ```

## Step 3.2 - Build the application with the REST API and test

1. Build the Micronaut application.
   ```
   ./gradlew assemble
   ```

1. Run the Micronaut application in the background.
   ```
   java -jar build/libs/example-atp-0.1-all.jar&
   ```

   **Note:** The PID will be printed out to the terminal, take note of this PID.

1. Access the endpoint.

   You can now access http://localhost:8080/pets for the /pet endpoint and http://localhost:8080/owners for the /owners endpoint. For example:
   ```
   curl -i http://localhost:8080/pets
   ```

   Will output:
   ```
   HTTP/1.1 200 OK
   Date: Tue, 13 Oct 2020 21:23:42 GMT
   Content-Type: application/json
   content-length: 55
   connection: keep-alive

   [{"name":"Dino"},{"name":"Baby Puss"},{"name":"Hoppy"}]
   ```

1. Recall the PID earlier and kill the Java process that is running in the background:
   ```
   kill PID
   ```

# Step 4.1 Write Integration Tests for the Micronaut Application
   The application will already have been setup with a single test that tests the application can startup successfully (and hence will test the logic of the init method defined in the previous section). Take a look at the test `cat src/test/java/example/atp/ExampleAtpTest.java`. To run the tests run the following:
   ```
   ./gradlew test
   ```

Congratulations! You have built a Micronaut application. Continue to the next lab to learn about how the application works.

## Continue through the following section

1. Micronaut Explained [Micronaut Explained](micronaut_exlained.md)

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
