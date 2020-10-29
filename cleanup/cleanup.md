# Load Data into an Instance

## Introduction

This lab will cleanup the resources after the lab. It is not necessary unless you want to have your computer and your cloud tenancy rewind in time like this lab never happened.

## Cloud

1. To clean up all of the OCI resources created by this lab simply terminate the VM and the ATP instances and delete the Container.

## Local Cleanup

1. Locally an RSA key was created. If you remove it you will no longer have access to the remote compute instance.
   ```
   rm ~/.ssh/local_id_rsa.pub
   ```

## Cloud Shell

1. In Cloud Shell an RSA key was created. If you remove it you will no longer have access to the remote compute instance.
   ```
   rm ~/.ssh/cloud_shell_id_rsa.pub
   rm Wallet_micronaut.zip
   ```

## Want to Learn More?

* [Oracle Cloud](http://www.oracle.com/cloud/free)
* [Oracle Live Labs](https://oracle.github.io/learning-library/developer-library/)
