## Overview

angular_play_scala is a simple demo app using AngularJS and the Play framework with Scala and MongoDB. You can run and develop the app locally, and with the provided Vagrant and Chef cookbooks you can set up a mini CI/CD environment in a VM that uses Jenkins to pull the code from this repository, test and run the app in a VM.

A picture is worth a thousand words.

![alt tag](https://raw.github.com/excellaco/angular_play_scala/master/play_ci.png)

## Pre-req installations

* Java 6 or higher
* Play framework for local dev - http://www.playframework.com/download (Use the Typesafe Activator for local dev)
* VirtualBox - https://www.virtualbox.org/wiki/Downloads
* Vagrant - http://www.vagrantup.com/downloads.html
* Git - if you don't already have

## 1. Vagrant

Create a new Linux VM or start a pre-existing one. Default specs: Ubuntu 64 bit, 4GB RAM.

    $ cd vagrant
    $ vagrant up

The first time you run this it's going to take a long time to complete, make sure you have a good Internet connection. Vagrant and Chef are installing Java, APT, RUnit, Python, Git, Play, MongoDB and Jenkins. The good news is that once you have the VM installed on your workstation subsequent 'vagrant up' runs will be pretty quick. 

NOTE: The Vagrantfile config uses a private network for the VM and a static IP of 192.168.33.10. If you're running OS X Mavericks and had VirtualBox installed pre-Mavericks, you may see the following error on vagrant up:

    $ Progress state: NS_ERROR_FAILURE
    $ VBoxManage: error: Failed to create the host-only adapter
    $ VBoxManage: error: VBoxNetAdpCtl: Error while adding new interface: failed to open /dev/vboxnetctl: No such file or directory

To resolve:

    $ sudo /Library/StartupItems/VirtualBox/VirtualBox restart
    $ vagrant up

Then:

    $ # if you need to SSH to the VM for any reason
    $ vagrant ssh

    $ # to restart the VM and re-provision
    $ vagrant reload --provision

    $ # clean shutdown
    $ vagrant halt

    $ # remove the VM completely
    $ vagrant destroy

## 2. MongoDB in the VM

Verify MongoDB is up in the VM: http://192.168.33.10:28017/

## 3. Start Play locally

Mac OS:

    $ sh activator ui

Windows:

    $ activator.bat ui

This will open a browser using the Typesafe Activator, compile the app code, run tests, and start the app in dev mode. When it's done to see the app go to: http://localhost:9000/

## 4. Jenkins in the VM

The job is configured to automatically poll the GitHub repo every 60 seconds and execute.

To view or manually start the job that builds and runs the app: http://192.168.33.10:8080/job/angular_play_scala

## 5. See the Play app in the VM

Go to: http://192.168.33.10:9000/

## TODO

* Jenkins hangs and never exits from the job when it starts up Play in Prod mode (Play is working and just keeps listening). Find way to spawn process outside of Jenkins and to kill when pulling changes/restart.
* Integrate Berkshelf for Chef recipe dependency management
* Add option for provisioning and deploying on AWS EC2
* Create Vagrant base box with the software above already installed on it
* For some reason the MongoDB Chef cookbook doesn't support creating user authentication passwords. Fork https://github.com/edelight/chef-mongodb, add support and submit pull request.
* Add static analysis via Sonar or something else: http://community.opscode.com/cookbooks/sonar