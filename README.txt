Notice:The file is encoded by UTF-8

HomePage:http://code.taobao.org/p/taokeeper/wiki/index/monitor/
CopyRight by Taobao.com
Any question to:yinshi.nc@taobao.com
   
   

1. Use To manage projects dependence using maven
2. Database Initialization: taokeeper-build/sql/taokeeper.sql
3. Implements com.taobao.taokeeper.reporter.alarm.MessageSender to send message.
4. Exec taokeeper-build/build.cmd to generate taokeeper-monitor.war



How to deploy(See more,please to http://rdc.taobao.com/team/jm/archives/1450 )

1. Download taokeeper.sql(http://115.com/file/anim36ag),init mysql.
2. Download taokeeper-monitor.tar.gz(http://115.com/file/c2uwxv38), 
   tar -zxvf taokeeper-monitor.tar.gz  to webapps of tomcat, make sure the path is %TOMCAT_HOME%\webapps\taokeeper-monitor\WEB-INF

3. Download taokeeper-monitor-config.properties(http://115.com/file/be6bp6er), store it such as /home/admin/taokeeper-monitor/config/taokeeper-monitor-config.properties

----------------------------------------------------------------
systemInfo.envName=TEST
#DBCP
dbcp.driverClassName=com.mysql.jdbc.Driver
dbcp.dbJDBCUrl=jdbc:mysql://1.1.1.1:3306/taokeeper
dbcp.characterEncoding=GBK
dbcp.username=root
dbcp.password=123456
dbcp.maxActive=30
dbcp.maxIdle=10
dbcp.maxWait=10000
#SystemConstant
SystemConstent.dataStoreBasePath=/home/admin/taokeeper-monitor/ZookeeperStore
#SSH account of zk server
SystemConstant.userNameOfSSH=admin
SystemConstant.passwordOfSSH=123456
------------------------------------------------------------------

4. Add JAVA_OPTS to tomcat catalina.bat or catalina.sh:
windows：set JAVA_OPTS=-DconfigFilePath="D:\server\tomcat-6.0.33\webapps\taokeeper-monitor-config.properties"
linux：JAVA_OPTS=-DconfigFilePath="/home/admin/taokeeper-monitor/config/taokeeper-monitor-config.properties"
5. Startup tomcat

6. http://127.0.0.1:8080/taokeeper-monitor
         
        