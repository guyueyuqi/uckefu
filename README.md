# uckefu
Lightweight Q &amp; A community
#UCKeFu-ASK
UCKeFu is a Spring Boot-based lightweight community system designed to provide a community platform for online communication. (QQ group: 555834343, the current project is under development, has not yet released v1.0 version, to join the QQ group can understand the latest development progress and technical advice.):

![输入图片说明](http://git.oschina.net/uploads/images/2017/0123/001823_7efad50c_1200081.png "在这里输入图片标题")

URL：[UCKeFu-Ask（UCKeFu-ASK）](http://112.74.54.80:8080/) ， Account:admin Password:123456


Project composition:

 ** 1. Front End: LayUI + Freemarker **
 
 ** 1. Backend: Spring Boot **

 ** 1. Database: MySQL + Elasticsearch **

Project operation mode:

### 1. Pull the code down

### 1. Compile pom.xml file, download a good jar package
This project has two dependencies, IP2REGION and UCKeFu-Core, which are added to the local Mavenue repository through the following instructions:
1、mvn install:install-file  -Dfile=src/main/resources/WEB-INF/lib/ip2region-1.2.3.jar -DgroupId=org.lionsoul.ip2region -DartifactId=ip2region -Dversion=1.2.3 -Dpackaging=jar

2、mvn install:install-file  -Dfile=src/main/resources/WEB-INF/lib/UCKeFu-Core-0.3.0-SNAPSHOT.jar -DgroupId=com.ukefu -DartifactId=UCKeFu-Core -Dversion=0.3.0-SNAPSHOT -Dpackaging=jar
 ** Make sure both dependencies are installed successfully **

### 1. Configure the project in maven format
### 1. Run the ukefu.sql script in the mysql database to create the database and tables


### 1. Configure the database connection in the application.properties file in your project

### Tip: The project uses Elasticsearch, the memory has certain requirements, it is recommended to allocate memory as follows:
java -Xms1240m -Xmx1240m -Xmn450m -XX:PermSize=512M  -XX:MaxPermSize=512m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseTLAB -XX:NewSize=128m -XX:MaxNewSize=128m -XX:MaxTenuringThreshold=0 -XX:SurvivorRatio=1024 -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=60 -Djava.awt.headless=true  -XX:+PrintGCDetails -Xloggc:gc.log -XX:+PrintGCTimeStamps -jar UCKeFu-ASK-0.3.0-SNAPSHOT.jar



At this point the configuration is over, run the look at the results!


![输入图片说明](http://git.oschina.net/uploads/images/2017/0205/104057_4a8ab4e9_1200081.png "在这里输入图片标题")

![输入图片说明](http://git.oschina.net/uploads/images/2017/0205/104114_95778c4c_1200081.png "在这里输入图片标题")

![输入图片说明](http://git.oschina.net/uploads/images/2017/0205/104132_323c5211_1200081.png "在这里输入图片标题")

![输入图片说明](http://git.oschina.net/uploads/images/2017/0205/104042_ca0fd40f_1200081.png "在这里输入图片标题")

![输入图片说明](http://git.oschina.net/uploads/images/2017/0206/224016_351784fa_1200081.png "在这里输入图片标题")

![输入图片说明](http://git.oschina.net/uploads/images/2017/0206/222355_531978d6_1200081.png "在这里输入图片标题")
