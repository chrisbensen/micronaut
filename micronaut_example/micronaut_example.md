# Load Data into an Instance

## Introduction

In this lab you will build an already build Micronaut application that connects to Oracle Autonomous Database.

Estimated Lab Time: 30 minutes

Objectives

In this lab you will:

   * Create Micronaut Data entities that map Oracle Database tables
   * Define Micronaut Data repositories to implement queries
   * Expose Micronaut Controllers as REST endpoints
   * Populate Data on Application Startup
   * Write tests for the Micronaut application
   * Run the Micronaut application locally

Prerequisites

   * An Oracle Cloud account, Free Trial, LiveLabs or a Paid account
   * Setup an ATP and Compute Instance
   * DATASOURCES_DEFAULT_PASSWORD environment variable set to the DB password
   * TNS_ADMIN environment variable set to the wallet

## Run Application

1. Clone the repository.
   ```
   git clone https://github.com/chrisbensen/micronaut
   cd micronaut/micronaut/files/app
   ```

1. Run the SQL to setup the user in the ADB for the app:
   ```
   /opt/oracle/sqlcl/bin/sql admin/Commodore-64@mnociatp_high @/home/opc/micronaut/micronaut/files/data/createUser.sql
   ```

1. Run the SQL to setup the schema in the database for the app. This will create the OWNER and PET tables:
   ```
   /opt/oracle/sqlcl/bin/sql mnocidemo/${DATASOURCES_DEFAULT_PASSWORD}@mnociatp_high @/home/opc/micronaut/micronaut/files/data/createSchema.sql
   ```

1. Build the Micronaut application.
   ```
   ./gradlew assemble
   ```

1. Run the Micronaut application.
   ```
   java -jar /home/opc/micronaut/micronaut/files/app/build/libs/example-atp-0.1-all.jar
   ```

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

Congradulations! You have built a Micronaut application. Continue to the next lab to learn about how the application works.

## Continue through the following section

1. About Micronaut [About Micronaut](about_micronaut.md)

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
