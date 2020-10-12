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
   cd /home/opc/micronaut-graal-lab/micronaut-helidon/micronaut/files/app

   ./gradlew assemble
   ```

1. Run the app.
   TODO Fix, It fails here
   ```
   ./gradlew -DMICRONAUT_OCI_DEMO_PASSWORD=HandsOnLabUser1 run
   java -jar -DMICRONAUT_OCI_DEMO_PASSWORD HandsOnLabUser1 /app/micronaut-data-jdbc-graal-atp-0.1-all.jar
   ```

1. The snippet used to run your application locally. It will look similar to this:

   ```shell script
   # run on local machine
   ./gradlew -DMICRONAUT_OCI_DEMO_PASSWORD [your generated password] run
   ```

## Continue through the following section

1. Deploy [Deploy](deploy.md)

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
