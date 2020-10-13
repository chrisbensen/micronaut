# Load Data into an Instance

## Introduction

*Describe the lab in one or two sentences, for example:* This lab walks you through the steps to ...

Estimated Lab Time: &lt;n&gt; minutes

## Run Application

TODO a demo scheme with pre-loaded data is available. Use this:

https://docs.oracle.com/en/cloud/paas/autonomous-data-warehouse-cloud/user/autonomous-sample-data.html#GUID-4BB2B49B-0C20-4E38-BCC7-A61D3F45390B

1. Clone the repository.
TODO Change this to the correct repository but this is where the source lives for now.
   ```
   git clone https://github.com/chrisbensen/micronaut
   ```

1. Run the scheme to setup the database for the app.
   ```
   /opt/oracle/sqlcl/bin/sql admin/Commodore-64@mnociatp_high @/home/opc/micronaut/micronaut/files/data/createSchema.sql
   ```

1. Build the repository.
   ```
   cd /home/opc/micronaut/micronaut/files/app

   ./gradlew assemble
   ```

1. Set the environment variables:

   ```
   export DATASOURCES_DEFAULT_PASSWORD=HandsOnLabUser1
   ```

      **Note:** TNS_ADMIN must be set for the app to use the wallet. This is set in [Setup OCI](setup_oci.md)

1. Run the app.
   ```
   ./gradlew run -t
   ```

      **Note:** that the -t argument is optional and activates continuous build so if you make changes to your application it will be automatically restart. This is extremely useful when developing an application.

   If any any point you want to deploy your application and use the JVM:
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

## Continue through the following section

1. Deploy [Deploy](deploy.md)

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
