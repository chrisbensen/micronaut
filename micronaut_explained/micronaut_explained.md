# Load Data into an Instance

## Introduction

In the previous lab we built and ran the Micronaut application. This Lab will walk you through and explain how each part works and what is going on.

## Each file in the project explained

In this step we will go over each file and it's significants.

1. ```src/main/java/example/atp/domain/Owner.java```
   Since there are two tables, PET and OWNER, we define entity classes that can be used to read/write data to the database tables.

   The @MappedEntity annotation is used to indicate that the entity is mapped to a database table. By default this will be a table using the same name as the class (in this case owner).

   The columns of the table are represented by each Java property. In the above case an id column will be used to represent the primary key and by using @GeneratedValue this sets up the mapping to assume the use of an identity column in Autonomous Database.

   The @Creator annotation is used on the constructor that will be used to instantiate the mapped entity and is also used to express required columns. In this case the name column is required and immutable whilst the age column is not and can be set independently using the setAge setter.

1. ```src/main/java/example/atp/domain/Pet.java```
   Note that the Pet class uses an automatically populated identity column as the primary key to demonstrate differing approaches to ID generation.

   A relationship between the Pet class and the Owner class is also defined using the @Relation(Relation.Kind.MANY_TO_ONE) annotation, indicating this is a many-to-one relationship.

1. ```src/main/java/example/atp/repositories/OwnerRepository.java```
   Repository interfaces allow you to implement queries. The CrudRepository interface takes two generic argument types. The first is the type of the entity, in this case Owner, and the second is the type if the ID, a Long.

   The CrudRepository interface defines methods that allow you to create, read, update and delete (CRUD) entities from the database with the appropriate SQL inserts, selects, updates and deletes computed for you at compilation time. For more information see the javadoc for [CrudRepository](https://micronaut-projects.github.io/micronaut-data/latest/api/io/micronaut/data/repository/CrudRepository.html).

   You can define methods within the interface that perform JDBC queries and automatically handle all the intricate details for you such as defining correct transaction semantics such as read-only transactions for queries, executing the query and mapping the result set to the Owner entity class you defined earlier.

   The findByName method in the OwnerRepsitory class will produce a query such as SELECT ID, NAME, AGE FROM OWNER WHERE NAME = ? automatically at compilation time.

   For more information on query methods and the types of queries you can define see the [documentation for query methods](https://micronaut-projects.github.io/micronaut-data/latest/guide/index.html#querying) in the Micronaut Data documentation.

1. ```src/main/java/example/atp/domain/NameDTO.java```
   The second repository for the Pet table uses a data transfer object (DTO) to perform an optimized query. A DTO is a simple POJO that allows you to select only the columns a particular query needs, thus producing a more optimized query.

1. ```src/main/java/example/atp/repositories/PetRepository.java```
   Take note of the list method that returns the DTO. This method will again be implemented for you at compilation time, but this time instead of retrieving all the columns of the Pet column it will only retrieve the name column and any other columns you may define.

   The findByName method uses is an important feature of Micronaut Data which is the @Join annotation allowing you to [specify join paths](https://micronaut-projects.github.io/micronaut-data/latest/guide/#joinQueries) to retrieve exactly the data needed via database joins resulting in much more efficient queries.

1. ```src/main/java/example/atp/controllers/OwnerController.java```

   REST endpoints in Micronaut are easy to write and defined as [controllers](https://docs.micronaut.io/latest/guide/index.html#httpServer) using the MVC pattern. A controller class is defined with the @Controller annotation which you can use to define the root URI that the controller maps to, in this case **/owners**.

   The @ExecuteOn annotation is used to tell Micronaut that the controller performs I/O communication with a database and therefore operations should [run on the I/O thread pool](https://docs.micronaut.io/latest/guide/index.html#reactiveServer).

   The OwnerController class uses [Micronaut dependency injection](https://docs.micronaut.io/latest/guide/index.html#ioc) to obtain a reference to the OwnerRepository repository interface you defined earlier and is used to implement two endpoints:

   / - The root endpoint lists all the owners
   /{name} - The second endpoint uses a [URI template](https://docs.micronaut.io/latest/guide/index.html#routing) to allow looking up an owner by name. The value of the URI variable {name} is provided as a parameter to the byName method.

1. ```src/main/java/example/atp/controllers/PetController.java```

   PetController is a second REST endpoint in this example and where PetRepository is injected to expose a list of pets and pets by name.

1. ```src/main/java/example/atp/Application.java```

   The next step is to populate some application data on startup. To do this you can use [Micronaut application events](https://docs.micronaut.io/latest/guide/index.html#contextEvents).

1. ```src/main/resources/logback.xml```
1. ```src/main/resources/application.xml```

1. ```src/test/java/example/atp/ExampleAtpTest.java```

   The application will already have been setup with a single test that tests the application can startup successfully (and hence will test the logic of the init method defined in the previous section).

   To execute your tests make sure you have set the TNS_ADMIN environment variable to the location of you Wallet directory and set DATASOURCES_DEFAULT_PASSWORD to the output value atp_schema_password produced by the Terraform script in the previous lab and then execute:
   ```
   ./gradlew test
   ```

1. Run the app.
   ```
   ./gradlew run -t
   ```

      **Note:** that the -t argument is optional and activates continuous build so if you make changes to your application it will be automatically restart. This is extremely useful when developing an application.

   If any any point you want to deploy your application using the JVM:
   ```
   java -jar /home/opc/micronaut/micronaut/files/app/build/libs/example-atp-0.1-all.jar
   ```


## Continue through the following section

1. Deploy [Deploy](deploy.md)

## Want to Learn More?

* [Micronaut Documentation](https://micronaut.io/documentation.html)
* [Micronaut Data Documentation](https://micronaut-projects.github.io/micronaut-data/latest/guide/index.html)
