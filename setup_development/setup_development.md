# Setup Development for Micronaut and GraalVM

## Introduction

In this lab you will take a fresh OCI compute instance and set it up for development for an application that uses Micronaut, ATP and GraalVM.

Estimated Lab Time: 10 minutes

Objectives

In this lab you will:

   * SSH into a remote VM
   * Install git, GraalVM and Micronaut

Prerequisites

   * Followed the [Setup OCI](setup_oci/setup_oci.md) lab
   * An OCI compute instance setup with RSA keys installed
   * The compute instance has the ATP wallet setup and installed

## Setup Development Machine

1. Use SSH to access your Development Compute Instance.
   You have a choice: connect to your compute instance from Cloud Shell or from your local terminal.
   ```bash
   ssh -i ~/.ssh/local_id_rsa opc@${COMPUTE_IP}
   ```

1. Setup the Compute Instance by installing the software needed to run this lab.

   Install Git:
   ```bash
   sudo yum install -y git
   git --version
   ```

   Install GraalVM:
   Use Graal Enterprise https://docs.oracle.com/en/graalvm/enterprise/20/guide/overview/oci/create-instance.html
   ```bash
   sudo yum -y install graalvm20-ee-11-20.2.0-1.el7.x86_64
   sudo yum -y install graalvm20-ee-11-native-image

   java -version
   ```

   Make sure the version number of the java command that is on the path is the one that was installed:
   ```bash
   java 11.0.8.0.2 2020-07-09 LTS
   Java(TM) SE Runtime Environment GraalVM EE 20.2.0 (build 11.0.8.0.2+1-LTS-jvmci-20.2-b03)
   Java HotSpot(TM) 64-Bit Server VM GraalVM EE 20.2.0 (build 11.0.8.0.2+1-LTS-jvmci-20.2-b03, mixed mode, sharing)
   ```

   Install SDKMAN:
   ```bash
   curl -s https://get.sdkman.io | bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```

   Install Micronaut:
   ```
   sdk install micronaut
   mn --version
   ```

      **Note:** This is a small chance this doesn't work in which case you can bypass using sdk manager:
      ```bash
      wget https://github.com/micronaut-projects/micronaut-starter/releases/download/v2.0.0/micronaut-cli-2.0.0.zip

      unzip micronaut-cli-2.0.0.zip

      rm micronaut-cli-2.0.0.zip
      echo 'export PATH=/home/opc/micronaut-cli-2.0.0/bin:$PATH' >> ~/.bashrc

      source ~/.bashrc
      mn --version
      ```

   Make sure the version number of the mn command that is on the path is the one that was installed:
   ```
   Micronaut Version: 2.0.0
   JVM Version: 11.0.8.0.2
   ```

   Install Gradle:
   ```bash
   sdk install gradle
   ```

1. TODO Setup VNC

   https://docs.oracle.com/en/operating-systems/oracle-linux/7/network/ol7-vnc.html#ol7-vnc-about

   ```
   sudo yum install -y tigervnc-server

   sudo useradd vncuser
   sudo passwd vncuser
   ```

   Enter **Commodore-64** as the password.

   ```
   sudo cp /lib/systemd/system/vncserver@.service /etc/systemd/system/vncserver@\:display.service
   sudo cp /lib/systemd/system/vncserver@.service /etc/systemd/system/vncserver-vncuser@\:display.service

   sudo nano /etc/systemd/system/vncserver-vncuser
   ```

   ```
   [Service]
   Type=forking
   WorkingDirectory=/home/vncuser
   User=vncuser
   Group=vncuser

   # Clean any existing files in /tmp/.X11-unix environment
   ExecStartPre=/bin/sh -c '/usr/bin/vncserver -kill %i > /dev/null 2>&1 || :'
   ExecStart=/usr/bin/vncserver %i
   PIDFile=/home/vncuser/.vnc/%H%i.pid
   ExecStop=/usr/bin/vncserver -kill %i
   ```


   ```
   sudo systemctl daemon-reload
   sudo firewall-cmd --permanent --zone=public --add-service=vnc-server
   sudo firewall-cmd --reload
   ```

   TODO open up the port 5900

   vnc://158.101.4.33


## Continue through the following section

1. [Micronaut Example](micronaut_example/micronaut_example.md)

or do it in steps:

1. [Micronaut Example Part 1](micronaut_example/micronaut_example_part1.md)

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
