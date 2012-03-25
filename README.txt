Notice:The file is encoded by UTF-8

HomePage:http://code.taobao.org/p/taokeeper/wiki/index/monitor/
CopyRight by Taobao.com
Any question to:yinshi.nc@taobao.com

一、代码提交须知
请认真检查所提交代码中是否包含内部信息，涉及的文件包括如下：

taokeeper
  |__config
       |__*.properties

taokeeper-monitor
  |__src/main/resource
         |__logback.xml(LOG_HOME)
  |__src/main/webapp/WEB-INF
         |__spring-beans.xml(configOfMsgCenter,dbJDBCUrl,username,password)
         
         
二、Database Initialization
init/taokeeper.sql



How to deploy

1. Download taokeeper.sql(http://115.com/file/anim36ag),init mysql.
2. Download taokeeper-monitor.tar.gz(http://115.com/file/anim3yc5), 
   tar -zxvf taokeeper-monitor.tar.gz  to webapps of tomcat, make sure the path is %TOMCAT_HOME%\webapps\taokeeper-monitor\WEB-INF

3. Download taokeeper-monitor-config.properties(http://115.com/file/c2upgkms), store it such as /home/admin/taokeeper-monitor/config/taokeeper-monitor-config.properties

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
         
        