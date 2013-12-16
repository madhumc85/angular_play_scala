## Overview

angular_play_scala is a simple demo app using AngularJS and the Play framework with Scala and MongoDB. Using Vagrant and Chef Solo you can set up a mini CI/CD environment in a VM that uses Jenkins to pull and run code from this repository.

A picture is worth a thousand words.

![alt tag](https://raw.github.com/excellaco/angular_play_scala/master/play_ci.png)

## Pre-req installations

* Java 6 or higher
* Play framework for local dev - http://downloads.typesafe.com/typesafe-activator/1.0.9/typesafe-activator-1.0.9.zip
* VirtualBox - https://www.virtualbox.org/wiki/Downloads
* Vagrant - http://www.vagrantup.com/downloads.html
* Git - if you don't already have

## 1. Vagrant

Creates a new Linux VM or starts a pre-existing one. Default config in the Vagrantfile: Ubuntu 64 bit, 4GB RAM.

    $ # cd to your clone directory of this repo
    $ vagrant up

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

## 2. MongoDB (in the VM)

Verify MongoDB is up in the VM: http://192.168.33.10:28017/

## 3. Start Play (locally)

Unzip the Play package, then:

    $ activator-1.0.9/activator ui

To start play in local OS in dev mode:

    $ play

Go to: 

## 4. Jenkins (in the VM)

The job is configured to automatically poll the GitHub repo every 60 seconds and execute.

To view or manually start the job that builds and runs the app: http://192.168.33.10:8080/job/angular_play_scala

## 5. See the Play app (in the VM)

Go to:

## TODO

* Integrate Berkshelf for Chef recipe dependency management
* Add option for provisioning and deploying on AWS EC2
* For some reason the MongoDB Chef cookbook doesn't support creating user authentication passwords. Fork https://github.com/edelight/chef-mongodb, add support and submit pull request.