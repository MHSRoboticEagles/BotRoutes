# BotRoutes
This program helps design and manage autonomous movement of odometry-enabled robots.

![Main Application Screen](https://github.com/MHSRoboticEagles/BotRoutes/blob/master/mainview.png)


## Installation Mac
* Download and install [OpenJDK 11](https://adoptopenjdk.net/). Choose version 11 and HotSpot JVM. Once the pkg file is downloaded
double-click and follow the instruction to install java.
Verify your installation.
Open the Terminal window and run this command in the Terminal:

```
$ java - version
```

* Download [Java FX SDK v11](https://github.com/MHSRoboticEagles/BotRoutes/install/javafx-sdk-11.0.2.zip)
Unzip the content of the zip archive, for example under your home directory ~/javafx-sdk-11.0.2

* Create a directory *botroutes* under your home directory: ~/botroutes

* Download the latest version of BotRoutes.jar from [Releases](https://github.com/MHSRoboticEagles/BotRoutes/releases)
and place it under ~/botroutes directory

* Download configuration file from [this repository](https://github.com/MHSRoboticEagles/BotRoutes/tree/master/config) 
and place them in the ~/botroutes directory.

* Start the program using the following command:

```
$java --module-path ~/javafx-sdk-11.0.2/lib/ --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics -jar ~/botroutes/BotRoutes.jar
```





## Installation Windows

* Download [JDK v11 for Windows](https://drive.google.com/file/d/11hsKFEIB7-xUSquy6NNCvm2jabP3TImC/view?usp=sharing)
* Unzip into a folder (e.g. c:\java-11)

* Download [Java FX SDK v11 for Windows](https://drive.google.com/file/d/12tRBLLat70vz7KL-LiJwN1T2W8K6ctAj/view?usp=sharing)
* Unzip into a folder (e.g. c:\javafx-11)

* Create a new folder c:\MasterOdo
* Download the latest version of BotRoutes.jar from [Releases](https://github.com/MHSRoboticEagles/BotRoutes/releases)
* Place BotRoutes.jar under c:\MasterOdo

* Create *botroutes* folder in your home directory c:\Users\<your-name>
* Download configuration file from [this repository](https://github.com/MHSRoboticEagles/BotRoutes/tree/master/config) and place them in the botroutes folder.

* Create a new file run.bat in c:\MasterOdo folder with the following content:
c:\java-11\bin\java --module-path c:\javafx-11\lib --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics -jar c:\MasterOdo\BotRoutes.jar`` 

* Save the run.bat file

* Create a shortcut to the file on the Desktop

* Double-click to run

