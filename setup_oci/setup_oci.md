# Setup


# Micronaut + Micronaut Data + Graal

## Before You Begin

This 2-hour lab walks you through the steps to use Micronaut, Micronaut Data
and GraalVM native image to connected to an Oracle Database. Everything in this
lab runs in the cloud so nothing needs to be installed on your local machine.

1. Create an Oracle Cloud account
1. Create an Autonomous Transaction Processing (ATP) Database
1. Create an Oracle Compute instance and setup/configure
1. Build an app with Micronaut, Micronaut Data and GraalVM
1. Run your app in the cloud

### What Do You Need?

* Free Tier OCI account with Cloud Credits **Note** You can slightly adjust the steps if you just have a Free Tier, but some things may not work and other things will take longer.
* Internet Browser
* [GitHub](https://github.com/) Account  
   If you do not already have a GitHub account, create one now
#* [Micronaut](https://micronaut.io/download.html)
#* [GraalVM](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-20.1.0)
#* [VS Code](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjijsO-we7qAhVjMX0KHdJTCgYQFjAAegQIBRAB&url=https%3A%2F%2Fcode.visualstudio.com%2Fdownload&usg=AOvVaw11fc5fOXYIyxQh75jYLjXg) or [IntelliJ](https://www.jetbrains.com/idea/download/#section=mac)

## Variables and Passwords

TODO If any of these change search the entire lab and change them.
TODO Change the names to better names before publishing the lab.

To make this lab easy below are a list of the variables and passwords that will be used.

* **mnociatp** is the name of the database
* **ATP Password** is **Commodore-64**
* **mnocidemo** is the name of the user created for the database
* **mnocidemo username password** is **HandsOnLabUser1**
* **Wallet Password** is **Pw4ZipFile**
* **mnocidemo** is the name of the VM Instance
* **DB_OCID** is the OCID of the ATP Database
* **COMPUTE_IP** is the public IP address of your computer instance
* **TNS_ADMIN** is ```/opt/oracle/wallet/```

## Part 1 - Create an Oracle Always-Free Cloud Account

1. Go to https://www.oracle.com/cloud/free/
2. Click "Start for free"
3. Populate the forms and create an account.

   **Note** Do not create your home region in "US West (San Jose)"

4. Once your account is created, [log in](https://www.oracle.com/cloud/sign-in.html) and go to the dashboard.  
   ![](images/cloudDashboard.png)

## Part 2 - Create a Compartments

A [Compartment](https://docs.cloud.oracle.com/iaas/Content/Identity/Tasks/managingcompartments.htm) is useful when you want to organize and isolate your cloud resources. Create a compartment for the objects used in this lab.

1. Click the menu icon in the upper left corner.
1. Scroll to the bottom, under Identity, click "Compartments".
   ![](images/compartmentMenu.png)
1. Click "Create Compartment".
   ![](images/compartmentCreate.png)
1. Populate the Name and Description.
1. Leave the parent compartment set to (root).
1. Click "Create Compartment"  
   ![](images/compartmentForm.png)
1. Click the "Oracle Cloud" logo to return to the dashboard.

## Part 3 - Create an ATP instance

You will need a database to complete the exercises.  An Oracle Autonomous Database handles a lot of the background admin tasks for you so you can focus on your project.

1. Click "Create an ATP database" in the Autonomous Transaction Processing box.  
   ![](images/cloudDashboard.png)
1. Choose your new compartment.
1. Enter `mnociatp` in Display name
1. Enter  `mnociatp` in Database name
1. Make sure "Transaction Processing" is selected.
1. Make sure "Shared Infrastructure" is selected.  
   ![](images/createATPForm1.png)
1. Scroll down to "Create administrator credentials".  Enter and confirm the ADMIN password. Use **Commodore-64**
   **Note:** The Admin account is the top level user for your new database. Create a strong password and keep it secure.
1. Scroll to the bottom and click "Create Autonomous Database".  
   ![](images/createATPForm2.png)  
   You will receive an email when your new ATP Database instance has been provisioned.
1. Locate your new database's OCID and click Copy.
   ![](images/createATPGetOcid.png)
1. While the database is provisioned click the Cloud Shell icon. This will open a preconfigured VM that you will use to access and setup your project. Cloud Shell has the OCI command line tools already configured. You can install these tools locally but this is an easy way to do it.
   ![](images/cloudShell.png)  
1. Once Cloud Shell is running, create an environment variable for your Database OCID you copied above.
   ```
   export DB_OCID=<pasteYourOCIDhere>
   ```
   ![](images/cloudSheelOcidEnv.png)  

   The Oracle Autonomous Database uses an extra level of security in the form of a wallet containing access keys for your new Database.  

   Once your ATP Database status is Available (the yellow box turns green) you can download the wallet inside the Cloud Shell using the pre-configured [OCI-CLI](https://docs.cloud.oracle.com/en-us/iaas/Content/API/Concepts/cliconcepts.htm).

   You should change the password value in this command to something more secure.

      **Note:** This password is for the .zip file, not your database.

   In your **Cloud Shell** Enter the following.

   ```
   oci db autonomous-database generate-wallet --autonomous-database-id ${DB_OCID} --password Pw4ZipFile --file ~/Wallet_micronaut.zip
   ```

1. Generate a new RSA key pair.
   ```
   ssh-keygen -t rsa -N "" -b 2048 -C "cloud_shell" -f ~/.ssh/cloud_shell_id_rsa
   ```
1. Display the public key and copy it.
   ```
   cat ~/.ssh/cloud_shell_id_rsa.pub
   ```

## Create a Compute Instance for Development

An Oracle Compute instance is a Cloud VM that you will use to install and run all of the software for the lab. This step will create a compute instance for development so nothing needs to be installed on your local machine and configuration is the same every time this lab is run. All the steps in this lab could be done locally with minor changes depending on platform.

1. Click "Create a VM instance" in the Compute box. In this lab the Compute Instance will be accessed from the Cloud Shell and a local Terminal via SSH.
   ![](images/cloudDashboard.png)
1. The default Free Tier machine is a VM.Standard.E2.1.Micro. We are going to build a development machine and you have free credits so let's get a machine that will run fast.
   1. Select "Shape"
   1. Select "Change"
   1. Select 2 OCPUS and 32GB of memory
   1. Click "Select Shape"
1. Populate the name with **mnocidemo**
   ![](images/computeForm1Create.png)
1. Scroll down the the "Add SSH keys" section.
1. Select "Paste SSH keys" and paste in the public SSH key created in the cloud shell earlier.
1. You may want to access this VM instance from your local Terminal, press the “+ Another Key”
   1. Generate a new RSA key pair.
      ```
      ssh-keygen -t rsa -N "" -b 2048 -C "local" -f ~/.ssh/local_id_rsa
      ```
   1. Display the public key and copy it.
      ```
      cat ~/.ssh/local_id_rsa.pub
      ```
   1. In the **Create Compute form**, paste the public key in the SSH KEYS box.
      ![](images/computeForm2Create.png)
      If you intend to SSH into your compute instance from any other machine, you may click the "+ Another Key" button and enter the public key for that machine.  
      (you may also want to save a copy of the Cloud Shell private key '~/.ssh/id_rsa' on your local machine.)  
      **DO NOT SHARE your private key**.  This key allows access to your compute instance.
1. Click "Create".
1. Once the Compute instance is Running, locate the Public IP Address and click Copy.  
Keep this IP address handy, it will be used throughout the lab and referred to as \<YourPublicIP>.
1. In your **Cloud Shell**  
   Create an environment variable to store the IP.
   ```
   export COMPUTE_IP=<YourPublicIP>
   ```
   ![](images/computeSaveComputeIp.png)

1. In your **Cloud Shell**  
   Use SCP to upload the wallet .zip file (downloaded earlier) to new Compute instance.
   ```
   scp Wallet_micronaut.zip opc@${COMPUTE_IP}:/home/opc/
   ```
   ![](images/computeSaveWallet.png)

1. In your local Terminal create an environment variable to store the IP.
   ```
   export COMPUTE_IP=<YourPublicIP>
   ```

1. Use SSH to access your Compute Instance.
   You have a choice connect to your compute instance from Cloud Shell or from your local terminal.
   ```
   ssh opc@${COMPUTE_IP}
   ```

1. Setup the Database Wallet
   ```
   sudo mkdir -p /opt/oracle/wallet

   sudo mv Wallet_micronaut.zip /opt/oracle/wallet/

   sudo unzip /opt/oracle/wallet/Wallet_micronaut.zip -d /opt/oracle/wallet/

   echo 'export TNS_ADMIN=/opt/oracle/wallet/' >> ~/.bashrc
   echo 'export DATASOURCES_DEFAULT_PASSWORD=HandsOnLabUser1' >> ~/.bashrc

   source ~/.bashrc
   ```

   Newer versions of Oracles ojdbc driver make it much easier to access a database using the extra wallet security. To enable these features, edit the wallet/ojdbc.properties file.

   ```
   sudo sed -i -e 's|oracle.net.wallet_location=|'"# oracle.net.wallet_location="'|' /opt/oracle/wallet/ojdbc.properties
   sudo sed -i -e 's|#javax.net.ssl.|'"javax.net.ssl."'|' /opt/oracle/wallet/ojdbc.properties
   sudo sed -i -e 's|<password_from_console>|'"Pw4ZipFile"'|' /opt/oracle/wallet/ojdbc.properties
   ```

1. Setup SQLcl

   ```
   sudo yum install -y https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
   sudo yum install -y mysql
   sudo yum install -y sqlcl
   /opt/oracle/sqlcl/bin/sql -v
   ```

You now have a database and a VM that is setup with all the tools needed and credentials to access the database via a secure wallet.

## Continue through the following section

1. Setup Development [Setup Development](setup_development.md)

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
