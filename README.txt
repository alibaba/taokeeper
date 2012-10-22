Notice:The file is encoded by UTF-8

HomePage: http://rdc.taobao.com/team/jm/archives/1450
CopyRight by Taobao.com
Any question to: nileader@gmail.com
   
   
1. Use To manage projects dependence using maven
2. Database Initialization: taokeeper-build/sql/taokeeper.sql
3. Implements com.taobao.taokeeper.reporter.alarm.MessageSender to send message.
4. Exec taokeeper-build/build.cmd to generate taokeeper-monitor.war



How to deploy(See more,please to http://rdc.taobao.com/team/jm/archives/1450 )

1. Download taokeeper.sql( https://github.com/downloads/taobao/taokeeper/taokeeper.sql ),init mysql.

2. Download taokeeper-monitor.tar.gz ( https://github.com/downloads/taobao/taokeeper/taokeeper-monitor.tar.gz ), 
    tar -zxvf taokeeper-monitor.tar.gz  to webapps of tomcat, 
    make sure the path is %TOMCAT_HOME%\webapps\taokeeper-monitor\WEB-INF

3. Download taokeeper-monitor-config.properties https://github.com/downloads/taobao/taokeeper/taokeeper-monitor-config.properties ), 
    store it such as /home/admin/taokeeper-monitor/config/taokeeper-monitor-config.properties

----------------------------------------------------------------
systemInfo.envName=TEST
#DBCP
dbcp.driverClassName=com.mysql.jdbc.Driver
dbcp.dbJDBCUrl=jdbc:mysql://1.1.1.1:3306/taokeeper
dbcp.characterEncoding=GBK
dbcp.username=xiaoming
dbcp.password=123456
dbcp.maxActive=30
dbcp.maxIdle=10
dbcp.maxWait=10000
#SystemConstant
SystemConstent.dataStoreBasePath=/home/xiaoming/taokeeper-monitor/ZookeeperStore
#SSH account of zk server
SystemConstant.userNameOfSSH=xiaoming
SystemConstant.passwordOfSSH=123456
------------------------------------------------------------------

4. Add JAVA_OPTS to tomcat catalina.bat or catalina.sh:
windows：set JAVA_OPTS=-DconfigFilePath="D:\server\tomcat-6.0.33\webapps\taokeeper-monitor-config.properties"
linux：JAVA_OPTS=-DconfigFilePath="/home/admin/taokeeper-monitor/config/taokeeper-monitor-config.properties"

5. Startup tomcat

6. Visit the page: http://127.0.0.1:8080/taokeeper-monitor
         
        