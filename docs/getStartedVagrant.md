## Fabric8 Vagrant Image

This is the fastest way to get going with Fabric8 and OpenShift on your laptop.

* Download and install [VirtualBox](https://www.virtualbox.org/wiki/Downloads) 
* Download and install [Vagrant](http://www.vagrantup.com/downloads.html)
  
First clone the [fabric8 installer git repository](https://github.com/fabric8io/fabric8-installer) repository and type these commands:

```sh
$ git clone https://github.com/fabric8io/fabric8-installer.git
$ cd fabric8-installer/vagrant/openshift-latest
```

Depending on your host operating system you need to install an additional vagrant plugin:

* `vagrant plugin install landrush` for Linux and OS X
* `vagrant plugin install vagrant-hostmanager-fabric8` for Windows

The next steps are needed for proper routing from the host to OpenShift services which are exposed via routes: 

* **Linux**: Setup up once a `dnsmasq` DNS proxy locally. The detailed procedure depend on the Linux distribution used. 
  Here is the example for Ubuntu:
   
```sh
sudo apt-get install -y resolvconf dnsmasq
sudo sh -c 'echo "server=/vagrant.dev/127.0.0.1#10053" > /etc/dnsmasq.d/vagrant-landrush'
sudo service dnsmasq restart
```

* **Windows**: Unfortunately for Windows no automatic routing for new services is possible. You have to add new routes 
  manually to `%WINDIR%\System32\drivers\etc\hosts`. For your convenience, a set of routes for default Fabric8 applications 
  has been pre-added. For new services look for the following line and add your new routes (`<service-name>.vagrant.f8`) to 
  this file on a new line like this:
  
```sh
## vagrant-hostmanager-start id: 9a4ba3f3-f5e4-4ad4-9e80-b4045c6cf2fc
172.28.128.4  vagrant.f8 fabric8.vagrant.f8 fabric8-master.vagrant.f8 jenkins.vagrant.f8 .....
172.28.128.4  myservice.vagrant.f8
## vagrant-hostmanager-end
```

* **OS X**: Nothing has to be done. OS X will automatically resolve all routes to `*.vagrant.f8` to 
  your Vagrant VM. This is done vial OS X's resolver feature (see `man 5 resolver` for details)

Now startup the Vagrant VM

```sh
vagrant up
```

Note the vagrant image is by default configured with 2 cpu cores and 4gb of memory. It is recommended to not exceed about half of your machine’s resources. In case you have plenty of resources on your machine you can increase the settings, by editing the `Vagrantfile`. The settings are defined in the bottom of the file:

```ruby
v.memory = 4096
v.cpus = 2
```

Then follow the on screen instructions.
 
* You should now have a running vagrant image running at IP address `172.28.128.4` or at `vagrant.f8`

* Downloading the docker images may take a few minutes so you might want to jump ahead to the [Setting up your local machine](#setting-up-your-local-machine) then coming back here when you're done. 

* After the vagrant box is created and docker images are downloaded, the [fabric8 console](http://fabric8.io/guide/console.html) should appear at [http://fabric8.vagrant.f8/](http://fabric8.vagrant.f8/)
  
* When you first open your browser Chrome will say:

```
Your connection is not private
```

* Don't panic! This is to be expected.
* Click on the small `Advanced` link on the bottom left
* Now click on the link that says `Proceed to fabric8.vagrant.f8 (unsafe)` bottom left
* Now the browser should redirect to the login page 
* Enter `admin` and `admin`
* You should now be in the main fabric8 console! That was easy eh! :)
* Make sure you start off in the `default` namespace.


### Installing other applications

When you are on the `Apps` tab in the [fabric8 console](http://fabric8.io/guide/console.html) click on the `Run...` button. 

This will list all of the installed [OpenShift Templates](http://docs.openshift.org/latest/dev_guide/templates.html) on your installation.

* To Run any of the installed templates just click the `Run` button (the green play button).
* To install any new [OpenShift Templates](http://docs.openshift.org/latest/dev_guide/templates.html) or other Kubernetes resources just drag and drop the JSON file onto the `Apps` tab!
  * You can download the [fabric8 templates 2.2.3 distribution](http://repo1.maven.org/maven2/io/fabric8/apps/distro/2.2.3/distro-2.2.3-templates.zip) unzip and drag the JSON files you want to install onto the [fabric8 console](http://fabric8.io/guide/console.html) and they should appear on the `Run...` page  
* You can also install other OpenShift Templates or Kubernetes resources via the **oc** command line tool:

    oc create -f jsonOrYamlFileOrUrl


### Setting up your local machine

Its useful being able to use the command line tools in OpenShift or using [fabric8 maven tooling](http://fabric8.io/guide/mavenPlugin.html) or reusing the docker daemon inside vagrant; so that all images built are accesible inside the OpenShift environment.

Follow these steps:

* [Download the recent OpenShift release binaries for your platform](https://github.com/openshift/origin/releases/)
* unpack the tarball and put the binaries on your PATH
* Set the following environment variables
  - Unix flavored OSs:
  
          export KUBERNETES_DOMAIN=vagrant.f8
          export DOCKER_HOST=tcp://vagrant.f8:2375

  - Windows:

          set KUBERNETES_DOMAIN=vagrant.f8
          set DOCKER_HOST=tcp://vagrant.f8:2375

 It is probably a good idea to add this into your `~/.profile` (Linux, OS X) or *System* -> *Advance System Settings* -> *Environment Variables* (Windows)
 

* Now login to OpenShift via this command:

```sh
oc login https://172.28.128.4:8443
```

* Enter `admin` and `admin` for user/password

Over time your token may expire and you will need to re-authenticate via:

```sh
oc login
```

Now to see the status of the system:

```sh
oc get pods
```
or you can watch from the command line via one of these commands:

```sh
watch oc get pods
oc get pods --watch
```

Have fun! We [love feedback](http://fabric8.io/community/)

### Trying a fresh image

Note: in case you already ran the above and want to update to the latest vagrant image, OpenShift and Fabric8 release you need to destroy and recreate the vagrant image.
You can do that using:

```sh
vagrant destroy -f
git pull
vagrant up
```

### Troubleshooting

The `oc` command can be really useful for viewing resources (pods, services, RCs, templates etc) and deleting pods so they get recreated, looking at logs etc.

If you add the `fabric8-installer/bin` folder to your `$PATH` then there are a few handy shell scripts

* `oc-bash name` finds the first pod with the given name pattern and runs a bash shell in the docker container
* `oc-log name` finds the first pod with the given name pattern and shows the container's log

On your host machine or inside the vagrant image you should have access to docker thanks to the environment variable:

```sh
export DOCKER_HOST=tcp://vagrant.f8:2375
```

So you should be able to run things like

```sh
docker ps
docker log nameOfContainer
```

You can SSH into the vagrant image using:

```sh
vagrant ssh
```

Then you can look around.

#### Looking at the OpenShift logs

If you hit any issues then try look at the logs for the `openshift` master:

```sh
vagrant ssh
sudo journalctl -u openshift
```

Or you can tail the logs via 

```sh
vagrant ssh
sudo journalctl -fu openshift
```

You can watch the docker daemon too via

```sh
vagrant ssh
sudo journalctl -fu docker
```

