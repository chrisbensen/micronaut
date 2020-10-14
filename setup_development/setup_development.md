# Local Development with Micronaut + Micronaut Data + Graal

## Part 1 - Cloning the code for this lab

1. Use SSH to access your Development Compute Instance.
   You have a choice: connect to your compute instance from Cloud Shell or from your local terminal.
   ```
   ssh opc@${COMPUTE_IP}
   ```

1. Now we need to setup the Compute Instance by installing the software needed to run this lab.

   Install Git:
   ```
   sudo yum install -y git
   git --version
   ```

   Install GraalVM:
   Use Graal Enterprise https://docs.oracle.com/en/graalvm/enterprise/20/guide/overview/oci/create-instance.html
   ```
   sudo yum -y install graalvm20-ee-11-20.2.0-1.el7.x86_64
   sudo yum -y install graalvm20-ee-11-native-image

   java -version
   ```

   You should see something like:
   ```
   [opc@graal ~]$ java --version
   java 11.0.8.0.2 2020-07-09 LTS
   Java(TM) SE Runtime Environment GraalVM EE 20.2.0 (build 11.0.8.0.2+1-LTS-jvmci-20.2-b03)
   Java HotSpot(TM) 64-Bit Server VM GraalVM EE 20.2.0 (build 11.0.8.0.2+1-LTS-jvmci-20.2-b03, mixed mode, sharing)
   ```

   Install Micronaut:
   ```
   curl -s https://get.sdkman.io | bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   sdk install micronaut
   mn --version
   ```

      **Note:** This is a small chance this doesn't work in which case you can bypass using package manager:
      ```
      wget https://github.com/micronaut-projects/micronaut-starter/releases/download/v2.0.0/micronaut-cli-2.0.0.zip

      unzip micronaut-cli-2.0.0.zip

      rm micronaut-cli-2.0.0.zip
      echo 'export PATH=/home/opc/micronaut-cli-2.0.0/bin:$PATH' >> ~/.bashrc

      source ~/.bashrc
      mn --version
      ```

   You should see something like:
   ```
   Micronaut Version: 2.0.0
   JVM Version: 11.0.8.0.2
   ```

## Continue through the following section

1. Micronaut [Micronaut](micronaut.md)

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
