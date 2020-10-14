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
   cd micronaut/micronaut/files/app
   ```

1. Run the schema to setup the user for the app.
   ```
   /opt/oracle/sqlcl/bin/sql admin/Commodore-64@mnociatp_high @/home/opc/micronaut/micronaut/files/data/createUser.sql
   ```

1. Run the scheme to setup the database for the app.
   ```
   /opt/oracle/sqlcl/bin/sql mnocidemo/HandsOnLabUser1@mnociatp_high @/home/opc/micronaut/micronaut/files/data/createSchema.sql
   ```

1. Build the repository.
   ```
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

1. To make this available to the world, let's live dangerously and open a port.

TODO https://stackoverflow.com/questions/54794217/opening-port-80-on-oracle-cloud-infrastructure-compute-node

   1. Open a port in the security list in Instance's network. On the VM details page, click on the 'subnet' link:
      ![](images/instanceSubnet.png)
   1. Click "Security Lists"
      ![](images/instanceSecurityList.png)
   1. Click "Default Security List"
      ![](images/instanceDefaultSecurityList.png)
   1. Click "Add Ingress Rules"
      ![](images/instanceAddIngressRules.png)
   1. On the "Add Ingress Rules" dialog, add two rules:
      ![](images/instanceAddIngressRulesDialog.png)
   1. You will also need to open up the VM's firewall:
      ```
      sudo firewall-cmd --permanent --zone=public --add-port=8080/tcp
      sudo firewall-cmd --permanent --zone=public --add-port=8080/udp
      sudo firewall-cmd --reload
      ```
   1. Now from any computer use a web browser and access the url http://<YourPublicIP>:8080/pets

## Continue through the following section

1. Deploy [Deploy](deploy.md)

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
